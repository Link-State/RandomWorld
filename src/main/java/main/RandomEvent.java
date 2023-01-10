package main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class RandomEvent {
	private final Player p;
	private final SimpleConfig userdata;
	private HashMap<String, Boolean> activated;
	private HashMap<String, ArrayList<Material>> itemFilter;
	private HashMap<String, HashMap<Material, Boolean>> itemBan;
	
	public RandomEvent(Player p) {
		this.p = p;
		this.activated = new HashMap<String, Boolean>();
		this.itemFilter = new HashMap<String, ArrayList<Material>>();
		this.itemBan = new HashMap<String, HashMap<Material, Boolean>>();
		
		File file = new File(Main.PLUGIN.getDataFolder() + File.separator + "userdata" + File.separator + this.p.getUniqueId() + ".yml");
		if (file.exists()) {
			this.userdata = Main.MANAGER.getNewConfig("/userdata/" + p.getUniqueId() + ".yml");
		} else {
			this.userdata = null;
		}
		
		SimpleConfig configToApply;
		if (this.userdata != null) {
			configToApply = this.userdata;
		} else {
			configToApply = Main.CONFIG;
		}
		
		String eventList = configToApply.getString("Enable_Events").replaceAll("\n", "").replaceAll(" ", "").replaceAll(",+", ",").toUpperCase();
		String[] eventNames;
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
		} else {
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
