package main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class RandomEvent {
	private final Player p; // 플레이어
	private final SimpleConfig userdata; // 유저 데이터
	private HashMap<String, Boolean> activated; // 각 이벤트 별, 활성화여부 맵
	private HashMap<String, HashMap<Material, Boolean>> itemFilter; // 랜덤 결과로 나오지 않을 아이템 맵
	private HashMap<String, HashMap<Material, Boolean>> itemBan; // 바꾸지 않을 아이템 맵
	
	
	// 생성자
	public RandomEvent(Player p) {
		this.p = p;
		this.userdata = Main.MANAGER.getNewConfig("/userdata/" + p.getUniqueId() + ".yml");
		this.activated = new HashMap<String, Boolean>();
		this.itemFilter = new HashMap<String, HashMap<Material, Boolean>>();
		this.itemBan = new HashMap<String, HashMap<Material, Boolean>>();
		
		// 유저데이터 만들고 등록하고 끝
		Set<String> event_names = Main.EVENTNAME_FIELD.keySet();
		
		if (!this.userdata.contains("Enable_Events")) {
			this.userdata.set("Enable_Events", "*", "활성화 할 이벤트 목록");
			this.userdata.set("ALL_EXCEPT", "", "모든 EXCEPT 이벤트에 적용");
			this.userdata.set("ALL_BAN", "", "모든 BAN 이벤트에 적용");
			
			for (String event_name : event_names) {
				this.userdata.set(event_name + "_EXCEPT", "", Main.EVENTNAME_FIELD.get(event_name));
				this.userdata.set(event_name + "_BAN", "");
			}
			
			this.userdata.saveConfig();
		}
		
		// 유저데이터에서 가져오기
		String eventsString = this.userdata.getString("Enable_Events").replaceAll("\n", "").replaceAll(" ", "").toUpperCase();
		
		String[] eventList;
		if (eventsString.equals("*")) {
			eventList = event_names.toArray(new String[0]);
		}
		else {
			eventList = eventsString.split(",");
		}
		
		// 유저가 활설화하고자 하는 이벤트 리스트를 하나씩 순회
		for (String event_name : eventList) {
			// 유저가 설정한 이벤트가 존재하면
			if (Main.EVENTNAME_FIELD.containsKey(event_name)) {
				this.setActivate(event_name, true); // 활성화
				
				String itemList_Except = this.userdata.getString(event_name + "_EXCEPT"); // 해당 이벤트의 Except 아이템들
				this.setFilter(event_name, itemList_Except); // 아이템 필터링 업데이트

				String itemList_Ban = this.userdata.getString(event_name + "_BAN"); // 해당 이벤트의 Ban 아이템들
				this.setBan(event_name, itemList_Ban); // 아이템 밴 업데이트
			}
		}
	}
	
	
	public Player getPlayer() {
		return this.p;
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
	
	
	public boolean isBan(String eventName, Material material) {
		if (this.itemBan.get(eventName).get(material) != null) {
			return true;
		}
		return false;
	}
	
	
	public void setBan(String eventName, String itemList) {
		HashMap<Material, Boolean> negativeItems = new HashMap<Material, Boolean>();
		itemList = itemList.replaceAll("\n", "").replaceAll(" ", "");
		String common_itemList = this.userdata.getString("ALL_BAN").replaceAll("\n", "").replaceAll(" ", "");
		String all_itemList = itemList.concat("," + common_itemList).replaceAll(",,", "").toLowerCase();
		
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
	
	
	public void setFilter(String eventName, String itemList) {
		HashMap<Material, Boolean> valuable = new HashMap<Material, Boolean>();
		
		itemList = itemList.replaceAll("\n", "").replaceAll(" ", "");
		String common_itemList = this.userdata.getString("ALL_EXCEPT").replaceAll("\n", "").replaceAll(" ", "");
		String all_itemList = itemList.concat("," + common_itemList).replaceAll(",,", ",").toLowerCase();
		
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
