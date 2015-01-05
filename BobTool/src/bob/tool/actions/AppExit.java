package bob.tool.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import bob.core.BobIcon;
import bob.tool.AbstractApplication;

/**
 * Aktion um Programm kontrolliert zu beenden. 
 */
public class AppExit extends AbstractAction {
	
	private static final Object ACTION_NAME = "Programm beenden";
	
	private final AbstractApplication main;
	
	public AppExit(final AbstractApplication main) {
		this.main = main;
		putValue(Action.NAME, ACTION_NAME);
		putValue(Action.SMALL_ICON, BobIcon.DOOR_OPEN);
	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {
		main.shutdown();
	}
	
}