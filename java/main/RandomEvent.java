package main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;

public class RandomEvent {
	private final String RESIDENT_NAME;
	private final SimpleConfig DATA; // 데이터
	private boolean super_user;
	private boolean admin_user;
	private String language;
	private HashMap<String, Boolean> activated; // 각 이벤트 별, 활성화여부 해시맵
	private HashMap<String, HashMap<Material, Boolean>> itemFilter; // 랜덤 결과로 나오지 않을 아이템 해시맵
	private HashMap<String, HashMap<Material, Boolean>> itemBan; // 바꾸지 않을 아이템 해시맵
	private HashMap<String, HashMap<PotionEffectType, Boolean>> potionFilter; // 랜덤 결과로 나오지 않을 포션효과 해시맵
	private HashMap<String, HashMap<PotionEffectType, Boolean>> potionBan; // 바꾸지 않을 포션효과 해시맵
	private HashMap<String, Integer> potionMax; // 바꾸지 않을 포션효과 해시맵
	private HashMap<String, HashMap<Enchantment, Boolean>> enchantFilter; // 랜덤 결과로 나오지 않을 인첸트효과 해시맵
	private HashMap<String, HashMap<Enchantment, Boolean>> enchantBan; // 바꾸지 않을 인첸트효과 해시맵
	
	// 생성자
	public RandomEvent(String resident_name) {
		this.RESIDENT_NAME = resident_name;
		this.DATA = Main.MANAGER.getNewConfig("/userdata/" + resident_name + ".yml");
		this.super_user = false;
		this.admin_user = false;
		this.language = "한국어";
		this.activated = new HashMap<String, Boolean>();
		this.itemFilter = new HashMap<String, HashMap<Material, Boolean>>();
		this.itemBan = new HashMap<String, HashMap<Material, Boolean>>();
		this.potionFilter = new HashMap<String, HashMap<PotionEffectType, Boolean>>();
		this.potionBan = new HashMap<String, HashMap<PotionEffectType, Boolean>>();
		this.potionMax = new HashMap<String, Integer>();
		this.enchantFilter = new HashMap<String, HashMap<Enchantment, Boolean>>();
		this.enchantBan = new HashMap<String, HashMap<Enchantment, Boolean>>();
		
		// 유저 데이터 생성
		this.createUserdata();
		
		this.super_user = this.DATA.getBoolean("Super");
		this.admin_user = this.DATA.getBoolean("Admin");
		
		// 등록된 이벤트 불러오기
		this.loadEvent();
	}
	
	public String getLanguage() {
		return this.language;
	}
	
	public boolean getActivate(String eventName) {
		Boolean activate = this.activated.get(eventName);
		if (activate != null && activate) {
			return true;
		}
		return false;
	}
	
	public int getPotionMax(String eventName) {
		Integer max = this.potionMax.get(eventName);
		if (max != null) {
			return max;
		}
		return -1;
	}
	
	
	public PotionEffectType getRandomEffect(String eventName) {
		// 사용자 필터링에 맞게 포션효과 리턴
		HashMap<PotionEffectType, Boolean> eventEffects = this.potionFilter.get(eventName);
		
		if (eventEffects == null || eventEffects.size() <= 0) {
			return null;
		}
		
		PotionEffectType[] keys = eventEffects.keySet().toArray(new PotionEffectType[0]);
		int randIdx = (int)(Math.random() * keys.length);
		
		return keys[randIdx];
	}
	

	public PotionEffectType getRandomEffect(String eventName, PotionEffectType origin) {
		// origin 포션효과가 ban list에 존재하면 return null
		if (this.potionBan.get(eventName) != null) {
			if (this.potionBan.get(eventName).get(origin) != null) {
				return null;
			}
		}
		
		// 사용자 필터링에 맞게 포션효과 리턴
		HashMap<PotionEffectType, Boolean> eventEffects = this.potionFilter.get(eventName);
		if (eventEffects == null || eventEffects.size() <= 0) {
			return null;
		}
		
		PotionEffectType[] keys = eventEffects.keySet().toArray(new PotionEffectType[0]);
		int randIdx = (int)(Math.random() * keys.length);
		
		return keys[randIdx];
	}
	
	
	public Enchantment getRandomEnchant(String eventName) {
		// 사용자 필터링에 맞게 포션효과 리턴
		HashMap<Enchantment, Boolean> eventEnchants = this.enchantFilter.get(eventName);
		if (eventEnchants == null || eventEnchants.size() <= 0) {
			return null;
		}
		
		Enchantment[] keys = eventEnchants.keySet().toArray(new Enchantment[0]);
		int randIdx = (int)(Math.random() * keys.length);
		
		return keys[randIdx];
	}
	
	public Enchantment getRandomEnchant(String eventName, Enchantment origin) {
		// origin 인첸트가 ban list에 존재하면 return null
		if (this.enchantBan.get(eventName) != null) {
			if (this.enchantBan.get(eventName).get(origin) != null) {
				return null;
			}
		}
		
		// 사용자 필터링에 맞게 인첸트 리턴
		HashMap<Enchantment, Boolean> eventEnchants = this.enchantFilter.get(eventName);
		if (eventEnchants == null || eventEnchants.size() <= 0) {
			return null;
		}
		
		Enchantment[] keys = eventEnchants.keySet().toArray(new Enchantment[0]);
		int randIdx = (int)(Math.random() * keys.length);
		
		return keys[randIdx];
	}
	
	
	public Material getRandomItem(String eventName) {
		HashMap<Material, Boolean> eventMaterials = this.itemFilter.get(eventName);
		if (eventMaterials != null && eventMaterials.size() > 0) {
			Material[] keys = this.itemFilter.get(eventName).keySet().toArray(new Material[0]);
			int randIdx = (int)(Math.random() * keys.length);
			
			return keys[randIdx];
		}
		return null;
	}
	
	
	public Material getRandomItem(String eventName, Material origin) {
		// origin 아이템이 ban list에 존재하면 return null
		if (this.itemBan.get(eventName) != null) {
			if (this.itemBan.get(eventName).get(origin) != null) {
				return null;
			}
		}
		
		// 랜덤 아이템 찾기
		HashMap<Material, Boolean> eventMaterials = this.itemFilter.get(eventName);
		if (eventMaterials != null && eventMaterials.size() > 0) {
			Material[] keys = this.itemFilter.get(eventName).keySet().toArray(new Material[0]);
			int randIdx = (int)(Math.random() * keys.length);
			return keys[randIdx];
		}
		return null;
	}
	
	
	public ArrayList<String> getActivateEvents(String eventName) {
		// _MAX는 들어오지 않는다고 가정
		ArrayList<String> result = new ArrayList<String>();
		
		String line = this.DATA.getString(eventName).replaceAll("\n", "").replaceAll(" ", "");
		int category = RandomWorldCommand.SETTING_CATEGORY.get(eventName);
		
		// ''(empty)일 경우
		if (line.isEmpty()) {
			// 빈 리스트 반환
			return result;
		}
		// - 일 경우
		else if (line.equals("-")) {
			// 기본값 가져오기
			line = Main.DEFAULT.DATA.getString(eventName);
		}
		
		switch (category) {
			case 0 : 
			case 1 : {
				// * 일 경우
				if (line.equals("*")) {
					// 다 가져오기
					Material[] materials = Material.values();
					for (Material material : materials) {
						if (material.isItem() && !material.isAir()) {
							result.add(material.name().toUpperCase());
						}
					}
				}
				else {
					String[] items = line.split(",");
					for (String item : items) {
						if (item != null && !item.isEmpty()) {
							result.add(item.toUpperCase());
						}
					}
				}
				
				break;
			}
			case 2 : 
			case 3 : {
				// * 일 경우
				if (line.equals("*")) {
					// 다 가져오기
					Iterator<PotionEffectType> effects = Registry.EFFECT.iterator();
					while (effects.hasNext()) {
						PotionEffectType effect = effects.next();
						result.add(effect.getKey().getKey().toUpperCase());
					}
				}
				else {
					// 있는것들
					String[] items = line.split(",");
					for (String item : items) {
						if (item != null && !item.isEmpty()) {
							result.add(item.toUpperCase());
						}
					}
				}
				
				break;
			}
			case 5 : 
			case 6 : {
				// * 일 경우
				if (line.equals("*")) {
					// 다 가져오기
					Iterator<Enchantment> enchants = Registry.ENCHANTMENT.iterator();
					while (enchants.hasNext()) {
						Enchantment enchant = enchants.next();
						result.add(enchant.getKey().getKey().toUpperCase());
					}
				}
				else {
					// 있는것들
					String[] items = line.split(",");
					for (String item : items) {
						if (item != null && !item.isEmpty()) {
							result.add(item.toUpperCase());
						}
					}
				}
				
				break;
			}
		}
		
		return result;
	}
	public int getActivateMaxEvents(String eventName) {
		int category = RandomWorldCommand.SETTING_CATEGORY.get(eventName);
		if (category != 4) {
			return -1;
		}
		
		int result = this.DATA.getInt(eventName);
		
		if (result == -1) {
			result = Main.DEFAULT.DATA.getInt(eventName);
		}
		
		return result;
	}
	public TreeSet<String> getEnabledEvents() {
		String line = this.DATA.getString("Enable_Events").replaceAll(" ", "").replaceAll("\n", "");
		
		if (line.equals("-")) {
			line = Main.DEFAULT.DATA.getString("Enable_Events").replaceAll(" ", "").replaceAll("\n", "");
		}
		
		TreeSet<String> events = new TreeSet<String>();
		
		if (line.equals("*")) {
			events.addAll(Main.ITEM_FIELD.keySet());
			events.addAll(Main.POTION_FIELD.keySet());
			events.addAll(Main.ENCHANT_FIELD.keySet());
		}
		else if (!line.isEmpty()) {
			String[] user_event = line.split(",");
			for (String event : user_event) {
				events.add(event);
			}
		}
		
		return events;
	}
	
	public boolean isItemBan(String eventName, Material material) {
		HashMap<Material, Boolean> event_item_ban = this.itemBan.get(eventName);
		if (event_item_ban != null && event_item_ban.get(material) != null) {
			return true;
		}
		return false;
	}
	
	public boolean isEffectBan(String eventName, PotionEffectType effect) {
		HashMap<PotionEffectType, Boolean> event_potion_ban = this.potionBan.get(eventName);
		if (event_potion_ban != null && event_potion_ban.get(effect) != null) {
			return true;
		}
		return false;
	}
	
	public boolean isEnchantBan(String eventName, Enchantment enchant) {
		HashMap<Enchantment, Boolean> event_enchant_ban = this.enchantBan.get(eventName);
		if (event_enchant_ban != null && event_enchant_ban.get(enchant) != null) {
			return true;
		}
		return false;
	}
	
	public boolean isSuper() {
		return this.super_user;
	}
	
	public boolean isAdmin() {
		return this.admin_user;
	}
	
	public static boolean hasEntity(String name) {
		File entitydata = new File(Main.PLUGIN.getDataFolder() + File.separator + "userdata" + File.separator + name + ".yml"); // 유저 파일
		
		if (entitydata.exists()) {
			return true;
		}
		
		return false;
	}
	
	public void setLanguage(String lang) {
		if (Language.LANGUAGE_DATA.get(lang) == null) {
			return;
		}
		
		this.language = lang;
		this.DATA.set("Language", lang);
		this.DATA.saveConfig();
		
		return;
	}
	
	public void setActivate(String eventName, boolean b) {
		this.activated.put(eventName, b);
	}
	
	
	public void setEffectBan(String eventName, String effectList) {
		HashMap<PotionEffectType, Boolean> negativeEffects = new HashMap<PotionEffectType, Boolean>();
		effectList = effectList.replaceAll("\n", "").replaceAll(" ", "");
		String common_effectList = this.DATA.getString("ALL_BAN").replaceAll("\n", "").replaceAll(" ", "");
		String all_effectList;
		
		// 공통설정객체가 아닌 사용자설정객체이고
		if (!this.RESIDENT_NAME.equals("DEFAULT")) {
			// 
			if (effectList.equals("-")) {
				effectList = Main.DEFAULT.DATA.getString(eventName + "_BAN").replaceAll("\n", "").replaceAll(" ", "");
			}
			
			// 사용자설정이 공란이면 공통설정(기본값) 적용
			if (common_effectList.equals("-")) {
				common_effectList = Main.DEFAULT.DATA.getString("ALL_BAN").replaceAll("\n", "").replaceAll(" ", "");
			}
		}

		all_effectList = effectList.concat("," + common_effectList).replaceAll(",,", ",").toUpperCase();
		
		
		if (effectList.equals("*") || common_effectList.equals("*")) {
			Iterator<PotionEffectType> re = Registry.EFFECT.iterator();
			while (re.hasNext()) {
				negativeEffects.put(re.next(), true);
			}
		}
		
		for (String effect : all_effectList.split(",")) {
			if (effect == null || effect.isEmpty()) {
				continue;
			}
			
			PotionEffectType str2eft = Registry.EFFECT.match(effect);
			if (str2eft != null) {
				negativeEffects.put(str2eft, true);
			}
		}
		
		this.potionBan.put(eventName, negativeEffects);
	}

	public void setEffectFilter(String eventName, String effectList) {
		// 전체 이펙트에서 거를 이펙트만 제거해서 this.potionFilter에 저장
		HashMap<PotionEffectType, Boolean> valuable = new HashMap<PotionEffectType, Boolean>();
		
		effectList = effectList.replaceAll("\n", "").replaceAll(" ", ""); // 유저 필터링
		String common_effectList = this.DATA.getString("ALL_EXCEPT").replaceAll("\n", "").replaceAll(" ", ""); // 공통 필터링
		String all_effectList; // 유저 + 공통 필터링
		
		// 공통설정객체가 아닌 사용자성정객체이고, 개인사용자 설정이 없어서 기본값으로
		if (!this.RESIDENT_NAME.equals("DEFAULT")) {
			// 
			if (effectList.equals("-")) {
				effectList = Main.DEFAULT.DATA.getString(eventName + "_EXCEPT").replaceAll("\n", "").replaceAll(" ", "");
			}
			// 사용자설정이 공란이면 공통설정(기본값) 적용
			if (common_effectList.equals("-")) {
				common_effectList = Main.DEFAULT.DATA.getString("ALL_EXCEPT").replaceAll("\n", "").replaceAll(" ", "");
			}
		}

		all_effectList = effectList.concat("," + common_effectList).replaceAll(",,", ",").toUpperCase();
		
		// 전부 제외할 때
		if (effectList.equals("*") || common_effectList.equals("*")) {
			this.potionFilter.put(eventName, valuable);
			return;
		}

		// 포션 효과 해시맵 생성
		Iterator<PotionEffectType> re = Registry.EFFECT.iterator();
		while (re.hasNext()) {
			// key는 minecraft:포션효과
			valuable.put(re.next(), true);
		}
		
		// 전체 포션효과 중, userdata에 적힌 포션효과만 골라서 remove
		for (String effect : all_effectList.split(",")) {
			// effect가 널도 아니고 빈 문자도 아니면
			if (effect == null || effect.isEmpty()) {
				continue;
			}
			
			PotionEffectType key = Registry.EFFECT.match(effect);
			
			if (key != null) {
				valuable.remove(key);
			}
		}
		
		this.potionFilter.put(eventName, valuable);
	}
	
	public void setEffectMax(String eventName, int maxCount) {

		// 공통설정객체가 아닌 사용자성정객체이고, 개인사용자 설정이 없어서 기본값으로
		if (!this.RESIDENT_NAME.equals("DEFAULT") && maxCount == -1) {
			int default_max = Main.DEFAULT.potionMax.get(eventName);
			this.potionMax.put(eventName, default_max);
			return;
		}
		
		this.potionMax.put(eventName, maxCount);
	}
	
	
	public void setEnchantBan(String eventName, String enchantList) {
		HashMap<Enchantment, Boolean> negativeEnchants = new HashMap<Enchantment, Boolean>();
		enchantList = enchantList.replaceAll("\n", "").replaceAll(" ", "");
		String common_enchantList = this.DATA.getString("ALL_BAN").replaceAll("\n", "").replaceAll(" ", "");
		String all_enchantList;
		
		// 공통설정객체가 아닌 사용자설정객체이고, 개인 사용자 설정이 없으면 기본값으로 설정
		if (!this.RESIDENT_NAME.equals("DEFAULT")) {
			if (enchantList.equals("-")) {
				enchantList = Main.DEFAULT.DATA.getString(eventName + "_BAN").replaceAll("\n", "").replaceAll(" ", "");
			}
			if (common_enchantList.equals("-")) {
				common_enchantList = Main.DEFAULT.DATA.getString("ALL_BAN").replaceAll("\n", "").replaceAll(" ", "");
			}
		}
		
		all_enchantList = enchantList.concat("," + common_enchantList).replaceAll(",,", "").toUpperCase();
		
		if (enchantList.equals("*") || common_enchantList.equals("*")) {
			Iterator<Enchantment> re = Registry.ENCHANTMENT.iterator();
			while (re.hasNext()) {
				negativeEnchants.put(re.next(), true);
			}
		}
		
		for (String enchant : all_enchantList.split(",")) {
			if (enchant == null || enchant.isEmpty()) {
				continue;
			}
			
			Enchantment str2eht = Registry.ENCHANTMENT.match(enchant);
			if (str2eht != null) {
				negativeEnchants.put(str2eht, true);
			}
		}
		
		this.enchantBan.put(eventName, negativeEnchants);
	}
	
	
	public void setEnchantFilter(String eventName, String enchantList) {
		// 전체 이펙트에서 거를 이펙트만 제거해서 this.potionFilter에 저장
		HashMap<Enchantment, Boolean> valuable = new HashMap<Enchantment, Boolean>();
		
		enchantList = enchantList.replaceAll("\n", "").replaceAll(" ", ""); // 유저 필터링
		String common_enchantList = this.DATA.getString("ALL_EXCEPT").replaceAll("\n", "").replaceAll(" ", ""); // 공통 필터링
		String all_enchantList; // 유저 + 공통 필터링
		
		// 공통설정객체가 아닌 사용자설정객체이고, 개인 사용자 설정이 없으면 기본값으로 설정
		if (!this.RESIDENT_NAME.equals("DEFAULT")) {
			if (enchantList.equals("-")) {
				enchantList = Main.DEFAULT.DATA.getString(eventName + "_EXCEPT").replaceAll("\n", "").replaceAll(" ", "");
			}
			if (common_enchantList.equals("-")) {
				common_enchantList = Main.DEFAULT.DATA.getString("ALL_EXCEPT").replaceAll("\n", "").replaceAll(" ", "");
			}
		}
		
		all_enchantList = enchantList.concat("," + common_enchantList).replaceAll(",,", ",").toUpperCase();
		
		// 전부 제외할 때
		if (enchantList.equals("*") || common_enchantList.equals("*")) {
			this.enchantFilter.put(eventName, valuable);
			return;
		}

		// 인첸트 해시맵 생성
		Iterator<Enchantment> re = Registry.ENCHANTMENT.iterator();
		while (re.hasNext()) {
			// key는 minecraft:인첸트명
			Enchantment ppp = re.next();
			valuable.put(ppp, true);
//			System.out.println(ppp.getKey());
		}
		
		// 전체 인첸트 중, userdata에 적힌 인첸트만 골라서 remove
		for (String enchant : all_enchantList.split(",")) {
			if (enchant == null || enchant.isEmpty()) {
				continue;
			}
			
			Enchantment key = Registry.ENCHANTMENT.match(enchant);
			if (key != null) {
				valuable.remove(key);
			}
		}
		
		this.enchantFilter.put(eventName, valuable);
	}
	
	
	public void setItemBan(String eventName, String itemList) {
		HashMap<Material, Boolean> negativeItems = new HashMap<Material, Boolean>();
		itemList = itemList.replaceAll("\n", "").replaceAll(" ", "");
		String common_itemList = this.DATA.getString("ALL_BAN").replaceAll("\n", "").replaceAll(" ", "");
		String all_itemList;
		
		// 공통설정객체가 아닌 사용자설정객체이고, 유저에게 설정된 item ban이 없으면 공통 item ban 설정으로 덮어씌우기
		if (!this.RESIDENT_NAME.equals("DEFAULT")) {
			if (itemList.equals("-")) {
				itemList = Main.DEFAULT.DATA.getString(eventName + "_BAN").replaceAll("\n", "").replaceAll(" ", "");
			}
			if (common_itemList.equals("-")) {
				common_itemList = Main.DEFAULT.DATA.getString("ALL_BAN").replaceAll("\n", "").replaceAll(" ", "");
			}
		}
		
		all_itemList = itemList.concat("," + common_itemList).replaceAll(",,", "").toUpperCase();
		
		if (itemList.equals("*") || common_itemList.equals("*")) {
			Material[] materials = Material.values();
			for (Material m : materials) {
				if (m.isItem() && !m.isAir()) {
					negativeItems.put(m, true);
				}
			}
		}
		
		for (String item : all_itemList.split(",")) {
			if (item == null || item.isEmpty()) {
				continue;
			}
			
			Material str2mtl = Material.matchMaterial(item);
			if (str2mtl != null) {
				negativeItems.put(str2mtl, true);
			}
		}
		
		this.itemBan.put(eventName, negativeItems);
	}
	
	
	public void setItemFilter(String eventName, String itemList) {
		HashMap<Material, Boolean> valuable = new HashMap<Material, Boolean>();
		
		itemList = itemList.replaceAll("\n", "").replaceAll(" ", "");
		String common_itemList = this.DATA.getString("ALL_EXCEPT").replaceAll("\n", "").replaceAll(" ", "");
		String all_itemList;
		
		// 공통설정객체가 아닌 사용자설정객체이고, 사용자 설정이 없으면 기본값 적용
		if (!this.RESIDENT_NAME.equals("DEFAULT")) {
			if (itemList.equals("-")) {
				itemList = Main.DEFAULT.DATA.getString(eventName + "_EXCEPT").replaceAll("\n", "").replaceAll(" ", "");
			}
			if (common_itemList.equals("-")) {
				common_itemList = Main.DEFAULT.DATA.getString("ALL_EXCEPT").replaceAll("\n", "").replaceAll(" ", "");
			}
		}
		
		all_itemList = itemList.concat("," + common_itemList).replaceAll(",,", ",").toUpperCase();
		
		// 전부 제외할 때
		if (itemList.equals("*") || common_itemList.equals("*")) {
			this.itemFilter.put(eventName, valuable);
			return;
		}
		
		// 사용 가능한 아이템만 판별 - 이 코드는 자주 호출하지 않는 것이 좋음
		Material[] materials = Material.values();
		for (Material m : materials) {
			if (m.isItem() && !m.isAir()) {
				valuable.put(m, true);
			}
		}
		
		// 전체 아이템 중, userdata.yml에 적힌 아이템 리스트만 골라서 remove
		for (String item : all_itemList.split(",")) {
			if (item == null || item.isEmpty()) {
				continue;
			}
			valuable.remove(Material.matchMaterial(item));
		}
		
		this.itemFilter.put(eventName, valuable);
	}
	
	public void setPermission(boolean isSuper, boolean isAdmin) {
		this.DATA.set("Super", isSuper);
		this.DATA.set("Admin", isAdmin);
		this.DATA.saveConfig();
		
		this.super_user = isSuper;
		this.admin_user = isAdmin;
	}
	
	public void write(String eventName, String file_context) {
		this.DATA.set(eventName, file_context);
		this.DATA.saveConfig();
	}
	public void write(String eventName, int value) {
		this.DATA.set(eventName, value);
		this.DATA.saveConfig();
	}
	

	public void createUserdata() {
		// 유저데이터 만들고 등록하고 끝
		Set<String> item_names = Main.ITEM_FIELD.keySet();
		Set<String> effect_names = Main.POTION_FIELD.keySet();
		Set<String> enchant_names = Main.ENCHANT_FIELD.keySet();
		int updated = 0;
		
		// 개인유저는 공통에서 덮어씌우는 것을 기본 값
		String default_event_list = "-";
		String default_value = "-";
		int default_max = -1;
		
		// 공통설정일 경우 기본값을
		if (this.RESIDENT_NAME.equals("DEFAULT")) {
			default_event_list = "*";
			default_value = "";
			default_max = 5;
		}
		
		if (!this.DATA.contains("Enable_Events")) {
			this.DATA.set("Enable_Events", default_event_list);
			updated++;
		}
		
		if (!this.DATA.contains("Language")) {
			this.DATA.set("Language", "English");
			updated++;
		}
		
		if (!this.DATA.contains("Super")) {
			this.DATA.set("Super", false);
			updated++;
		}
		
		if (!this.DATA.contains("Admin")) {
			this.DATA.set("Admin", false);
			updated++;
		}
		
		if (!this.DATA.contains("ALL_EXCEPT")) {
			this.DATA.set("ALL_EXCEPT", default_value);
			updated++;
		}
		
		if (!this.DATA.contains("ALL_BAN")) {
			this.DATA.set("ALL_BAN", default_value);
			updated++;
		}
		
		// 포션효과 이벤트
		// e.g) 신호기로부터 얻는 이펙트, 신호기에서 바꿀 이펙트, 최대 갯수

		this.DATA.set("<Effect>",  "----------------------<Efffect>----------------------");
		for (String name : effect_names) {
			if (!this.DATA.contains(name + "_EXCEPT")) {
				this.DATA.set(name + "_EXCEPT", default_value);
				updated++;
			}
			
			if (!this.DATA.contains(name + "_BAN")) {
				this.DATA.set(name + "_BAN", default_value);
				updated++;
			}
			
			if (!this.DATA.contains(name + "_MAX")) {
				this.DATA.set(name + "_MAX", default_max);
				updated++;
			}
		}
		
		this.DATA.set("<Enchant>",  "----------------------<Enchant>----------------------");
		
		// 인첸트 이벤트
		for (String name : enchant_names) {
			if (!this.DATA.contains(name + "_EXCEPT")) {
				this.DATA.set(name + "_EXCEPT", default_value);
				updated++;
			}
			
			if (!this.DATA.contains(name + "_BAN")) {
				this.DATA.set(name + "_BAN", default_value);
				updated++;
			}
		}
		
		// 아이템 이벤트
		this.DATA.set("<Craft>",  "----------------------<Craft>----------------------");
		for (String name : item_names) {
			if (!this.DATA.contains(name + "_EXCEPT")) {
				this.DATA.set(name + "_EXCEPT", default_value);
				updated++;
			}
			
			if (!this.DATA.contains(name + "_BAN")) {
				this.DATA.set(name + "_BAN", default_value);
				updated++;
			}
		}
		
		// 수정사항이 있으면 저장
		if (updated > 0) {
			this.DATA.saveConfig();	
		}
	}
	
	public void loadEvent() {
		Set<String> item_names = Main.ITEM_FIELD.keySet();
		Set<String> effect_names = Main.POTION_FIELD.keySet();
		Set<String> enchant_names = Main.ENCHANT_FIELD.keySet();
		
		// 유저데이터에서 가져오기
		String eventsString = this.DATA.getString("Enable_Events").replaceAll("\n", "").replaceAll(" ", "").toUpperCase();

		Boolean super_user = this.DATA.getBoolean("Super");
		Boolean admin_user = this.DATA.getBoolean("Admin");
		this.super_user = super_user;
		this.admin_user = admin_user;

		String lang = this.DATA.getString("Language");
		this.language = lang;
		
		if (!this.RESIDENT_NAME.equals("DEFAULT") && eventsString.equals("-")) {
			eventsString = Main.DEFAULT.DATA.getString("Enable_Events").replaceAll("\n", "").replaceAll(" ", "").toUpperCase();
		}
		
		ArrayList<String> eventList;
		if (eventsString.equals("*")) {
			eventList = new ArrayList<String>();
			eventList.addAll(item_names);
			eventList.addAll(effect_names);
			eventList.addAll(enchant_names);
		}
		else {
			eventList = new ArrayList<String>(Arrays.asList(eventsString.split(",")));
		}
		
		// 유저가 활성화하고자 하는 이벤트 리스트를 하나씩 순회
		for (String name : eventList) {
			// 유저가 설정한 아이템 이벤트가 존재하면
			String except = this.DATA.getString(name + "_EXCEPT");
			String ban = this.DATA.getString(name + "_BAN");
			
			if (Main.ITEM_FIELD.get(name) != null) {
				
				this.setActivate(name, true); // 활성화
				this.setItemFilter(name, except); // 아이템 필터링 업데이트
				this.setItemBan(name, ban); // 아이템 밴 업데이트
			}
			// 유저가 설정한 포션효과 이벤트가 존재하면
			else if (Main.POTION_FIELD.get(name) != null) {
				int max = this.DATA.getInt(name + "_MAX");
				
				this.setActivate(name, true); // 활성화
				this.setEffectFilter(name, except); // 포션 필터링 업데이트
				this.setEffectBan(name, ban); // 포션 밴 업데이트
				this.setEffectMax(name, max); // 포션 최대 갯수 업데이트
			}
			// 유저가 설정한 인첸트 이벤트가 존재하면
			else if (Main.ENCHANT_FIELD.get(name) != null) {
				this.setActivate(name, true); // 활성화
				this.setEnchantFilter(name, except); // 인첸트 필터링 업데이트
				this.setEnchantBan(name, ban); // 인첸트 밴 업데이트
			}
		}
	}
}
