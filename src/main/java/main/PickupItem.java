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
		
		// Entity가 Player 객체인 경우
		if (e.getEntity() instanceof Player) {
			
			Player p = (Player) e.getEntity();
			RandomEvent re = Main.REGISTED_PLAYER.get(p.getUniqueId()); // 해당 플레이어가 등록되지 않았으면 null

			// 해당 플레이어에게 '아이템 획득 랜덤효과'가 적용 되어있는지 확인
			if (re != null && re.getActivate("PICKUP")) {
				
				ItemStack stack = e.getItem().getItemStack(); // 플레이어가 주운 아이템 스택
				Material material = re.getRandomItem("PICKUP"); // 무작위 아이템 1개 선택
				
				// 변경한 아이템이 null이 아니고 config에서 밴 된(필터링 처리 된) 아이템일 경우
				if (material != null) {
					if (!re.isItemBan("PICKUP", stack.getType())) {
						
						// 무작위로 선택된 아이템으로 변경
						changeRandomItem(stack, material);
					}
				}
			}
		}
	}
}