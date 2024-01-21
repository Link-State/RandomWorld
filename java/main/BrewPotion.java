package main;

import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.ItemStack;

public class BrewPotion extends RandomItem implements Listener {
	
	@EventHandler
	public void brewPotion(BrewEvent e) {

		// 해당 월드 밴일 때
		if (Main.DISABLE_WORLD.get(e.getBlock().getWorld()) != null) {
			return;
		}
		
		// 이벤트가 활성화 되어 있지 않을 때
		// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

		// 양조된 아이템 목록
		List<ItemStack> potions = e.getResults();
		
		// 각 만들어진 포션에 대해
		for (ItemStack potion : potions) {
			// 포션효과에 대해
			prepareItem(potion);
		}
	}
}
