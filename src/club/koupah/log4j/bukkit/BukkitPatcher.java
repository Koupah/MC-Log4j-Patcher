package club.koupah.log4j.bukkit;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import club.koupah.log4j.Patcher;
import club.koupah.log4j.PatcherPlatform;
import club.koupah.log4j.global.configuration.ConfigEntry;

/**
 * @author Koupah
 * @createdAt 7:05:08 pm on 11 Dec 2021
 */

public class BukkitPatcher extends JavaPlugin implements Patcher {

	private static BukkitPatcher instance;

	public void onEnable() {
		instance = this;
		startPatcher();
	}

	@Override
	public void patchPlatform() {
		if (Option.BUKKIT_CHECKS.get().asBoolean())
			ListenerPatchers.reloadAll();
	}

	@Override
	public PatcherPlatform getPlatform() {
		return PatcherPlatform.BUKKIT;
	}

	public static enum Option {

		BUKKIT_CHECKS(new ConfigEntry<Boolean>("bukkit_checks", true,
				"If set to false, no Bukkit/Spigot specific patches or checks (listed below) will be active. [true/false]")),
		BUKKIT_REPLACEMENT(new ConfigEntry<String>("bukkit_format_replacement", "&7&oRemoved&r",
				"This replaces the contents of any formats when bukkit_action is REPLACE. Examples: ${jndi:ldap://abc.xyz/a} -> ${Removed}, ${date:yyyy} -> ${Removed}. [Any String (Supports color codes)]")),
		BUKKIT_CHAT_LISTENER(new ConfigEntry<Boolean>("bukkit_chat_listener", true,
				"Should chat be checked for Log4j formats. If a Log4j format is detected the bukkit_action is used used. [true/false]")),
		BUKKIT_CHAT_ACTION(new ConfigEntry<ConfigEntry.ActionType>("bukkit_chat_action", ConfigEntry.ActionType.REPLACE,
				"The action that should be taken on any Log4j formats in chat messages. [REPLACE (replaces formats with value of bukkit_format_replacement), STOP (stops the message completely)]")),
		BUKKIT_MOB_LISTENER(new ConfigEntry<Boolean>("bukkit_mob_listener", true,
				"Should we prevent mobs from being spawned or renamed with Log4j formats in their name? [true/false]")),
		BUKKIT_USERNAME_LISTENER(new ConfigEntry<Boolean>("bukkit_username_listener", true,
				"Should players be kicked if their name contains Log4j formats? Useful for servers with online mode disabled, or servers under a bungee/proxy. [true/false]")),
		BUKKIT_ITEM_LISTENER(new ConfigEntry<Boolean>("bukkit_item_listener", true,
				"Should item names be checked for Log4j formats? If enabled item names will be checked for Log4j formats (If found, name will be removed). [true/false]"));

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

	@Override
	public List<ConfigEntry<?>> getConfigEntries() {
		return Option.entries();
	}

	public static Plugin getInstance() {
		return instance;
	}

}
