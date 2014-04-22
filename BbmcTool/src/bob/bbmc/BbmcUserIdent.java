package bob.bbmc;

import java.util.concurrent.atomic.AtomicBoolean;

import bob.api.IUserIdent;
import bob.core.Utils;

public class BbmcUserIdent implements IUserIdent {

	private AtomicBoolean authorized = new AtomicBoolean(false);
	
	@Override
	public boolean isAuthorized() {
		return authorized.get();
	}

	@Override
	public void authenticate(final String user, final char[] pass) {
		if (Utils.isEmpty(user) || null == pass) {
			authorized.set(false);
		} else {
			authorized.set(user.equals(String.valueOf(pass)));
		}
	}

}
