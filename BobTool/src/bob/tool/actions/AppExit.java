package bob.tool.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import bob.core.BobIcon;
import bob.tool.BtMain;

/**
 * Aktion um Programm kontrolliert zu beenden. 
 */
public class AppExit extends AbstractAction {
	
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