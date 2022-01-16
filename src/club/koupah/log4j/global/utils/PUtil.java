package club.koupah.log4j.global.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import club.koupah.log4j.Patcher;

/**
 * Patcher Util (PUtil)
 * 
 * @author Koupah
 * @createdAt 8:05:36 pm on 12 Dec 2021
 */

public final class PUtil {

	private static final String prefix = String.format(Loggable.prefixFormat, Patcher.name);

	/*
	 * This is the old way I used to detect the Log4j format/lookup thingos. The
	 * reason I stopped using it was because it ended up being flawed and it was too
	 * complex to make it detect things such as the validity of what it was
	 * checking. Example: ${date:y} is valid, ${hostName} is valid. Now if we
	 * introduce spaces, ${date: y } is valid and ${hostName } is invalid. I am not
	 * smart enough to make a regex to determine validity.
	 */
//	public static final Pattern log4jFormat = Pattern.compile("((?<=\\$\\{)(?! )(.[^ \\s]*\\[?)(?=(?<! )\\}))");
//
//	public static boolean hasLog4jFormat(String input) {
//		return input == null ? false : getLog4jMatcher(input).find();
//	}
//
//	public static Matcher getLog4jMatcher(String input) {
//		return log4jFormat.matcher(input);
//	}
//
//	public static String cleanMessage(String message, String replacement) {
//		return getLog4jMatcher(message).replaceAll(replacement);
//	}

	/*
	 * New way of checking!
	 */
	public static boolean hasLog4jFormat(String text) {
		return text == null ? false : Formatter.hasFormat(text);
	}

	public static String replaceFormats(String format, String replacement) {
		return new Formatter(format).replaceFormats(replacement).getContent();
	}

	public static void log(String log) {
		System.out.println(prefix + log);
	}

	public static boolean setField(String className, String fieldName, Object newValue) {
		try {
			Class<?> clazz = Class.forName(className);
			return setField(clazz, fieldName, null, newValue);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean setField(Class<?> clazz, String fieldName, Object instance, Object newValue) {
		try {
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);

			try {
				Field modifiers = Field.class.getDeclaredField("modifiers");
				modifiers.setAccessible(true);
				modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			} catch (Exception e) {
			}

			field.set(instance, newValue);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	public static boolean setEnv(String key, String value) {
		try {
			Map<String, String> env = System.getenv();
			Class<?> cl = env.getClass();
			Field field = cl.getDeclaredField("m");
			field.setAccessible(true);
			Map<String, String> writableEnv = (Map<String, String>) field.get(env);
			writableEnv.put(key, value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Used in {@link JndiContextPatch JndiContextPatch}
	 */
	public static Class<?> classLoaderClassLoad(ClassLoader cl, String className) {
		Class<?> clazz;
		try {
			clazz = cl.loadClass(className);
		} catch (Throwable t) {
			try {
				clazz = Class.forName(className, false, cl);
			} catch (Throwable t1) {
				clazz = null;
			}
		}

		return clazz;
	}

}
