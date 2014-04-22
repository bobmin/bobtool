package bob.tool;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import bob.core.BobIcon;

public class BtMenuBar extends JMenuBar {
	
	/** Schaltfläche zum An- und Abmelden */
	private JButton jButtonLock;
	
	public BtMenuBar(final BtMain main) {
		setupLoginButton(main.getLoginManager());
		add(Box.createHorizontalStrut(1));
		setupProgramMenu(main);
	}

	private void setupLoginButton(final BtLoginManager lm) {
		final Action aaa = lm.getActionWithoutText();
		jButtonLock = new JButton(aaa);
		jButtonLock.setContentAreaFilled(false);
		jButtonLock.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		add(jButtonLock);
	}
	
	private void setupProgramMenu(final BtMain main) {
		final JMenu jMenuProgram = new JMenu("Werkzeugmenü");
		
		jMenuProgram.add(new JMenuItem(new AppExit(main)));
		
		add(jMenuProgram);
	}
	
	/**
	 * Aktion um Programm kontrolliert zu beenden. 
	 */
	public static class AppExit extends AbstractAction {
		
		private static final Object ACTION_NAME = "Programm beenden";
		
		private final BtMain main;
		
		public AppExit(final BtMain main) {
			this.main = main;
			putValue(Action.NAME, ACTION_NAME);
			putValue(Action.SMALL_ICON, BobIcon.DOOR_OPEN);
		}

		@Override
		public void actionPerformed(final ActionEvent arg0) {
			main.shutdown();
		}
		
	}
	
}
