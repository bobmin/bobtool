package bob.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

public final class Services {
	
	private Services() {
	}

	public static <T> T locate(final Class<T> clazz) {
		final List services = locateAll(clazz);
		return (T) (services.isEmpty() ? (T) null : services.get(0));
	}

	public static <T> List<T> locateAll(final Class<T> clazz) {
		final Iterator<T> iterator = ServiceLoader.load(clazz).iterator();
		final List<T> services = new ArrayList<T>();
		while (iterator.hasNext()) {
			try {
				services.add(iterator.next());
			} catch (Error e) {
				e.printStackTrace(System.err);
			}
		}
		return services;
	}
}
