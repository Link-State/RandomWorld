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
import org.bukkit.entity.Player;
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
	
	private Player commander;
	private Integer currentTargetType; // 0 - user, 1 - entity, 2 - default
	private String currentTargetName; // player : UUID OR entity : NAME OR default : DEFAULT
	private RandomEvent currentTargetRandomEvent; // 변경하려는 대상의 랜덤이벤트
	private Integer currentEventType; // 0 - ITEM, 1 - POTION, 2 - ENCHANT
	private String currentEventName; // PICKUP, AREA_EFFECT_CLOUD, SMITE, ...
	private String currentSettingType; // EXCEPT, BAN, MAX
	private final ArrayList<String> work_stack =  new ArrayList<String>();
	
	
	public InventoryGUI(Player p) {
		this.commander = p;
		this.currentTargetType = null;
		this.currentTargetName = null;
		this.currentTargetRandomEvent = null;
		this.currentEventType = null;
		this.currentEventName = null;
		this.currentSettingType = null;
		this.work_stack.clear();
		
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
	public boolean openEntityTypeSelect() {

		Inventory inv = createWindow(45, "개체 종류 선택");
		if (inv == null) {
			return false;
		}
		
		// 플레이어
		ItemStack player_icon = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta player_icon_meta = (SkullMeta) player_icon.getItemMeta();
		player_icon_meta.setDisplayName(ChatColor.WHITE + "플레이어");
		player_icon.setItemMeta(player_icon_meta);
		inv.setItem(20, player_icon);
		
		// 엔티티
		ItemStack entity_icon = new ItemStack(Material.SKELETON_SKULL, 1);
		SkullMeta entity_icon_meta = (SkullMeta) entity_icon.getItemMeta();
		entity_icon_meta.setDisplayName(ChatColor.WHITE + "엔티티");
		entity_icon.setItemMeta(entity_icon_meta);
		inv.setItem(22, entity_icon);
		
		// 공통
		ItemStack default_icon = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta default_icon_meta = (SkullMeta) default_icon.getItemMeta();
		PlayerProfile default_icon_profile = createProfile("c69196b330c6b8962f23ad5627fb6ecce472eaf5c9d44f791f6709c7d0f4dece");
		default_icon_meta.setOwnerProfile(default_icon_profile);
		default_icon_meta.setDisplayName(ChatColor.WHITE + "공통");
		default_icon.setItemMeta(default_icon_meta);
		inv.setItem(24, default_icon);
		
		this.currentTargetType = null;
		this.commander.openInventory(inv);
		return true;
	}
	
	
	
	// 개체 선택 GUI
	public boolean openEntitySelect(int page) {

		if (this.currentTargetType == null) {
			return false;
		}

		ArrayList<ItemStack> icons = new ArrayList<ItemStack>();
		
		// 플레이어인 경우
		if (this.currentTargetType == 0) {
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
		Inventory inv = createPageWindow(54, "개체 선택", icons, page);
		if (inv == null) {
			return false;
		}

		// 플레이어인 경우 검색버튼 활성화
		if (this.currentTargetType == 0) {
			ItemStack search_icon = new ItemStack(Material.SPYGLASS, 1);
			ItemMeta search_icon_meta = search_icon.getItemMeta();
			search_icon_meta.setDisplayName(ChatColor.GRAY + "검색");
			search_icon.setItemMeta(search_icon_meta);
			inv.setItem(4, search_icon);
		}

		this.currentTargetName = null;
		this.commander.openInventory(inv);
		return true;
	}
	
	
	
	// 이벤트 종류 선택하는 GUI
	public boolean openEventTypeSelect() {

		if (this.currentTargetType == null ||
			this.currentTargetName == null ||
			this.currentTargetRandomEvent == null) {
			return false;
		}
		
		Inventory inv = createWindow(45, "이벤트 종류 선택");
		if (inv == null) {
			return false;
		}
		
		// 아이템
		ItemStack item_icon = new ItemStack(Material.CRAFTING_TABLE, 1);
		ItemMeta item_icon_meta = item_icon.getItemMeta();
		item_icon_meta.setDisplayName(ChatColor.WHITE + "아이템");
		item_icon.setItemMeta(item_icon_meta);
		inv.setItem(20, item_icon);
		
		// 포션
		ItemStack potion_icon = new ItemStack(Material.POTION, 1);
		ItemMeta potion_icon_meta = potion_icon.getItemMeta();
		potion_icon_meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		potion_icon_meta.setDisplayName(ChatColor.WHITE + "포션효과");
		potion_icon.setItemMeta(potion_icon_meta);
		inv.setItem(22, potion_icon);
		
		// 인첸트
		ItemStack enchant_icon = new ItemStack(Material.ENCHANTED_BOOK, 1);
		ItemMeta enchant_icon_meta = enchant_icon.getItemMeta();
		enchant_icon_meta.setDisplayName(ChatColor.WHITE + "인첸트");
		enchant_icon.setItemMeta(enchant_icon_meta);
		inv.setItem(24, enchant_icon);
		
		this.currentEventType = null;
		this.commander.openInventory(inv);
		return true;
	}
	
	
	
	// 이벤트 선택 GUI
	public boolean openEventSelect(int page) {
		
		/*
		 * Event Type
		 * 
		 * 0 - item
		 * 1 - potion
		 * 2 - enchant
		 * 
		 */
		
		if (this.currentTargetType == null ||
			this.currentTargetName == null ||
			this.currentEventType == null ||
			this.currentTargetRandomEvent == null) {
			return false;
		}
		
		// ArrayList<String> fields = 선택한 이벤트에 알맞게 이벤트이름 알파벳순으로 가져오기
		TreeSet<String> all_field_set = null;
		ArrayList<String> all_field = null;
		TreeMap<String, Boolean> user_field_map = new TreeMap<String, Boolean>();
		
		switch(this.currentEventType) {
			// ITEM 필드 선택
			case 0 : {
				all_field_set = new TreeSet<String>(Main.ITEM_FIELD.keySet());
				break;
			}
			// POTION 필드 선택
			case 1 : {
				all_field_set = new TreeSet<String>(Main.POTION_FIELD.keySet());
				break;
			}
			// ENCHANT 필드 선택
			case 2 : {
				all_field_set = new TreeSet<String>(Main.ENCHANT_FIELD.keySet());
				break;
			}
			default : {
				all_field_set = new TreeSet<String>();
			}
		}
		all_field = new ArrayList<String>(all_field_set);
		
		RandomEvent re = this.currentTargetRandomEvent;
		
		for (String field : all_field) {
			user_field_map.put(field, re.getActivate(field));
		}
		
		ArrayList<ItemStack> icons = new ArrayList<ItemStack>();
		
		// 페이지가 1개면 페이지 이동버튼 생성 안함
//		if (lastPage > 1) {
//			// 이전페이지 버튼
//			ItemStack prev_icon = new ItemStack(Material.PLAYER_HEAD, 1);
//			SkullMeta prev_icon_meta = (SkullMeta) prev_icon.getItemMeta();
//			prev_icon_meta.setOwnerProfile(createProfile("77334cddfab45d75ad28e1a47bf8cf5017d2f0982f6737da22d4972952510661"));
//			prev_icon_meta.setDisplayName(ChatColor.GRAY + "이전페이지");
//			prev_icon_meta.setLore(Arrays.asList(ChatColor.GOLD + "(" + ChatColor.GREEN + currentPage+ ChatColor.GOLD + " / " + lastPage + ")"));
//			prev_icon.setItemMeta(prev_icon_meta);
//			inv.setItem(3, prev_icon);
//			
//			// 다음페이지 버튼
//			ItemStack next_icon = new ItemStack(Material.PLAYER_HEAD, 1);
//			SkullMeta next_icon_meta = (SkullMeta) next_icon.getItemMeta();
//			next_icon_meta.setOwnerProfile(createProfile("e7742034f59db890c8004156b727c77ca695c4399d8e0da5ce9227cf836bb8e2"));
//			next_icon_meta.setDisplayName(ChatColor.GRAY + "다음페이지");
//			next_icon_meta.setLore(Arrays.asList(ChatColor.GOLD + "(" + ChatColor.GREEN + currentPage + ChatColor.GOLD + " / " + lastPage + ")"));
//			next_icon.setItemMeta(next_icon_meta);
//			inv.setItem(5, next_icon);
//		}
		
		
		// all_field으로 전체 이벤트 아이콘 생성
		// 그리고 키값을 user_field_map에 넣어서 활성화여부 lore에 저장하기
		for (String field : all_field) {
			ItemStack field_icon = new ItemStack(Material.PLAYER_HEAD, 1);
			SkullMeta field_icon_meta = (SkullMeta) field_icon.getItemMeta();
			field_icon_meta.setOwnerProfile(createProfile("e7742034f59db890c8004156b727c77ca695c4399d8e0da5ce9227cf836bb8e2"));
			field_icon_meta.setDisplayName(ChatColor.GRAY + field);
			
			Boolean activate = user_field_map.get(field);
			field_icon_meta.setLore(Arrays.asList(
					ChatColor.GRAY + "상태 : " + (activate ? ChatColor.GOLD + "활성화" : ChatColor.RED + "비활성화"),
					ChatColor.GRAY + "좌클릭해서 " + (activate ? ChatColor.RED + "비활성화" : ChatColor.GOLD + "활성화"),
					ChatColor.GRAY + "우클릭해서 " + ChatColor.YELLOW + "이벤트 세부 설정"
				));
			field_icon.setItemMeta(field_icon_meta);
			icons.add(field_icon);
		}
		
		Inventory inv = createPageWindow(54, "이벤트 선택", icons, page);
		if (inv == null) {
			return false;
		}

		this.currentEventName = null;
		this.commander.openInventory(inv);
		return true;
	}
	
	
	
	// 이벤트 세부 설정 GUI
	public boolean openEventDetailSetting() {

		if (this.currentTargetType == null ||
			this.currentTargetName == null ||
			this.currentEventType == null ||
			this.currentEventName == null ||
			this.currentTargetRandomEvent == null) {
			return false;
		}
		
		Inventory inv = createWindow(45, "이벤트 세부 설정");
		if (inv == null) {
			return false;
		}
		
		// except 아이콘
		ItemStack except_icon = new ItemStack(Material.WRITABLE_BOOK, 1);
		ItemMeta except_icon_meta = except_icon.getItemMeta();
		except_icon_meta.setDisplayName(ChatColor.GOLD + "필터");
		except_icon.setItemMeta(except_icon_meta);
		
		// ban 아이콘
		ItemStack ban_icon = new ItemStack(Material.WRITABLE_BOOK, 1);
		ItemMeta ban_icon_meta = ban_icon.getItemMeta();
		ban_icon_meta.setDisplayName(ChatColor.GOLD + "밴");
		ban_icon.setItemMeta(ban_icon_meta);
		
		// max 아이콘
		Integer category = RandomWorldCommand.SETTING_CATEGORY.get(this.currentEventName + "_MAX");
		if (category != null && category == 4) {
			ItemStack max_icon = new ItemStack(Material.WRITABLE_BOOK, 1);
			ItemMeta max_icon_meta = max_icon.getItemMeta();
			max_icon_meta.setDisplayName(ChatColor.GOLD + "최대버프");
			max_icon.setItemMeta(max_icon_meta);
			inv.setItem(20, except_icon);
			inv.setItem(22, ban_icon);
			inv.setItem(24, max_icon);
		}
		else {
			inv.setItem(21, except_icon);
			inv.setItem(23, ban_icon);
		}
		
		this.currentSettingType = null;
		this.commander.openInventory(inv);
		return true;
	}
	
	
	
	// 값 수정 GUI
	public boolean openEditGUI(int page) {

		/*
		 * Event Type
		 * 
		 * 0 - item
		 * 1 - potion
		 * 2 - enchant
		 * 
		 */

		if (this.currentTargetType == null ||
			this.currentTargetName == null ||
			this.currentEventType == null ||
			this.currentEventName == null ||
			this.currentSettingType == null ||
			this.currentTargetRandomEvent == null) {
			return false;
		}
		
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		Set<String> applying_items = new HashSet<String>(this.currentTargetRandomEvent.getActivateEvents(this.currentEventName + "_" + this.currentSettingType));
		
		switch (this.currentEventType) {
			// ITEM이면 모든 아이템을 items에 넣기
			case 0 : {
				Material[] materials = Material.values();
				
				for (Material material : materials) {
					if (!material.isItem() || material.isAir()) {
						continue;
					}
					
					ItemStack stack = new ItemStack(material, 1);
					ItemMeta meta = stack.getItemMeta();
					boolean status = applying_items.contains(material.name().toUpperCase());
					meta.setLore(Arrays.asList(
							ChatColor.GRAY + "상태 : " + ( status ? ChatColor.GOLD + "적용" : ChatColor.RED + "미적용"),
							ChatColor.GRAY + "좌클릭해서 " + ( status ? ChatColor.RED + "미적용" : ChatColor.GOLD + "적용")
							));

					stack.setItemMeta(meta);
					items.add(stack);
				}
				break;
			}
			// POTION이면 모든 포션효과를 item에 넣기
			case 1 : {
				Iterator<PotionEffectType> potions = Registry.EFFECT.iterator();
				while (potions.hasNext()) {
					PotionEffectType potion = potions.next();

					ItemStack stack = new ItemStack(Material.POTION, 1);
					PotionMeta meta = (PotionMeta) stack.getItemMeta();
					meta.setDisplayName(ChatColor.GRAY + potion.getKey().getKey().toUpperCase());
					meta.addCustomEffect(new PotionEffect(potion, 0, 0, false, false), false);
					boolean status = applying_items.contains(potion.getKey().getKey().toUpperCase());
					meta.setLore(Arrays.asList(
							ChatColor.GRAY + "상태 : " + ( status ? ChatColor.GOLD + "적용" : ChatColor.RED + "미적용"),
							ChatColor.GRAY + "좌클릭해서 " + ( status ? ChatColor.RED + "미적용" : ChatColor.GOLD + "적용")
							));
					
					stack.setItemMeta(meta);
					items.add(stack);
				}
				break;
			}
			// ENCHANT이면 모든 인첸트를 items에 넣기
			case 2 : {
				Iterator<Enchantment> enchants = Registry.ENCHANTMENT.iterator();
				while (enchants.hasNext()) {
					Enchantment enchant = enchants.next();

					ItemStack stack = new ItemStack(Material.ENCHANTED_BOOK, 1);
					EnchantmentStorageMeta meta = (EnchantmentStorageMeta) stack.getItemMeta();
					meta.setDisplayName(ChatColor.GRAY + enchant.getKey().getKey().toUpperCase());
					meta.addStoredEnchant(enchant, 1, false);
					boolean status = applying_items.contains(enchant.getKey().getKey().toUpperCase());
					meta.setLore(Arrays.asList(
							ChatColor.GRAY + "상태 : " + ( status ? ChatColor.GOLD + "적용" : ChatColor.RED + "미적용"),
							ChatColor.GRAY + "좌클릭해서 " + ( status ? ChatColor.RED + "미적용" : ChatColor.GOLD + "적용")
							));

					stack.setItemMeta(meta);
					items.add(stack);
				}
				break;
			}
		}
		
		Inventory inv = createPageWindow(54, "이벤트 세부 설정", items, page);
		if (inv == null) {
			return false;
		}
		
		this.commander.openInventory(inv);
		return true;
	}
	
	
	
	// Int값 입력받을 때 사용하는 GUI
	public boolean openEditIntGUI() {
		return openEditIntGUI(0);
	}
	public boolean openEditIntGUI(int default_value) {

		if (this.currentTargetType == null ||
			this.currentTargetName == null ||
			this.currentEventType == null ||
			this.currentEventName == null ||
			this.currentSettingType == null ||
			this.currentTargetRandomEvent == null) {
			return false;
		}
	
		
		
		Inventory inv = createWindow(45, "숫자 입력");
		if (inv == null) {
			return false;
		}

		ItemStack one_icon = new ItemStack(Material.IRON_NUGGET, 1);
		ItemMeta one_icon_meta = one_icon.getItemMeta();
		one_icon_meta.setDisplayName(ChatColor.GRAY + "좌클릭해서 " + ChatColor.RED + ChatColor.BOLD + "+ 1");
		one_icon_meta.setLore(Arrays.asList(
				ChatColor.GRAY + "우클릭해서 " + ChatColor.BLUE + ChatColor.BOLD + "- 1"
				));
		one_icon.setItemMeta(one_icon_meta);
		inv.setItem(14, one_icon);

		ItemStack five_icon = new ItemStack(Material.IRON_INGOT, 1);
		ItemMeta five_icon_meta = five_icon.getItemMeta();
		five_icon_meta.setDisplayName(ChatColor.GRAY + "좌클릭해서 " + ChatColor.RED + ChatColor.BOLD + "+ 5");
		five_icon_meta.setLore(Arrays.asList(
				ChatColor.GRAY + "우클릭해서 " + ChatColor.BLUE + ChatColor.BOLD + "- 5"
				));
		five_icon.setItemMeta(five_icon_meta);
		inv.setItem(23, five_icon);

		ItemStack ten_icon = new ItemStack(Material.IRON_BLOCK, 1);
		ItemMeta ten_icon_meta = ten_icon.getItemMeta();
		ten_icon_meta.setDisplayName(ChatColor.GRAY + "좌클릭해서 " + ChatColor.RED + ChatColor.BOLD + "+ 10");
		ten_icon_meta.setLore(Arrays.asList(
				ChatColor.GRAY + "우클릭해서 " + ChatColor.BLUE + ChatColor.BOLD + "- 10"
				));
		ten_icon.setItemMeta(ten_icon_meta);
		inv.setItem(32, ten_icon);
		
		ItemStack value_icon = new ItemStack(Material.NAME_TAG, 1);
		ItemMeta value_icon_meta = value_icon.getItemMeta();
		value_icon_meta.setDisplayName("" + ChatColor.GOLD + ChatColor.BOLD + default_value);
		value_icon_meta.setLore(Arrays.asList(
				ChatColor.GRAY + "좌클릭해서 " + ChatColor.YELLOW + ChatColor.BOLD + "적용"
				));
		value_icon.setItemMeta(value_icon_meta);
		inv.setItem(22, value_icon);
		
		this.commander.openInventory(inv);
		return true;
	}
	
	
	
	// 확인GUI (사용안함)
	public boolean openConfirmGUI() {
		Inventory inv = createWindow(45, "저장 및 적용");
		if (inv == null) {
			return false;
		}
		
		inv.setItem(0, null);

		ItemStack alert_icon = new ItemStack(Material.NAME_TAG, 1);
		ItemMeta alert_icon_meta = alert_icon.getItemMeta();
		alert_icon_meta.setDisplayName("" + ChatColor.WHITE + "적용");
		alert_icon_meta.setLore(Arrays.asList("" + ChatColor.WHITE + "적용"));
		inv.setItem(13, alert_icon);
		
		ItemStack accept_icon = new ItemStack(Material.NAME_TAG, 1);
		ItemMeta accept_icon_meta = accept_icon.getItemMeta();
		accept_icon_meta.setDisplayName("" + ChatColor.GREEN + ChatColor.BOLD + "적용");
		inv.setItem(29, accept_icon);
		
		ItemStack deny_icon = new ItemStack(Material.NAME_TAG, 1);
		ItemMeta deny_icon_meta = deny_icon.getItemMeta();
		deny_icon_meta.setDisplayName("" + ChatColor.RED + ChatColor.BOLD + "취소");
		inv.setItem(33, deny_icon);
		
		this.commander.openInventory(inv);
		return true;
	}
	
	
	
	// 페이지뷰 생성
	public Inventory createPageWindow(int size, String title, ArrayList<ItemStack> contents) {
		return createPageWindow(size, title, contents, 1);
	}
	public Inventory createPageWindow(int size, String title, ArrayList<ItemStack> contents, int page) {
		// 18 <= size <= 54 이어야 함
		if (size < 18 || 54 < size) {
			return null;
		}
		
		float unit = (float) (size - 9);
		int lastPage = (int) Math.ceil(contents.size() / unit);
		 
		if (page > lastPage) {
			return null;
		}
		
		if (page <= 0) {
			page = 1;
		}
		
		int start = (int) (unit * (page - 1));     
		
		Inventory inv = createWindow(size, title, contents, start);
		if (inv == null) {
			return null;
		}

		// 이전페이지 버튼
		ItemStack prev_icon = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta prev_icon_meta = (SkullMeta) prev_icon.getItemMeta();
		prev_icon_meta.setOwnerProfile(createProfile("77334cddfab45d75ad28e1a47bf8cf5017d2f0982f6737da22d4972952510661"));
		prev_icon_meta.setDisplayName(ChatColor.GRAY + "이전페이지");
		prev_icon_meta.setLore(Arrays.asList(ChatColor.GOLD + "(" + ChatColor.GREEN + page+ ChatColor.GOLD + " / " + lastPage + ")"));
		prev_icon.setItemMeta(prev_icon_meta);
		inv.setItem(3, prev_icon);
		
		// 다음페이지 버튼
		ItemStack next_icon = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta next_icon_meta = (SkullMeta) next_icon.getItemMeta();
		next_icon_meta.setOwnerProfile(createProfile("e7742034f59db890c8004156b727c77ca695c4399d8e0da5ce9227cf836bb8e2"));
		next_icon_meta.setDisplayName(ChatColor.GRAY + "다음페이지");
		next_icon_meta.setLore(Arrays.asList(ChatColor.GOLD + "(" + ChatColor.GREEN + page + ChatColor.GOLD + " / " + lastPage + ")"));
		next_icon.setItemMeta(next_icon_meta);
		inv.setItem(5, next_icon);
		
		return inv;
	}
	
	
	
	// 일반 뷰 생성
	public Inventory createWindow(int size, String title) {
		return createWindow(size, title, new ArrayList<ItemStack>(), 0);
	}
	public Inventory createWindow(int size, String title, ArrayList<ItemStack> contents) {
		return createWindow(size, title, contents, 0);
	}
	public Inventory createWindow(int size, String title, ArrayList<ItemStack> contents, int start) {
		if (size % 9 != 0 || size < 9 || 54 < size) {
			return null;
		}

		if (start >= contents.size()) {
			start = 0;
		}

		Inventory inv = Bukkit.createInventory(null, size, title);
		
		// stack 길이가 0이면 뒤로가기 icon 없음
		if (this.work_stack.size() > 0) {
			// 뒤로가기 버튼
			ItemStack backward_icon = new ItemStack(Material.PLAYER_HEAD, 1);
			SkullMeta backward_icon_meta = (SkullMeta) backward_icon.getItemMeta();
			backward_icon_meta.setOwnerProfile(createProfile("1fc2611fbabe4e799062f6b470ac5ae727e32ef00d2b115d38656e341c128936"));
			backward_icon_meta.setDisplayName(ChatColor.GRAY + "뒤로가기");
			backward_icon.setItemMeta(backward_icon_meta);
			inv.setItem(0, backward_icon);
		}
		
		// 닫기 버튼
		ItemStack close_icon = new ItemStack(Material.BARRIER, 1);
		ItemMeta close_icon_meta = close_icon.getItemMeta();
		close_icon_meta.setDisplayName(ChatColor.RED + "닫기");
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
