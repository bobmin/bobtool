package bob.tool;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Objects;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import bob.api.IAction;
import bob.api.IChangeable;
import bob.api.IToolSettings;
import bob.api.IValidation;
import bob.core.BobCrashHandler;
import bob.core.CrashException;
import bob.core.ServiceUtils;
import bob.core.Utils;

abstract public class AbstractApplication {
	
	/** die Build-Nummer aus <tt>/src/resources/burtool.build</tt> */
	public static String BUILD_NUMBER = null;
	
	/** die Programmvorgaben */
	private final IToolSettings settings;
	
	/** die Benutzeroberfläche */
	private final BtFrame frame;

	private final BtLoginManager lm;
	
	private final BtPluginManager pm;
	
	/** <code>true</code> wenn Demonstrationsmodus */
	private boolean demoActivated = false;
	
	/** Werkzeug im Bearbeitungsmodus */
	private IChangeable changeable = null;

	/**
	 * Bereitet die Anwendung zum Start vor. Holt die Programmvorgaben, 
	 * konfiguriert einen {@link UncaughtExceptionHandler}, setzt das 
	 * <i>Look & Feel</i> und instanziiert die Benutzeroberfläche.
	 */
	public AbstractApplication() {
		// Einstellungen suchen/holen
		settings = ServiceUtils.locate(IToolSettings.class);
		if (null == settings) {
			throw new CrashException.ServiceUnreachable(IToolSettings.class);
		}
		// Ausnahemfehler protokollieren
		final BobCrashHandler handler = 
				new BobCrashHandler(settings.getToolTitle());
		Thread.setDefaultUncaughtExceptionHandler(handler);
		// Manager starten
		lm = new BtLoginManager(this);
		pm = new BtPluginManager(this);
		// Look & Feel
		//    TODO LookAndFeel über Settings
		Utils.setupNimbus();
		frame = new BtFrame(this);
		
	}
	
	public void show() {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				frame.setVisible(true);
			}
		});
	}

	/**
	 * Liefert die Programmvorgaben.
	 * @return ein Objekt, niemals <code>null</code>
	 */
	public IToolSettings getSettings() {
		return settings;
	}

	/**
	 * Beendet die Anwendung kontrolliert.
	 */
	public void shutdown() {
		// Schwerer Ausnahmefehler?
//		if (!showExceptionBlocker()) {
//			// Bearbeitungsmodus?
//			if (!showChangeableBlocker()) {
//				// Plugin stoppen
//				pluginManager.stopCurrentPlugin();
//				// UI ausmachen
//				if (null != gui) {
//					gui.setVisible(false);
//				}
//				// Config speichern
//				Config.getDefault().save();
				// VM beenden
				System.exit(0);
//			}
//		}
	}

	public boolean isDemoActivated() {
		return demoActivated ;
	}

	/**
	 * Liefert das Programmfenster.
	 * @return ein Objekt, niemals <code>null</code>
	 */
	public BtFrame getFrame() {
		return frame;
	}

	/**
	 * Liefert die Benutzerverwaltung.
	 * @return ein Objekt, niemals <code>null</code>
	 */
	public BtLoginManager getLoginManager() {
		return lm;
	}
	
	/**
	 * Zeigt ein {@link JOptionPane}.
	 * @param msg der Nachrichtentext
	 * @param type der Nachrichtentyp
	 */
	public void showMessage(final String msg, final int type) {
		String title = null;
		switch (type) {
		case JOptionPane.WARNING_MESSAGE:
			title = "Warnung";
			break;
		case JOptionPane.ERROR_MESSAGE:
			title = "Fehler";
			break;
		default:
			title = "Hinweis";
		}
		JOptionPane.showMessageDialog(frame, msg, title, type);
	}
	
	public void showDialog(final JPanel panel, 
			final String title, final int type, final String[] options,
			final ActionListener listener, final IValidation validation) {
		BtDialog.show(this, panel, title, type, options, listener, validation);
	}

	/**
	 * Liefert den {@link BtPluginManager}.
	 * @return ein Objekt, niemals <code>null</code>
	 */
	public BtPluginManager getPluginManager() {
		return pm;
	}
	
	public boolean isChangeMode() {
		return (null != changeable);
	}
	
	/**
	 * Liefert <code>true</code> wenn die Anwendung im Bearbeitungsmodus ist.
	 * Ein Hinweis wird eingeblendet.
	 * @return <code>true</code> wenn Bearbeitungsmodus
	 */
	public boolean showChangeableBlocker() {
		boolean x = false;
		if (isChangeMode()) {
			JOptionPane.showMessageDialog(frame, 
					IChangeable.CHANGEABLE_BLOCKER_TEXT, 
					settings.getToolTitle(), JOptionPane.ERROR_MESSAGE);
			x = true;
		}
		return x;
	}

	/**
	 * Sperrt das Anwendungsfenster oder gibt es wieder frei.
	 * @param b <code>false</code> sperrt das Anwendungsfenster
	 */
	public void switchGlassPane(final boolean b) {
		final Component x = frame.getGlassPane();
		x.setVisible(!b);
	}

	public boolean dataSave() {
		Objects.requireNonNull(changeable, "[changeable] is NULL");
		boolean success = Boolean.valueOf(false);
		if (changeable.dataSave()) {
			success = Boolean.valueOf(true);
			changeable = null;
		}
		return success;
	}
	
	public boolean dataReset() {
		Objects.requireNonNull(changeable, "[changeable] is NULL");
		boolean success = Boolean.valueOf(false);
		if (changeable.dataReset()) {
			success = Boolean.valueOf(true);
			changeable = null;
		}
		return success;
	}

	public void showPlugin(
			final JPanel topComponent, final Set<IAction> actions, 
			final String helpurl) {
		frame.setTopComponent(topComponent);
		frame.setActions(actions);
		frame.setHelpUrl(helpurl);
		frame.repaint();
	}

	public void fireException(final String message) {
		frame.startException(message);
	}

}
