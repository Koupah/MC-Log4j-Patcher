package club.koupah.log4j.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import club.koupah.log4j.bukkit.BukkitPatcher.Option;
import club.koupah.log4j.bukkit.ListenerPatchers;
import club.koupah.log4j.global.utils.Loggable;
import club.koupah.log4j.global.utils.PUtil;

/**
 * @author Koupah
 * @createdAt 3:29:17 am on 14 Dec 2021
 */

public class PlayerNamePatcher extends ListenerPatchers implements Loggable {

	private static final String replacement = "Removed Log4J Format";

	public PlayerNamePatcher() {
		super(Option.BUKKIT_USERNAME_LISTENER);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
		if (PUtil.hasLog4jFormat(event.getName())) {
			// Try to remove the name before it can possibly be logged by any plugins
			PUtil.setField(event.getClass(), "name", event, replacement);

			event.disallow(Result.KICK_OTHER, "Disallowed Username.");
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLoginFinal(AsyncPlayerPreLoginEvent event) {
		if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED && (event.getName() == null
				|| replacement.equals(event.getName()) || PUtil.hasLog4jFormat(event.getName()))) {

			event.disallow(Result.KICK_OTHER, "Disallowed Username.");
			error("For some reason a player with a disallowed username was allowed to join. They've been disallowed again.");
		}
	}

}
