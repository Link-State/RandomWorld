package main;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

public class InventoryGUI {
	public static final TreeMap<String, OfflinePlayer> SORTED_PLAYERS = new TreeMap<String, OfflinePlayer>();
	public static final TreeMap<String, EntityType> SORTED_LIVING_ENTITIES = new TreeMap<String, EntityType>();
	
	private Player commander;

	private String currentTargetType; // user, entity, default
	private String currentTargetName; // player : UUID OR entity : NAME OR default : DEFAULT
	private RandomEvent currentTargetRandomEvent; // 변경하려는 대상의 랜덤이벤트
	private String currentEventType; // ITEM, POTION, ENCHANT
	private String currentEventName; // PICKUP, AREA_EFFECT_CLOUD, SMITE, ...
	private String currentSettingType; // EXCEPT, BAN, MAX
	private final ArrayList<String> stack =  new ArrayList<String>();
	private final HashMap<String, ArrayList<String>> modifying_StringValue = new HashMap<String, ArrayList<String>>(); // String 값 저장 시 여기에 저장
	private final HashMap<String, Integer> modifying_IntValue = new HashMap<String, Integer>(); // int값 저장 시 여기에 저장
	
	
	public InventoryGUI(Player p) {
		this.commander = p;
		
		OfflinePlayer[] all_player = Bukkit.getOfflinePlayers();
		for (OfflinePlayer player : all_player) {
			if (RandomEvent.hasEntity(player.getUniqueId().toString())) {
				SORTED_PLAYERS.put(player.getName(), player);
			}
		}
		

		EntityType[] all_entity = EntityType.values();
		
		for (EntityType entity : all_entity) {
			if (!entity.isAlive() || entity.equals(EntityType.PLAYER)) {
				continue;
			}
			
			SORTED_LIVING_ENTITIES.put(entity.name(), entity);
		}
	}

	// 설정창0 : 플레이어/엔티티/공통 선택 (해당 플레이어가 op 또는 super일 경우에만 활성화)
	// 설정창1 : (플레이어/엔티티 선택 시) 플레이어/엔티티 선택 (해당 플레이어가 op 또는 super일 경우에만 활성화)
	// 설정창2 : 아이템/포션/인첸트 선택 (뒤로가기 버튼) (해당 플레이어가 Admin일 경우에만 활성화)

	// 설정창3 : 이벤트(e.g : PICKUP) 아이콘 선택 (뒤로가기 버튼, 이전/다음페이지 버튼)
	
	// 설정창4-1 : _BAN, _EXCEPT일 경우, 책 GUI open, 각 라인하나 당 옵션 하나 (open시 엔티티 설정 불러오기)
	// 설정창4-2 : _MAX일 경우, 숫자 입력받기 (+1, +5, +10, -1, -5, -10 아이템아이콘 만들어서 눌러서 숫자 조절하는 방식, 최대는 포션 갯수만큼)
	// 설정창5 : 책 닫을 시, 저장 및 적용 여부 창 (강제종료 시 저장안함)
	
	
	// 개체 타입을 선택하는 GUI
	public boolean openEntityTypeSelect() {

		Inventory inv = Bukkit.createInventory(null, 45, "개체 종류 선택");
		
		// 닫기 버튼
		ItemStack close_icon = new ItemStack(Material.BARRIER, 1);
		ItemMeta close_icon_meta = close_icon.getItemMeta();
		close_icon_meta.setDisplayName(ChatColor.RED + "닫기");
		close_icon.setItemMeta(close_icon_meta);
		
		// 플레이어
		ItemStack player_icon = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta player_icon_meta = (SkullMeta) player_icon.getItemMeta();
		player_icon_meta.setDisplayName(ChatColor.WHITE + "플레이어");
		player_icon.setItemMeta(player_icon_meta);
		
		// 엔티티
		ItemStack entity_icon = new ItemStack(Material.SKELETON_SKULL, 1);
		SkullMeta entity_icon_meta = (SkullMeta) entity_icon.getItemMeta();
		entity_icon_meta.setDisplayName(ChatColor.WHITE + "엔티티");
		entity_icon.setItemMeta(entity_icon_meta);
		
		// 공통
		ItemStack default_icon = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta default_icon_meta = (SkullMeta) default_icon.getItemMeta();
		PlayerProfile default_icon_profile = createProfile("c69196b330c6b8962f23ad5627fb6ecce472eaf5c9d44f791f6709c7d0f4dece");
		default_icon_meta.setOwnerProfile(default_icon_profile);
		default_icon_meta.setDisplayName(ChatColor.WHITE + "공통");
		default_icon.setItemMeta(default_icon_meta);
		
		
		inv.setItem(8, close_icon);
		inv.setItem(20, player_icon);
		inv.setItem(22, entity_icon);
		inv.setItem(24, default_icon);
		
		this.commander.openInventory(inv);
		return true;
	}
	
	// 개체 선택 GUI
	public boolean openEntitySelect(Boolean isPlayer, int start) {
		Inventory inv = Bukkit.createInventory(null, 54, "개체 선택");
		
		int end = start + 45;
		// 현재 페이지 = 전체길이 / 45
		int currentPage = 1;
		int lastPage = 1;

		// 뒤로가기 버튼
		ItemStack backward_icon = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta backward_icon_meta = (SkullMeta) backward_icon.getItemMeta();
		backward_icon_meta.setOwnerProfile(createProfile("1fc2611fbabe4e799062f6b470ac5ae727e32ef00d2b115d38656e341c128936"));
		backward_icon_meta.setDisplayName(ChatColor.GRAY + "뒤로가기");
		backward_icon.setItemMeta(backward_icon_meta);
		inv.setItem(0, backward_icon);
		
		// 닫기 버튼
		ItemStack close_icon = new ItemStack(Material.BARRIER, 1);
		ItemMeta close_icon_meta = close_icon.getItemMeta();
		close_icon_meta.setDisplayName(ChatColor.RED + "닫기");
		close_icon.setItemMeta(close_icon_meta);
		inv.setItem(8, close_icon);
		
		
		if (isPlayer) {
			
			// 플레이어인 경우 검색버튼 활성화
			ItemStack search_icon = new ItemStack(Material.SPYGLASS, 1);
			ItemMeta search_icon_meta = search_icon.getItemMeta();
			search_icon_meta.setDisplayName(ChatColor.GRAY + "검색");
			search_icon.setItemMeta(search_icon_meta);
			inv.setItem(4, search_icon);
			
			ArrayList<String> all_player = new ArrayList<String>( new TreeSet<String>(SORTED_PLAYERS.keySet()) );
			
			currentPage = ((int) (start / 45.0)) + 1;
			lastPage = (int) Math.ceil(all_player.size() / 45.0);
			if (end > all_player.size()) {
				end = all_player.size();
			}
			
			for (int i = start; i < end; i++) {
				String player = all_player.get(i);
				
				ItemStack player_icon = new ItemStack(Material.PLAYER_HEAD, 1);
				SkullMeta player_icon_meta = (SkullMeta) player_icon.getItemMeta();
				player_icon_meta.setOwningPlayer(SORTED_PLAYERS.get(player));
				player_icon_meta.setDisplayName(ChatColor.WHITE + player);
				player_icon.setItemMeta(player_icon_meta);
				inv.setItem(i - start + 9, player_icon);
			}
			
		}
		else {
			ArrayList<String> all_living_entity = new ArrayList<String>( new TreeSet<String>(SORTED_LIVING_ENTITIES.keySet()) );
			
			currentPage = ((int) (start / 45.0)) + 1;
			lastPage = (int) Math.ceil(all_living_entity.size() / 45.0);
			
			if (end > all_living_entity.size()) {
				end = all_living_entity.size();
			}
			
			for (int i = start; i < end; i++) {
				String entity = all_living_entity.get(i);
				
				ItemStack entity_icon = new ItemStack(Material.SKELETON_SKULL, 1);
				SkullMeta entity_icon_meta = (SkullMeta) entity_icon.getItemMeta();
				entity_icon_meta.setDisplayName(ChatColor.WHITE + entity);
				entity_icon.setItemMeta(entity_icon_meta);
				inv.setItem(i - start + 9, entity_icon);
			}
		}
		
		
		// 이전페이지 버튼
		ItemStack prev_icon = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta prev_icon_meta = (SkullMeta) prev_icon.getItemMeta();
		prev_icon_meta.setOwnerProfile(createProfile("77334cddfab45d75ad28e1a47bf8cf5017d2f0982f6737da22d4972952510661"));
		prev_icon_meta.setDisplayName(ChatColor.GRAY + "이전페이지");
		prev_icon_meta.setLore(Arrays.asList(ChatColor.GOLD + "(" + ChatColor.GREEN + currentPage+ ChatColor.GOLD + " / " + lastPage + ")"));
		prev_icon.setItemMeta(prev_icon_meta);
		inv.setItem(3, prev_icon);
		
		// 다음페이지 버튼
		ItemStack next_icon = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta next_icon_meta = (SkullMeta) next_icon.getItemMeta();
		next_icon_meta.setOwnerProfile(createProfile("e7742034f59db890c8004156b727c77ca695c4399d8e0da5ce9227cf836bb8e2"));
		next_icon_meta.setDisplayName(ChatColor.GRAY + "다음페이지");
		next_icon_meta.setLore(Arrays.asList(ChatColor.GOLD + "(" + ChatColor.GREEN + currentPage + ChatColor.GOLD + " / " + lastPage + ")"));
		next_icon.setItemMeta(next_icon_meta);
		inv.setItem(5, next_icon);
		
		
		this.commander.openInventory(inv);
		return true;
	}
	
	// 이벤트 종류 선택하는 GUI
	public boolean openEventTypeSelect() {
		
		Inventory inv = Bukkit.createInventory(null, 45, "이벤트 종류 선택");
		
		// 뒤로가기 버튼
		ItemStack backward_icon = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta backward_icon_meta = (SkullMeta) backward_icon.getItemMeta();
		backward_icon_meta.setOwnerProfile(createProfile("1fc2611fbabe4e799062f6b470ac5ae727e32ef00d2b115d38656e341c128936"));
		backward_icon_meta.setDisplayName(ChatColor.GRAY + "뒤로가기");
		backward_icon.setItemMeta(backward_icon_meta);
		
		// 닫기 버튼
		ItemStack close_icon = new ItemStack(Material.BARRIER, 1);
		ItemMeta close_icon_meta = close_icon.getItemMeta();
		close_icon_meta.setDisplayName(ChatColor.RED + "닫기");
		close_icon.setItemMeta(close_icon_meta);
		
		// 아이템
		ItemStack item_icon = new ItemStack(Material.CRAFTING_TABLE, 1);
		ItemMeta item_icon_meta = item_icon.getItemMeta();
		item_icon_meta.setDisplayName(ChatColor.WHITE + "아이템");
		item_icon.setItemMeta(item_icon_meta);
		
		// 포션
		ItemStack potion_icon = new ItemStack(Material.POTION, 1);
		ItemMeta potion_icon_meta = potion_icon.getItemMeta();
		potion_icon_meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		potion_icon_meta.setDisplayName(ChatColor.WHITE + "포션효과");
		potion_icon.setItemMeta(potion_icon_meta);
		
		// 인첸트
		ItemStack enchant_icon = new ItemStack(Material.ENCHANTED_BOOK, 1);
		ItemMeta enchant_icon_meta = enchant_icon.getItemMeta();
		enchant_icon_meta.setDisplayName(ChatColor.WHITE + "인첸트");
		enchant_icon.setItemMeta(enchant_icon_meta);
		

		inv.setItem(0, backward_icon);
		inv.setItem(8, close_icon);
		inv.setItem(20, item_icon);
		inv.setItem(22, potion_icon);
		inv.setItem(24, enchant_icon);
		
		this.commander.openInventory(inv);
		return true;
	}
	
	// 이벤트 선택 GUI
	public boolean openEventSelect(String name, int start, int type) {
		
		/*
		 * Event Type
		 * 
		 * 0 - item
		 * 1 - potion
		 * 2 - enchant
		 * 
		 */

		// Main의 각 ITEM_FIELD / POTION_FIELD / ENCHANT_FIELD 중, 설정창2에서 선택한 이벤트랑 알맞는 필드에서 키값 정렬하여 가져오고, 해당 플레이어의 이벤트 필드 가져와서 보여주기.
		// PICKUP, ENCHANTING등 (_EXCEPT, _BAN 말고) 아이콘 띄워서 lore에 활성화 여부 보여주기. 우클릭해서 활성화/비활성화 토글, 좌클릭해서 세부설정
		
		Inventory inv = Bukkit.createInventory(null, 54, "이벤트 선택");

		int end = start + 45;
		int currentPage = 1;
		int lastPage = 1;
		
		// ArrayList<String> fields = 선택한 이벤트에 알맞게 이벤트이름 알파벳순으로 가져오기
		TreeSet<String> all_field_set = null;
		ArrayList<String> all_field = null;
		TreeMap<String, Boolean> user_field_map = new TreeMap<String, Boolean>();
		
		switch(type) {
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
		
		RandomEvent re = new RandomEvent(name);
		
		for (String field : all_field) {
			user_field_map.put(field, re.getActivate(field));
		}
		
		// currentPage, lastPage 계산
		currentPage = ((int) (start / 45.0)) + 1;
		lastPage = (int) Math.ceil(all_field.size() / 45.0);
		if (end > all_field.size()) {
			end = all_field.size();
		}
		
		
		// 뒤로가기 버튼
		ItemStack backward_icon = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta backward_icon_meta = (SkullMeta) backward_icon.getItemMeta();
		backward_icon_meta.setOwnerProfile(createProfile("1fc2611fbabe4e799062f6b470ac5ae727e32ef00d2b115d38656e341c128936"));
		backward_icon_meta.setDisplayName(ChatColor.GRAY + "뒤로가기");
		backward_icon.setItemMeta(backward_icon_meta);
		inv.setItem(0, backward_icon);

		// 닫기 버튼
		ItemStack close_icon = new ItemStack(Material.BARRIER, 1);
		ItemMeta close_icon_meta = close_icon.getItemMeta();
		close_icon_meta.setDisplayName(ChatColor.RED + "닫기");
		close_icon.setItemMeta(close_icon_meta);
		inv.setItem(8, close_icon);
		
		
		// 페이지가 1개면 페이지 이동버튼 생성 안함
		if (lastPage > 1) {
			// 이전페이지 버튼
			ItemStack prev_icon = new ItemStack(Material.PLAYER_HEAD, 1);
			SkullMeta prev_icon_meta = (SkullMeta) prev_icon.getItemMeta();
			prev_icon_meta.setOwnerProfile(createProfile("77334cddfab45d75ad28e1a47bf8cf5017d2f0982f6737da22d4972952510661"));
			prev_icon_meta.setDisplayName(ChatColor.GRAY + "이전페이지");
			prev_icon_meta.setLore(Arrays.asList(ChatColor.GOLD + "(" + ChatColor.GREEN + currentPage+ ChatColor.GOLD + " / " + lastPage + ")"));
			prev_icon.setItemMeta(prev_icon_meta);
			inv.setItem(3, prev_icon);
			
			// 다음페이지 버튼
			ItemStack next_icon = new ItemStack(Material.PLAYER_HEAD, 1);
			SkullMeta next_icon_meta = (SkullMeta) next_icon.getItemMeta();
			next_icon_meta.setOwnerProfile(createProfile("e7742034f59db890c8004156b727c77ca695c4399d8e0da5ce9227cf836bb8e2"));
			next_icon_meta.setDisplayName(ChatColor.GRAY + "다음페이지");
			next_icon_meta.setLore(Arrays.asList(ChatColor.GOLD + "(" + ChatColor.GREEN + currentPage + ChatColor.GOLD + " / " + lastPage + ")"));
			next_icon.setItemMeta(next_icon_meta);
			inv.setItem(5, next_icon);
		}
		
		
		// all_field으로 전체 이벤트 아이콘 생성
		// 그리고 키값을 user_field_map에 넣어서 활성화여부 lore에 저장하기
		for (int i = start; i < end; i++) {
			String field = all_field.get(i);
			
			ItemStack field_icon = new ItemStack(Material.PLAYER_HEAD, 1);
			SkullMeta field_icon_meta = (SkullMeta) field_icon.getItemMeta();
			field_icon_meta.setOwnerProfile(createProfile("e7742034f59db890c8004156b727c77ca695c4399d8e0da5ce9227cf836bb8e2"));
			field_icon_meta.setDisplayName(ChatColor.GRAY + field);
			
			Boolean activate = user_field_map.get(field);
			field_icon_meta.setLore(Arrays.asList(
					ChatColor.GRAY + "상태 : " + (activate ? ChatColor.GOLD + "활성화" : ChatColor.RED + "비활성화"),
					ChatColor.GRAY + "좌클릭해서 " + (activate ? ChatColor.RED + "비활성화" : ChatColor.GOLD + "활성화"),
					ChatColor.GRAY + "우클릭해서 이벤트 세부 설정"
				));
			field_icon.setItemMeta(field_icon_meta);
			inv.setItem(i - start + 9, field_icon);
		}
		
		this.commander.openInventory(inv);
		return true;
	}
	
	// 이벤트 세부 설정 GUI
	public boolean openEventDetailSetting(String name, String eventName) {
		
		Inventory inv = Bukkit.createInventory(null, 45, "이벤트 세부 설정");
		
		RandomEvent re = new RandomEvent(name);

		ArrayList<String> excepts = re.getActivateEvents(eventName + "_EXCEPT");
		ArrayList<String> bans = re.getActivateEvents(eventName + "_BAN");
		int max = -1;

		Integer category = RandomWorldCommand.SETTING_CATEGORY.get(eventName + "_MAX");
		if (category != null && category == 4) {
			max = re.getActivateMaxEvents(eventName + "_MAX");
		}

		// 뒤로가기 버튼
		ItemStack backward_icon = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta backward_icon_meta = (SkullMeta) backward_icon.getItemMeta();
		backward_icon_meta.setOwnerProfile(createProfile("1fc2611fbabe4e799062f6b470ac5ae727e32ef00d2b115d38656e341c128936"));
		backward_icon_meta.setDisplayName(ChatColor.GRAY + "뒤로가기");
		backward_icon.setItemMeta(backward_icon_meta);
		inv.setItem(0, backward_icon);

		// 닫기 버튼
		ItemStack close_icon = new ItemStack(Material.BARRIER, 1);
		ItemMeta close_icon_meta = close_icon.getItemMeta();
		close_icon_meta.setDisplayName(ChatColor.RED + "닫기");
		close_icon.setItemMeta(close_icon_meta);
		inv.setItem(8, close_icon);
		
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
		if (max != -1) {
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
		
		this.commander.openInventory(inv);
		return true;
	}
	
	
	
	// 값 수정 GUI
	public boolean openEditGUI(int type, int page) {

		/*
		 * Event Type
		 * 
		 * 0 - item
		 * 1 - potion
		 * 2 - enchant
		 * 
		 */
		
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		
		switch (type) {
			// ITEM이면 모든 아이템을 items에 넣기
			case 0 : {
				Material[] materials = Material.values();
				
				// this.currentTargetRandomEvent 이용하기
				
				for (Material material : materials) {
					if (!material.isItem()) {
						continue;
					}
					
					ItemStack stack = new ItemStack(material, 1);
					ItemMeta meta = stack.getItemMeta();
					meta.setLore(Arrays.asList("", ""));
					
					items.add(stack);
				}
				break;
			}
			// POTION이면 모든 포션효과를 item에 넣기
			case 1 : {
				// 포션 베이스 이용할까?
				break;
			}
			// ENCHANT이면 모든 인첸트를 items에 넣기
			case 2 : {
				// 싹다 똑같은 인첸트 북
				break;
			}
		}
		
		Inventory inv = createPageWindow(54, "이벤트 세부 설정", page, items);
		
		this.commander.openInventory(inv);
		return true;
	}
	public boolean openEditIntGUI() {
		Inventory inv = createWindow(45, "이벤트 세부 설정");
		
		
		
		this.commander.openInventory(inv);
		return true;
	}
	
	
	public Inventory createPageWindow(int size, String title, int page, ArrayList<ItemStack> contents) {
		// 18 <= size <= 54 이어야 함
		if (size < 18 || 54 < size) {
			return null;
		}
		
		float unit = (float) (size - 9);
		int lastPage = (int) Math.ceil(contents.size() / unit);
		 
		if (page > lastPage) {
			return null;
		}
		
		int start = (int) (unit * (page - 1));     
		
		Inventory inv = createWindow(size, title, contents, start);

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
		if (this.stack.size() > 0) {
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
