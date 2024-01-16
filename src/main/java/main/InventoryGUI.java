package main;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class InventoryGUI {
	private Player player;
	
	public InventoryGUI(Player p) {
		this.player = p;
	}
	
	// 이벤트 타입을 설정하는 GUI
	public boolean openEventTypeSelect() {
		
		Inventory inv = Bukkit.createInventory(null, 54, "이벤트 범주 선택");
		
		
		
		this.player.openInventory(inv);
		return true;
	}
	
	public boolean openEntityTypeSelect() {

		Inventory inv = Bukkit.createInventory(null, 54, "적용 범위 선택");
		
		
		
		this.player.openInventory(inv);
		return true;
	}
}
