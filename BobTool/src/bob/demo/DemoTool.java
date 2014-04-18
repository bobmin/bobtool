package bob.demo;

import bob.tool.Main;

public class DemoTool extends Main {

	/** eindeutiger Programmname */
	public static final String TITLE = "BobTool v0.1";
	
	public DemoTool() {
		super(TITLE);
	}
	
	public static void main(final String[] args) {
		startApp(TITLE);
	}
	
}
