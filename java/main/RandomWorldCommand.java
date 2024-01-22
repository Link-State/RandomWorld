package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Registry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.StringUtil;

public class RandomWorldCommand implements TabCompleter {
	private final ArrayList<String> COMMANDS1 = new ArrayList<String>(Arrays.asList("add", "remove", "set", "switch", "setting", "permission"));
	private final ArrayList<String> TARGET = new ArrayList<String>(Arrays.asList("player", "entity", "default"));
	private final ArrayList<String> PERMISSION_LEVEL = new ArrayList<String>(Arrays.asList("user", "admin", "super"));
	private final ArrayList<String> PLAYERS = new ArrayList<String>();
	private final ArrayList<String> ENTITIES = new ArrayList<String>();
	private final ArrayList<String> SETTINGS = new ArrayList<String>();
	public static final HashMap<String, Integer> SETTING_CATEGORY = new HashMap<String, Integer>();
	public static final ArrayList<String> ITEMS = new ArrayList<String>();
	public static final ArrayList<String> POTIONS = new ArrayList<String>();
	public static final ArrayList<String> ENCHANTS = new ArrayList<String>();
	
	/* 
	 * SETTING_CATEGORY
	 *  
	 * 0 - ITEM EXCEPT FIELD
	 * 1 - ITEM BAN FIELD
	 * 2 - POTION EXCEPT FIELD
	 * 3 - POTION BAN FIELD
	 * 4 - POTION MAX FIELD
	 * 5 - ENCHANT EXCEPT FIELD
	 * 6 - ENCHANT BAN FIELD
	 * 
	 */
	
	public RandomWorldCommand() {
		// 현재 접속 중인 플레이어 이름 목록
		Iterator<? extends Player> online_player_list = Bukkit.getOnlinePlayers().iterator();
		while (online_player_list.hasNext()) {
			PLAYERS.add(online_player_list.next().getName());
		}
		
		// 현재 접속하지 않은 플레이어 이름 목록
		OfflinePlayer[] offline_player_list = Bukkit.getOfflinePlayers();
		for (OfflinePlayer player : offline_player_list) {
			PLAYERS.add(player.getName());
		}
		
		// 생명이 있는 엔티티 이름 목록
		EntityType[] entities = EntityType.values();
		for (EntityType entity : entities) {
			// 플레이어를 제외한 살아있는 엔티티 중에서
			if (!entity.isAlive() || entity.equals(EntityType.PLAYER)) {
				continue;
			}
			ENTITIES.add(entity.name());
		}
		
		// 각 이벤트 설정
		Iterator<String> keys;
		
		keys = Main.POTION_FIELD.keySet().stream().sorted().iterator();
		while (keys.hasNext()) {
			String field_name = keys.next();
			SETTINGS.add(field_name + "_EXCEPT");
			SETTINGS.add(field_name + "_BAN");
			SETTINGS.add(field_name + "_MAX");
			SETTING_CATEGORY.put(field_name + "_EXCEPT", 2);
			SETTING_CATEGORY.put(field_name + "_BAN", 3);
			SETTING_CATEGORY.put(field_name + "_MAX", 4);
		}
		
		keys = Main.ENCHANT_FIELD.keySet().stream().sorted().iterator();
		while (keys.hasNext()) {
			String field_name = keys.next();
			SETTINGS.add(field_name + "_EXCEPT");
			SETTINGS.add(field_name + "_BAN");
			SETTING_CATEGORY.put(field_name + "_EXCEPT", 5);
			SETTING_CATEGORY.put(field_name + "_BAN", 6);
		}
		
		keys = Main.ITEM_FIELD.keySet().stream().sorted().iterator();
		while (keys.hasNext()) {
			String field_name = keys.next();
			System.out.println(field_name);
			SETTINGS.add(field_name + "_EXCEPT");
			SETTINGS.add(field_name + "_BAN");
			SETTING_CATEGORY.put(field_name + "_EXCEPT", 0);
			SETTING_CATEGORY.put(field_name + "_BAN", 1);
		}

		Iterator<Material> materials = Registry.MATERIAL.iterator();
		Iterator<PotionEffectType> effects = Registry.EFFECT.iterator();
		Iterator<Enchantment> enchantments = Registry.ENCHANTMENT.iterator();
		
		while (materials.hasNext()) {
			Material material = materials.next();
			if (!material.isItem()) {
				continue;
			}
			ITEMS.add(material.name().toUpperCase());
		}
		
		while (effects.hasNext()) {
			POTIONS.add(effects.next().getKey().getKey().toUpperCase());
		}
		
		while (enchantments.hasNext()) {
			ENCHANTS.add(enchantments.next().getKey().getKey().toUpperCase());
		}
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		final List<String> completions = new ArrayList<>();
		
		// 명령어 자동 완성
		switch (args.length) {
			case 1 : {
				StringUtil.copyPartialMatches(args[0], COMMANDS1, completions);
				break;
			}
			case 2 : {
				if (args[0].equals("add") || args[0].equals("remove") || args[0].equals("set") || args[0].equals("switch")) {
					StringUtil.copyPartialMatches(args[1], TARGET, completions);
				}
				else if (args[0].equals("permission")) {
					StringUtil.copyPartialMatches(args[1], PLAYERS, completions);
				}
				break;
			}
			case 3 : {
				if (args[0].equals("add") || args[0].equals("remove") || args[0].equals("set") || args[0].equals("switch")) {
					if (args[1].equals("player")) {
						StringUtil.copyPartialMatches(args[2], PLAYERS, completions);
					}
					else if (args[1].equals("entity")) {
						StringUtil.copyPartialMatches(args[2], ENTITIES, completions);
					}
					else if (args[1].equals("default")) {
						StringUtil.copyPartialMatches(args[2], new ArrayList<String>(Arrays.asList("default")), completions);
					}
				}
				else if (args[0].equals("permission")) {
					StringUtil.copyPartialMatches(args[2], PERMISSION_LEVEL, completions);
				}
				break;
			}
			case 4 : {
				if ((args[1].equals("player") && PLAYERS.indexOf(args[2]) >= 0) || 
					(args[1].equals("entity") && ENTITIES.indexOf(args[2]) >= 0) ||
					args[1].equals("default") && args[2].equals("default")) {
					StringUtil.copyPartialMatches(args[3], SETTINGS, completions);
				} 
				break;
			}
			default : {
				if (args[0].equals("switch")) {
					break;
				}
				if (args.length < 5) {
					break;
				}
				
				Integer category = SETTING_CATEGORY.get(args[3]);
				if (category == null) {
					break;
				}

				// Item 관련이면
				if (category >= 0 && category <= 1) {
					StringUtil.copyPartialMatches(args[args.length-1], ITEMS, completions);
				}
				// Potion 관련이면
				else if (category >= 2 && category <= 3) {
					StringUtil.copyPartialMatches(args[args.length-1], POTIONS, completions);
				}
				// Enchant 관련이면
				else if (category >= 5 && category <= 6) {
					StringUtil.copyPartialMatches(args[args.length-1], ENCHANTS, completions);
				}
			}
		}
		return completions;
	}
	
	
	/*
	 * Permission Rank
	 * 
	 * 0 - not this plugin user
	 * 1 - user
	 * 2 - admin
	 * 3 - super
	 * 4 - op
	 * 
	 */
	// 플레이어 권한 수준 반환
	public static int getRank(UUID uuid) {
		OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
		
		// OP 유저
		if (p.isOp()) {
			return 4;
		}
		
		if (!RandomEvent.hasEntity(p.getUniqueId().toString())) {
			return 0;
		}

		RandomEvent re = Main.REGISTED_PLAYER.get(p.getUniqueId());
		if (re == null) {
			re = new RandomEvent(p.getUniqueId().toString());
		}
		
		// 슈퍼 유저
		if (re.isSuper()) {
			return 3;
		}
		
		if (re.isAdmin()) {
			return 2;
		}
		
		// 일반 유저
		return 1;
	}
	public static int getRank(String name) {
		Player p = Bukkit.getPlayer(name);
		if (p != null) {
			return getRank(p.getUniqueId());
		}
		
		ArrayList<OfflinePlayer> offline_players = new ArrayList<OfflinePlayer>(Arrays.asList(Bukkit.getOfflinePlayers()));
		
		for (OfflinePlayer offline_player : offline_players) {
			if (offline_player.getName().equals(name)) {
				return getRank(offline_player.getUniqueId());
			}
		}
		
		return 0;
	}
	public static int getRank(Player player) {
		return getRank(player.getUniqueId());
	}
	
	
	// 설정 GUI창 열기
	public static boolean openSettingGUI(Player player, int rank) {
		
		ArrayList<String> stack = new ArrayList<String>();
		stack.add(player.getName());
		InventoryGUI gui = new InventoryGUI();
		Inventory inv = null;
		switch (rank) {
			// admin
			case 2 : {
				stack.add("player");
				stack.add(player.getName());
				inv = gui.openEventTypeSelect(stack);

				break;
			}
			// op 또는 super
			case 3 :
			case 4 : {
				inv = gui.openEntityTypeSelect();
				break;
			}
		}
		
		if (inv == null) {
			return false;
		}
		
		player.openInventory(inv);
		
		return true;
	}
	
	
	// 이벤트 필터링 적용
	public static boolean setEvents(CommandSender sender, String cmd_option, String entityType, String entityName, String eventName, ArrayList<String> fields) {
		// fields의 길이가 0이면 이벤트 공란으로 설정
		
		RandomEvent re;
		if (entityType.equals("entity")) {
			EntityType entity_type;
			
			try {
				entity_type = EntityType.valueOf(entityName);
			}
			catch (IllegalArgumentException err) {
				sender.sendMessage(ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "해당 개체가 존재하지 않습니다.");
				return false;
			}
			
			re = Main.REGISTED_ENTITY.get(entity_type);
		}
		else if (entityType.equals("player")) {
			OfflinePlayer p = Bukkit.getPlayer(entityName);
			if (p == null) {
				OfflinePlayer[] players = Bukkit.getOfflinePlayers();
				
				for (OfflinePlayer off_p : players) {
					if (off_p.getName().equals(entityName)) {
						p = off_p;
						break;
					}
				}
				
				if (p == null) {
					sender.sendMessage(ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "해당 플레이어가 존재하지 않습니다.");
					return false;
				}
			}

			re = Main.REGISTED_PLAYER.get(p.getUniqueId());
			if (re == null) {
				re = new RandomEvent(p.getUniqueId().toString());
			}
		}
		else if (entityType.equals("default") && entityName.equals("default")) {
			re = Main.DEFAULT;
		}
		else {
			sender.sendMessage(ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "/randomworld <add | remove | set> <entity | player> <target_name> <event_name> <values...>");
			return false;
		}
		
		String eventName_prefix = eventName.replace("_EXCEPT", "").replace("_BAN", "").replace("_MAX", "");
		if (!re.getActivate(eventName_prefix)) {
			sender.sendMessage(ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "존재하지 않거나 비활성화 된 이벤트 입니다.");
			return false;
		}
		
		Integer category = SETTING_CATEGORY.get(eventName);
		if (category == null) {
			sender.sendMessage(ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "존재하지 않는 이벤트 입니다.");
			return false;
		}
		
		
		
		// _MAX인 경우, add나 remove가 안되고 set으로만 할 수 있도록.
		if (category == 4) {
			if (!cmd_option.equals("set")) {
				sender.sendMessage(ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "해당 이벤트는 set 명령만 허용됩니다.");
				return false;
			}
			
			int max_value = -1;
			if (fields.size() >= 1) {
				try {
					max_value = Integer.parseInt(fields.get(0));
				}
				catch (NumberFormatException err) {
					sender.sendMessage(ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "해당 이벤트는 정수값만 허용됩니다.");
					return false;
				}
			}
			re.write(eventName, max_value);
			re.setEffectMax(eventName_prefix, max_value);
			sender.sendMessage(ChatColor.GREEN + "[RandomWorld] : " + ChatColor.AQUA + "수정이 완료되었습니다.");
			return true;
		}
		
		
		
		// 
		ArrayList<String> default_list = Main.DEFAULT.getActivateEvents(eventName);
		Set<String> default_set = new HashSet<String>(default_list);
		ArrayList<String> origin_list = re.getActivateEvents(eventName);
		Set<String> origin_set = new HashSet<String>(origin_list);
		ArrayList<String> apply = null;
		Set<String> apply_set = null;
		ArrayList<String> event_type = null;
		
		switch(category) {
			case 0 :
			case 1 :
			{
				event_type = ITEMS;
				break;
			}
			case 2:
			case 3: {
				event_type = POTIONS;
				break;
			}
			case 5:
			case 6: {
				event_type = ENCHANTS;
				break;
			}
		}

		if (fields.size() == 1 && fields.get(0).equals("*")) {
			apply = new ArrayList<String>(event_type);
		}
		else if (fields.size() == 1 && fields.get(0).equals("-")) {
			// 공통값 가져오기
			apply = new ArrayList<String>(default_list);
		}
		else {
			apply = new ArrayList<String>(fields);
		}
		
		apply_set = new HashSet<String>(apply);
		
		// 
		if (cmd_option.equals("add")) {
			apply_set.addAll(origin_set);
			apply = new ArrayList<String>(apply_set);
		}
		else if (cmd_option.equals("remove")) {
			origin_set.removeAll(apply_set);
			apply = new ArrayList<String>(origin_set);
			apply_set = new HashSet<String>(apply);
			origin_set = new HashSet<String>(origin_list);
		}
		else if (!cmd_option.equals("set")) {
			sender.sendMessage(ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "/randomworld <add | remove | set> <entity | user> <target_name> <event_name> <values...>");
			return false;
		}
		
		// origin_set과 apply와 내용물이 같은 경우
		if (origin_set.equals(apply_set)) {
			sender.sendMessage(ChatColor.GREEN + "[RandomWorld] : " + ChatColor.YELLOW + "변경사항이 없습니다.");
			return false;
		}

		String file_context = "";
		if (default_set.equals(apply_set)) {
			file_context = "-";
		}
		else if (apply.size() >= event_type.size()) {
			file_context = "*";
		}
		else if (apply.size() <= 0) {
			file_context = "";
		}
		else {
			file_context = String.join(", ", apply);
		}
		
		// 파일 및 해시값 수정
		switch (category) {
			case 0 : {
				re.write(eventName, file_context);
				re.setItemFilter(eventName_prefix, file_context);
				break;
			}
			case 1 : {
				re.write(eventName, file_context);
				re.setItemBan(eventName_prefix, file_context);
				break;
			}
			case 2 : {
				re.write(eventName, file_context);
				re.setEffectFilter(eventName_prefix, file_context);
				break;
			}
			case 3 : {
				re.write(eventName, file_context);
				re.setEffectBan(eventName_prefix, file_context);
				break;
			}
			case 5 : {
				re.write(eventName, file_context);
				re.setEnchantFilter(eventName_prefix, file_context);
				break;
			}
			case 6 : {
				re.write(eventName, file_context);
				re.setEnchantBan(eventName_prefix, file_context);
				break;
			}
		}
		
		sender.sendMessage(ChatColor.GREEN + "[RandomWorld] : " + ChatColor.AQUA + "수정이 완료되었습니다.");
		
		return true;
	}
	
	
	public static boolean toggleEvent(CommandSender sender, String entityType, String entityName, String eventName) {
		RandomEvent re;
		if (entityType.equals("entity")) {
			EntityType entity_type;
			
			try {
				entity_type = EntityType.valueOf(entityName);
			}
			catch (IllegalArgumentException err) {
				sender.sendMessage(ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "해당 개체가 존재하지 않습니다.");
				return false;
			}
			
			re = Main.REGISTED_ENTITY.get(entity_type);
		}
		else if (entityType.equals("player")) {
			OfflinePlayer p = Bukkit.getPlayer(entityName);
			if (p == null) {
				OfflinePlayer[] players = Bukkit.getOfflinePlayers();
				
				for (OfflinePlayer off_p : players) {
					if (off_p.getName().equals(entityName)) {
						p = off_p;
						break;
					}
				}
				
				if (p == null) {
					sender.sendMessage(ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "해당 플레이어가 존재하지 않습니다.");
					return false;
				}
			}

			re = Main.REGISTED_PLAYER.get(p.getUniqueId());
			if (re == null) {
				re = new RandomEvent(p.getUniqueId().toString());
			}
		}
		else if (entityType.equals("default") && entityName.equals("default")) {
			re = Main.DEFAULT;
		}
		else {
			sender.sendMessage(ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "/randomworld switch <entity | player> <target_name> <event_name>");
			return false;
		}
		
		// 파일에 업데이트 안된 이벤트일 경우
		
		if (re.getActivate(eventName)) {
			
			// 라인 가져와서 수정 후 저장
		}
		else {

			// 라인 가져와서 수정 후 저장
		}
		
		return false;
	}
	
	
	public static boolean setPermission(CommandSender sender, String level, String username) {
		
		OfflinePlayer p = Bukkit.getPlayer(username);
		if (p == null) {
			OfflinePlayer[] off_players = Bukkit.getOfflinePlayers();
			for (OfflinePlayer off_p : off_players) {
				if (off_p.getName().equals(username)) {
					p = off_p;
				}
			}
			
			if (p == null) {
				sender.sendMessage(ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "해당 플레이어가 존재하지 않습니다.");
				return false;
			}
		}

		RandomEvent re = Main.REGISTED_PLAYER.get(p.getUniqueId());
		if (re == null) {
			re = new RandomEvent(p.getUniqueId().toString());
		}
		boolean isSuper = false;
		boolean isAdmin = false;
		
		if (level.equals("super")) {
			isSuper = true;
			isAdmin = true;
		}
		else if (level.equals("admin")) {
			isAdmin = true;
		}
		else if (!level.equals("user")){
			sender.sendMessage(ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "해당 플레이어가 존재하지 않습니다.");
			return false;
		}
		
		re.setPermission(isSuper, isAdmin);
		sender.sendMessage(ChatColor.GREEN + "[RandomWorld] : " + ChatColor.AQUA + "권한 수정이 완료되었습니다.");
		
		return true;
	}
}
