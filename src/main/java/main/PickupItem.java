package main;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class PickupItem extends RandomItem implements Listener {
	
	@EventHandler
	public void pickupItem(EntityPickupItemEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			RandomEvent re = Main.REGISTED_PLAYER.get(p.getUniqueId());
			if (re != null && re.getActivate("PICKUP")) {
				ItemStack stack = e.getItem().getItemStack();
				Material material = re.getRandomItem("PICKUP");
				if (material != null) {
					if (!re.isBan("PICKUP", stack.getType())) {
						changeRandomItem(stack, material);
					}
				}
			}
		}
	}
}