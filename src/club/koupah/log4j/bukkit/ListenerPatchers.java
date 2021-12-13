package club.koupah.log4j.bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import club.koupah.log4j.bukkit.BukkitPatcher.Option;
import club.koupah.log4j.bukkit.listeners.ChatPatcher;
import club.koupah.log4j.bukkit.listeners.MobNamePatcher;
import club.koupah.log4j.bukkit.listeners.PlayerNamePatcher;

/**
 * @author Koupah
 * @createdAt 1:44:02 am on 14 Dec 2021
 */

public class ListenerPatchers implements Listener {

	public static List<ListenerPatchers> patches = new ArrayList<ListenerPatchers>(
			Arrays.asList(new ChatPatcher(), new MobNamePatcher(), new PlayerNamePatcher()));

	Option option;
	boolean enabled;

	public ListenerPatchers(Option option) {
		this.option = option;
		this.enabled = false;
	}

	public void enable() {
		if (this.enabled || !option.get().asBoolean())
			return;

		this.enabled = true;
		Bukkit.getPluginManager().registerEvents(this, BukkitPatcher.getInstance());
	}

	public void disable() {
		if (!this.enabled)
			return;

		this.enabled = false;
		HandlerList.unregisterAll(this);
	}

	public static void reloadAll() {
		for (ListenerPatchers patch : patches) {
			patch.disable();
			patch.enable();
		}
	}

}
