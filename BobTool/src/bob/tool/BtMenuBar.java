package bob.tool;


import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import bob.tool.actions.AppExit;


/**
 * Das Hauptmenü vom Programmfenster.
 * 
 * @author maik@btmx.net
 *
 */
public class BtMenuBar extends JMenuBar {
	
	/** Beschriftung vom Werkzeugmenü */
	private static final String TOOL_MENU_LABEL = "Werkzeugmenü";
	
	/** Schaltfläche zum An- und Abmelden */
	private JButton jButtonLock;
	
	public BtMenuBar(final BtMain main) {
		setupLoginButton(main.getLoginManager());
		add(Box.createHorizontalStrut(1));
		setupToolsMenu(main);
	}

	private void setupLoginButton(final BtLoginManager lm) {
		final Action aaa = lm.getActionWithoutText();
		jButtonLock = new JButton(aaa);
		jButtonLock.setContentAreaFilled(false);
		jButtonLock.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		add(jButtonLock);
	}
	
	private void setupToolsMenu(final BtMain main) {
		final JMenu jMenuProgram = new JMenu(TOOL_MENU_LABEL);
		jMenuProgram.add(new JMenuItem(new AppExit(main)));
		add(jMenuProgram);
	}
	
}
