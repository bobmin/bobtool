package bob.tool;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import bob.api.IChangeable;
import bob.core.BobIcon;
import bob.core.BobUserIdent;

/**
 * Organisiert die Anmeldung und Abmeldung vom Programm.
 * 
 * @author maik@btmx.net
 *
 */
public class BtLoginManager {

	/** Beschriftung Anmelden */
	private static final String ANMELDEN_LABEL = "Anmelden";

	/** Beschreibung Anmelden */
	private static final String ANMELDEN_DESC = 
			"Erfragt Benutzername und Kennwort. Bei erfolgreicher " +
			"Anmeldung stehen erweiterte Funktionen zur Verfügung.";

	/** Beschriftung Abmelden */
	private static final String ABMELDEN_LABEL = "Abmelden";

	/** Beschreibung Abmelden */
	private static final String ABMELDEN_DESC = 
			"Benutzer abmelden und die erweiterten " +
			"Funktionen deaktivieren.";

	/** Meldung wenn erfolgreich abgemeldet */
	private static final String LOGOUT_SUCCESS = "Erfolgreich abgemeldet.";

	/** IP-Adresse oder Benutzername */
	private BobUserIdent userIdent = new BobUserIdent();
	
	/** die An-/Abmeldeaktion ohne Text */
	private LoginAction actionWithoutText = null;
	
	/** die An-/Abmeldeaktion mit Text */
	private LoginAction actionWithText = null;
	
	private final BtMain main;
	
	public BtLoginManager(final BtMain main) {
		this.main = main;
	}
	
	private void doLogin() {
	}
	
	/**
	 * Anmeldung zurücksetzen und erfolgreiche Abmeldung anzeigen.
	 */
	private void doLogout() {
		authenticate(null, null);
		setupActions(true);
		main.showMessage(LOGOUT_SUCCESS, JOptionPane.INFORMATION_MESSAGE);
	}

	public boolean authenticate(final String user, final char[] pass) {
		userIdent.authenticate(user, pass);
		return userIdent.isAuthorized();
	}
	
	private void setupActions(final boolean login) {
		setupAction(actionWithoutText, login);
		setupAction(actionWithText, login);
	}
	
	private void setupAction(final LoginAction a, final boolean login) {	
		if (null != a) {
			if (login) {
				a.setupLogin();
			} else {
				a.setupLogout();
			}
		}
	}
	
	public Action getActionWithoutText() {
		if (null == actionWithoutText) {
			actionWithoutText = new LoginAction(false);
		}
		return actionWithoutText;
	}
	
	public Action getActionWithText() {
		if (null == actionWithText) {
			actionWithText = new LoginAction(false);
		}
		return actionWithText;
	}
	
	/**
	 * Erfragt Benutzername/Kennwort und führt einen Anmeldeversuch durch.
	 */
	public class LoginAction extends AbstractAction {
		
		/** <code>true</code> zeigt Beschriftung */
		private final boolean labelVisible;
		
		/**
		 * Erstellt eine neue Anmeldung.
		 * @param context die Programmumgebung
		 * @param showText <code>true</code> zeigt Text
		 */
		public LoginAction(final boolean labelVisible) {
			this.labelVisible = labelVisible;
			if (userIdent.isAuthorized()) {
				setupLogout();
			} else { 
				setupLogin();
			}
		}

		public boolean isLabelVisible() {
			return labelVisible;
		}
		
		@Override
		public void actionPerformed(final ActionEvent evt) {
			if (main.isChangeMode()) {
				main.showMessage(
						IChangeable.CHANGEABLE_BLOCKER_TEXT, 
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (userIdent.isAuthorized()) {
				doLogout();
			} else {
				doLogin();
			}					
		}
		
		private void setupLogin() {
			if (labelVisible) {
				putValue(Action.NAME, ANMELDEN_LABEL);
			}
			putValue(Action.SMALL_ICON, BobIcon.LOCK_OPEN);
			putValue(Action.SHORT_DESCRIPTION, ANMELDEN_DESC);
		}
		
		private void setupLogout() {
			if (labelVisible) {
				putValue(Action.NAME, ABMELDEN_LABEL);
			}
			putValue(Action.SMALL_ICON, BobIcon.LOCK);
			putValue(Action.SHORT_DESCRIPTION, ABMELDEN_DESC);
		}
		
	}

}
