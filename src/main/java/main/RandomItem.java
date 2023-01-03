package main;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class RandomItem {
	public static final NamespacedKey KEY = new NamespacedKey(Main.PLUGIN, "randomStatus");
	public static final ArrayList<Material> MATERIALS = new ArrayList<Material>() {
		private static final long serialVersionUID = -3707007329761881137L;
		{
			Material[] materials = Material.values();
			ArrayList<Material> buffer = new ArrayList<Material>();
			
			// 자연적으로 얻을 수 있는 아이템인지 판별
			for (Material m : materials) {
				if (m.isItem()) {
					buffer.add(m);
				}
			}
			
			// buffer에서 특정 아이템만 적용, * = 전체적용, BLOCK = 설치가능한 블럭만 적용, 등등... 특정 아이템 이름 적용
			
			// 위에서 적용한 아이템중 특정 아이템만 제외, "" = 제외안함, BLOCK = 설치가능한 블럭만 제외, 등등... 특정 아이템 이름 제외
		}
	};
	public static final HashMap<InventoryType, Integer> INV_TYPES = new HashMap<InventoryType, Integer>() {
		private static final long serialVersionUID = -4875235924228832373L;
		{
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
			if (!stack.getType().isAir()) {
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
			int randIdx = (int)(Math.random() * MATERIALS.size());
			System.out.println(stack.getType() + " => " + MATERIALS.get(randIdx));
			stack.setType(MATERIALS.get(randIdx));
		}
	}
}
