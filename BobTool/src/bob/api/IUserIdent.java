package bob.api;

/**
 * Beschreibt die 
 * <a href="http://de.wikipedia.org/wiki/Authentifizierung">Authentifizierung</a> 
 * für einen Benutzer.
 * 
 * @author maik.boettcher@bur-kg.de
 *
 */
public interface IUserIdent {

	public boolean isAuthorized();

	public void authenticate(final String user, final char[] pass);
	
}
