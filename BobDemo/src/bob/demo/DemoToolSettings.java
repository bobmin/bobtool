package bob.demo;

import bob.api.IToolSettings;

public class DemoToolSettings implements IToolSettings {

	/** eindeutiger Programmname */
	public static final String TOOL_TITLE = "BobTool v0.1";
	
	@Override
	public String getToolTitle() {
		return TOOL_TITLE;
	}

}
