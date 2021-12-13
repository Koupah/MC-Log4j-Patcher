package club.koupah.log4j.bukkit.listeners;

import club.koupah.log4j.bukkit.BukkitPatcher.Option;
import club.koupah.log4j.global.utils.PUtil;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import club.koupah.log4j.bukkit.ListenerPatchers;

/**
 * @author Koupah
 * @createdAt 1:43:32 am on 14 Dec 2021
 */

public class MobNamePatcher extends ListenerPatchers {

	public MobNamePatcher() {
		super(Option.BUKKIT_MOB_LISTENER);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (PUtil.hasLog4jFormat(event.getEntity().getCustomName())) {
			event.getEntity().setCustomName(null);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEntityEvent event) {
		ItemStack used;
		if ((used = event.getPlayer().getItemInHand()) != null) {
			ItemMeta meta;
			if (used.hasItemMeta() && (meta = used.getItemMeta()).hasDisplayName()
					&& PUtil.hasLog4jFormat(meta.getDisplayName())) {
				event.setCancelled(true);
				meta.setDisplayName(null);
				used.setItemMeta(meta);
			}
		}
	}

}
