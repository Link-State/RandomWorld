package main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class RandomEvent {
	private final Player p; // 플레이어
	private final SimpleConfig userdata; // 유저 데이터
	private HashMap<String, Boolean> activated; // 각 이벤트 별, 활성화여부 맵
	private HashMap<String, ArrayList<Material>> itemFilter; // 랜덤 결과로 나오지 않을 아이템 맵
	private HashMap<String, HashMap<Material, Boolean>> itemBan; // 바꾸지 않을 아이템 맵
	private String[] eventNames = new String[] {
			"PICKUP",
			"WORKBENCH",
			"CRAFTING",
			"FURNACE",
			"BLAST_FURNACE",
			"SMOKER",
			"BREWING",
			"STONECUTTER",
			"SMITHING",
			"CARTOGRAPHY",
			"LOOM",
			"ANVIL",
			"ENCHANTING",
			"GRINDSTONE",
			"MERCHANT"
		};
	private String[][] settingDescript = {
			{"아이템을 주웠을 때"},
			{"제작대를 사용했을 때"},
			{"플레이어의 2x2 제작대를 사용했을 때"},
			{"화로를 사용했을 때"},
			{"용광로를 사용했을 때"},
			{"훈연기를 사용했을 때"},
			{"물약 제조가 완료됐을 때"},
			{"석재 절단기를 사용했을 때"},
			{"대장장이 작업대를 사용했을 때"},
			{"지도 제작대를 사용했을 때"},
			{"베틀을 사용했을 때"},
			{"모루를 사용했을 때"},
			{"마법부여가 완료됐을 때"},
			{"숫돌을 사용했을 때"},
			{"상인에게서 물건을 구입했을 때"}
	};
	
	// 생성자
	public RandomEvent(Player p) {
		this.p = p;
		this.activated = new HashMap<String, Boolean>();
		this.itemFilter = new HashMap<String, ArrayList<Material>>();
		this.itemBan = new HashMap<String, HashMap<Material, Boolean>>();
		
		// 유저 개인 설정파일
		File file = new File(Main.PLUGIN.getDataFolder() + File.separator + "userdata" + File.separator + this.p.getUniqueId() + ".yml");
		if (!file.exists()) {
			
			// 유저데이터 만들고 등록하고 끝
			SimpleConfig userdata = Main.MANAGER.getNewConfig("/userdata/" + p.getUniqueId() + ".yml");
			userdata.set("Enable Events", "*", "활성화 할 이벤트 목록");
			userdata.set("COMMON_EXCEPT", "", "모든 EXCEPT 이벤트에 적용");
			userdata.set("COMMON_BAN", "", "모든 BAN 이벤트에 적용");
			for (int i = 0; i < eventNames.length; i++) {
				userdata.set(eventNames[i] + "_EXCEPT", "", settingDescript[i]);
				userdata.set(eventNames[i] + "_BAN", "", settingDescript[i]);
			}
			
			userdata.saveConfig();
		}
		
		this.userdata = Main.MANAGER.getNewConfig("/userdata/" + p.getUniqueId() + ".yml");
		
		
		// 개인 설정파일에 따를 것인지, 공통 설정을 따를 것인지,
		SimpleConfig configToApply;
		if (this.userdata != null) {
			configToApply = this.userdata;
		} else {
			configToApply = Main.CONFIG;
		}
		
		// 적용된 이벤트 목록
		String eventList = configToApply.getString("Enable_Events").replaceAll("\n", "").replaceAll(" ", "").replaceAll(",+", ",").toUpperCase();
		
		// 이벤트 목록이 비어있는 경우 전체 이벤트 등록
		if (eventList.isEmpty() || eventList.equals(",")) {
			eventNames = new String[] {
				"PICKUP",
				"BREWING",
				"ENCHANTING",
				"WORKBENCH",
				"CRAFTING",
				"FURNACE",
				"BLAST_FURNACE",
				"SMOKER",
				"STONECUTTER",
				"SMITHING",
				"CARTOGRAPHY",
				"LOOM",
				"ANVIL",
				"GRINDSTONE",
				"MERCHANT"
			};
		}
		// 명시된 이벤트만 등록
		else {
			eventNames = eventList.split(",");
		}
		
		for (String eventName : eventNames) {
			this.activated.put(eventName, true);
			String itemList_Except = configToApply.getString(eventName + "_EXCEPT");
			String itemList_Ban = configToApply.getString(eventName + "_BAN");
			this.setFilter(eventName, itemList_Except);
			this.setBan(eventName, itemList_Ban);
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
		if (this.activated.get(eventName) != null && this.activated.get(eventName)) {
			ArrayList<Material> eventMaterials = this.itemFilter.get(eventName);
			if (eventMaterials != null && eventMaterials.size() > 0) {
				int randIdx = (int)(Math.random() * eventMaterials.size());
				return eventMaterials.get(randIdx);
			}
		}
		return null;
	}
	
	
	public boolean isBan(String eventName, Material material) {
		if (this.itemBan.get(eventName).get(material) != null) {
			return this.itemBan.get(eventName).get(material);
		}
		return false;
	}
	
	
	public void setBan(String eventName, String itemList) {
		HashMap<Material, Boolean> negativeItems = new HashMap<Material, Boolean>();

		if (!itemList.isEmpty()) {
			int unmatchCount = 0;
			String[] itemNames = itemList.replaceAll("\n", "").replaceAll(" ", "").replaceAll(",+", ",").toLowerCase().split(",");
			for (String itemName : itemNames) {
				Material material = Material.matchMaterial(itemName);
				if (material != null) {
					negativeItems.put(material, true);
				} else {
					unmatchCount++;
				}
			}
			System.out.println("unmatched " + unmatchCount + " material(s)");
		}
		
		this.itemBan.put(eventName, negativeItems);
	}
	
	
	public void setFilter(String eventName, String itemList) {
		ArrayList<Material> negativeItems = new ArrayList<Material>();
		HashMap<Material, Material> buffer = new HashMap<Material, Material>();

		// 이 코드는 자주 호출하지 않는 것이 좋음
		Material[] materials = Material.values();
		for (Material m : materials) {
			if (m.isItem()) {
				buffer.put(m, m);
			}
		}
		
		if (!itemList.isEmpty()) {
			int unmatchCount = 0;
			String[] itemNames = itemList.replaceAll("\n", "").replaceAll(" ", "").replaceAll(",+", ",").toLowerCase().split(",");
			
			for (String itemName : itemNames) {
				Material material = Material.matchMaterial(itemName);
				if (material != null) {
					negativeItems.add(material);
				} else {
					unmatchCount++;
				}
			}
			
			System.out.println("unmatched " + unmatchCount + " material(s)");
		}
		
		for (Material material : negativeItems) {
			buffer.remove(material);
		}
		this.itemFilter.put(eventName, new ArrayList<Material>(buffer.values()));
	}
}
