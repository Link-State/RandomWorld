package main;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class RandomItem {
	public static final NamespacedKey KEY = new NamespacedKey(Main.PLUGIN, "randomStatus");

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
	
	public void changeRandomItem(ItemStack stack, Material material) {
		int status = getItemStatus(stack);
		if (status == 1 || status == 2) {
			changeTag(stack, "changed");
			System.out.println(stack.getType() + " => " + material);
			stack.setType(material);
		}
	}
}
