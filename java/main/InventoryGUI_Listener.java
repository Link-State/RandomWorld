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
		
		String lang = "English";
		RandomEvent re = Main.REGISTED_PLAYER.get(p.getUniqueId());
		if (re != null) {
			lang = re.getLanguage();
		}
		
		if (clicked_icon.equals(Language.LANGUAGE_DATA.get(lang).get("SELECTED_INFO"))) {
			e.setCancelled(true);
			return;
		}
		
		if (clicked_icon.equals(Language.LANGUAGE_DATA.get(lang).get("CLOSE"))) {
			e.setCancelled(true);
			p.closeInventory();
			return;
		}
		
		Inventory inv = null;
		if (title.equals(Language.LANGUAGE_DATA.get(lang).get("SELECT_ENTITY_TYPE"))) {
			inv = selectedEntityType(lang, e.getInventory(), clicked_icon);
		}
		else if (title.equals(Language.LANGUAGE_DATA.get(lang).get("SELECT_ENTITY"))) {
			inv = selectedEntity(lang, e.getInventory(), clicked_meta);
		}
		else if (title.equals(Language.LANGUAGE_DATA.get(lang).get("SELECT_EVENT_TYPE"))) {
			int rank = RandomWorldCommand.getRank(p);
			inv = selectedEventType(lang, e.getInventory(), clicked_meta, rank);
		}
		else if (title.equals(Language.LANGUAGE_DATA.get(lang).get("SELECT_EVENT"))) {
			ClickType click_btn = e.getClick();
			inv = selectedEvent(lang, e.getInventory(), clicked_meta, click_btn, p);
		}
		else if (title.equals(Language.LANGUAGE_DATA.get(lang).get("SET_EVENT"))) {
			inv = selectedEventDetail(lang, e.getInventory(), clicked_meta);
		}
		else if (title.equals(Language.LANGUAGE_DATA.get(lang).get("SET_EVENT_DETAIL"))) {
			ClickType click_btn = e.getClick();
			inv = selectedEditOption(lang, e.getInventory(), clicked, click_btn, p);
		}
		else if (title.equals(Language.LANGUAGE_DATA.get(lang).get("INPUT_INT"))) {
			ClickType click_btn = e.getClick();
			inv = inputInt(lang, e.getInventory(), clicked, click_btn, p);
		}
		else {
			return;
		}
		
		e.setCancelled(true);
		
		if (inv == null) {
			return;
		}
		
		p.openInventory(inv);
	}

	// 개체 종류 선택
	// openEntityTypeSelect
	private Inventory selectedEntityType(String lang, Inventory GUI, String clicked) {
		
		String entityType = "";
		if (clicked.equals(Language.LANGUAGE_DATA.get(lang).get("PLAYER"))) {
			entityType = "player";
		}
		else if (clicked.equals(Language.LANGUAGE_DATA.get(lang).get("ENTITY"))) {
			entityType = "entity";
		}
		else if (clicked.equals(Language.LANGUAGE_DATA.get(lang).get("DEFAULT"))) {
			ArrayList<String> stack = new ArrayList<String>();
			stack.add("default");
			stack.add("default");
			Inventory inv = openEventTypeSelect(lang, stack);
			return inv;
		}
		else {
			return null;
		}
		
		ArrayList<String> stack = new ArrayList<String>();
		stack.add(entityType);
		
		Inventory inv = openEntitySelect(lang, stack, 1);
		return inv;
	}
	
	// 개체 선택
	// selectEntityTypeSelect
	private Inventory selectedEntity(String lang, Inventory GUI, ItemMeta clicked_meta) {
		
		ItemStack info_item = GUI.getItem(1);
		if (info_item == null || info_item.getType().isAir()) {
			return null;
		}
		ItemMeta info_meta = info_item.getItemMeta();
		ArrayList<String> stack = new ArrayList<String>(info_meta.getLore());
		
		Inventory inv = null;
		String clicked_name = clicked_meta.getDisplayName();
		
		if (clicked_name.equals(Language.LANGUAGE_DATA.get(lang).get("PREV_PAGE"))) {
			String page = clicked_meta.getLore().get(0);
			page = page.replaceAll(ChatColor.GOLD + "\\(" + ChatColor.GREEN, "");
			page = page.replaceAll(ChatColor.GOLD + " / [0-9]+\\)", "");
			int prev_page = Integer.parseInt(page) - 1;
			
			inv = openEntitySelect(lang, stack, prev_page);
		}
		else if (clicked_name.equals(Language.LANGUAGE_DATA.get(lang).get("NEXT_PAGE"))) {
			String page = clicked_meta.getLore().get(0);
			page = page.replaceAll(ChatColor.GOLD + "\\(" + ChatColor.GREEN, "");
			page = page.replaceAll(ChatColor.GOLD + " / [0-9]+\\)", "");
			int next_page = Integer.parseInt(page) + 1;
			
			inv = openEntitySelect(lang, stack, next_page);
		}
		else if (clicked_name.equals(Language.LANGUAGE_DATA.get(lang).get("BACKWARD"))) {
			inv = openEntityTypeSelect(lang);
		}
		else if (clicked_name.equals(Language.LANGUAGE_DATA.get(lang).get("SEARCH"))) {
			// search
		}
		else {
			String name = clicked_name.replaceAll(ChatColor.WHITE + "", "");
			stack.add(name);
			inv = openEventTypeSelect(lang, stack);
		}
		
		return inv;
	}

	// 이벤트 종류 선택
	//openEventTypeSelect
	private Inventory selectedEventType(String lang, Inventory GUI, ItemMeta clicked_meta, int rank) {
		
		ItemStack info_item = GUI.getItem(1);
		if (info_item == null || info_item.getType().isAir()) {
			return null;
		}
		ItemMeta info_meta = info_item.getItemMeta();
		ArrayList<String> stack = new ArrayList<String>(info_meta.getLore());
		
		Inventory inv = null;
		String clicked_name = clicked_meta.getDisplayName();
		
		if (clicked_name.equals(Language.LANGUAGE_DATA.get(lang).get("ITEM"))) {
			stack.add("item");
			inv = openEventSelect(lang, stack, 1);
		}
		else if (clicked_name.equals(Language.LANGUAGE_DATA.get(lang).get("POTION_EFFECT"))) {
			stack.add("potion");
			inv = openEventSelect(lang, stack, 1);
		}
		else if (clicked_name.equals(Language.LANGUAGE_DATA.get(lang).get("ENCHANT"))) {
			stack.add("enchant");
			inv = openEventSelect(lang, stack, 1);
		}
		else if (clicked_name.equals(Language.LANGUAGE_DATA.get(lang).get("BACKWARD")) && rank >= 3) {
			
			String entityType = stack.get(0);
			String entityName = stack.get(1);
			
			if (entityType.equals("default") && entityName.equals("default")) {
				inv = openEntityTypeSelect(lang);
			}
			else {
				stack.remove(stack.size() - 1);
				inv = openEntitySelect(lang, stack, 1);
			}
			
		}
		
		return inv;
	}

	// 이벤트 선택
	// openEventSelect
	private Inventory selectedEvent(String lang, Inventory GUI, ItemMeta clicked_meta, ClickType btn, Player sender) {
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
		
		if (clicked_name.equals(Language.LANGUAGE_DATA.get(lang).get("PREV_PAGE"))) {
			inv = openEventSelect(lang, stack, current_page - 1);
		}
		else if (clicked_name.equals(Language.LANGUAGE_DATA.get(lang).get("NEXT_PAGE"))) {
			inv = openEventSelect(lang, stack, current_page + 1);
		}
		else if (clicked_name.equals(Language.LANGUAGE_DATA.get(lang).get("BACKWARD"))) {
			stack.remove(stack.size() - 1);
			inv = openEventTypeSelect(lang, stack);
		}
		else if (btn.equals(ClickType.LEFT)) {
			String entityType = stack.get(0);
			String entityName = stack.get(1);
			String eventName = clicked_name.replaceAll("" + ChatColor.GRAY, "");
			RandomWorldCommand.toggleEvent(sender, entityType, entityName, eventName);
			
			inv = openEventSelect(lang, stack, current_page);
		}
		else if (btn.equals(ClickType.RIGHT)) {
			String eventName = clicked_name.replaceAll(ChatColor.GRAY + "", "");
			stack.add(eventName);
			inv = openEventDetailSetting(lang, stack);
		}
		
		return inv;
	}

	// 이벤트 설정
	// openEventDetailSetting
	private Inventory selectedEventDetail(String lang, Inventory GUI, ItemMeta clicked_meta) {
		ItemStack info_item = GUI.getItem(1);
		if (info_item == null || info_item.getType().isAir()) {
			return null;
		}
		ItemMeta info_meta = info_item.getItemMeta();
		ArrayList<String> stack = new ArrayList<String>(info_meta.getLore());
		
		Inventory inv = null;
		String clicked_name = clicked_meta.getDisplayName();
		
		if (clicked_name.equals(Language.LANGUAGE_DATA.get(lang).get("EXCEPT"))) {
			stack.add("EXCEPT");
			inv = openEditGUI(lang, stack, 1);
		}
		else if (clicked_name.equals(Language.LANGUAGE_DATA.get(lang).get("BAN"))) {
			stack.add("BAN");
			inv = openEditGUI(lang, stack, 1);
		}
		else if (clicked_name.equals(Language.LANGUAGE_DATA.get(lang).get("MAX"))) {
			stack.add("MAX");
			inv = openEditIntGUI(lang, stack);
		}
		else if (clicked_name.equals(Language.LANGUAGE_DATA.get(lang).get("BACKWARD"))) {
			stack.remove(stack.size() - 1);
			inv = openEventSelect(lang, stack, 1);
		}
		
		return inv;
	}

	// 이벤트 세부 설정
	// openEditGUI
	private Inventory selectedEditOption(String lang, Inventory GUI, ItemStack clicked, ClickType btn, Player sender) {
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
		ItemMeta clicked_meta = clicked.getItemMeta();
		String clicked_name = clicked_meta.getDisplayName();
		if (clicked_name.isEmpty()) {
			clicked_name = clicked.getType().name();
		}
		
		
		if (clicked_name.equals(Language.LANGUAGE_DATA.get(lang).get("PREV_PAGE"))) {
			inv = openEditGUI(lang, stack, current_page - 1);
		}
		else if (clicked_name.equals(Language.LANGUAGE_DATA.get(lang).get("NEXT_PAGE"))) {
			inv = openEditGUI(lang, stack, current_page + 1);
		}
		else if (clicked_name.equals(Language.LANGUAGE_DATA.get(lang).get("BACKWARD"))) {
			stack.remove(stack.size() - 1);
			inv = openEventDetailSetting(lang, stack);
		}
		else if (btn.equals(ClickType.LEFT)) {
			String detailName = clicked_name.replaceAll(ChatColor.GRAY + "", "");
			String status = clicked_meta.getLore().get(0);
			
			// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
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
			inv = openEditGUI(lang, stack, current_page);
		}
		else if (btn.equals(ClickType.RIGHT)) {
			// 미정
		}
		
		return inv;
	}

	// 숫자 입력
	// openEditIntGUI
	private Inventory inputInt(String lang, Inventory GUI, ItemStack clicked_stack, ClickType btn, Player sender) {
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
		

		if (clicked_name.equals(Language.LANGUAGE_DATA.get(lang).get("BACKWARD"))) {
			stack.remove(stack.size() - 1);
			inv = openEventDetailSetting(lang, stack);
		}
		else if (clicked_type.equals(Material.NAME_TAG)) {
			String entityType = stack.get(0);
			String entityName = stack.get(1);
			String eventName = stack.get(3) + "_" + stack.get(4);
			ArrayList<String> fields = new ArrayList<String>();
			fields.add(value_str);

			RandomWorldCommand.setEvents(sender, "set", entityType, entityName, eventName, fields);
			
			inv = openEditIntGUI(lang, stack, value);
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
			
			inv = openEditIntGUI(lang, stack, value);
		}
		
		return inv;
	}
}
