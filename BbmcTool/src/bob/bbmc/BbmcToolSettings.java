package bob.bbmc;

import bob.api.IToolSettings;

public class BbmcToolSettings implements IToolSettings {

	@Override
	public String getToolTitle() {
		return "BbmcTool v0.9";
	}

	@Override
	public String getDefaultPlugin() {
		// TODO Standardwerkzeug einbinden
		return null;
	}

	@Override
	public String getProgramIcon() {
		return "/resources/gnome-control-center32.png";
	}

}
