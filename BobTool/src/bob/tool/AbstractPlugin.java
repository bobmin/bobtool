package bob.tool;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.Icon;

import bob.api.IAction;
import bob.api.IPlugin;

/**
 * Zentrale Methoden für Werkzeuge.
 * 
 * @author maik.boettcher@bur-kg.de
 *
 */
public abstract class AbstractPlugin implements IPlugin {
	
	/** die Bezeichnung */
	private final String label;
	
	/** das Icon */
	private final Icon icon;
	
	/** die Beschreibung */
	private final String desc;
	
	public AbstractPlugin(final String label, final Icon icon, final String desc) {
		this.label = label;
		this.icon = icon;
		this.desc = desc;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public Icon getIcon() {
		return icon;
	}

	@Override
	public String getDesc() {
		return desc;
	}

	/**
	 * Führt keine Aktion aus (Standard).
	 */
	@Override
	public void stop() {
	}

	/**
	 * Führt keine Aktion aus (Standard).
	 */
	@Override
	public void started() {
	}

	/**
	 * Liefert ein leeres {@link LinkedHashSet}.
	 */
	@Override
	public Set<IAction> getActions() {
		final Set<IAction> x = new LinkedHashSet<>();
		return x;
	}

	/**
	 * Liefert keine Adresse zu einer Hilfeseite (Standard).
	 */
	@Override
	public String getHelpUrl() {
		return null;
	}

}
