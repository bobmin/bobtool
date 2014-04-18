package bob.core;

/**
 * Lokal im Dateisystem gespeicherte Benutzerkonfiguration.
 * 
 * @author maik@btmx.net
 *
 */
public class Config {
	
	private static final Config _self = new Config();
	
	private Config() {
	}

	public static Config getDefault() {
		return _self;
	}

	public Integer getInteger(final String key) {
		return null;
	}
	
}
