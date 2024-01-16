package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
	private final ArrayList<String> COMMANDS1 = new ArrayList<String>(Arrays.asList("modify", "setting"));
	private final ArrayList<String> TARGET = new ArrayList<String>(Arrays.asList("user", "entity"));
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
				break;
			}
			case 3 : {
				if (args[1].equals("user")) {
					StringUtil.copyPartialMatches(args[2], PLAYERS, completions);
				}
				else if (args[1].equals("entity")) {
					StringUtil.copyPartialMatches(args[2], ENTITIES, completions);
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
	
	
	// 플러그인 온 오프
	private void switchPlugin(boolean b) {	
		return;
	}
	
	
	// 이벤트 필터링 적용
	private void applyEvents(String list) {
		// 해당 리스트가 비었으면 전체 등록
		if (list.isEmpty()) {
			
		}
		// 그렇지않다면 일부 등록
		else {
			String[] events = Main.CONFIG.getString("Enable_Events").replaceAll(" ", "").replaceAll(",+", ",").toUpperCase().split(",");
			for (String e : events) {
				if (
						e.equals("WORKBENCH") ||
						e.equals("CRAFTING") ||
						e.equals("FURNACE") ||
						e.equals("BLAST_FURNACE") ||
						e.equals("SMOKER") ||
						e.equals("STONECUTTER") ||
						e.equals("SMITHING") ||
						e.equals("CARTOGRAPHY") ||
						e.equals("LOOM") ||
						e.equals("ANVIL") ||
						e.equals("GRINDSTONE") ||
						e.equals("MERCHANT")
					) {
					// <hashMap>
					// EVENTS.get("InvClick").
					// 	Crafting : item1, item2, ...
					// 	Furnace : item1, item2, ...
					// ...
					// 인벤토리 클릭 이벤트
				} else if (e.equalsIgnoreCase("PICKUP")) {
					// 줍기 이벤트
				} else if (e.equalsIgnoreCase("BREWING")) {
					// 양조 이벤트
				} else if (e.equalsIgnoreCase("ENCHANTING")) {
					// 인첸트 이벤트
				} else {
					
				}
			}
		}
		return;
	}
}
