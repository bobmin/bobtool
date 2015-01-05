package bob.tool.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import bob.api.IChangeable;
import bob.core.BobIcon;
import bob.tool.AbstractApplication;

/**
 * Erfragt Benutzername und Kennwort und führt einen Anmeldeversuch durch. Ist 
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
			"Anmeldung stehen erweiterte Funktionen zur Verfügung.";

	/** Beschriftung Abmelden */
	static final String ABMELDEN_LABEL = "Abmelden";

	/** Beschreibung Abmelden */
	static final String ABMELDEN_DESC = 
			"Benutzer abmelden und die erweiterten " +
			"Funktionen deaktivieren.";

	/** das Programm */
	private final AbstractApplication app;
	
	/** <code>true</code> zeigt Beschriftung */
	private final boolean labelVisible;
	
	/**
	 * Erstellt eine neue Anmeldung.
	 * @param btLoginManager TODO
	 * @param context die Programmumgebung
	 * @param showText <code>true</code> zeigt Text
	 */
	public LoginAction(final AbstractApplication app, final boolean labelVisible) {
		this.app = app;
		this.labelVisible = labelVisible;
		if (app.getLoginManager().getUserIdent().isAuthorized()) {
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
		if (app.isChangeMode()) {
			app.showMessage(
					IChangeable.CHANGEABLE_BLOCKER_TEXT, 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		app.getLoginManager().doAuth();
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