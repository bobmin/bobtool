package bob.tool.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import bob.core.BobIcon;
import bob.tool.AbstractApplication;

/**
 * Aktion um Programm kontrolliert zu beenden. 
 */
public class PluginHelp extends AbstractAction {
	
	private static final Object ACTION_NAME = "Programm beenden";
	
	private final AbstractApplication app;
	
	public PluginHelp(final AbstractApplication app) {
		this.app = app;
		putValue(Action.NAME, "Hilfeseite zum Werkzeug");
		putValue(Action.SMALL_ICON, BobIcon.HELP);
		putValue(Action.SHORT_DESCRIPTION, "Öffnet eine Hilfeseite im Browser mit weiteren Informationen zum aktuellen Werkzeug.");	
	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {
		// TODO Hilfeseite vom aktuelle Werkzeug aufrufen
		// http://java.sun.com/developer/technicalArticles/J2SE/Desktop/javase6/desktop_api/
//		boolean success = false;
//		if (Desktop.isDesktopSupported()) {
//			desktop = Desktop.getDesktop();
//			if (desktop.isSupported(Desktop.Action.BROWSE)) {
//				try {
//		            final URI uri = new URI(url);
//		            desktop.browse(uri);
//					success = true;
//		        } catch(final IOException ex) {
//		            ex.printStackTrace();
//		        } catch(final URISyntaxException ex) {
//		            ex.printStackTrace();
//		        }
//	        }
//		}
//		if (!success) {
//			JOptionPane.showMessageDialog(BurToolGui.this, 
//					"Die Hilfeseite konnte nicht geöffnet werden.\n" +
//					"Adresse: " + url, 
//					TITLE, JOptionPane.ERROR_MESSAGE);
//		}
	}
	
}