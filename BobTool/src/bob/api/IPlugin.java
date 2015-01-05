package bob.api;

import java.util.Set;

import javax.swing.Icon;
import javax.swing.JPanel;

/**
 * Beschreibt ein Werkzeug.
 * 
 * @author maik@btmx.net
 *
 */
public interface IPlugin {
	
	/** 
	 * Liefert einen Bezeichner. 
	 * @return eine Zeichenkette, niemals <code>null</code>
	 */
	String getLabel();
	
	/**
	 * Liefert ein Icon.
	 * @return ein Objekt, niemals <code>null</code>
	 */
	Icon getIcon();
	
	/**
	 * Liefert eine Beschreibung.
	 * @return eine Zeichenkette, niemals <code>null</code>
	 */
	String getDesc();

	void stop();

	/**
	 * Startet das Werkzeug und liefert die Benutzeroberfläche. Kann das 
	 * Werkzeug nicht gestartet werden, wird <code>null</code> geliefert.
	 * @param context die Programmumgebung
	 * @param modifiers die Tastaturkonf.
	 * @return ein Objekt oder <code>null</code>
	 */
	JPanel start(final IToolContext context, final int modifiers);

	/**
	 * Wird aufgerufen, wenn das Werkzeug komplett gestartet ist.
	 */
	void started();

	Set<IAction> getActions();

	/**
	 * Liefert die Adresse für eine Hilfeseite zum Werkzeug. Wenn keine Hilfe 
	 * angeboten wird, wird <code>null</code> geliefert.
	 * @return eine Zeichenkette oder <code>null</code>
	 */
	String getHelpUrl();

}
