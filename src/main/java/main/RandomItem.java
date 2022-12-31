package main;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class RandomItem implements Listener {
	public static final NamespacedKey KEY = new NamespacedKey(Main.PLUGIN, "randomStatus");
	public static final Material[] MATERIALS = Material.values();
	public static final HashMap<InventoryType, Integer> INV_TYPES = new HashMap<InventoryType, Integer>() {
		private static final long serialVersionUID = -3707007329761881137L;

		{
//			put(InventoryType.ANVIL, "ANVIL");
//			put(InventoryType.BLAST_FURNACE, "BLAST_FURNACE");
//			put(InventoryType.CARTOGRAPHY, "CARTOGRAPHY");
//			put(InventoryType.CRAFTING, "CRAFTING");
//			put(InventoryType.FURNACE, "FURNACE");
//			put(InventoryType.GRINDSTONE, "GRINDSTONE");
//			put(InventoryType.LOOM, "LOOM");
//			put(InventoryType.MERCHANT, "MERCHANT");
//			put(InventoryType.SMITHING, "SMITHING");
//			put(InventoryType.SMOKER, "SMOKER");
//			put(InventoryType.STONECUTTER, "STONECUTTER");
//			put(InventoryType.WORKBENCH, "WORKBENCH");
			put(InventoryType.CRAFTING, 0);
			put(InventoryType.WORKBENCH, 0);
			put(InventoryType.STONECUTTER, 1);
			put(InventoryType.ANVIL, 2);
			put(InventoryType.BLAST_FURNACE, 2);
			put(InventoryType.CARTOGRAPHY, 2);
			put(InventoryType.FURNACE, 2);
			put(InventoryType.GRINDSTONE, 2);
			put(InventoryType.MERCHANT, 2);
			put(InventoryType.SMITHING, 2);
			put(InventoryType.SMOKER, 2);
			put(InventoryType.LOOM, 3);
		}
	};
	
	/**
	 * -ITEM STATUS-
	 * 0 - NULL
	 * 1 - INTEGRITY
	 * 2 - READY
	 * 3 - CHANGED
	 **/
	public int getItemStatus(ItemStack stack) {
		if (stack != null) {
			if (!stack.getType().equals(Material.AIR)) {
				ItemMeta meta = stack.getItemMeta();
				if (meta != null) {
					PersistentDataContainer tag = meta.getPersistentDataContainer();
					if (tag.has(KEY, PersistentDataType.STRING)) {
						if (tag.get(KEY, PersistentDataType.STRING).equals("ready")) {
							return 2;
						} else if (tag.get(KEY, PersistentDataType.STRING).equals("changed")) {
							return 3;
						}
					} else {
						return 1;
					}
				}
			}
		}
		return 0;
	}
	
	/**
	 * -Change Item Tag-
	 * Must be used only when the return value of getItemStatus() is not 0.
	 **/
	public void changeTag(ItemStack stack, String value) {
		ItemMeta meta = stack.getItemMeta();
		PersistentDataContainer tag = meta.getPersistentDataContainer();
		tag.set(KEY, PersistentDataType.STRING, value);
		stack.setItemMeta(meta);
	}
	
	public void prepareItem(ItemStack stack) {
		int status = getItemStatus(stack);
		if (status == 1) {
			changeTag(stack, "ready");
		}
	}
	
	public void changeRandomItem(ItemStack stack) {
		int status = getItemStatus(stack);
		if (status == 1 || status == 2) {
			changeTag(stack, "changed");
			int randIdx = (int)(Math.random() * MATERIALS.length);
			System.out.println(stack.getType() + " => " + MATERIALS[randIdx]);
			stack.setType(MATERIALS[randIdx]);
		}
	}

	@EventHandler
	public void inventoryClick(InventoryClickEvent e) {
		if (e.getSlotType().equals(InventoryType.SlotType.RESULT)) {
			// #가끔 적용 안됨 (모루, ) => 왜?
			if (e.getCursor() != null && e.getCursor().getType().equals(Material.AIR)) {
				System.out.println("try Change");
				changeRandomItem(e.getCurrentItem());
				
			} else {
				System.out.println("try ready - 1");
				prepareItem(e.getCurrentItem());
			}
		} else if (e.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
			// #가끔 적용 안됨 (훈연기, ) => 왜?
			InventoryType invType = e.getWhoClicked().getOpenInventory().getType();
			if (INV_TYPES.get(invType) != null) {
				int rawSlotID = INV_TYPES.get(invType);
				ItemStack resultStack = e.getWhoClicked().getOpenInventory().getItem(rawSlotID);
				if (e.getCursor() != null) {
					if (e.getCursor().getType().equals(resultStack.getType())) {
						System.out.println("try ready - 2");
						prepareItem(resultStack);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void pickupItem(EntityPickupItemEvent e) {
		changeRandomItem(e.getItem().getItemStack());
	}
	
	@EventHandler
	public void brewPotion(BrewEvent e) {
		e.getResults().forEach(potion -> {
			changeRandomItem(potion);
		});
	}
	
	@EventHandler
	public void enchantItem(EnchantItemEvent e) {
		// #가끔 적용 안됨 => 왜?
		changeRandomItem(e.getItem());
	}
}
