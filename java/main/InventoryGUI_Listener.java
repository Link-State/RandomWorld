package main;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryGUI_Listener extends InventoryGUI implements Listener {
	private final Inventory TEST_INV = Bukkit.createInventory(null, 9, "test");

	@EventHandler
	public void inventoryClick(InventoryClickEvent e) {
		
		if (!e.getView().getType().equals(TEST_INV.getType())) {
			return;
		}
		
		if (!(e.getWhoClicked() instanceof Player)) {
			return;
		}
		
		if (e.getClickedInventory() == null) {
			return;
		}
		
		if (!e.getClickedInventory().getType().equals(InventoryType.CHEST)) {
			return;
		}
		
		if (e.getCurrentItem() == null) {
			return;
		}
		
		if (e.getCurrentItem().getType().isAir()) {
			return;
		}
		
		Player p = (Player) e.getWhoClicked();
		ItemStack clicked = e.getCurrentItem();
		ItemMeta clicked_meta = clicked.getItemMeta();
		String clicked_icon = clicked_meta.getDisplayName();
		String title = e.getView().getTitle();
		
		if (clicked_icon.equals(ChatColor.WHITE + "선택 정보")) {
			e.setCancelled(true);
			return;
		}
		
		if (clicked_icon.equals(ChatColor.RED + "닫기")) {
			e.setCancelled(true);
			p.closeInventory();
			return;
		}
		
		Inventory inv = null;
		if (title.equals("개체 종류 선택")) {
			inv = selectedEntityType(e.getInventory(), clicked_icon);
		}
		else if (title.equals("개체 선택")) {
			inv = selectedEntity(e.getInventory(), clicked_meta);
		}
		else if (title.equals("이벤트 종류 선택")) {
			int rank = RandomWorldCommand.getRank(p);
			inv = selectedEventType(e.getInventory(), clicked_meta, rank);
		}
		else if (title.equals("이벤트 선택")) {
			ClickType click_btn = e.getClick();
			inv = selectedEvent(e.getInventory(), clicked_meta, click_btn);
		}
		else if (title.equals("이벤트 설정")) {
			inv = selectedEventDetail(e.getInventory(), clicked_meta);
		}
		else if (title.equals("이벤트 세부 설정")) {
			ClickType click_btn = e.getClick();
			inv = selectedEditOption(e.getInventory(), clicked_meta, click_btn, p);
		}
		else if (title.equals("숫자 입력")) {
			ClickType click_btn = e.getClick();
			inv = inputInt(e.getInventory(), clicked, click_btn);
		}
		
		if (inv == null) {
			return;
		}
		
		e.setCancelled(true);
		p.openInventory(inv);
	}
	
	@EventHandler
	public void inventoryClose(InventoryCloseEvent e) {
	}

	// 개체 종류 선택
	// openEntityTypeSelect
	private Inventory selectedEntityType(Inventory GUI, String clicked) {
		
		String entityType = "";
		if (clicked.equals(ChatColor.WHITE + "플레이어")) {
			entityType = "player";
		}
		else if (clicked.equals(ChatColor.WHITE + "엔티티")) {
			entityType = "entity";
		}
		else if (clicked.equals(ChatColor.WHITE + "공통")) {
			entityType = "default";
		}
		else {
			return null;
		}
		
		ArrayList<String> stack = new ArrayList<String>();
		stack.add(entityType);
		
		Inventory inv = openEntitySelect(stack, 1);
		return inv;
	}
	
	// 개체 선택
	// selectEntityTypeSelect
	private Inventory selectedEntity(Inventory GUI, ItemMeta clicked_meta) {
		
		ItemStack info_item = GUI.getItem(1);
		if (info_item == null || info_item.getType().isAir()) {
			return null;
		}
		ItemMeta info_meta = info_item.getItemMeta();
		ArrayList<String> stack = new ArrayList<String>(info_meta.getLore());
		
		Inventory inv = null;
		String clicked_name = clicked_meta.getDisplayName();
		
		if (clicked_name.equals(ChatColor.GRAY + "이전페이지")) {
			String page = clicked_meta.getLore().get(0);
			page = page.replaceAll(ChatColor.GOLD + "\\(" + ChatColor.GREEN, "");
			page = page.replaceAll(ChatColor.GOLD + " / [0-9]+\\)", "");
			int prev_page = Integer.parseInt(page) - 1;
			
			inv = openEntitySelect(stack, prev_page);
		}
		else if (clicked_name.equals(ChatColor.GRAY + "다음페이지")) {
			String page = clicked_meta.getLore().get(0);
			page = page.replaceAll(ChatColor.GOLD + "\\(" + ChatColor.GREEN, "");
			page = page.replaceAll(ChatColor.GOLD + " / [0-9]+\\)", "");
			int next_page = Integer.parseInt(page) + 1;
			
			inv = openEntitySelect(stack, next_page);
		}
		else if (clicked_name.equals(ChatColor.GRAY + "뒤로가기")) {
			inv = openEntityTypeSelect();
		}
		else if (clicked_name.equals(ChatColor.GRAY + "검색")) {
			// search
		}
		else {
			String name = clicked_name.replaceAll(ChatColor.WHITE + "", "");
			stack.add(name);
			inv = openEventTypeSelect(stack);
		}
		
		return inv;
	}

	// 이벤트 종류 선택
	//openEventTypeSelect
	private Inventory selectedEventType(Inventory GUI, ItemMeta clicked_meta, int rank) {
		
		ItemStack info_item = GUI.getItem(1);
		if (info_item == null || info_item.getType().isAir()) {
			return null;
		}
		ItemMeta info_meta = info_item.getItemMeta();
		ArrayList<String> stack = new ArrayList<String>(info_meta.getLore());
		
		Inventory inv = null;
		String clicked_name = clicked_meta.getDisplayName();
		
		if (clicked_name.equals(ChatColor.WHITE + "아이템")) {
			stack.add("item");
			inv = openEventSelect(stack, 1);
		}
		else if (clicked_name.equals(ChatColor.WHITE + "포션효과")) {
			stack.add("potion");
			inv = openEventSelect(stack, 1);
		}
		else if (clicked_name.equals(ChatColor.WHITE + "인첸트")) {
			stack.add("enchant");
			inv = openEventSelect(stack, 1);
		}
		else if (clicked_name.equals(ChatColor.GRAY + "뒤로가기") && rank >= 3) {
			stack.remove(stack.size() - 1);
			inv = openEntitySelect(stack, 1);
		}
		
		return inv;
	}

	// 이벤트 선택
	// openEventSelect
	private Inventory selectedEvent(Inventory GUI, ItemMeta clicked_meta, ClickType btn) {
		ItemStack info_item = GUI.getItem(1);
		if (info_item == null || info_item.getType().isAir()) {
			return null;
		}
		ItemMeta info_meta = info_item.getItemMeta();
		ArrayList<String> stack = new ArrayList<String>(info_meta.getLore());
		
		ItemStack page_icon = GUI.getItem(3);
		int current_page = 1;
		if (page_icon != null && !page_icon.getType().isAir()) {
			String page = page_icon.getItemMeta().getLore().get(0);
			page = page.replaceAll(ChatColor.GOLD + "\\(" + ChatColor.GREEN, "");
			page = page.replaceAll(ChatColor.GOLD + " / [0-9]+\\)", "");
			current_page = Integer.parseInt(page);
		}
		
		Inventory inv = null;
		String clicked_name = clicked_meta.getDisplayName();
		
		if (clicked_name.equals(ChatColor.GRAY + "이전페이지")) {
			inv = openEventSelect(stack, current_page - 1);
		}
		else if (clicked_name.equals(ChatColor.GRAY + "다음페이지")) {
			inv = openEventSelect(stack, current_page + 1);
		}
		else if (clicked_name.equals(ChatColor.GRAY + "뒤로가기")) {
			stack.remove(stack.size() - 1);
			inv = openEventTypeSelect(stack);
		}
		else if (btn.equals(ClickType.LEFT)) {
			// 이벤트 활성화
			// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
			inv = openEventSelect(stack, current_page);
		}
		else if (btn.equals(ClickType.RIGHT)) {
			String eventName = clicked_name.replaceAll(ChatColor.GRAY + "", "");
			stack.add(eventName);
			inv = openEventDetailSetting(stack);
		}
		
		return inv;
	}

	// 이벤트 설정
	// openEventDetailSetting
	private Inventory selectedEventDetail(Inventory GUI, ItemMeta clicked_meta) {
		ItemStack info_item = GUI.getItem(1);
		if (info_item == null || info_item.getType().isAir()) {
			return null;
		}
		ItemMeta info_meta = info_item.getItemMeta();
		ArrayList<String> stack = new ArrayList<String>(info_meta.getLore());
		
		Inventory inv = null;
		String clicked_name = clicked_meta.getDisplayName();
		
		if (clicked_name.equals(ChatColor.GOLD + "필터")) {
			stack.add("EXCEPT");
			inv = openEditGUI(stack, 1);
		}
		else if (clicked_name.equals(ChatColor.GOLD + "밴")) {
			stack.add("BAN");
			inv = openEditGUI(stack, 1);
		}
		else if (clicked_name.equals(ChatColor.GOLD + "최대버프")) {
			stack.add("MAX");
			inv = openEditIntGUI(stack);
		}
		else if (clicked_name.equals(ChatColor.GRAY + "뒤로가기")) {
			stack.remove(stack.size() - 1);
			inv = openEventSelect(stack, 1);
		}
		
		return inv;
	}

	// 이벤트 세부 설정
	// openEditGUI
	private Inventory selectedEditOption(Inventory GUI, ItemMeta clicked_meta, ClickType btn, Player sender) {
		ItemStack info_item = GUI.getItem(1);
		if (info_item == null || info_item.getType().isAir()) {
			return null;
		}
		ItemMeta info_meta = info_item.getItemMeta();
		ArrayList<String> stack = new ArrayList<String>(info_meta.getLore());
		
		ItemStack page_icon = GUI.getItem(3);
		int current_page = 1;
		if (page_icon != null && !page_icon.getType().isAir()) {
			String page = page_icon.getItemMeta().getLore().get(0);
			page = page.replaceAll(ChatColor.GOLD + "\\(" + ChatColor.GREEN, "");
			page = page.replaceAll(ChatColor.GOLD + " / [0-9]+\\)", "");
			current_page = Integer.parseInt(page);
		}
		
		Inventory inv = null;
		String clicked_name = clicked_meta.getDisplayName();
		
		if (clicked_name.equals(ChatColor.GRAY + "이전페이지")) {
			inv = openEditGUI(stack, current_page - 1);
		}
		else if (clicked_name.equals(ChatColor.GRAY + "다음페이지")) {
			inv = openEditGUI(stack, current_page + 1);
		}
		else if (clicked_name.equals(ChatColor.GRAY + "뒤로가기")) {
			stack.remove(stack.size() - 1);
			inv = openEventDetailSetting(stack);
		}
		else if (btn.equals(ClickType.LEFT)) {
			String detailName = clicked_name.replaceAll(ChatColor.GRAY + "", "");
			String status = clicked_meta.getLore().get(0);
			status = status.replaceAll(ChatColor.BOLD + "", "");
			status = status.replaceAll(ChatColor.RESET + "", "");
			status = status.replaceAll(ChatColor.GRAY + "", "");
			status = status.replaceAll(ChatColor.GOLD + "", "");
			status = status.replaceAll(ChatColor.RED + "", "");
			status = status.replaceAll("상태 : \\[", "");
			status = status.replaceAll("\\]", "");

			String cmd_option = "";
			String entityType = stack.get(0);
			String entityName = stack.get(1);
			String eventName = stack.get(3) + "_" + stack.get(4);
			ArrayList<String> fields = new ArrayList<String>();
			fields.add(detailName);
			
			if (status.equals("적용")) {
				cmd_option = "remove";
			}
			else if (status.equals("미적용")) {
				cmd_option = "add";
			}
			else {
				cmd_option = "set";
			}
			
			RandomWorldCommand.setEvents(sender, cmd_option, entityType, entityName, eventName, fields);
			inv = openEditGUI(stack, current_page);
		}
		else if (btn.equals(ClickType.RIGHT)) {
			// 미정
		}
		
		return inv;
	}

	// 숫자 입력
	// openEditIntGUI
	private Inventory inputInt(Inventory GUI, ItemStack clicked_stack, ClickType btn) {
		ItemStack info_item = GUI.getItem(1);
		if (info_item == null || info_item.getType().isAir()) {
			return null;
		}
		
		ItemStack value_icon = GUI.getItem(22);
		if (value_icon == null || value_icon.getType().isAir()) {
			return null;
		}
		
		ItemMeta info_meta = info_item.getItemMeta();
		ArrayList<String> stack = new ArrayList<String>(info_meta.getLore());
		
		Inventory inv = null;
		String clicked_name = clicked_stack.getItemMeta().getDisplayName();
		Material clicked_type = clicked_stack.getType();
		String value_str = value_icon.getItemMeta().getDisplayName();
		value_str = value_str.replaceAll("" + ChatColor.GOLD + ChatColor.BOLD, "");
		int value = Integer.parseInt(value_str);
		

		if (clicked_name.equals(ChatColor.GRAY + "뒤로가기")) {
			stack.remove(stack.size() - 1);
			inv = openEventDetailSetting(stack);
		}
		else {
			int acc = 1;
			int sign = 1;
			
			if (clicked_type.equals(Material.IRON_NUGGET)) {
				acc = 1;
			}
			else if (clicked_type.equals(Material.IRON_INGOT)) {
				acc = 5;
			}
			else if (clicked_type.equals(Material.IRON_BLOCK)) {
				acc = 10;
			}

			if (btn.equals(ClickType.LEFT)) {
				sign = 1;
			}
			else if (btn.equals(ClickType.RIGHT)) {
				sign = -1;
			}
			
			value += (sign * acc);
			
			inv = openEditIntGUI(stack, value);
		}
		
		return inv;
	}
}
