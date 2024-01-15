package main;

import java.util.ArrayList;
import java.util.Map;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

public class EnchantItem extends RandomItem implements Listener {

	@EventHandler
	// 아이템을 인첸트할 때
	public void enchantItem(EnchantItemEvent e) {
		RandomEvent re = Main.REGISTED_PLAYER.get(e.getEnchanter().getUniqueId()); // 랜덤효과를 받는 유저목록에서 해당유저 가져옴 
		
		// 랜덤효과를 허용하지 않았거나 인첸트 테이블로 인한 랜덤효과를 허용하지 않았을 경우
		if (re == null || !re.getActivate("ENCHANTING")) {
			return;
		}
		
		
		Map<Enchantment, Integer> origins = e.getEnchantsToAdd(); // 인첸트 테이블에서 추가된 인첸트 목록
		ArrayList<Enchantment> origin_keys = new ArrayList<Enchantment>(origins.keySet().stream().toList());
		
		// 인첸트 테이블에서 얻은 각각의 인첸트에 대해 변환 시도
		for (Enchantment key : origin_keys) {
			Enchantment enchant = re.getRandomEnchant("ENCHANTING", key); // 마인크래프트 인게임 중 무작위 인첸트 1개를 선택
			
			// 해당 랜덤변환을 허용하지 않는 경우
			if (enchant == null) {
				continue;
			}
			
			// 인첸트 적용
			origins.put(enchant, origins.get(key));
			origins.remove(key);
		}
	}
}
