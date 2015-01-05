package bob.core;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Helfer für {@link ServiceLoader}.
 * 
 * @author maik.boettcher@bur-kg.de
 *
 */
public final class ServiceUtils {
	
	/** ein Logger */
	private static final Logger LOG = 
			Logger.getLogger(ServiceUtils.class.getName());
	
	/**
	 * Geschützter Konstruktor. Statische Methoden sollen benutzt werden.
	 */
	private ServiceUtils() {
	}

	/**
	 * Liefert den ersten Eintrag zum Typ.
	 * @param clazz gesuchter Typ
	 * @return ein Objekt oder <code>null</code>
	 */
	public static <T> T locate(final Class<T> clazz) {
		final Set<T> services = locateAll(clazz);
		return services.isEmpty() ? (T) null : services.iterator().next();
	}

	/**
	 * Liefert alle Einträge zum Typ.
	 * @param clazz gesuchter Typ
	 * @return Objekte oder <code>null</code>
	 */
	public static <T> Set<T> locateAll(final Class<T> clazz) {
		final Iterator<T> iterator = ServiceLoader.load(clazz).iterator();
		final Set<T> services = new LinkedHashSet<T>();
		while (iterator.hasNext()) {
			services.add(iterator.next());
		}
		return services;
	}
}
