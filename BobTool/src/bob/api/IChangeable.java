package bob.api;

/**
 * Beschreibt ein Werkzeug, das nur �ber die Speicherung der Daten bzw. die 
 * R�cknahme der aktuellen �nderungen verlassen werden kann.
 * 
 * @author maik.boettcher@bur-kg.de
 *
 */
public interface IChangeable {
	
	/** Meldung wenn Bearbeitungsmodus Aktion verhindert */
	String CHANGEABLE_BLOCKER_TEXT = 
			"Der Bearbeitungsmodus vom aktiven Werkzeug verhindert die Aktion.\n" +
			"Die �nderung muss gespeichert oder r�ckg�ngig gemacht werden.";

	/**
	 * Startet den Bearbeitungsmodus.
	 */
	void startChange();
	
	/**
	 * L�st die Speicherung der Daten aus und liefert <code>true</code> bei 
	 * erfolgreicher Ausf�hrung.
	 * @return <code>true</code> bei Erfolg
	 */
	boolean dataSave();
	
	/**
	 * L�st die R�cknahme der Daten aus und liefert <code>true</code> bei 
	 * erfolgreicher Ausf�hrung.
	 * @return <code>true</code> bei Erfolg
	 */
	boolean dataReset();

}