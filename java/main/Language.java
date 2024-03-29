package main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

public class Language {
	public static HashMap<String, SimpleConfig> LANGUAGE;
	public static HashMap<String, HashMap<String, String>> LANGUAGE_DATA;
	public static HashSet<String> LANG_KEY;
	
	public static void loadLanguage() {
		LANGUAGE = new HashMap<String, SimpleConfig>();
		LANGUAGE_DATA = new HashMap<String, HashMap<String, String>>();
		LANG_KEY = new HashSet<String>();
		
		LANG_KEY.add("ONLY_PLAYER");
		LANG_KEY.add("NO_PERMISSION");
		LANG_KEY.add("WRONG_LANGUAGE_COMMAND");
		LANG_KEY.add("NOT_EXSIT_LANGUAGE");
		LANG_KEY.add("COMPLETE_LANGUAGE_EDIT");
		LANG_KEY.add("WRONG_PERMISSION_COMMAND");
		LANG_KEY.add("WRONG_EDIT_COMMAND");
		LANG_KEY.add("NOT_EXIST_ENTITY");
		LANG_KEY.add("NOT_EXIST_PLAYER");
		LANG_KEY.add("INACTIVATED_EVENT");
		LANG_KEY.add("NOT_EXIST_EVENT");
		LANG_KEY.add("ONLY_ALLOW_SET");
		LANG_KEY.add("ONLY_ALLOW_INT");
		LANG_KEY.add("COMPLETE_EDIT");
		LANG_KEY.add("NOT_MODIFIED");
		LANG_KEY.add("WRONG_SWITCH_COMMAND");
		LANG_KEY.add("COMPLETE_PERMISSION_EDIT");
		
		LANG_KEY.add("PLAYER");
		LANG_KEY.add("ENTITY");
		LANG_KEY.add("DEFAULT");
		LANG_KEY.add("ITEM");
		LANG_KEY.add("POTION_EFFECT");
		LANG_KEY.add("ENCHANT");
		LANG_KEY.add("EXCEPT");
		LANG_KEY.add("BAN");
		LANG_KEY.add("MAX");
		LANG_KEY.add("SEARCH");
		LANG_KEY.add("BACKWARD");
		LANG_KEY.add("CLOSE");
		LANG_KEY.add("PREV_PAGE");
		LANG_KEY.add("NEXT_PAGE");
		LANG_KEY.add("SELECTED_INFO");
		LANG_KEY.add("STATUS_ENABLE");
		LANG_KEY.add("STATUS_DISABLE");
		LANG_KEY.add("LEFTCLICK_ENABLE");
		LANG_KEY.add("LEFTCLICK_DISABLE");
		LANG_KEY.add("RIGHTCLICK_EVENT_DETAIL");
		LANG_KEY.add("STATUS_ACTIVATE");
		LANG_KEY.add("STATUS_INACTIVATE");
		LANG_KEY.add("LEFTCLICK_ACTIVATE");
		LANG_KEY.add("LEFTCLICK_INACTIVATE");
		LANG_KEY.add("LEFTCLICK_1");
		LANG_KEY.add("RIGHTCLICK_1");
		LANG_KEY.add("LEFTCLICK_5");
		LANG_KEY.add("RIGHTCLICK_5");
		LANG_KEY.add("LEFTCLICK_10");
		LANG_KEY.add("RIGHTCLICK_10");
		LANG_KEY.add("LEFTCLICK_APPLY");
		
		LANG_KEY.add("SELECT_ENTITY_TYPE");
		LANG_KEY.add("SELECT_ENTITY");
		LANG_KEY.add("SELECT_EVENT_TYPE");
		LANG_KEY.add("SELECT_EVENT");
		LANG_KEY.add("SET_EVENT");
		LANG_KEY.add("SET_EVENT_DETAIL");
		LANG_KEY.add("INPUT_INT");
		
		// 영어,한국어 파일 없으면 생성하기
		// lang 폴더 내 언어파일들을 전부 스캔해서 서버의 언어 해시맵 변수에 저장하기
		Plugin plugin = Bukkit.getPluginManager().getPlugin("RandomWorld");
		
		// 한국어 파일이 없는 경우 생성
		SimpleConfig korean_yml = Main.MANAGER.getNewConfig(File.separator + "lang" + File.separator + "한국어.yml");
		HashSet<String> keys = null;
		
		keys = new HashSet<String>(korean_yml.getKeys());
		if (!keys.equals(LANG_KEY)) {
			korean_yml.set("ONLY_PLAYER", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "해당 명령어는 플레이어만 사용할 수 있습니다.");
			korean_yml.set("NO_PERMISSION", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "권한이 없습니다.");
			korean_yml.set("WRONG_LANGUAGE_COMMAND", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "/randomworld language <user_name> <lang>");
			korean_yml.set("NOT_EXSIT_LANGUAGE", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "해당 언어가 존재하지 않습니다.");
			korean_yml.set("COMPLETE_LANGUAGE_EDIT", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.AQUA + "언어 수정이 완료되었습니다.");
			korean_yml.set("WRONG_PERMISSION_COMMAND", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "/randomworld permission <user_name> <user | admin | super>");
			korean_yml.set("WRONG_EDIT_COMMAND", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "/randomworld <add | remove | set> <player | entity | default> <entity_name> <event_name> <args...>");
			korean_yml.set("NOT_EXIST_ENTITY", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "해당 개체가 존재하지 않습니다.");
			korean_yml.set("NOT_EXIST_PLAYER", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "해당 플레이어가 존재하지 않습니다.");
			korean_yml.set("INACTIVATED_EVENT", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "존재하지 않거나 비활성화 된 이벤트 입니다.");
			korean_yml.set("NOT_EXIST_EVENT", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "존재하지 않는 이벤트 입니다.");
			korean_yml.set("ONLY_ALLOW_SET", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "해당 이벤트는 set 명령만 허용됩니다.");
			korean_yml.set("ONLY_ALLOW_INT", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "해당 이벤트는 정수값만 허용됩니다.");
			korean_yml.set("COMPLETE_EDIT", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.AQUA + "수정이 완료되었습니다.");
			korean_yml.set("NOT_MODIFIED", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.YELLOW + "변경사항이 없습니다.");
			korean_yml.set("WRONG_SWITCH_COMMAND", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "/randomworld switch <entity | player> <target_name> <event_name>");
			korean_yml.set("COMPLETE_PERMISSION_EDIT", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.AQUA + "권한 수정이 완료되었습니다.");
			korean_yml.set("PLAYER", ChatColor.WHITE + "플레이어");
			korean_yml.set("ENTITY", ChatColor.WHITE + "엔티티");
			korean_yml.set("DEFAULT", ChatColor.WHITE + "공통");
			korean_yml.set("ITEM", ChatColor.WHITE + "아이템");
			korean_yml.set("POTION_EFFECT", ChatColor.WHITE + "포션효과");
			korean_yml.set("ENCHANT", ChatColor.WHITE + "인첸트");
			korean_yml.set("EXCEPT", ChatColor.GOLD + "필터");
			korean_yml.set("BAN", ChatColor.GOLD + "밴");
			korean_yml.set("MAX", ChatColor.GOLD + "최대버프");
			korean_yml.set("SEARCH", ChatColor.GRAY + "검색");
			korean_yml.set("BACKWARD", ChatColor.GRAY + "뒤로가기");
			korean_yml.set("CLOSE", ChatColor.RED + "닫기");
			korean_yml.set("PREV_PAGE", ChatColor.GRAY + "이전페이지");
			korean_yml.set("NEXT_PAGE", ChatColor.GRAY + "다음페이지");
			korean_yml.set("SELECTED_INFO", ChatColor.WHITE + "선택한 정보");
			korean_yml.set("STATUS_ENABLE", ChatColor.GRAY + "상태 : [" + ChatColor.BOLD + ChatColor.GOLD + "활성화" + ChatColor.RESET + ChatColor.GRAY + "]");
			korean_yml.set("STATUS_DISABLE", ChatColor.GRAY + "상태 : [" + ChatColor.BOLD + ChatColor.RED + "비활성화" + ChatColor.RESET + ChatColor.GRAY + "]");
			korean_yml.set("LEFTCLICK_ENABLE", ChatColor.GRAY + "좌클릭해서 " + ChatColor.GOLD + "활성화");
			korean_yml.set("LEFTCLICK_DISABLE", ChatColor.GRAY + "좌클릭해서 " + ChatColor.RED + "비활성화");
			korean_yml.set("RIGHTCLICK_EVENT_DETAIL", ChatColor.GRAY + "우클릭해서 " + ChatColor.YELLOW + "이벤트 세부 설정");
			korean_yml.set("STATUS_ACTIVATE", ChatColor.GRAY + "상태 : [" + ChatColor.BOLD + ChatColor.GOLD + "적용" + ChatColor.RESET + ChatColor.GRAY + "]");
			korean_yml.set("STATUS_INACTIVATE", ChatColor.GRAY + "상태 : [" + ChatColor.BOLD + ChatColor.RED + "미적용" + ChatColor.RESET + ChatColor.GRAY + "]");
			korean_yml.set("LEFTCLICK_ACTIVATE", ChatColor.GRAY + "좌클릭해서 " + ChatColor.GOLD + "적용");
			korean_yml.set("LEFTCLICK_INACTIVATE", ChatColor.GRAY + "좌클릭해서 " + ChatColor.RED + "미적용");
			korean_yml.set("LEFTCLICK_1", ChatColor.GRAY + "좌클릭해서 " + ChatColor.RED + ChatColor.BOLD + "+ 1");
			korean_yml.set("RIGHTCLICK_1", ChatColor.GRAY + "우클릭해서 " + ChatColor.BLUE + ChatColor.BOLD + "- 1");
			korean_yml.set("LEFTCLICK_5", ChatColor.GRAY + "좌클릭해서 " + ChatColor.RED + ChatColor.BOLD + "+ 5");
			korean_yml.set("RIGHTCLICK_5", ChatColor.GRAY + "우클릭해서 " + ChatColor.BLUE + ChatColor.BOLD + "- 5");
			korean_yml.set("LEFTCLICK_10", ChatColor.GRAY + "좌클릭해서 " + ChatColor.RED + ChatColor.BOLD + "+ 10");
			korean_yml.set("RIGHTCLICK_10", ChatColor.GRAY + "우클릭해서 " + ChatColor.BLUE + ChatColor.BOLD + "- 10");
			korean_yml.set("LEFTCLICK_APPLY", ChatColor.GRAY + "좌클릭해서 " + ChatColor.YELLOW + ChatColor.BOLD + "적용");
			korean_yml.set("SELECT_ENTITY_TYPE", "개체 종류 선택");
			korean_yml.set("SELECT_ENTITY", "개체 선택");
			korean_yml.set("SELECT_EVENT_TYPE", "이벤트 종류 선택");
			korean_yml.set("SELECT_EVENT", "이벤트 선택");
			korean_yml.set("SET_EVENT", "이벤트 설정");
			korean_yml.set("SET_EVENT_DETAIL", "이벤트 세부 설정");
			korean_yml.set("INPUT_INT", "숫자 입력");
			
			korean_yml.saveConfig();
		}
		LANGUAGE.put("한국어", korean_yml);

		// 영어 파일이 없는 경우 생성
		SimpleConfig english_yml = Main.MANAGER.getNewConfig(File.separator + "lang" + File.separator + "English.yml");

		keys = new HashSet<String>(english_yml.getKeys());
		if (!keys.equals(LANG_KEY)) {
			english_yml.set("ONLY_PLAYER", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "This command allowed only Player.");
			english_yml.set("NO_PERMISSION", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "No Permission.");
			english_yml.set("WRONG_LANGUAGE_COMMAND", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "/randomworld language <user_name> <lang>");
			english_yml.set("NOT_EXSIT_LANGUAGE", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "This Language is not exist.");
			english_yml.set("COMPLETE_LANGUAGE_EDIT", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.AQUA + "Edit language complete.");
			english_yml.set("WRONG_PERMISSION_COMMAND", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "/randomworld permission <user_name> <user | admin | super>");
			english_yml.set("WRONG_EDIT_COMMAND", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "/randomworld <add | remove | set> <player | entity | default> <entity_name> <event_name> <args...>");
			english_yml.set("NOT_EXIST_ENTITY", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "This Entity is not exist.");
			english_yml.set("NOT_EXIST_PLAYER", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "This Player is not exist.");
			english_yml.set("INACTIVATED_EVENT", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "This event is not exist or inactivated.");
			english_yml.set("NOT_EXIST_EVENT", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "This event is not exist.");
			english_yml.set("ONLY_ALLOW_SET", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "This event allowed only 'set'.");
			english_yml.set("ONLY_ALLOW_INT", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "This event allowed only Integer.");
			english_yml.set("COMPLETE_EDIT", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.AQUA + "Edit Complete.");
			english_yml.set("NOT_MODIFIED", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.YELLOW + "No Change.");
			english_yml.set("WRONG_SWITCH_COMMAND", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "/randomworld switch <entity | player> <target_name> <event_name>");
			english_yml.set("COMPLETE_PERMISSION_EDIT", ChatColor.GREEN + "[RandomWorld] : " + ChatColor.AQUA + "Permission Edit Complete");
			english_yml.set("PLAYER", ChatColor.WHITE + "Player");
			english_yml.set("ENTITY", ChatColor.WHITE + "Entity");
			english_yml.set("DEFAULT", ChatColor.WHITE + "Default");
			english_yml.set("ITEM", ChatColor.WHITE + "Item");
			english_yml.set("POTION_EFFECT", ChatColor.WHITE + "Potion Effect");
			english_yml.set("ENCHANT", ChatColor.WHITE + "Enchant");
			english_yml.set("EXCEPT", ChatColor.GOLD + "Filter");
			english_yml.set("BAN", ChatColor.GOLD + "Ban");
			english_yml.set("MAX", ChatColor.GOLD + "Max effect");
			english_yml.set("SEARCH", ChatColor.GRAY + "Search");
			english_yml.set("BACKWARD", ChatColor.GRAY + "Backward");
			english_yml.set("CLOSE", ChatColor.RED + "Close");
			english_yml.set("PREV_PAGE", ChatColor.GRAY + "Prev");
			english_yml.set("NEXT_PAGE", ChatColor.GRAY + "Next");
			english_yml.set("SELECTED_INFO", ChatColor.WHITE + "Selected Info");
			english_yml.set("STATUS_ENABLE", ChatColor.GRAY + "Status : [" + ChatColor.BOLD + ChatColor.GOLD + "Activated" + ChatColor.RESET + ChatColor.GRAY + "]");
			english_yml.set("STATUS_DISABLE", ChatColor.GRAY + "Status : [" + ChatColor.BOLD + ChatColor.RED + "Inactivated" + ChatColor.RESET + ChatColor.GRAY + "]");
			english_yml.set("LEFTCLICK_ENABLE", ChatColor.GRAY + "Left-Click " + ChatColor.GOLD + "Activation");
			english_yml.set("LEFTCLICK_DISABLE", ChatColor.GRAY + "Left-Click " + ChatColor.RED + "Inactivation");
			english_yml.set("RIGHTCLICK_EVENT_DETAIL", ChatColor.GRAY + "Right-Click " + ChatColor.YELLOW + "Setting event detail");
			english_yml.set("STATUS_ACTIVATE", ChatColor.GRAY + "Status : [" + ChatColor.BOLD + ChatColor.GOLD + "Allowed" + ChatColor.RESET + ChatColor.GRAY + "]");
			english_yml.set("STATUS_INACTIVATE", ChatColor.GRAY + "Status : [" + ChatColor.BOLD + ChatColor.RED + "Denied" + ChatColor.RESET + ChatColor.GRAY + "]");
			english_yml.set("LEFTCLICK_ACTIVATE", ChatColor.GRAY + "Left-Click " + ChatColor.GOLD + "Allow");
			english_yml.set("LEFTCLICK_INACTIVATE", ChatColor.GRAY + "Left-Click " + ChatColor.RED + "Deny");
			english_yml.set("LEFTCLICK_1", ChatColor.GRAY + "Left-Click " + ChatColor.RED + ChatColor.BOLD + "+ 1");
			english_yml.set("RIGHTCLICK_1", ChatColor.GRAY + "Right-Click " + ChatColor.BLUE + ChatColor.BOLD + "- 1");
			english_yml.set("LEFTCLICK_5", ChatColor.GRAY + "Left-Click " + ChatColor.RED + ChatColor.BOLD + "+ 5");
			english_yml.set("RIGHTCLICK_5", ChatColor.GRAY + "Right-Click " + ChatColor.BLUE + ChatColor.BOLD + "- 5");
			english_yml.set("LEFTCLICK_10", ChatColor.GRAY + "Left-Click " + ChatColor.RED + ChatColor.BOLD + "+ 10");
			english_yml.set("RIGHTCLICK_10", ChatColor.GRAY + "Right-Click " + ChatColor.BLUE + ChatColor.BOLD + "- 10");
			english_yml.set("LEFTCLICK_APPLY", ChatColor.GRAY + "Left-Click " + ChatColor.YELLOW + ChatColor.BOLD + "Apply");
			english_yml.set("SELECT_ENTITY_TYPE", "Select Entity Type");
			english_yml.set("SELECT_ENTITY", "Select Entity");
			english_yml.set("SELECT_EVENT_TYPE", "Select Event Type");
			english_yml.set("SELECT_EVENT", "Select Event");
			english_yml.set("SET_EVENT", "Set Event");
			english_yml.set("SET_EVENT_DETAIL", "Set Event Detail");
			english_yml.set("INPUT_INT", "Input Number");
			
			english_yml.saveConfig();
		}
		LANGUAGE.put("English", english_yml);
		
		// lang 폴더 내 언어파일 스캔 및 각 파일별로 언어정보 가져오기
		File lang_folder = new File(plugin.getDataFolder() + File.separator + "lang");
		ArrayList<String> lang_list = new ArrayList<String>(Arrays.asList(lang_folder.list()));
		
		for (String lang_file : lang_list) {
			if (lang_file.indexOf(".yml") <= -1) {
				continue;
			}
			
			fetchLanguage(lang_file.replaceAll(".yml", ""));
		}
	}
	
	public static void fetchLanguage(String name) {
		SimpleConfig LANG = Main.MANAGER.getNewConfig(File.separator + "lang" + File.separator + name + ".yml");
		HashMap<String, String> lang_map = new HashMap<String, String>();
		
		for (String key : LANG_KEY) {
			if (!LANG.contains(key)) {
				return;
			}
			
			lang_map.put(key, LANG.getString(key));
		}
		
		if (lang_map.size() <= 0) {
			return;
		}
		
		LANGUAGE_DATA.put(name, lang_map);
	}
	
	public static String fetchString(String lang, String key) {
		HashMap<String, String> lang_map = LANGUAGE_DATA.get(lang);
		if (lang_map == null) {
			System.out.println("[RandomWorld] : What is " + lang + "?");
			return "";
		}
		
		String result = lang_map.get(key);
		
		if (result == null) {
			System.out.println("[RandomWorld] : Not Found " + key);
			return null;
		}
		
		return result;
	}
	
	public static String getClearString(String lang, String key) {
		String result = fetchString(lang, key);
		
		return result.replaceAll("§.", "");
	}
	
	public static boolean equalsIgnoreColor(String lang, String str1, String key) {
		String changed_1 = str1.replaceAll("§.", "");
		String changed_2 = getClearString(lang, key);
		
		if (changed_1.equals(changed_2)) {
			return true;
		}
		
		return false;
	}
}
