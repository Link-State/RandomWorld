package main;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class InventoryGUI_Listener implements Listener {
	private final Inventory TEST_INV = Bukkit.createInventory(null, 9, "test");

	@EventHandler
	public void inventoryClick(InventoryClickEvent e) {
		
		if (!e.getView().getType().equals(TEST_INV.getType())) {
			return;
		}
		
		if (!(e.getWhoClicked() instanceof Player)) {
			return;
		}
		
		Player p = (Player) e.getWhoClicked();
		InventoryGUI gui = InventoryGUI.USING_PLAYERS.get(p.getUniqueId());
		if (gui == null) {
			return;
		}
		
		String title = e.getView().getTitle();
		
		if (title.equals("개체 종류 선택")) {
			selectedEntityType();
		}
		else if (title.equals("개체 선택")) {
			selectedEntity();
		}
		else if (title.equals("이벤트 종류 선택")) {
			selectedEventType();
		}
		else if (title.equals("이벤트 선택")) {
			ClickType click_btn = e.getClick();
			selectedEvent(click_btn);
		}
		else if (title.equals("이벤트 설정")) {
			selectedEventDetail();
		}
		else if (title.equals("이벤트 세부 설정")) {
			ClickType click_btn = e.getClick();
			selectedEditOption(click_btn);
		}
		else if (title.equals("숫자 입력")) {
			ClickType click_btn = e.getClick();
			inputInt(click_btn);
		}
	}
	
	@EventHandler
	public void inventoryClose(InventoryCloseEvent e) {
		
	}

	// 개체 종류 선택
	// openEntityTypeSelect
	private void selectedEntityType() {
		
	}
	
	// 개체 선택
	// selectEntityTypeSelect
	private void selectedEntity() {
		
	}

	// 이벤트 종류 선택
	//openEventTypeSelect
	private void selectedEventType() {
		
	}

	// 이벤트 선택
	// openEventSelect
	private void selectedEvent(ClickType btn) {
		
	}

	// 이벤트 설정
	// openEventDetailSetting
	private void selectedEventDetail() {
		
	}

	// 이벤트 세부 설정
	// openEditGUI
	private void selectedEditOption(ClickType btn) {
		
	}

	// 숫자 입력
	// openEditIntGUI
	private void inputInt(ClickType btn) {
		
	}
	
//	private void 
}
