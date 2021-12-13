package club.koupah.log4j.bukkit.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import club.koupah.log4j.bukkit.BukkitPatcher.Option;
import club.koupah.log4j.bukkit.ListenerPatchers;
import club.koupah.log4j.global.utils.Loggable;
import club.koupah.log4j.global.utils.PUtil;

/**
 * @author Koupah
 * @createdAt 3:29:17 am on 14 Dec 2021
 */

public class ItemNamePatcher extends ListenerPatchers implements Loggable {

	public ItemNamePatcher() {
		super(Option.BUKKIT_ITEM_LISTENER);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onItemAnvilRename(InventoryClickEvent event) {
		if (event.getInventory().getType() == InventoryType.ANVIL && event.getSlotType() == SlotType.RESULT) {
			final String name = getItemName(event.getCurrentItem());
			if (name != null && PUtil.hasLog4jFormat(name)) {
				event.setCurrentItem(removeItemName(event.getCurrentItem()));
			}
		}
	}

	/*
	 * This is because I know some plugins let people show their items in chat, so
	 * lets check their item!
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent event) {
		final String name = getItemName(event.getPlayer().getItemInHand());
		if (name != null && PUtil.hasLog4jFormat(name))
			event.getPlayer().setItemInHand(removeItemName(event.getPlayer().getItemInHand()));
	}

	/*
	 * To prevent any death/damage messages having a no no name
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onDamageEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			final Player damager = (Player) event.getDamager();

			final String name = getItemName(damager.getItemInHand());
			if (name != null && PUtil.hasLog4jFormat(name))
				damager.setItemInHand(removeItemName(damager.getItemInHand()));
		}
	}

	public String getItemName(ItemStack is) {
		return is != null && is.getType() != Material.AIR && is.hasItemMeta() ? is.getItemMeta().getDisplayName()
				: null;
	}

	public ItemStack removeItemName(ItemStack is) {
		ItemMeta meta = is.getItemMeta();
		meta.setDisplayName(null);
		is.setItemMeta(meta);
		return is;
	}

}
