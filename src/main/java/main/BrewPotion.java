package main;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;

public class BrewPotion extends RandomItem implements Listener {

	@EventHandler
	public void brewPotion(BrewEvent e) {
		
		// 양조된 아이템 목록
		e.getResults().forEach(potion -> {
//			changeRandomItem(potion);
			
		});
	}
}
