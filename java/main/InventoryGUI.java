package main;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

public class InventoryGUI {
	public static final TreeMap<String, OfflinePlayer> SORTED_PLAYERS = new TreeMap<String, OfflinePlayer>();
	public static final TreeMap<String, EntityType> SORTED_LIVING_ENTITIES = new TreeMap<String, EntityType>();
	
	public InventoryGUI() {
		OfflinePlayer[] all_player = Bukkit.getOfflinePlayers();
		for (OfflinePlayer player : all_player) {
			if (RandomEvent.hasEntity(player.getUniqueId().toString())) {
				SORTED_PLAYERS.put(player.getName(), player);
			}
		}
		
		if (SORTED_LIVING_ENTITIES.size() > 0) {
			return;
		}
		
		EntityType[] all_entity = EntityType.values();
		
		for (EntityType entity : all_entity) {
			if (!entity.isAlive() || entity.equals(EntityType.PLAYER)) {
				continue;
			}
			
			SORTED_LIVING_ENTITIES.put(entity.name(), entity);
		}
	}
	
	
	
	// 개체 타입을 선택하는 GUI
	public Inventory openEntityTypeSelect(String lang) {
		
		ArrayList<String> stack = new ArrayList<String>();
		Inventory inv = createWindow(lang, 45, Language.fetchString(lang, "SELECT_ENTITY_TYPE"), stack);
		if (inv == null) {
			return null;
		}
		
		// 플레이어
		ItemStack player_icon = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta player_icon_meta = (SkullMeta) player_icon.getItemMeta();
		player_icon_meta.setDisplayName(Language.fetchString(lang, "PLAYER"));
		player_icon.setItemMeta(player_icon_meta);
		inv.setItem(20, player_icon);
		
		// 엔티티
		ItemStack entity_icon = new ItemStack(Material.SKELETON_SKULL, 1);
		SkullMeta entity_icon_meta = (SkullMeta) entity_icon.getItemMeta();
		entity_icon_meta.setDisplayName(Language.fetchString(lang, "ENTITY"));
		entity_icon.setItemMeta(entity_icon_meta);
		inv.setItem(22, entity_icon);
		
		// 공통
		ItemStack default_icon = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta default_icon_meta = (SkullMeta) default_icon.getItemMeta();
		PlayerProfile default_icon_profile = createProfile("c69196b330c6b8962f23ad5627fb6ecce472eaf5c9d44f791f6709c7d0f4dece");
		default_icon_meta.setOwnerProfile(default_icon_profile);
		default_icon_meta.setDisplayName(Language.fetchString(lang, "DEFAULT"));
		default_icon.setItemMeta(default_icon_meta);
		inv.setItem(24, default_icon);
		
		return inv;
	}
	
	
	
	// 개체 선택 GUI
	public Inventory openEntitySelect(String lang, ArrayList<String> stack, int page) {

		if (stack.size() <= 0) {
			return null;
		}
		
		String entityType = stack.get(stack.size() - 1);
		ArrayList<ItemStack> icons = new ArrayList<ItemStack>();
		
		// 플레이어인 경우
		if (entityType.equals("player")) {
			ArrayList<String> all_player = new ArrayList<String>( new TreeSet<String>(SORTED_PLAYERS.keySet()) );
			
			for (String player : all_player) {
				ItemStack player_icon = new ItemStack(Material.PLAYER_HEAD, 1);
				SkullMeta player_icon_meta = (SkullMeta) player_icon.getItemMeta();
				player_icon_meta.setOwningPlayer(SORTED_PLAYERS.get(player));
				player_icon_meta.setDisplayName(ChatColor.WHITE + player);
				player_icon.setItemMeta(player_icon_meta);
				icons.add(player_icon);
			}
			
		}
		// 엔티티인 경우
		else {
			ArrayList<String> all_living_entity = new ArrayList<String>( new TreeSet<String>(SORTED_LIVING_ENTITIES.keySet()) );
			
			for (String entity : all_living_entity) {
				ItemStack entity_icon = new ItemStack(Material.SKELETON_SKULL, 1);
				SkullMeta entity_icon_meta = (SkullMeta) entity_icon.getItemMeta();
				entity_icon_meta.setDisplayName(ChatColor.WHITE + entity);
				entity_icon.setItemMeta(entity_icon_meta);
				icons.add(entity_icon);
			}
		}
		
		// 페이지뷰 생성
		Inventory inv = createPageWindow(lang, 54, Language.fetchString(lang, "SELECT_ENTITY"), icons, page, stack);
		if (inv == null) {
			return null;
		}

		// 플레이어인 경우 검색버튼 활성화
		if (entityType.equals("player")) {
			ItemStack search_icon = new ItemStack(Material.SPYGLASS, 1);
			ItemMeta search_icon_meta = search_icon.getItemMeta();
			search_icon_meta.setDisplayName(Language.fetchString(lang, "SEARCH"));
			search_icon.setItemMeta(search_icon_meta);
			inv.setItem(4, search_icon);
		}

		return inv;
	}
	
	
	
	// 이벤트 종류 선택하는 GUI
	public Inventory openEventTypeSelect(String lang, ArrayList<String> stack) {
		
		if (stack.size() <= 1) {
			return null;
		}
		
		Inventory inv = createWindow(lang, 45, Language.fetchString(lang, "SELECT_EVENT_TYPE"), stack);
		if (inv == null) {
			return null;
		}
		
		// 아이템
		ItemStack item_icon = new ItemStack(Material.CRAFTING_TABLE, 1);
		ItemMeta item_icon_meta = item_icon.getItemMeta();
		item_icon_meta.setDisplayName(Language.fetchString(lang, "ITEM"));
		item_icon.setItemMeta(item_icon_meta);
		inv.setItem(20, item_icon);
		
		// 포션
		ItemStack potion_icon = new ItemStack(Material.POTION, 1);
		ItemMeta potion_icon_meta = potion_icon.getItemMeta();
		potion_icon_meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		potion_icon_meta.setDisplayName(Language.fetchString(lang, "POTION_EFFECT"));
		potion_icon.setItemMeta(potion_icon_meta);
		inv.setItem(22, potion_icon);
		
		// 인첸트
		ItemStack enchant_icon = new ItemStack(Material.ENCHANTED_BOOK, 1);
		ItemMeta enchant_icon_meta = enchant_icon.getItemMeta();
		enchant_icon_meta.setDisplayName(Language.fetchString(lang, "ENCHANT"));
		enchant_icon.setItemMeta(enchant_icon_meta);
		inv.setItem(24, enchant_icon);
		
		return inv;
	}
	
	
	
	// 이벤트 선택 GUI
	public Inventory openEventSelect(String lang, ArrayList<String> stack, int page) {
		
		/*
		 * Event Type
		 * 
		 * 0 - item
		 * 1 - potion
		 * 2 - enchant
		 * 
		 */
		
		if (stack.size() <= 2) {
			return null;
		}
		
		// ArrayList<String> fields = 선택한 이벤트에 알맞게 이벤트이름 알파벳순으로 가져오기
		String eventType = stack.get(2);
		String entityName = stack.get(1);
		String entityType_str = stack.get(0);
		
		TreeSet<String> all_field_set = null;
		ArrayList<String> all_field = null;
		TreeMap<String, Boolean> user_field_map = new TreeMap<String, Boolean>();
		
		if (eventType.equals("item")) {
			all_field_set = new TreeSet<String>(Main.ITEM_FIELD.keySet());
		}
		else if (eventType.equals("potion")) {
			all_field_set = new TreeSet<String>(Main.POTION_FIELD.keySet());
		}
		else if (eventType.equals("enchant")) {
			all_field_set = new TreeSet<String>(Main.ENCHANT_FIELD.keySet());
		}
		else {
			all_field_set = new TreeSet<String>();
		}
		all_field = new ArrayList<String>(all_field_set);
		
		RandomEvent re = null;
		if (entityType_str.equals("player")) {
			OfflinePlayer p = SORTED_PLAYERS.get(entityName);

			re = Main.REGISTED_PLAYER.get(p.getUniqueId());
			if (re == null) {
				re = new RandomEvent(p.getUniqueId().toString());
			}
		}
		else if (entityType_str.equals("entity")) {
			EntityType entityType = EntityType.valueOf(entityName);
			re = Main.REGISTED_ENTITY.get(entityType);
		}
		else if (entityType_str.equals("default")) {
			re = Main.DEFAULT;
		}
		
		for (String field : all_field) {
			user_field_map.put(field, re.getActivate(field));
		}
		
		ArrayList<ItemStack> icons = new ArrayList<ItemStack>();
		
		// all_field으로 전체 이벤트 아이콘 생성
		for (String field : all_field) {
			ItemStack field_icon = new ItemStack(Material.HOPPER, 1);
			ItemMeta field_icon_meta = field_icon.getItemMeta();
			field_icon_meta.setDisplayName(ChatColor.GRAY + field);
			
			Boolean activate = user_field_map.get(field);
			field_icon_meta.setLore(Arrays.asList(
					activate ? Language.fetchString(lang, "STATUS_ENABLE") : Language.fetchString(lang, "STATUS_DISABLE"),
					activate ? Language.fetchString(lang, "LEFTCLICK_DISABLE") : Language.fetchString(lang, "LEFTCLICK_ENABLE"),
					Language.fetchString(lang, "RIGHTCLICK_EVENT_DETAIL")
				));
			field_icon.setItemMeta(field_icon_meta);
			icons.add(field_icon);
		}
		
		Inventory inv = createPageWindow(lang, 54, Language.fetchString(lang, "SELECT_EVENT"), icons, page, stack);
		if (inv == null) {
			return null;
		}

		return inv;
	}
	
	
	
	// 이벤트 세부 설정 GUI
	public Inventory openEventDetailSetting(String lang, ArrayList<String> stack) {
		
		if (stack.size() <= 3) {
			return null;
		}
		
		String eventName = stack.get(3);
		
		Inventory inv = createWindow(lang, 45, Language.fetchString(lang, "SET_EVENT"), stack);
		if (inv == null) {
			return null;
		}
		
		// except 아이콘
		ItemStack except_icon = new ItemStack(Material.WRITABLE_BOOK, 1);
		ItemMeta except_icon_meta = except_icon.getItemMeta();
		except_icon_meta.setDisplayName(Language.fetchString(lang, "EXCEPT"));
		except_icon.setItemMeta(except_icon_meta);
		
		// ban 아이콘
		ItemStack ban_icon = new ItemStack(Material.WRITABLE_BOOK, 1);
		ItemMeta ban_icon_meta = ban_icon.getItemMeta();
		ban_icon_meta.setDisplayName(Language.fetchString(lang, "BAN"));
		ban_icon.setItemMeta(ban_icon_meta);
		
		// max 아이콘
		Integer category = RandomWorldCommand.SETTING_CATEGORY.get(eventName + "_MAX");
		if (category != null && category == 4) {
			ItemStack max_icon = new ItemStack(Material.WRITABLE_BOOK, 1);
			ItemMeta max_icon_meta = max_icon.getItemMeta();
			max_icon_meta.setDisplayName(Language.fetchString(lang, "MAX"));
			max_icon.setItemMeta(max_icon_meta);
			inv.setItem(20, except_icon);
			inv.setItem(22, ban_icon);
			inv.setItem(24, max_icon);
		}
		else {
			inv.setItem(21, except_icon);
			inv.setItem(23, ban_icon);
		}
		
		return inv;
	}
	
	
	
	// 값 수정 GUI
	public Inventory openEditGUI(String lang, ArrayList<String> stack, int page) {
		
		if (stack.size() <= 4) {
			return null;
		}

		String settingType = stack.get(4);
		String eventName = stack.get(3);
		String eventType = stack.get(2);
		String entityName = stack.get(1);
		String entityType_str = stack.get(0);

		RandomEvent re = null;
		if (entityType_str.equals("player")) {
			OfflinePlayer p = SORTED_PLAYERS.get(entityName);

			re = Main.REGISTED_PLAYER.get(p.getUniqueId());
			if (re == null) {
				re = new RandomEvent(p.getUniqueId().toString());
			}
		}
		else if (entityType_str.equals("entity")) {
			EntityType entityType = EntityType.valueOf(entityName);
			re = Main.REGISTED_ENTITY.get(entityType);
		}
		else if (entityType_str.equals("default")) {
			re = Main.DEFAULT;
		}
		
		TreeMap<String, ItemStack> items = new TreeMap<String, ItemStack>();
		Set<String> applying_items = new HashSet<String>(re.getActivateEvents(eventName + "_" + settingType));
		
		if (eventType.equals("item")) {
			Material[] materials = Material.values();
			
			for (Material material : materials) {
				if (!material.isItem() || material.isAir()) {
					continue;
				}
				
				ItemStack item_stack = new ItemStack(material, 1);
				ItemMeta item_meta = item_stack.getItemMeta();
				boolean status = applying_items.contains(material.name().toUpperCase());
				item_meta.setLore(Arrays.asList(
						status ? Language.fetchString(lang, "STATUS_ACTIVATE") : Language.fetchString(lang, "STATUS_INACTIVATE"),
						status ? Language.fetchString(lang, "LEFTCLICK_INACTIVATE") : Language.fetchString(lang, "LEFTCLICK_ACTIVATE")
						));
				item_stack.setItemMeta(item_meta);
				items.put(material.name().toUpperCase(), item_stack);
			}
		}
		else if (eventType.equals("potion")) {
			Iterator<PotionEffectType> potions = Registry.EFFECT.iterator();
			while (potions.hasNext()) {
				PotionEffectType potion = potions.next();

				ItemStack item_stack = new ItemStack(Material.POTION, 1);
				PotionMeta item_meta = (PotionMeta) item_stack.getItemMeta();
				item_meta.setDisplayName(ChatColor.GRAY + potion.getKey().getKey().toUpperCase());
				item_meta.addCustomEffect(new PotionEffect(potion, 0, 0, false, false), false);
				boolean status = applying_items.contains(potion.getKey().getKey().toUpperCase());
				item_meta.setLore(Arrays.asList(
						status ? Language.fetchString(lang, "STATUS_ACTIVATE") : Language.fetchString(lang, "STATUS_INACTIVATE"),
						status ? Language.fetchString(lang, "LEFTCLICK_INACTIVATE") : Language.fetchString(lang, "LEFTCLICK_ACTIVATE")
						));
				
				item_stack.setItemMeta(item_meta);
				items.put(item_meta.getDisplayName(), item_stack);
			}
		}
		else if (eventType.equals("enchant")) {
			Iterator<Enchantment> enchants = Registry.ENCHANTMENT.iterator();
			while (enchants.hasNext()) {
				Enchantment enchant = enchants.next();

				ItemStack item_stack = new ItemStack(Material.ENCHANTED_BOOK, 1);
				EnchantmentStorageMeta item_meta = (EnchantmentStorageMeta) item_stack.getItemMeta();
				item_meta.setDisplayName(ChatColor.GRAY + enchant.getKey().getKey().toUpperCase());
				item_meta.addStoredEnchant(enchant, 1, false);
				boolean status = applying_items.contains(enchant.getKey().getKey().toUpperCase());
				item_meta.setLore(Arrays.asList(
						status ? Language.fetchString(lang, "STATUS_ACTIVATE") : Language.fetchString(lang, "STATUS_INACTIVATE"),
						status ? Language.fetchString(lang, "LEFTCLICK_INACTIVATE") : Language.fetchString(lang, "LEFTCLICK_ACTIVATE")
						));

				item_stack.setItemMeta(item_meta);
				items.put(item_meta.getDisplayName(), item_stack);
			}
		}
		
		ArrayList<ItemStack> sorted_items = new ArrayList<ItemStack>();
		TreeSet<String> keys = new TreeSet<String>(items.keySet());
		for (String key : keys) {
			sorted_items.add(items.get(key));
		}
		
		
		Inventory inv = createPageWindow(lang, 54, Language.fetchString(lang, "SET_EVENT_DETAIL"), sorted_items, page, stack);
		if (inv == null) {
			return null;
		}
		
		return inv;
	}
	
	
	
	// Int값 입력받을 때 사용하는 GUI
	public Inventory openEditIntGUI(String lang, ArrayList<String> stack) {

		if (stack.size() <= 4) {
			return null;
		}

		String settingType = stack.get(4);
		String eventName = stack.get(3);
		String entityName = stack.get(1);
		String entityType_str = stack.get(0);
		
		if (!settingType.equals("MAX")) {
			return null;
		}

		RandomEvent re = null;
		if (entityType_str.equals("player")) {
			OfflinePlayer p = SORTED_PLAYERS.get(entityName);

			re = Main.REGISTED_PLAYER.get(p.getUniqueId());
			if (re == null) {
				re = new RandomEvent(p.getUniqueId().toString());
			}
		}
		else if (entityType_str.equals("entity")) {
			EntityType entityType = EntityType.valueOf(entityName);
			re = Main.REGISTED_ENTITY.get(entityType);
		}
		else if (entityType_str.equals("default")) {
			re = Main.DEFAULT;
		}
		else {
			return null;
		}
		
		int value = re.getActivateMaxEvents(eventName + "_" + settingType);
		
		return openEditIntGUI(lang, stack, value);
	}
	public Inventory openEditIntGUI(String lang, ArrayList<String> stack, int default_value) {

		if (stack.size() <= 4) {
			return null;
		}
		
		Inventory inv = createWindow(lang, 45, Language.fetchString(lang, "INPUT_INT"), stack);
		if (inv == null) {
			return null;
		}

		ItemStack one_icon = new ItemStack(Material.IRON_NUGGET, 1);
		ItemMeta one_icon_meta = one_icon.getItemMeta();
		one_icon_meta.setDisplayName(Language.fetchString(lang, "LEFTCLICK_1"));
		one_icon_meta.setLore(Arrays.asList(
				Language.fetchString(lang, "RIGHTCLICK_1")
				));
		one_icon.setItemMeta(one_icon_meta);
		inv.setItem(14, one_icon);

		ItemStack five_icon = new ItemStack(Material.IRON_INGOT, 1);
		ItemMeta five_icon_meta = five_icon.getItemMeta();
		five_icon_meta.setDisplayName(Language.fetchString(lang, "LEFTCLICK_5"));
		five_icon_meta.setLore(Arrays.asList(
				Language.fetchString(lang, "RIGHTCLICK_5")
				));
		five_icon.setItemMeta(five_icon_meta);
		inv.setItem(23, five_icon);

		ItemStack ten_icon = new ItemStack(Material.IRON_BLOCK, 1);
		ItemMeta ten_icon_meta = ten_icon.getItemMeta();
		ten_icon_meta.setDisplayName(Language.fetchString(lang, "LEFTCLICK_10"));
		ten_icon_meta.setLore(Arrays.asList(
				Language.fetchString(lang, "RIGHTCLICK_10")
				));
		ten_icon.setItemMeta(ten_icon_meta);
		inv.setItem(32, ten_icon);
		
		ItemStack value_icon = new ItemStack(Material.NAME_TAG, 1);
		ItemMeta value_icon_meta = value_icon.getItemMeta();
		value_icon_meta.setDisplayName("" + ChatColor.GOLD + ChatColor.BOLD + default_value);
		value_icon_meta.setLore(Arrays.asList(
				Language.fetchString(lang, "LEFTCLICK_APPLY")
				));
		value_icon.setItemMeta(value_icon_meta);
		inv.setItem(22, value_icon);
		
		return inv;
	}
	
	
	// 페이지뷰 생성
	public Inventory createPageWindow(String lang, int size, String title, ArrayList<ItemStack> contents, ArrayList<String> stack) {
		return createPageWindow(lang, size, title, contents, 1, stack);
	}
	public Inventory createPageWindow(String lang, int size, String title, ArrayList<ItemStack> contents, int page, ArrayList<String> stack) {
		// 18 <= size <= 54 이어야 함
		if (size < 18 || 54 < size) {
			return null;
		}
		
		float unit = (float) (size - 9);
		int lastPage = (int) Math.ceil(contents.size() / unit);
		 
		if (page > lastPage) {
			page = lastPage;
		}
		
		if (page <= 0) {
			page = 1;
		}
		
		int start = (int) (unit * (page - 1));     
		
		Inventory inv = createWindow(lang, size, title, contents, start, stack);
		if (inv == null) {
			return null;
		}

		// 이전페이지 버튼
		ItemStack prev_icon = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta prev_icon_meta = (SkullMeta) prev_icon.getItemMeta();
		prev_icon_meta.setOwnerProfile(createProfile("77334cddfab45d75ad28e1a47bf8cf5017d2f0982f6737da22d4972952510661"));
		prev_icon_meta.setDisplayName(Language.fetchString(lang, "PREV_PAGE"));
		prev_icon_meta.setLore(Arrays.asList(ChatColor.GOLD + "(" + ChatColor.GREEN + page + ChatColor.GOLD + " / " + lastPage + ")"));
		prev_icon.setItemMeta(prev_icon_meta);
		inv.setItem(3, prev_icon);
		
		// 다음페이지 버튼
		ItemStack next_icon = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta next_icon_meta = (SkullMeta) next_icon.getItemMeta();
		next_icon_meta.setOwnerProfile(createProfile("e7742034f59db890c8004156b727c77ca695c4399d8e0da5ce9227cf836bb8e2"));
		next_icon_meta.setDisplayName(Language.fetchString(lang, "NEXT_PAGE"));
		next_icon_meta.setLore(Arrays.asList(ChatColor.GOLD + "(" + ChatColor.GREEN + page + ChatColor.GOLD + " / " + lastPage + ")"));
		next_icon.setItemMeta(next_icon_meta);
		inv.setItem(5, next_icon);
		
		return inv;
	}
	
	
	
	// 일반 뷰 생성
	public Inventory createWindow(String lang, int size, String title, ArrayList<String> stack) {
		return createWindow(lang, size, title, new ArrayList<ItemStack>(), 0, stack);
	}
	public Inventory createWindow(String lang, int size, String title, ArrayList<ItemStack> contents, ArrayList<String> stack) {
		return createWindow(lang, size, title, contents, 0, stack);
	}
	public Inventory createWindow(String lang, int size, String title, ArrayList<ItemStack> contents, int start, ArrayList<String> stack) {
		if (size % 9 != 0 || size < 9 || 54 < size) {
			return null;
		}

		if (start >= contents.size()) {
			start = 0;
		}

		Inventory inv = Bukkit.createInventory(null, size, title);
		
		// stack 길이가 0이면 뒤로가기 icon 없음
		if (stack.size() > 0) {
			// 뒤로가기 버튼
			ItemStack backward_icon = new ItemStack(Material.PLAYER_HEAD, 1);
			SkullMeta backward_icon_meta = (SkullMeta) backward_icon.getItemMeta();
			backward_icon_meta.setOwnerProfile(createProfile("1fc2611fbabe4e799062f6b470ac5ae727e32ef00d2b115d38656e341c128936"));
			backward_icon_meta.setDisplayName(Language.fetchString(lang, "BACKWARD"));
			backward_icon.setItemMeta(backward_icon_meta);
			inv.setItem(0, backward_icon);
		}

		// 선택 정보
		if (stack.size() > 0) {
			ItemStack info_icon = new ItemStack(Material.PRIZE_POTTERY_SHERD, 1);
			ItemMeta info_icon_meta = info_icon.getItemMeta();
			info_icon_meta.setDisplayName(Language.fetchString(lang, "SELECTED_INFO"));
			info_icon_meta.setLore(stack);
			info_icon.setItemMeta(info_icon_meta);
			inv.setItem(1, info_icon);
		}
		
		// 닫기 버튼
		ItemStack close_icon = new ItemStack(Material.BARRIER, 1);
		ItemMeta close_icon_meta = close_icon.getItemMeta();
		close_icon_meta.setDisplayName(Language.fetchString(lang, "CLOSE"));
		close_icon.setItemMeta(close_icon_meta);
		inv.setItem(8, close_icon);
		
		int unit = size - 9;
		int end = start + unit;
		if (end > contents.size()) {
			end = contents.size();
		}
		
		for (int i = start; i < end; i++) {
			inv.setItem(i - start + 9, contents.get(i));
		}
		
		return inv;
	}
	
	
	// 캐릭터 프로파일 생성
	private PlayerProfile createProfile(String url) {
		PlayerProfile profile = Bukkit.createPlayerProfile("test");
		PlayerTextures texture = profile.getTextures();
		
		URL skin_url;
		try {
			skin_url = new URL("https://textures.minecraft.net/texture/" + url);
		} catch (MalformedURLException e) {
			OfflinePlayer[] all_player = Bukkit.getOfflinePlayers();
			int randIdx = (int) (Math.random() * all_player.length);
			return all_player[randIdx].getPlayerProfile();
		}
		
		texture.setSkin(skin_url);
		profile.setTextures(texture);
		
		return profile;
	}
}
