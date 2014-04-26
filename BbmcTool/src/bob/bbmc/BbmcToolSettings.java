package bob.bbmc;

import bob.api.IToolSettings;

public class BbmcToolSettings implements IToolSettings {

	/** eindeutiger Programmtitel */
	private static final String TOOL_TITLE = "BbmcTool v0.9";
	
	@Override
	public String getToolTitle() {
		return TOOL_TITLE;
	}

}
