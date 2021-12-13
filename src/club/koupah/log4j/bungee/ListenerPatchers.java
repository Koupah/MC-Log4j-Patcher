package club.koupah.log4j.bungee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import club.koupah.log4j.bungee.BungeePatcher.Option;
import club.koupah.log4j.bungee.listeners.ChatPatcher;
import club.koupah.log4j.bungee.listeners.PlayerNamePatcher;
import net.md_5.bungee.api.plugin.Listener;

/**
 * @author Koupah
 * @createdAt 6:30:50 am on 14 Dec 2021
 */

public class ListenerPatchers implements Listener {
	public static List<ListenerPatchers> patches = new ArrayList<ListenerPatchers>(
			Arrays.asList(new ChatPatcher(), new PlayerNamePatcher()));

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

		BungeePatcher.getInstance().getProxy().getPluginManager().registerListener(BungeePatcher.getInstance(), this);
	}

	public void disable() {
		if (!this.enabled)
			return;

		this.enabled = false;
		BungeePatcher.getInstance().getProxy().getPluginManager().unregisterListener(this);
	}

	public static void reloadAll() {
		for (ListenerPatchers patch : patches) {
			patch.disable();
			patch.enable();
		}
	}
}
