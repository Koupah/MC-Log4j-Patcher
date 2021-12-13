package club.koupah.log4j.global.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.naming.Context;

/**
 * All credit for <a href=
 * "https://github.com/0-x-2-2/CVE-2021-44228/tree/b07f4016836efa1f5bbd133878572c7043bee767">this</a>
 * belongs to <a href="https://github.com/0-x-2-2">0x22</a>
 * 
 * @author 0x22
 */

public class JndiContextPatch {
	/*
	 * All credit for this belongs to 0x22 (https://github.com/0-x-2-2).
	 * 
	 * You can see his repository here: https://github.com/0-x-2-2/CVE-2021-44228
	 * 
	 * The Spigot I use doesn't seem to have the class files that this patch uses,
	 * and I was using my Spigot as grounds for testing because I CBF making my game
	 * vulnerable, however it is still worth including it incase some peoples MC
	 * version or servers jars have it.
	 */

	public static boolean execute() {
		final ClassLoader ourClassLoader = JndiContextPatch.class.getClassLoader();

		final Set<ClassLoader> classLoaders = new HashSet<>();

		boolean fixed = false;

		classLoaders.add(ClassLoader.getSystemClassLoader());
		classLoaders.add(Thread.currentThread().getContextClassLoader());
		classLoaders.add(ourClassLoader);
		for (final ClassLoader classLoader : classLoaders) {
			if (classLoader == null)
				continue;

			Class<?> abstractManager = PUtil.classLoaderClassLoad(classLoader,
					"org.apache.logging.log4j.core.appender.AbstractManager");

			Class<?> jndiManagerClass = PUtil.classLoaderClassLoad(classLoader,
					"org.apache.logging.log4j.core.net.JndiManager");

			fixed = initializeJndiManager(jndiManagerClass);

			if (jndiManagerClass != null && abstractManager != null) {
				// Patch all instances of the JNDI manager with our custom context.
				final Field[] fields = abstractManager.getDeclaredFields();
				for (final Field field : fields) {
					try {
						if (Modifier.isStatic(field.getModifiers()) && Map.class.isAssignableFrom(field.getType())) {
							stripFinal(field).setAccessible(true);
							final Map<?, ?> map = (Map<?, ?>) field.get(null);
							if (map == null)
								continue;

							for (final Object value : map.values())
								if ((jndiManagerClass.isAssignableFrom(value.getClass())))
									if (patchJndiContext(value))
										fixed = true;

							map.clear();
						}
					} catch (Throwable t) {
					}
				}
			}
		}

		return fixed;
	}

	private static boolean initializeJndiManager(Class<?> jndiManagerClass) {
		boolean success = false;
		try {
			// Initialize the JNDI manager for classloader and patch the context.
			final Method getDefaultManager = jndiManagerClass.getDeclaredMethod("getDefaultManager");
			final Object defaultManagerInstance = getDefaultManager.invoke(null);
			if (defaultManagerInstance != null && patchJndiContext(defaultManagerInstance)) {
				success = true;
			}

			// Alternative hacky fallback patch (we are unable to use the hook method due to
			// classloading issues)
			try {
				final Field field = jndiManagerClass.getDeclaredField("FACTORY");
				stripFinal(field).setAccessible(true);
				field.set(null, null);
			} catch (final Throwable t) {
			}
		} catch (final Throwable t) {
		}

		return success;
	}

	/**
	 * Patch the JNDI context to use our custom context.
	 * 
	 * @return true if the context was patched, false otherwise.
	 */
	private static boolean patchJndiContext(final Object jndiManager) throws ReflectiveOperationException {
		boolean fixed = false;
		Class<?> currClass = jndiManager.getClass();
		while (currClass != null) {
			final Field[] fields = currClass.getDeclaredFields();
			for (final Field field : fields) {
				if (Context.class.isAssignableFrom(field.getType())) {
					stripFinal(field).setAccessible(true);
					field.set(jndiManager, null);
					fixed = true;
				}
			}
			currClass = currClass.getSuperclass();
		}
		return fixed;
	}

	/**
	 * Strip the final modifier from a field.
	 * 
	 * @param field the field to strip.
	 * @return the field with the final modifier stripped.
	 */
	private static Field stripFinal(final Field field) throws ReflectiveOperationException {
		final Field modifiersField = Field.class.getDeclaredField("modifiers");
		final boolean modifiersAccessible = modifiersField.isAccessible();
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		modifiersField.setAccessible(modifiersAccessible);
		return field;
	}

}
