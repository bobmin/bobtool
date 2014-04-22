package bob.api;

import java.awt.event.ActionEvent;

/**
 * Beschreibt eine �berpr�fung von Daten. 
 * 
 * @author maik@btmx.net
 *
 */
public interface IValidation {
	
	/**
	 * Liefert <code>true</code> wenn die Daten korrekt sind. Wird 
	 * <code>false</code> geliefert, kann mit {@link IValidation#getMessage()}
	 * ein Hinweis abgefragt werden.
	 * @param evt TODO
	 * @return <code>true</code> wenn alles korrekt
	 */
	public boolean validate(ActionEvent evt);

	/**
	 * Liefert eine Meldung zum Zustand der Daten bei der letzten Pr�fung mit
	 * {@link IValidation#validate(ActionEvent)}. Wurde keine Meldung produziert oder
	 * wurden die Daten noch nicht gepr�ft, wird <code>null</code> geliefert.
	 * @return eine Meldung oder <code>null</code>
	 */
	public String getMessage();

}
