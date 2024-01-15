package main;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class PickupItem extends RandomItem implements Listener {
	
	// 아이템을 주웠을 때
	@EventHandler
	public void pickupItem(EntityPickupItemEvent e) {
		
		ItemStack stack = e.getItem().getItemStack(); // 플레이어가 주운 아이템 스택
		
		if (getItemStatus(stack) == 3) {
			return;
		}
		
		// Entity가 Player 객체가 아닌 경우
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		
		Player p = (Player) e.getEntity();
		RandomEvent re = Main.REGISTED_PLAYER.get(p.getUniqueId()); // 해당 플레이어가 등록되지 않았으면 null

		// 무작위로 선택된 아이템으로 변경
		changeRandomItem(re, "PICKUP", stack);
		
		return;
	}
}