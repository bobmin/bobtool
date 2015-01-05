package bob.core;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Eine Bibliothek für die Symbole.
 * 
 * @author maik.boettcher@bur-kg.de
 *
 */
public class BobIcon {

	public static final Icon DOOR_OPEN = 
			new ImageIcon(BobIcon.class.getResource("/icons/door_open.png"));
	
	public static final Icon LOCK_OPEN = 
			new ImageIcon(BobIcon.class.getResource("/icons/lock_open.png"));
	
	public static final Icon LOCK = 
			new ImageIcon(BobIcon.class.getResource("/icons/lock.png"));
	
	/** ein Anwendungsfenster: Standard für Werkzeuge */
	public static final Icon APPLICATION = 
			new ImageIcon(BobIcon.class.getResource("/icons/application.png"));

	/** das Fragezeigen: Werkzeug-Hilfe */
	public static final Icon HELP = 
			new ImageIcon(BobIcon.class.getResource("/icons/help.png"));

}
