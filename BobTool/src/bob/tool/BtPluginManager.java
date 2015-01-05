package bob.tool;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import bob.api.IAction;
import bob.api.IPlugin;
import bob.api.IToolContext;
import bob.core.CrashException;
import bob.core.ServiceUtils;
import bob.core.Utils;

/**
 * Verwaltet die registrierten Werkzeuge. Kennt das Standardwerkzeug. Es wird
 * benutzt, wenn der Start eines Werkzeugs fehlschlägt. Werkzeuge werden über
 * {@link ServiceLoader} mit Interface <tt>bob.api.IPlugin</tt> gefunden. 
 * 
 * @author maik@btmx.net
 *
 */
public class BtPluginManager {
	
	/** ein Logger */
	private static final Logger LOG = 
			Logger.getLogger(BtPluginManager.class.getName());
	
	/** das Programm */
	private final AbstractApplication app;
	
	/** registrierte Werkzeuge */
	public Set<IPlugin> pluginList = null;
	
	/** das Standardwerkzeug */
	private IPlugin defaultPlugin;
	
	/** die Werkzeugstart-Aktionen */
	private final HashMap<IPlugin, PluginAction> actionMap = new HashMap<>();
	
	/** das aktuelle Werkzeug */
	private IPlugin currentPlugin = null;
	
	public BtPluginManager(final AbstractApplication app) {
		this.app = app;
		// alle Plugins suchen/laden
		pluginList = ServiceUtils.locateAll(IPlugin.class);
		if (null == pluginList || 0 == pluginList.size()) {
			throw new CrashException.ServiceUnreachable(IPlugin.class);
		}
		// Standard-Plugin ermitteln
		//    TODO mit Wert aus Settings belegen
		final String configDefaultPlugin = app.getSettings().getDefaultPlugin();
		search: for (IPlugin p: pluginList) {
			Class<?> searchClass = null;
			// wenn Standardwerkzeug definiert, dann instanziieren
			if (Utils.isNotEmpty(configDefaultPlugin)) {
				try {
					searchClass = Class.forName(configDefaultPlugin);
					if (p.getClass() == searchClass) {
						defaultPlugin = p;
						break search;
					}
				} catch (ClassNotFoundException e) {
					LOG.log(Level.WARNING, "STANDARDWERKZEUG FEHLT", e);
				}
			}			
		}
	}

	/**
	 * Liefert die {@link Action} zum Werkzeugstart.
	 * @param p das Werkzeug
	 * @return ein Objekt, niemals <code>null</code>
	 */
	public Action getPluginAction(final IPlugin p) {
		if (!actionMap.containsKey(p)) {
			final PluginAction action = new PluginAction(p);
			actionMap.put(p, action);
		}
		return actionMap.get(p);
	}
	
	private void firePluginChanged(final IPlugin p, final int modifiers) {		
		final ManagerEvent evt = new ManagerEvent(p, modifiers);		
		pluginChange(evt);
	}
	
	private void pluginChange(final ManagerEvent evt) {
		if (app.showChangeableBlocker()) {
			return;
		}
		
		app.switchGlassPane(false);

		// neues Plugin starten
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public  void run() {
								
				// wenn altes Plugin vorhanden, 
				// dann stoppen
				if (null != currentPlugin) {
					currentPlugin.stop();
				}
				
//				try {
					final IPlugin plugin = evt.getNewPlugin();
					
					// Programmumgebung für neues Werkzeug
					final IToolContext context = new BtContext(app);
					
					// neues Werkzeug starten
					JPanel topComponent = plugin.start(context, evt.getModifiers());

					// Fallback: Default-Werkzeug verwenden
					if (null == topComponent) {
						topComponent = defaultPlugin.start(context, evt.getModifiers());
						currentPlugin = defaultPlugin;
					}
					
					// TopComponent + Aufgaben + Hilfeseite
					final Set<IAction> actionsNew = plugin.getActions();
					final String url = plugin.getHelpUrl();
					app.showPlugin(topComponent, actionsNew, url);
					
					// Historie aktualisieren
					if (!evt.isReused()) {
						// TODO Historie/Tracker implementieren
//						tracker.createMilestone(plugin.getLabel());
					}

					app.switchGlassPane(true);

					// Signal: Werkzeug wurde gestartet
					plugin.started();
					
					final String label = plugin.getLabel();
					LOG.info("plugin was started: *****  " + label + "  *****");
					
//				} catch (final BobException ex) {
//					ex.printStackTrace();
//					app.fireException(ex.getMessage());
//					
//				}
			}
		});
		
	}

	/**
	 * Eine {@link Action} für den Werkzeugstart.
	 */
	public class PluginAction extends AbstractAction {
		
		/** das Werkzeug */
		private IPlugin plugin = null;		
		
		public PluginAction(final IPlugin plugin) {
			this.plugin = plugin;
			putValue(Action.NAME, plugin.getLabel());
			putValue(Action.SMALL_ICON, plugin.getIcon());
			putValue(Action.SHORT_DESCRIPTION, plugin.getDesc());
		}

		@Override
		public void actionPerformed(final ActionEvent evt) {
			firePluginChanged(plugin, evt.getModifiers());
		}
		
	}
	
	private class ManagerEvent {
		
		private final IPlugin newPlugin;
		
		private final int modifiers;
		
		public ManagerEvent(final IPlugin newPlugin, final int modifiers) {
			this.newPlugin = newPlugin;
			this.modifiers = modifiers;
		}
		
		/**
		 * Liefert <code>true</code> wenn es sich beim Ereignis um eine 
		 * Wiederholung handelt.
		 * @return <code>true</code> wenn Wiederholung
		 */
		public boolean isReused() {
			// TODO Wiederholung implementieren
			return false;
		}

		public IPlugin getNewPlugin() {
			return newPlugin;
		}
		
		public int getModifiers() {
			return modifiers;
		}
		
	}
	
}
