package main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class RandomEvent {
	private final String residentName;
	private final SimpleConfig data; // 데이터
	private HashMap<String, Boolean> activated; // 각 이벤트 별, 활성화여부 해시맵
	private HashMap<String, HashMap<Material, Boolean>> itemFilter; // 랜덤 결과로 나오지 않을 아이템 해시맵
	private HashMap<String, HashMap<Material, Boolean>> itemBan; // 바꾸지 않을 아이템 해시맵
	private HashMap<String, HashMap<PotionEffectType, Boolean>> potionFilter; // 랜덤 결과로 나오지 않을 포션효과 해시맵
	private HashMap<String, HashMap<PotionEffectType, Boolean>> potionBan; // 바꾸지 않을 포션효과 해시맵
	private HashMap<String, HashMap<Enchantment, Boolean>> enchantFilter; // 랜덤 결과로 나오지 않을 인첸트효과 해시맵
	private HashMap<String, HashMap<Enchantment, Boolean>> enchantBan; // 바꾸지 않을 인첸트효과 해시맵
	
	// 생성자
	public RandomEvent(String resident_name) {
		this.residentName = resident_name;
		this.data = Main.MANAGER.getNewConfig("/userdata/" + resident_name + ".yml");
		this.activated = new HashMap<String, Boolean>();
		this.itemFilter = new HashMap<String, HashMap<Material, Boolean>>();
		this.itemBan = new HashMap<String, HashMap<Material, Boolean>>();
		this.potionFilter = new HashMap<String, HashMap<PotionEffectType, Boolean>>();
		this.potionBan = new HashMap<String, HashMap<PotionEffectType, Boolean>>();
		this.enchantFilter = new HashMap<String, HashMap<Enchantment, Boolean>>();
		this.enchantBan = new HashMap<String, HashMap<Enchantment, Boolean>>();
		
		// 유저데이터 만들고 등록하고 끝
		Set<String> item_names = Main.ITEM_FIELD.keySet();
		Set<String> effect_names = Main.POTION_FIELD.keySet();
		Set<String> enchant_names = Main.ENCHANT_FIELD.keySet();
		int updated = 0;
		
		if (!this.data.contains("Enable_Events")) {
			this.data.set("Enable_Events", "*", "활성화 할 이벤트 목록");
			updated++;
		}
		
		if (!this.data.contains("ALL_EXCEPT")) {
			this.data.set("ALL_EXCEPT", "", "모든 EXCEPT 이벤트에 적용");
			updated++;
		}
		
		if (!this.data.contains("ALL_BAN")) {
			this.data.set("ALL_BAN", "", "모든 BAN 이벤트에 적용");
			updated++;
		}
		
		for (String name : item_names) {
			if (!this.data.contains(name + "_EXCEPT")) {
				this.data.set(name + "_EXCEPT", "", Main.ITEM_FIELD.get(name));
				updated++;
			}
			
			if (!this.data.contains(name + "_BAN")) {
				this.data.set(name + "_BAN", "");
				updated++;
			}
		}
		
		for (String name : effect_names) {
			if (!this.data.contains(name + "_EXCEPT")) {
				this.data.set(name + "_EXCEPT", "", Main.POTION_FIELD.get(name));
				updated++;
			}
			
			if (!this.data.contains(name + "_BAN")) {
				this.data.set(name + "_BAN", "");
				updated++;
			}
		}
		
		for (String name : enchant_names) {
			if (!this.data.contains(name + "_EXCEPT")) {
				this.data.set(name + "_EXCEPT", "", Main.ENCHANT_FIELD.get(name));
				updated++;
			}
			
			if (!this.data.contains(name + "_BAN")) {
				this.data.set(name + "_BAN", "");
				updated++;
			}
		}
		
		if (updated > 0) {
			this.data.saveConfig();	
		}

		// 유저데이터에서 가져오기
		String eventsString = this.data.getString("Enable_Events").replaceAll("\n", "").replaceAll(" ", "").toUpperCase();
		
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
			
			String except = this.data.getString(name + "_EXCEPT");
			String ban = this.data.getString(name + "_BAN");
			
			if (Main.ITEM_FIELD.containsKey(name)) {
				this.setActivate(name, true); // 활성화
				this.setItemFilter(name, except); // 아이템 필터링 업데이트
				this.setItemBan(name, ban); // 아이템 밴 업데이트
			}
			// 유저가 설정한 포션효과 이벤트가 존재하면
			else if (Main.POTION_FIELD.containsKey(name)) {
				this.setActivate(name, true); // 활성화
				this.setEffectFilter(name, except); // 포션 필터링 업데이트
				this.setEffectBan(name, ban); // 포션 밴 업데이트
			}
			// 유저가 설정한 인첸트 이벤트가 존재하면
			else if (Main.ENCHANT_FIELD.containsKey(name)) {
				this.setActivate(name, true); // 활성화
				this.setEnchantFilter(name, except); // 인첸트 필터링 업데이트
				this.setEnchantBan(name, ban); // 인첸트 밴 업데이트
			}
		}
	}
	
	
	public boolean getActivate(String eventName) {
		if (this.activated.get(eventName) != null && this.activated.get(eventName)) {
			return true;
		}
		return false;
	}
	
	
	public void setActivate(String eventName, boolean b) {
		this.activated.put(eventName, b);
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
	
	
	public boolean isItemBan(String eventName, Material material) {
		if (this.itemBan.get(eventName).get(material) != null) {
			return true;
		}
		return false;
	}
	
	public boolean isEffectBan(String eventName, PotionEffectType effect) {
		return true;
	}
	
	public boolean isEnchantBan(String eventName, Enchantment enchant) {
		return true;
	}
	
	
	public void setEffectBan(String eventName, String effectList) {
		HashMap<PotionEffectType, Boolean> negativeEffects = new HashMap<PotionEffectType, Boolean>();
		effectList = effectList.replaceAll("\n", "").replaceAll(" ", "");
		String common_effectList = this.data.getString("ALL_BAN").replaceAll("\n", "").replaceAll(" ", "");
		String all_effectList = effectList.concat("," + common_effectList).replaceAll(",,", "").toLowerCase();
		
		// 공통설정객체가 아닌 사용자설정객체이고, 사용자설정이 공란이면 공통설정(기본값) 적용
		if (!this.residentName.equals("DEFAULT") && all_effectList.equals(",")) {
			negativeEffects = Main.DEFAULT.potionBan.get(eventName);
			this.potionBan.put(eventName, negativeEffects);
			return;
		}
		
		if (effectList.equals("*") || common_effectList.equals("*")) {
			Iterator<PotionEffectType> re = Registry.EFFECT.iterator();
			while (re.hasNext()) {
				negativeEffects.put(re.next(), true);
			}
		}
		
		for (String effect : all_effectList.split(",")) {
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
		String common_effectList = this.data.getString("ALL_EXCEPT").replaceAll("\n", "").replaceAll(" ", ""); // 공통 필터링
		String all_effectList = effectList.concat("," + common_effectList).replaceAll(",,", ",").toLowerCase(); // 유저 + 공통 필터링
		
		// 공통설정객체가 아닌 사용자성정객체이고, 개인사용자 설정이 없어서 기본값으로
		if (!this.residentName.equals("DEFAULT") && all_effectList.equals(",")) {
			valuable = Main.DEFAULT.potionFilter.get(eventName);
			this.potionFilter.put(eventName, valuable);
			return;
		}
		
		// 전부 제외할 때
		if (effectList.equals("*") || common_effectList.equals("*")) {
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
			PotionEffectType key = Registry.EFFECT.match(effect);
			if (key != null) {
				valuable.remove(key);
			}
		}
		
		this.potionFilter.put(eventName, valuable);
	}
	
	
	public void setEnchantBan(String eventName, String enchantList) {
		HashMap<Enchantment, Boolean> negativeEnchants = new HashMap<Enchantment, Boolean>();
		enchantList = enchantList.replaceAll("\n", "").replaceAll(" ", "");
		String common_enchantList = this.data.getString("ALL_BAN").replaceAll("\n", "").replaceAll(" ", "");
		String all_enchantList = enchantList.concat("," + common_enchantList).replaceAll(",,", "").toLowerCase();
		
		// 공통설정객체가 아닌 사용자설정객체이고, 개인 사용자 설정이 없으면 기본값으로 설정
		if (!this.residentName.equals("DEFAULT") && all_enchantList.equals(",")) {
			negativeEnchants = Main.DEFAULT.enchantBan.get(eventName);
			this.enchantBan.put(eventName, negativeEnchants);
			return;
		}
		
		if (enchantList.equals("*") || common_enchantList.equals("*")) {
			Iterator<Enchantment> re = Registry.ENCHANTMENT.iterator();
			while (re.hasNext()) {
				negativeEnchants.put(re.next(), true);
			}
		}
		
		for (String enchant : all_enchantList.split(",")) {
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
		String common_enchantList = this.data.getString("ALL_EXCEPT").replaceAll("\n", "").replaceAll(" ", ""); // 공통 필터링
		String all_enchantList = enchantList.concat("," + common_enchantList).replaceAll(",,", ",").toLowerCase(); // 유저 + 공통 필터링

		// 공통설정객체가 아닌 사용자설정객체이고, 개인 사용자 설정이 없으면 기본값으로 설정
		if (!this.residentName.equals("DEFAULT") && all_enchantList.equals(",")) {
			valuable = Main.DEFAULT.enchantFilter.get(eventName);
			this.enchantFilter.put(eventName, valuable);
			return;
		}
		
		// 전부 제외할 때
		if (enchantList.equals("*") || common_enchantList.equals("*")) {
			return;
		}

		// 포션 효과 해시맵 생성
		Iterator<Enchantment> re = Registry.ENCHANTMENT.iterator();
		while (re.hasNext()) {
			// key는 minecraft:인첸트명
			Enchantment ppp = re.next();
			valuable.put(ppp, true);
//			System.out.println(ppp.getKey());
		}
		
		// 전체 포션효과 중, userdata에 적힌 포션효과만 골라서 remove
		for (String enchant : all_enchantList.split(",")) {
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
		String common_itemList = this.data.getString("ALL_BAN").replaceAll("\n", "").replaceAll(" ", "");
		String all_itemList = itemList.concat("," + common_itemList).replaceAll(",,", "").toLowerCase();
		
		// 공통설정객체가 아닌 사용자설정객체이고, 유저에게 설정된 item ban이 없으면 공통 item ban 설정으로 덮어씌우기
		if (!this.residentName.equals("DEFAULT") && all_itemList.equals(",")) {
			negativeItems = Main.DEFAULT.itemBan.get(eventName);
			this.itemBan.put(eventName, negativeItems);
			return;
		}
		
		if (itemList.equals("*") || common_itemList.equals("*")) {
			Material[] materials = Material.values();
			for (Material m : materials) {
				if (m.isItem()) {
					negativeItems.put(m, true);
				}
			}
		}
		
		for (String item : all_itemList.split(",")) {
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
		String common_itemList = this.data.getString("ALL_EXCEPT").replaceAll("\n", "").replaceAll(" ", "");
		String all_itemList = itemList.concat("," + common_itemList).replaceAll(",,", ",").toLowerCase();
		
		// 공통설정객체가 아닌 사용자설정객체이고, 사용자 설정이 없으면 기본값 적용
		if (!this.residentName.equals("DEFAULT") && all_itemList.equals(",")) {
			valuable = Main.DEFAULT.itemFilter.get(eventName);
			this.itemFilter.put(eventName, valuable);
			return;
		}
		
		// 전부 제외할 때
		if (itemList.equals("*") || common_itemList.equals("*")) {
			return;
		}

		// 사용 가능한 아이템만 판별 - 이 코드는 자주 호출하지 않는 것이 좋음
		Material[] materials = Material.values();
		for (Material m : materials) {
			if (m.isItem()) {
				valuable.put(m, true);
			}
		}
		
		// 전체 아이템 중, userdata.yml에 적힌 아이템 리스트만 골라서 remove
		for (String item : all_itemList.split(",")) {
			valuable.remove(Material.matchMaterial(item));
		}
		
		this.itemFilter.put(eventName, valuable);
	}
}
