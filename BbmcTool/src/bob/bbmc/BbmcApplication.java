package bob.bbmc;

import bob.tool.AbstractApplication;

/**
 * Verwaltet den Lebenszyklus der Anwendung.
 * 
 * @author maik.boettcher@bur-kg.de
 *
 */
public class BbmcApplication extends AbstractApplication {
	
	/**
	 * Startet die Anwendung.
	 * @param args die Konsolenparameter
	 */
	public static void main(final String[] args) {
		final BbmcApplication app = new BbmcApplication();
		app.show();
	}

}
