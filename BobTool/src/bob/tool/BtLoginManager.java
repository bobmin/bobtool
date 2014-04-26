package bob.tool;


import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import bob.api.IUserIdent;
import bob.core.BobException;
import bob.core.Services;
import bob.tool.actions.LoginAction;

/**
 * Organisiert die An- und Abmeldung eines Benutzer vom Programm.
 * 
 * @author maik@btmx.net
 *
 */
public class BtLoginManager {
	
	/** ein Logger */
	private static final Logger LOG = 
			Logger.getLogger(BtLoginManager.class.getName());

	/** Meldung wenn erfolgreich abgemeldet */
	private static final String LOGOUT_SUCCESS = "Erfolgreich abgemeldet.";

	/** IP-Adresse oder Benutzername */
	private final IUserIdent userIdent;
	
	/** die An-/Abmeldeaktion ohne Text */
	private LoginAction actionWithoutText = null;
	
	/** die An-/Abmeldeaktion mit Text */
	private LoginAction actionWithText = null;
	
	final BtMain main;
	
	public BtLoginManager(final BtMain main) throws BobException {
		this.main = main;
		// IUserIdent instanziieren
		final List<IUserIdent> list = Services.locateAll(IUserIdent.class);
		if (null == list || 0 == list.size()) {
			throw new BobException.SettingsUnreachabl(IUserIdent.class);
		} else {
			this.userIdent = list.get(0);
		}
	}
	
	public IUserIdent getUserIdent() {
		return userIdent;
	}
	
	public void doLogin() {
		final DefaultLoginPanel panel = new DefaultLoginPanel();
		final String[] options = new String[]{"Anmelden", "Abbrechen"};
		final ActionListener listener = new ActionListener() {
			
			@Override
			public void actionPerformed(final ActionEvent evt) {
				final String user = panel.getUser();
				final char[] pass = panel.getPass();
				userIdent.authenticate(user, pass);
				final boolean authorized = userIdent.isAuthorized();
				setupActions(!authorized);
				LOG.info("login performed, user = " + user 
						+ ", authorized = " + authorized);
			}
		};
		main.showDialog(panel, "Benutzer anmelden", 
				JOptionPane.QUESTION_MESSAGE, options, listener, null);
	}
	
	/**
	 * Anmeldung zurücksetzen und erfolgreiche Abmeldung anzeigen.
	 */
	public void doLogout() {
		authenticate(null, null);
		setupActions(true);
		main.showMessage(LOGOUT_SUCCESS, JOptionPane.INFORMATION_MESSAGE);
	}

	public boolean authenticate(final String user, final char[] pass) {
		userIdent.authenticate(user, pass);
		return userIdent.isAuthorized();
	}
	
	/**
	 * Konfiguriert Text und Icon von den Login-Schaltflächen.
	 * @param login <code>true</code> besagt, der Benutzer kann sich anmelden
	 */
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
			actionWithoutText = new LoginAction(main, false);
		}
		return actionWithoutText;
	}
	
	public Action getActionWithText() {
		if (null == actionWithText) {
			actionWithText = new LoginAction(main, false);
		}
		return actionWithText;
	}
	
	/**
	 * Einfaches {@link JPanel} zur Eingabe von Benutzername und Kennwort.
	 * 
	 * @author maik@btmx.net
	 *
	 */
	public static class DefaultLoginPanel extends JPanel {
		
		private final JTextField user;
		
		private final JPasswordField pass;
		
		public DefaultLoginPanel() {
			setLayout(new GridLayout(2, 2, 5, 5));
			add(new JLabel("Benutzername:", SwingConstants.TRAILING));
			user = new JTextField();
			add(user);
			add(new JLabel("Kennwort:", SwingConstants.TRAILING));
			pass = new JPasswordField();
			add(pass);
		}

		public String getUser() {
			return user.getText();
		}
		
		public char[] getPass() {
			return pass.getPassword();
		}

	}

}
