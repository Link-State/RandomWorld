package main;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryGUI_Listener implements Listener {

	@EventHandler
	public void inventoryClick(InventoryClickEvent e) {
		e.getView().getTitle();
	}
}
