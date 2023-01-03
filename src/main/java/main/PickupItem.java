package main;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class PickupItem extends RandomItem implements Listener {
	
	PickupItem(String filter_list) {
		super(filter_list);
	}

	@EventHandler
	public void pickupItem(EntityPickupItemEvent e) {
		// #가끔 적용 안됨 => 왜? = 자연생성 아이템이 아니어서.
		changeRandomItem(e.getItem().getItemStack());
	}
}