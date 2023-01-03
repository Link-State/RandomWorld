package main;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class CreateItem extends RandomItem implements Listener {

	@EventHandler
	public void inventoryClick(InventoryClickEvent e) {
		if (e.getSlotType().equals(InventoryType.SlotType.RESULT)) {
			// #가끔 적용 안됨 (모루, ) => 왜? = 자연생성 아이템이 아니어서.
			if (e.getCursor() != null && e.getCursor().getType().equals(Material.AIR)) {
				System.out.println("try Change");
				changeRandomItem(e.getCurrentItem());
			} else {
				System.out.println("try ready - 1");
				prepareItem(e.getCurrentItem());
			}
		} else if (e.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
			// #가끔 적용 안됨 (훈연기, ) => 왜? = 자연생성 아이템이 아니어서.
			InventoryType invType = e.getWhoClicked().getOpenInventory().getType();
			if (INV_TYPES.get(invType) != null) {
				int rawSlotID = INV_TYPES.get(invType);
				ItemStack resultStack = e.getWhoClicked().getOpenInventory().getItem(rawSlotID);
				if (e.getCursor() != null) {
					if (e.getCursor().getType().equals(resultStack.getType())) {
						System.out.println("try ready - 2");
						prepareItem(resultStack);
					}
				}
			}
		}
	}
}
