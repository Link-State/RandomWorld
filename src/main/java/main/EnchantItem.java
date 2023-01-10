package main;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;

public class EnchantItem extends RandomItem implements Listener {

	@EventHandler
	public void enchantItem(EnchantItemEvent e) {
		RandomEvent re = Main.REGISTED_PLAYER.get(e.getEnchanter().getUniqueId());
		if (re != null && re.getActivate("ENCHANTING")) {
			ItemStack stack = e.getItem();
			Material material = re.getRandomItem("ENCHANTING");
			if (material != null) {
				if (!re.isBan("ENCHANTING", stack.getType())) {
					changeRandomItem(stack, material);
				}
			}
		}
	}
}
