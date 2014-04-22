package bob.demo;

import java.util.concurrent.atomic.AtomicBoolean;

import bob.api.IUserIdent;

public class DemoUserIdent implements IUserIdent {

	private final AtomicBoolean authorized = new AtomicBoolean(false);
	
	@Override
	public boolean isAuthorized() {
		return authorized.get();
	}

	@Override
	public void authenticate(final String user, final char[] pass) {
		synchronized (authorized) {
			final boolean state = authorized.get();
			authorized.set(!state);
		}
	}

}
