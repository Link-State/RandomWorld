package main;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class PickupItem extends RandomItem implements Listener {
	
	// 아이템을 주웠을 때
	@EventHandler
	public void pickupItem(EntityPickupItemEvent e) {

		// 해당 월드 밴일 때
		if (Main.DISABLE_WORLD.get(e.getEntity().getWorld()) != null) {
			return;
		}
		
		ItemStack stack = e.getItem().getItemStack(); // 플레이어가 주운 아이템 스택
		
		if (getItemStatus(stack) == 3) {
			return;
		}
		
		// Entity가 Player 객체가 아닌 경우
		RandomEvent re;
		if (e.getEntity() instanceof Player) {
			re = Main.REGISTED_PLAYER.get(e.getEntity().getUniqueId()); // 해당 플레이어가 등록되지 않았으면 null
		}
		else {
			re = Main.REGISTED_ENTITY.get(e.getEntity().getType());
		}

		// 무작위로 선택된 아이템으로 변경
		changeRandomItem(re, "PICKUP", stack);
		
		return;
	}
}