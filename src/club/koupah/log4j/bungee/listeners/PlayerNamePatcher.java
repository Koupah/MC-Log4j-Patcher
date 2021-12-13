package club.koupah.log4j.bungee.listeners;

import club.koupah.log4j.bungee.BungeePatcher.Option;
import club.koupah.log4j.bungee.ListenerPatchers;
import club.koupah.log4j.global.utils.Loggable;
import club.koupah.log4j.global.utils.PUtil;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 * @author Koupah
 * @createdAt 6:32:53 am on 14 Dec 2021
 */

public class PlayerNamePatcher extends ListenerPatchers implements Loggable {

	private static final String replacement = "Removed Log4J Format";

	public PlayerNamePatcher() {
		super(Option.BUNGEE_USERNAME_LISTENER);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPreLogin(PreLoginEvent event) {
		final PendingConnection connection = event.getConnection();
		final String name = connection.getName();

		if (PUtil.hasLog4jFormat(name)) {
			// Try to remove the name before it can possibly be logged by any plugins
			PUtil.setField(connection.getClass(), "name", connection, replacement);

			event.setCancelReason(TextComponent.fromLegacyText("Disallowed Username"));
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPreLoginLast(PreLoginEvent event) {
		if (!event.isCancelled()) {
			final String name = event.getConnection().getName();

			if (name.equals(replacement) || PUtil.hasLog4jFormat(name)) {
				event.setCancelReason(TextComponent.fromLegacyText("Disallowed Username"));
				event.setCancelled(true);

				error("For some reason a player with a disallowed username was allowed to join. They've been disallowed again.");
			}
		}
	}

}
