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
	
	public static class SettingsUnreachabl extends BobException {
		
		private static final String MESSAGE_FORMAT = "SETTINGS UNREACHABLE: %s";
		
		public SettingsUnreachabl(final Class<?> type) {
			super(String.format(MESSAGE_FORMAT, type.getName()));
		}
		
	}

}
