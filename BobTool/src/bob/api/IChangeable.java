package bob.api;

/**
 * Beschreibt ein Werkzeug, das nur über die Speicherung der Daten bzw. die 
 * Rücknahme der aktuellen Änderungen verlassen werden kann.
 * 
 * @author maik.boettcher@bur-kg.de
 *
 */
public interface IChangeable {
	
	/** Meldung wenn Bearbeitungsmodus Aktion verhindert */
	String CHANGEABLE_BLOCKER_TEXT = 
			"Der Bearbeitungsmodus vom aktiven Werkzeug verhindert die Aktion.\n" +
			"Die Änderung muss gespeichert oder rückgängig gemacht werden.";

	/**
	 * Startet den Bearbeitungsmodus.
	 */
	void startChange();
	
	/**
	 * Löst die Speicherung der Daten aus und liefert <code>true</code> bei 
	 * erfolgreicher Ausführung.
	 * @return <code>true</code> bei Erfolg
	 */
	boolean dataSave();
	
	/**
	 * Löst die Rücknahme der Daten aus und liefert <code>true</code> bei 
	 * erfolgreicher Ausführung.
	 * @return <code>true</code> bei Erfolg
	 */
	boolean dataReset();

}