package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class BrewPotion extends RandomItem implements Listener {
	
	@EventHandler
	public void brewPotion(BrewEvent e) {
		
		// 포션 양조 시, 아이템 랜덤교체를 허용하는지 확인
		RandomEvent re = Main.DEFAULT;
		if (re == null || !re.getActivate("BREWING")) {
			return;
		}

		// 양조된 아이템 목록
		List<ItemStack> potions = e.getResults();
		
		// 각 만들어진 포션에 대해
		for (ItemStack potion : potions) {
			
			// 
		}
		
		
	}
}
