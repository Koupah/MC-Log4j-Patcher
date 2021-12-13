package club.koupah.log4j;

import java.util.List;

import club.koupah.log4j.global.configuration.Config;
import club.koupah.log4j.global.configuration.ConfigEntry;
import club.koupah.log4j.global.log4j.Log4JFilter;
import club.koupah.log4j.global.utils.PUtil;

/**
 * @author Koupah
 * @createdAt 7:04:02 pm on 11 Dec 2021
 */

public interface Patcher {

	public static final String name = "Log4JPatcher";
	public static final String version = "0.0.1";
	public static final String identifier = name + " [" + version + "]";

	public abstract List<ConfigEntry<?>> getConfigEntries();

	public abstract PatcherPlatform getPlatform();

	public default void startPatcher() {
		String patcherName = getPlatform().getName();
		PUtil.log("Log4j " + patcherName + " Patcher v" + version + " is starting...");
		PUtil.log("");

		if (isAlreadyPatched()) {

		}

		PUtil.log("Initializing Patcher config...");
		Config.addConfigSettings(getPlatform().getName(), getConfigEntries());
		createConfig();
		PUtil.log("");

		PUtil.log("Attempting to patch all Log4J formats (including jndi) from Logger.");
		patchLogger();
		PUtil.log("");

		PUtil.log("Performing other changes, you can safely ignore these if they fail.");
		PUtil.log(" - These don't really help, but may as well run them");
		patchJndi();
		PUtil.log("");

		PUtil.log("Generic Patches complete.");
		PUtil.log("");

		PUtil.log("Running Platform specific Patches...");
		patchPlatform();
		PUtil.log("Completed Platform Patches.");
		enabled("Enabled Log4j " + patcherName + " Patcher v" + version + ".");
	}

	public default void patchLogger() {
		PUtil.log("Adding Log4J Log Filter. [Success: " + Log4JFilter.addFilter() + "]");
	}

	public default void patchJndi() {
		PUtil.log("Disabling rmi object URL trust. [Success: " + disableRmiURLTrust() + "]");
		PUtil.log("Disabling cosnaming object URL trust. [Success: " + disableCosnamingURLTrust() + "]");

		PUtil.log("Setting formatMsgNoLookups system property to \"true\". [Success: " + formatMsgNoLookupsProperty()
				+ "]");
		PUtil.log("Setting FORMAT_MSG_NO_LOOKUPS environment variable to \"true\". [Success: "
				+ formatMsgNoLookupsEnvironment() + "]");

		PUtil.log("Setting Log4J2's Constants value for FORMAT_MESSAGES_PATTERN_DISABLE_LOOKUPS to \"true\". [Success: "
				+ PUtil.setField("org.apache.logging.log4j.core.util.Constants",
						"FORMAT_MESSAGES_PATTERN_DISABLE_LOOKUPS", true)
				+ "]");
	}

	public void patchPlatform();

	public default Config createConfig() {
		return Config.create();
	}

	public default void enabled(String patcherName) {
		PUtil.log("Log4j " + getPlatform().getName() + " Patcher has been enabled.");
	}

	// https://logging.apache.org/log4j/2.x/security.html
	public static boolean formatMsgNoLookupsProperty() {
		return setProperty("log4j2.formatMsgNoLookups", "true");
	}

	// https://logging.apache.org/log4j/2.x/security.html
	public static boolean formatMsgNoLookupsEnvironment() {
		String response;
		return PUtil.setEnv("LOG4J_FORMAT_MSG_NO_LOOKUPS", "true")
				&& (response = System.getenv("LOG4J_FORMAT_MSG_NO_LOOKUPS")) != null && response.equals("true");
	}

	// https://www.oracle.com/java/technologies/javase/8u121-relnotes.html
	public static boolean disableRmiURLTrust() {
		return setProperty("com.sun.jndi.rmi.object.trustURLCodebase", "false");
	}

	// https://www.oracle.com/java/technologies/javase/8u121-relnotes.html
	public static boolean disableCosnamingURLTrust() {
		return setProperty("com.sun.jndi.cosnaming.object.trustURLCodebase", "false");
	}

	public static boolean isAlreadyPatched() {
		return getProperty(identifier) != null;
	}

	public static String getProperty(String key) {
		return System.getProperty(key);
	}

	public static boolean setProperty(String key, String value) {
		System.setProperty(key, value);

		final String current = System.getProperty(key);
		return current != null && current.equals(value);
	}

}
