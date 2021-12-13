package club.koupah.log4j.bungee.listeners;

import club.koupah.log4j.bungee.BungeePatcher.Option;
import club.koupah.log4j.bungee.ListenerPatchers;

/**
 * @author Koupah
 * @createdAt 6:32:47 am on 14 Dec 2021
 */

public class ChatPatcher extends ListenerPatchers {

	public ChatPatcher() {
		super(Option.BUNGEE_CHAT_LISTENER);
	}

}
