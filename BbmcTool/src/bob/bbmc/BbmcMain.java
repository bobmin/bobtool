package bob.bbmc;

import bob.tool.BtMain;

/**
 * Hauptklasse vom BBMC-Werkzeugkasten. Konfiguriert und startet den 
 * Werkzeugkasten.
 * 
 * @author maik.boettcher@bur-kg.de
 *
 */
public class BbmcMain {
	
	private static final String TITLE = "BbmcTool v0.9";
	
	public BbmcMain() {
	}
	
	/**
	 * Startet die Anwendung.
	 * @param args optinonale Konsolenparameter
	 */
	public static void main(final String[] args) {
		BtMain.startApp(TITLE);
	}

}
