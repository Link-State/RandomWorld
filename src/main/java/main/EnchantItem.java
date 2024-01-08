package main;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;

public class EnchantItem extends RandomItem implements Listener {

	@EventHandler
	// 아이템을 인첸트할 때
	public void enchantItem(EnchantItemEvent e) {
		RandomEvent re = Main.REGISTED_PLAYER.get(e.getEnchanter().getUniqueId()); // 랜덤효과를 받는 유저목록에서 해당유저 가져옴 
		
		// 랜덤아이템 효과를 받고, 인첸트 테이블에 대한 효과도 받을 경우
		if (re != null && re.getActivate("ENCHANTING")) {
			ItemStack stack = e.getItem(); // 인첸트 된 아이템
			Material material = re.getRandomItem("ENCHANTING"); // 마인크래프트 인게임 중 무작위 아이템성분 1개를 선택
			
			// 성분이 null이 아닐 경우
			if (material != null) {
				// 해당 플레이어에게 해당 아이템을 인첸트하는 것이 허용 된 경우
				if (!re.isBan("ENCHANTING", stack.getType())) {
					changeRandomItem(stack, material);
				}
			}
		}
	}
}
