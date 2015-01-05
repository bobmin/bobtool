package bob.core;

/**
 * Definiert die schweren Ausnahmefehler, die beim BobTool auftreten können.
 * 
 * @author maik@btmx.net
 *
 */
public class BobException extends Exception {
	
	public BobException(final String message) {
		super(message);
	}
	
}
