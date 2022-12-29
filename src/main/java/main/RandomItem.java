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
	public static final NamespacedKey KEY = new NamespacedKey(Main.PLUGIN, "randomCheck");
	public static final Material[] MATERIALS = Material.values().clone();
	public static final HashMap<InventoryType, String> INV_TYPES = new HashMap<InventoryType, String>(){
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
	
	public void changeRandomItem(ItemStack stack) {
		if (stack != null) {
			if (!stack.getType().equals(Material.AIR)) {
				if (stack.getItemMeta() != null) {
					ItemMeta meta = stack.getItemMeta();
					PersistentDataContainer tag = meta.getPersistentDataContainer();
					if (!tag.has(KEY, PersistentDataType.STRING)) {
						tag.set(KEY, PersistentDataType.STRING, "true");
						stack.setItemMeta(meta);
						int randIdx = (int)(Math.random() * MATERIALS.length);
						System.out.println(stack.getType() + " => " + MATERIALS[randIdx]);
						stack.setType(MATERIALS[randIdx]);
					}
				}
			}
		}
	}

	@EventHandler
	public void inventoryClick(InventoryClickEvent e) {
		if (e.getSlotType().equals(InventoryType.SlotType.RESULT)) {
			// 슬롯타입이 RESULT인 칸을 클릭했을 때,
			// #가끔 적용 안됨 (모루, ) => 왜?
			// #다른 아이템이 커서에 있는 상태에서 누르면 조합대에서 미리 볼 수있음 => 악용가능
			changeRandomItem(e.getCurrentItem());
		} else if (e.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
			// 아이템을 더블클릭해서 같은 종류의 아이템을 한꺼번에 모았을 때,
			// #가끔 적용 안됨 (훈연기, ) => 왜?
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
				ItemStack resultStack = e.getWhoClicked().getOpenInventory().getItem(rawSlotID);
				if (e.getCursor() != null) {
					if (e.getCursor().getType().equals(resultStack.getType())) {
						// #조합대에서 미리 볼 수있음. => 악용 가능성 있음
						changeRandomItem(resultStack);
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
		// #가끔 적용 안됨 => 어째서?
		changeRandomItem(e.getItem());
	}
}
