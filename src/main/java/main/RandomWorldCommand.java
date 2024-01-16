package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Registry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.StringUtil;

public class RandomWorldCommand implements TabCompleter {
	private final ArrayList<String> COMMANDS1 = new ArrayList<String>(Arrays.asList("modify", "setting", "permission"));
	private final ArrayList<String> TARGET = new ArrayList<String>(Arrays.asList("user", "entity"));
	private final ArrayList<String> PERMISSION_OPTION = new ArrayList<String>(Arrays.asList("add", "remove"));
	private final ArrayList<String> PLAYERS = new ArrayList<String>();
	private final ArrayList<String> ENTITIES = new ArrayList<String>();
	private final ArrayList<String> SETTINGS = new ArrayList<String>();
	private final HashMap<String, Integer> SETTING_CATEGORY = new HashMap<String, Integer>();
	private final ArrayList<String> ITEMS = new ArrayList<String>();
	private final ArrayList<String> POTIONS = new ArrayList<String>();
	private final ArrayList<String> ENCHANTS = new ArrayList<String>();
	
	/* 
	 * SETTING_CATEGORY
	 *  
	 * 0 - ITEM FIELD
	 * 1 - POTION FIELD
	 * 2 - ENCHANT FIELD
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
			SETTING_CATEGORY.put(field_name + "_EXCEPT", 1);
			SETTING_CATEGORY.put(field_name + "_BAN", 1);
			SETTING_CATEGORY.put(field_name + "_MAX", 1);
		}
		
		keys = Main.ENCHANT_FIELD.keySet().stream().sorted().iterator();
		while (keys.hasNext()) {
			String field_name = keys.next();
			SETTINGS.add(field_name + "_EXCEPT");
			SETTINGS.add(field_name + "_BAN");
			SETTING_CATEGORY.put(field_name + "_EXCEPT", 2);
			SETTING_CATEGORY.put(field_name + "_BAN", 2);
		}
		
		keys = Main.ITEM_FIELD.keySet().stream().sorted().iterator();
		while (keys.hasNext()) {
			String field_name = keys.next();
			SETTINGS.add(field_name + "_EXCEPT");
			SETTINGS.add(field_name + "_BAN");
			SETTING_CATEGORY.put(field_name + "_EXCEPT", 0);
			SETTING_CATEGORY.put(field_name + "_BAN", 0);
		}

		Iterator<Material> materials = Registry.MATERIAL.iterator();
		Iterator<PotionEffectType> effects = Registry.EFFECT.iterator();
		Iterator<Enchantment> enchantments = Registry.ENCHANTMENT.iterator();
		
		while (materials.hasNext()) {
			Material material = materials.next();
			if (!material.isItem()) {
				continue;
			}
			ITEMS.add(material.name());
		}
		
		while (effects.hasNext()) {
			POTIONS.add(effects.next().getKey().getKey());
		}
		
		while (enchantments.hasNext()) {
			ENCHANTS.add(enchantments.next().getKey().getKey());
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
				if (args[0].equals("modify")) {
					StringUtil.copyPartialMatches(args[1], TARGET, completions);
				}
				else if (args[0].equals("permission")) {
					StringUtil.copyPartialMatches(args[1], PERMISSION_OPTION, completions);
				}
				break;
			}
			case 3 : {
				if (args[0].equals("modify")) {
					if (args[1].equals("user")) {
						StringUtil.copyPartialMatches(args[2], PLAYERS, completions);
					}
					else if (args[1].equals("entity")) {
						StringUtil.copyPartialMatches(args[2], ENTITIES, completions);
					}
				}
				else if (args[0].equals("permission")) {
					if (PERMISSION_OPTION.indexOf(args[1]) >= 0) {
						StringUtil.copyPartialMatches(args[2], PLAYERS, completions);	
					}
				}
				break;
			}
			case 4 : {
				if ((args[1].equals("user") && PLAYERS.indexOf(args[2]) >= 0) || 
					(args[1].equals("entity") && ENTITIES.indexOf(args[2]) >= 0)) {
					StringUtil.copyPartialMatches(args[3], SETTINGS, completions);
				}
				break;
			}
			default : {
				if (args.length < 5) {
					break;
				}
				
				Integer category = SETTING_CATEGORY.get(args[3]);
				if (category == null) {
					break;
				}

				// Item 관련이면
				if (category == 0) {
					StringUtil.copyPartialMatches(args[args.length-1], ITEMS, completions);
				}
				// Potion 관련이면
				else if (category == 1) {
					StringUtil.copyPartialMatches(args[args.length-1], POTIONS, completions);
				}
				// Enchant 관련이면
				else if (category == 2) {
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
		// cmd창에서 오류남
		OfflinePlayer p = Bukkit.getPlayer(uuid);
		
		// OP 유저
		if (p.isOp()) {
			return 4;
		}

		RandomEvent re = Main.REGISTED_PLAYER.get(uuid);
		
		// 해당 플러그인 이용자가 아님
		if (re == null) {
			return 0;
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
	public static boolean openSettingGUI(int rank) {
		// 인벤토리 만들고
		// 해당플레이어에게 열고
		// 클릭이벤트 클래스 생성, CreateItem의 클릭 이벤트랑 구분하기
		
		// 설정창0 : 플레이어/엔티티/공통 선택 (해당 플레이어가 op 또는 super일 경우에만 활성화)
		// 설정창1 : (플레이어/엔티티 선택 시) 플레이어/엔티티 선택 (해당 플레이어가 op 또는 super일 경우에만 활성화)
		// 설정창2 : 아이템/포션/인첸트 선택 (뒤로가기 버튼) (해당 플레이어가 Admin일 경우에만 활성화)
		// 설정창3 : 이벤트(e.g : PICKUP_BAN) 아이콘 선택 (뒤로가기 버튼, 이전/다음페이지 버튼)
		// 설정창4 : 책 GUI open, 각 라인하나 당 옵션 하나 (open시 엔티티 설정 불러오기)
		// 설정창5 : 책 닫을 시, 저장 및 적용 여부 창 (강제종료 시 저장안함)
		
		return false;
	}
	
	
	// 이벤트 필터링 적용
	public static boolean setEvents(String entityType, String entityName, String eventName, ArrayList<String> fields) {
		// fields의 길이가 0이면 이벤트 공란으로 설정
		System.out.println("test");
		
		return false;
	}
}
