package bob.api;

/**
 * Beschreibt die globalen Einstellungen für den Werkzeugkasten.
 * 
 * @author maik@btmx.net
 *
 */
public interface IToolSettings {
	
	/**
	 * Liefert den Programmtitel.
	 * @return eine Zeichenkette, niemals <code>null</code>
	 */
	String getToolTitle();
	
	/**
	 * Liefert den vollständigen Klassennamen vom Standardwerkzeug. 
	 * @return eine Zeichenkette, niemals <code>null</code>
	 */
	String getDefaultPlugin();

	/**
	 * Liefert den Pfad zum Programmsymbol.
	 * @return eine Zeichenkette, niemals <code>null</code>
	 */
	String getProgramIcon();

}
