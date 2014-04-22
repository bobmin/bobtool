package bob.tool.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import bob.api.IChangeable;
import bob.core.BobIcon;
import bob.tool.BtMain;

/**
 * Erfragt Benutzername und Kennwort und f�hrt einen Anmeldeversuch durch. Ist 
 * der Benutzer angemeldet, wird er abgemeldet.
 * 
 * @author maik@btmx.net
 * 
 */
public class LoginAction extends AbstractAction {
	
	/** Beschriftung Anmelden */
	static final String ANMELDEN_LABEL = "Anmelden";

	/** Beschreibung Anmelden */
	static final String ANMELDEN_DESC = 
			"Erfragt Benutzername und Kennwort. Bei erfolgreicher " +
			"Anmeldung stehen erweiterte Funktionen zur Verf�gung.";

	/** Beschriftung Abmelden */
	static final String ABMELDEN_LABEL = "Abmelden";

	/** Beschreibung Abmelden */
	static final String ABMELDEN_DESC = 
			"Benutzer abmelden und die erweiterten " +
			"Funktionen deaktivieren.";

	/** das Programm */
	private final BtMain main;
	
	/** <code>true</code> zeigt Beschriftung */
	private final boolean labelVisible;
	
	/**
	 * Erstellt eine neue Anmeldung.
	 * @param btLoginManager TODO
	 * @param context die Programmumgebung
	 * @param showText <code>true</code> zeigt Text
	 */
	public LoginAction(final BtMain main, final boolean labelVisible) {
		this.main = main;
		this.labelVisible = labelVisible;
		if (main.isAuthorized()) {
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
		main.doAuth();
	}
	
	public void setupLogin() {
		if (labelVisible) {
			putValue(Action.NAME, ANMELDEN_LABEL);
		}
		putValue(Action.SMALL_ICON, BobIcon.LOCK_OPEN);
		putValue(Action.SHORT_DESCRIPTION, ANMELDEN_DESC);
	}
	
	public void setupLogout() {
		if (labelVisible) {
			putValue(Action.NAME, ABMELDEN_LABEL);
		}
		putValue(Action.SMALL_ICON, BobIcon.LOCK);
		putValue(Action.SHORT_DESCRIPTION, ABMELDEN_DESC);
	}
	
}