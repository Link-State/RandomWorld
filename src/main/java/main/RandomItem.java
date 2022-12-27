package main;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
	public static final NamespacedKey KEY = new NamespacedKey(Main.PLUGIN, "randomCheck");
	public static final Material[] MATERIALS = Material.values().clone();
	public static final HashMap<InventoryType, String> INV_TYPES = new HashMap<InventoryType, String>(){
		/**
		 * 
		 */
		private static final long serialVersionUID = -3707007329761881137L;

		{
			put(InventoryType.ANVIL, "ANVIL");
			put(InventoryType.BLAST_FURNACE, "BLAST_FURNACE");
			put(InventoryType.CARTOGRAPHY, "CARTOGRAPHY");
			put(InventoryType.CRAFTING, "CRAFTING");
			put(InventoryType.FURNACE, "FURNACE");
			put(InventoryType.GRINDSTONE, "GRINDSTONE");
			put(InventoryType.LOOM, "LOOM");
			put(InventoryType.MERCHANT, "MERCHANT");
			put(InventoryType.SMITHING, "SMITHING");
			put(InventoryType.SMOKER, "SMOKER");
			put(InventoryType.STONECUTTER, "STONECUTTER");
			put(InventoryType.WORKBENCH, "WORKBENCH");
		}
	};

	@EventHandler
	public void inventoryClick(InventoryClickEvent e) {
		if (e.getSlotType().equals(InventoryType.SlotType.RESULT)) {
			if (e.getCurrentItem() != null) {
				if (!e.getCurrentItem().getType().equals(Material.AIR)) {
					ItemStack stack = e.getCurrentItem();
					ItemMeta meta = stack.getItemMeta();
					PersistentDataContainer tag = meta.getPersistentDataContainer();
					if (!tag.has(KEY, PersistentDataType.STRING)) {
						tag.set(KEY, PersistentDataType.STRING, "true");
						stack.setItemMeta(meta);
						int randIdx = (int)(Math.random() * MATERIALS.length);
						stack.setType(MATERIALS[randIdx]);
					}
				}
			}
		} else if (e.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
			InventoryType invType = e.getWhoClicked().getOpenInventory().getType();
			if (INV_TYPES.get(invType) != null) {
				int rawSlotID = -1;
				switch (INV_TYPES.get(invType)) {
					// result rawslot = 0
					case "CRAFTING" :
					case "WORKBENCH" :
						rawSlotID = 0;
						break;
					// result rawslot = 1
					case "STONECUTTER" :
						rawSlotID = 1;
						break;
					// result rawslot = 2
					case "ANVIL" :
					case "BLAST_FURNACE" :
					case "CARTOGRAPHY" :
					case "FURNACE" :
					case "GRINDSTONE" :
					case "SMITHING" :
					case "SMOKER" :
					case "MERCHANT" :
						rawSlotID = 2;
						break;
					// result rawslot = 3
					case "LOOM" :
						rawSlotID = 3;
						break;
				}
				ItemStack stack = e.getWhoClicked().getOpenInventory().getItem(rawSlotID);
				if (stack != null && e.getCursor() != null) {
					if (!stack.getType().equals(Material.AIR) && e.getCursor().getType().equals(stack.getType())) {
						ItemMeta meta = stack.getItemMeta();
						PersistentDataContainer tag = meta.getPersistentDataContainer();
						if (!tag.has(KEY, PersistentDataType.STRING)) {
							tag.set(KEY, PersistentDataType.STRING, "true");
							stack.setItemMeta(meta);
							int randIdx = (int)(Math.random() * MATERIALS.length);
							stack.setType(MATERIALS[randIdx]);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void pickupItem(EntityPickupItemEvent e) {
		ItemStack stack = e.getItem().getItemStack();
		ItemMeta meta = stack.getItemMeta();
		PersistentDataContainer tag = meta.getPersistentDataContainer();
		if (!tag.has(KEY, PersistentDataType.STRING)) {
			tag.set(KEY, PersistentDataType.STRING, "true");
			stack.setItemMeta(meta);
			int randIdx = (int)(Math.random() * MATERIALS.length);
			stack.setType(MATERIALS[randIdx]);
		}
	}
	
	@EventHandler
	public void brewItem(BrewEvent e) {
		System.out.println("양조기 추출완료");
	}
}
