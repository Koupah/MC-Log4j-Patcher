package club.koupah.log4j.bungee;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import club.koupah.log4j.Patcher;
import club.koupah.log4j.PatcherPlatform;
import club.koupah.log4j.global.configuration.ConfigEntry;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * @author Koupah
 * @createdAt 6:08:32 am on 14 Dec 2021
 */

public class BungeePatcher extends Plugin implements Patcher {

	private static BungeePatcher instance;

	@Override
	public void onEnable() {
		instance = this;
		startPatcher();
	}

	@Override
	public List<ConfigEntry<?>> getConfigEntries() {
		return Option.entries();
	}

	@Override
	public PatcherPlatform getPlatform() {
		return PatcherPlatform.BUNGEE;
	}

	@Override
	public void patchPlatform() {
		if (Option.BUNGEE_CHECKS.get().asBoolean())
			ListenerPatchers.reloadAll();
	}

	public static BungeePatcher getInstance() {
		return instance;
	}

	public static enum Option {
		BUNGEE_CHECKS(new ConfigEntry<Boolean>("bungee_checks", true,
				"If set to false, no Bungee specific patches or checks (listed below) will be active. [true/false]")),
		BUNGEE_REPLACEMENT(new ConfigEntry<String>("bungee_format_replacement", "&7&oRemoved&r",
				"This replaces the contents of any formats when bungee_chat_action is REPLACE. Examples: ${jndi:ldap://abc.xyz/a} -> ${Removed}, ${date:yyyy} -> ${Removed}. [Any String (Supports color codes)]")),
		BUNGEE_CHAT_LISTENER(new ConfigEntry<Boolean>("bungee_chat_listener", true,
				"Should chat be checked for Log4j formats? If a Log4j format is detected the bungee_action is used. This *should* protect all servers under the Bungee from Log4j formats in chat messages. [true/false]")),
		BUNGEE_CHAT_ACTION(new ConfigEntry<ConfigEntry.ActionType>("bungee_chat_action", ConfigEntry.ActionType.REPLACE,
				"The action that should be taken on any Log4j formats in chat messages. [REPLACE (replaces formats with value of bungee_format_replacement), STOP (stops the message completely)]")),
		BUNGEE_USERNAME_LISTENER(new ConfigEntry<Boolean>("bungee_username_listener", true,
				"Should players be kicked if their name contains Log4j formats? Useful for bungee servers with online mode disabled. [true/false]"));

		ConfigEntry<?> value;

		Option(ConfigEntry<?> configEntry) {
			this.value = configEntry;
		}

		public ConfigEntry<?> get() {
			return value;
		}

		static List<ConfigEntry<?>> entries() {
			return Arrays.asList(values()).stream().map(option -> option.get()).collect(Collectors.toList());
		}

	}

}
