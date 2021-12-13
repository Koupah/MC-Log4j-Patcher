package club.koupah.log4j.bukkit.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import club.koupah.log4j.bukkit.BukkitPatcher.Option;
import club.koupah.log4j.bukkit.ListenerPatchers;
import club.koupah.log4j.global.configuration.ConfigEntry.ActionType;
import club.koupah.log4j.global.utils.PUtil;

/**
 * @author Koupah
 * @createdAt 1:55:41 am on 14 Dec 2021
 */

public class ChatPatcher extends ListenerPatchers {
	public ChatPatcher() {
		super(Option.BUKKIT_CHAT_LISTENER);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent event) {
		if (PUtil.hasLog4jFormat(event.getMessage())) {
			if (Option.BUKKIT_CHAT_ACTION.get().asAction() == ActionType.REPLACE) {
				event.setMessage(PUtil.replaceFormats(event.getMessage(),
						ChatColor.translateAlternateColorCodes('&', Option.BUKKIT_REPLACEMENT.get().asString())));
			} else {
				// should still nullify message so it cannot be logged accidentally
				event.setMessage(null);

				event.setCancelled(true);
			}
		}
	}

}
