package main;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

public class Main extends JavaPlugin {
	public static Plugin PLUGIN; // 해당 플러그인
	public static SimpleConfigManager MANAGER;
	public static SimpleConfig CONFIG;
	public static HashMap<String, Listener> EVENTS; // 이벤트 목록 해시맵
	public static HashMap<UUID, RandomEvent> REGISTED_PLAYER; // 플레이어에게 적용할 랜덤이벤트 해시맵

	public static final HashMap<InventoryType, Boolean> DEACTIVATED_INVENTORY_TYPE = new HashMap<InventoryType, Boolean>(); // result슬롯 없는 인벤토리 해시맵
	public static final HashMap<String, InventoryType> ACTIVATED_INVENTORY_TYPE = new HashMap<String, InventoryType>(); // 인벤토리 유형 해시맵
	public static final HashMap<InventoryType, Integer> RESULT_SLOT = new HashMap<InventoryType, Integer>(); // 결과 슬롯 해시맵
	public static final HashMap<String, Boolean> ITEM_FIELD = new HashMap<String, Boolean>(); // 아이템 관련 이벤트 명
	public static final HashMap<String, Boolean> POTION_FIELD = new HashMap<String, Boolean>(); // 포션 관련 이벤트 명
	public static final HashMap<String, Boolean> ENCHANT_FIELD = new HashMap<String, Boolean>(); // 인첸트 관련 이벤트 명
	
	public static RandomEvent DEFAULT; // 공통으로 적용 할 랜덤이벤트 해시맵
	public static RandomEvent ENTITY; // 엔티티에게 적용 할 랜덤이벤트 해시맵
	public static HashMap<String, World> DISABLE_WORLD; // 월드 제한 수
	
	// 플러그인 활성화 시,
	@Override
	public void onEnable() {
		// 이벤트 이름:설명리스트 해시맵
//		ITEM_FIELD.put("WORKBENCH", new String[] {"제작대를 사용했을 때"});
//		ITEM_FIELD.put("CRAFTING",new String[] {"플레이어의 2x2 제작대를 사용했을 때"});
//		ITEM_FIELD.put("FURNACE", new String[] {"화로를 사용했을 때"});
//		ITEM_FIELD.put("BLAST_FURNACE", new String[] {"용광로를 사용했을 때"});
//		ITEM_FIELD.put("SMOKER", new String[] {"훈연기를 사용했을 때"});
//		ITEM_FIELD.put("STONECUTTER", new String[] {"석재 절단기를 사용했을 때"});
//		ITEM_FIELD.put("SMITHING", new String[] {"대장장이 작업대를 사용했을 때"});
//		ITEM_FIELD.put("CARTOGRAPHY", new String[] {"지도 제작대를 사용했을 때"});
//		ITEM_FIELD.put("LOOM", new String[] {"베틀을 사용했을 때"});
//		ITEM_FIELD.put("ANVIL", new String[] {"모루를 사용했을 때"});
//		ITEM_FIELD.put("GRINDSTONE", new String[] {"숫돌을 사용했을 때"});
//		ITEM_FIELD.put("MERCHANT", new String[] {"상인에게서 물건을 구입했을 때"});
		
		// 포션이펙트 관련
		Cause[] effect_causes = EntityPotionEffectEvent.Cause.values();
		for (Cause cause : effect_causes) {
			if (cause.equals(EntityPotionEffectEvent.Cause.PLUGIN) ||
				cause.equals(EntityPotionEffectEvent.Cause.COMMAND) ||
				cause.equals(EntityPotionEffectEvent.Cause.MILK) ||
				cause.equals(EntityPotionEffectEvent.Cause.ILLUSION) ||
				cause.equals(EntityPotionEffectEvent.Cause.EXPIRATION) ||
				cause.equals(EntityPotionEffectEvent.Cause.DEATH) ||
				cause.equals(EntityPotionEffectEvent.Cause.CONVERSION)) {
				continue;
			}
			
			POTION_FIELD.put(cause.name(), true);
		}
		POTION_FIELD.put("GET_EFFECT_ITEM",true);
		POTION_FIELD.put("BREWING", true);
		
		// 인첸트 관련
		ENCHANT_FIELD.put("ENCHANTING", true);
		ENCHANT_FIELD.put("GET_ENCHANT_ITEM", true);
		
		// 인벤토리 클릭 관련
		ITEM_FIELD.put("PICKUP", true);
		ITEM_FIELD.put("GET_BRUSHABLE_ITEM", true);
		
		// config파일을 생성 및 불러오고 성공 시 true 반환
		boolean isPluginOn = loadConfigFile(); 
		if (!isPluginOn) {
			return;
		}
		
		// config 파일 불러오기 성공 시
		PLUGIN = Bukkit.getPluginManager().getPlugin("RandomWorld"); // 플러그인 객체
		
		// 이벤트 해시맵에 저장
		EVENTS = new HashMap<String, Listener>();
		EVENTS.put("pickup", new PickupItem());
		EVENTS.put("brew", new BrewPotion());
		EVENTS.put("enchant", new EnchantItem());
		EVENTS.put("inventoryclick", new CreateItem());
		EVENTS.put("potion", new GivePotionEffect());
		EVENTS.put("playerIO", new PlayerIO());
		
		// 서버 내 랜덤효과 쓰는 플레이어 등록
		REGISTED_PLAYER = new HashMap<UUID, RandomEvent>();
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		for (Player p : players) {
			RandomEvent re = new RandomEvent(p.getUniqueId().toString());
			REGISTED_PLAYER.put(p.getUniqueId(), re);
		}

		// 이벤트 등록
		Bukkit.getPluginManager().registerEvents(EVENTS.get("pickup"), this);
		Bukkit.getPluginManager().registerEvents(EVENTS.get("brew"), this);
		Bukkit.getPluginManager().registerEvents(EVENTS.get("enchant"), this);
		Bukkit.getPluginManager().registerEvents(EVENTS.get("inventoryclick"), this);
		Bukkit.getPluginManager().registerEvents(EVENTS.get("potion"), this);
		Bukkit.getPluginManager().registerEvents(EVENTS.get("playerIO"), this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
//		 먼저 plugin.yml 작성
//		/randomworld enable
//		/randomworld disable
//		/randomworld <이벤트 리스트> - userdata 있는 유저는 제외
//		/randomworld <아이템 리스트> - userdata 있는 유저는 제외
//		/randomworld <이벤트> <아이템 리스트> - userdata 있는 유저는 제외
//		/randomworld <플레이어 이름> <이벤트 리스트> - userdata 생성
//		/randomworld <플레이어 이름> <이벤트> <아이템 리스트> - userdata 생성
//		/randomworld reset <플레이어 이름> - userdata 삭제
//		/randomworld reset * - userdata 모두 삭제
		return super.onCommand(sender, command, label, args);
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
			String[] events = CONFIG.getString("Enable_Events").replaceAll(" ", "").replaceAll(",+", ",").toUpperCase().split(",");
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
	
	
	// config파일 불러오기
	private boolean loadConfigFile() {
		File userdata = new File(this.getDataFolder() + File.separator + "userdata"); // 유저 파일
		
		// 유저데이터 폴더가 없는 경우 생성
		if (!userdata.exists()) {
			try {
				userdata.mkdirs();
			} catch (Exception e) {
				e.printStackTrace();
				userdata = null;
			}
		}
		
		MANAGER = new SimpleConfigManager(this);
		CONFIG = MANAGER.getNewConfig("config.yml"); // config 파일
		
		// config파일 작성
		if (!CONFIG.contains("Enable_Plugin")) {
			CONFIG.set("Enable_Plugin", true);
			CONFIG.set("Enable_Entity", true);
			CONFIG.set("Disable_World", "");
			CONFIG.set("DEACTIVATED", "");
			CONFIG.set("ACTIVATED", "");
			CONFIG.saveConfig();
		}
		
		loadSetting();
		
		return CONFIG.getBoolean("Enable_Plugin");
	}
	
	private void loadSetting() {
		
		// 파일 불러오기	
		String deactivated = CONFIG.getString("DEACTIVATED");
		String activated = CONFIG.getString("ACTIVATED");
		InventoryType invType;
		
		// result타입이 없는 인벤토리들을 파일에서 불러오기
		if (!deactivated.isEmpty()) {
			for (String key : deactivated.split(",")) {
				try {
					invType = InventoryType.valueOf(key);
					DEACTIVATED_INVENTORY_TYPE.put(invType, true);
				} catch (IllegalArgumentException err) {
					System.out.println("[Plugin-RandomWorld] YOU NEED TO FIX 'DEACTIVATED' at config.yml\nThe wrong Inventory Name filled in.");
				}
			}	
		}
		
		// result타입이 있는 인벤토리들을 파일에서 불러오기
		if (!activated.isEmpty()) {
			for (String key : activated.split(",")) {
				if (key == null || key.isEmpty()) {
					continue;
				}
				
				String[] tuple = key.split("@");
				if (tuple.length != 2) {
					continue;	
				}

				// 인벤토리 이름
				String name = tuple[0];
				if (name == null || name.isEmpty()) {
					continue;
				}
				
				try {
					int slotID = Integer.parseInt(tuple[1]); // NumberFormatException
					invType = InventoryType.valueOf(name); // IllegalArgumentException
					
					ITEM_FIELD.put(name, true);
					ACTIVATED_INVENTORY_TYPE.put(name, invType);
					RESULT_SLOT.put(invType, slotID);
					
				} catch (NumberFormatException err) {
					System.out.println("[Plugin-RandomWorld] YOU NEED TO FIX 'ACTIVATED' at config.yml\n" + name + "@");
				} catch (IllegalArgumentException err) {
					System.out.println("[Plugin-RandomWorld] YOU NEED TO FIX 'ACTIVATED' at config.yml\nThe wrong Inventory Name filled in.");
				}
			}
		}

		DEFAULT = new RandomEvent("DEFAULT"); // 공통 랜덤효과
		ENTITY = null; // 엔티티 랜덤효과
		
		// 엔티티도 랜덤효과를 허용한 경우
		if (CONFIG.getBoolean("Enable_Entity")) {
			ENTITY = new RandomEvent("ENTITY");
		}
		// DISABLE_WORLD = CONFIG.getString("Disable_World");
		
	}
}
