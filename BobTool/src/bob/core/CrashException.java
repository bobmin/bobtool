package bob.core;

/**
 * Beschreibt schwere Ausnahmefehler, die zum Programmabsturz führen.
 * 
 * @author maik.boettcher@bur-kg.de
 *
 */
public class CrashException extends RuntimeException {
	
	public CrashException(final String message) {
		super(message);
	}
	
	public static class ServiceUnreachable extends CrashException {
		
		private static final String MESSAGE_FORMAT = "SERVICE UNREACHABLE: %s";
		
		public ServiceUnreachable(final Class<?> type) {
			super(String.format(MESSAGE_FORMAT, type.getName()));
		}
		
	}

}
