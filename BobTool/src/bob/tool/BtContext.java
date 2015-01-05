package bob.tool;

import bob.api.IToolContext;

public class BtContext implements IToolContext {
	
	private final AbstractApplication app;
	
	public BtContext(final AbstractApplication app) {
		this.app = app;
	}

}
