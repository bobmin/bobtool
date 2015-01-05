package bob.api;

import javax.swing.Action;

public interface IAction {
	
	/** Aktion aktiv bei ausgeschaltetem Bearbeitungsmodus (Standard) */
	int EDIT_MODE_OFF = 1;
	
	/** Aktion aktiv wenn Bearbetungsmodus eingeschaltet ist */
	int EDIT_MODE_ON = 2;
	
	/** Aktion immer aktiv */
	int EDIT_MODE_BOTH = 4;

	int getVisibility();

	Action getAction();

}
