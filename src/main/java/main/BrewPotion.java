package main;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;

public class BrewPotion extends RandomItem implements Listener {

	BrewPotion(String filter_list) {
		super(filter_list);
	}

	@EventHandler
	public void brewPotion(BrewEvent e) {
		e.getResults().forEach(potion -> {
			changeRandomItem(potion);
		});
	}
}
