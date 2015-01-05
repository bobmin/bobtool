package bob.demo;

import bob.tool.AbstractApplication;

/**
 * Verwaltet den Lebenszyklus der Anwendung.
 * 
 * @author maik.boettcher@bur-kg.de
 *
 */
public class DemoApplication extends AbstractApplication {
	
	/**
	 * Startet die Anwendung.
	 * @param args die Konsolenparameter
	 */
	public static void main(final String[] args) {
		final DemoApplication app = new DemoApplication();
		app.show();
	}

}
