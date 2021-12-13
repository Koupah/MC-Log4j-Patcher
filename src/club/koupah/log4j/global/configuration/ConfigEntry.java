package club.koupah.log4j.global.configuration;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Koupah
 * @createdAt 8:20:20 pm on 12 Dec 2021
 */

public class ConfigEntry<T> {

	public static enum ActionType {
		REPLACE, STOP;
	}

	public static Set<String> categories = new HashSet<String>();

	String category;

	String comment;
	String key;
	T value;
	Class<?> typeClass;

	public ConfigEntry(String key, T defaultValue) {
		this.key = key;
		this.value = defaultValue;
		this.typeClass = defaultValue.getClass();
		setCategory("General Settings");
	}

	public ConfigEntry(String key, T defaultValue, String comment) {
		this(key, defaultValue);
		this.comment = comment;
	}

	public T setValue(T newValue) {
		T old = value;
		this.value = newValue;
		return old;
	}

	@SuppressWarnings("unchecked")
	public boolean loadValue(String configValue) {
		try {
			if (typeClass == String.class) {
				setValue((T) configValue);
			} else {
				Method valueOf = typeClass.getDeclaredMethod("valueOf", String.class);
				setValue((T) valueOf.invoke(null, configValue));
			}

			return true;
		} catch (Exception e) {
			System.out.println("Failed to load value \"" + configValue + "\" for ConfigEntry \"" + this.key + "\"!");
			e.printStackTrace();
			return false;
		}
	}

	public T getValue() {
		return this.value;
	}

	public String getKey() {
		return this.key;
	}

	public boolean hasComment() {
		return comment != null;
	}

	public String getComment() {
		return this.comment;
	}

	public boolean asBoolean() {
		return (boolean) getValue();
	}

	public String asString() {
		return (String) getValue();
	}

	public ActionType asAction() {
		return (ActionType) getValue();
	}

	public ConfigEntry<T> setCategory(String category) {
		this.category = category;
		categories.add(category);
		return this;
	}

}
