package main;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

public class Main extends JavaPlugin {
	public static Plugin PLUGIN; // 해당 플러그인
	public static HashMap<String, Listener> EVENTS; // 사용할 이벤트 해시맵
	public static HashMap<UUID, RandomEvent> REGISTED_PLAYER; // 랜덤 아이템을 적용할 플레이어 해시맵
	public static RandomEvent COMMON; // 공통으로 적용할 해시맵
	public static RandomEvent ENTITY;
	public static int MAX_EFFECT_COUNT = 5;
	public static SimpleConfigManager MANAGER;
	public static SimpleConfig CONFIG;
	public static final HashMap<String, String[]> ITEM_FIELD = new HashMap<String, String[]>(); // 아이템 관련 이벤트 명
	public static final HashMap<String, String[]> POTION_FIELD = new HashMap<String, String[]>(); // 포션 관련 이벤트 명
	public static final HashMap<String, String[]> ENCHANT_FIELD = new HashMap<String, String[]>(); // 인첸트 관련 이벤트 명
	public static ArrayList<World> DISABLE_WORLD;
	
	// 플러그인 활성화 시,
	@Override
	public void onEnable() {
		// 이벤트 이름:설명리스트 해시맵
		ITEM_FIELD.put("PICKUP", new String[] {"아이템을 주웠을 때"});
		ITEM_FIELD.put("WORKBENCH", new String[] {"제작대를 사용했을 때"});
		ITEM_FIELD.put("CRAFTING",new String[] {"플레이어의 2x2 제작대를 사용했을 때"});
		ITEM_FIELD.put("FURNACE", new String[] {"화로를 사용했을 때"});
		ITEM_FIELD.put("BLAST_FURNACE", new String[] {"용광로를 사용했을 때"});
		ITEM_FIELD.put("SMOKER", new String[] {"훈연기를 사용했을 때"});
		ITEM_FIELD.put("BREWING", new String[] {"물약 제조가 완료됐을 때"});
		ITEM_FIELD.put("STONECUTTER", new String[] {"석재 절단기를 사용했을 때"});
		ITEM_FIELD.put("SMITHING", new String[] {"대장장이 작업대를 사용했을 때"});
		ITEM_FIELD.put("CARTOGRAPHY", new String[] {"지도 제작대를 사용했을 때"});
		ITEM_FIELD.put("LOOM", new String[] {"베틀을 사용했을 때"});
		ITEM_FIELD.put("ANVIL", new String[] {"모루를 사용했을 때"});
		ITEM_FIELD.put("ENCHANTING", new String[] {"마법부여가 완료됐을 때"});
		ITEM_FIELD.put("GRINDSTONE", new String[] {"숫돌을 사용했을 때"});
		ITEM_FIELD.put("MERCHANT", new String[] {"상인에게서 물건을 구입했을 때"});

		POTION_FIELD.put("POTION", new String[] {"물약 효과를 받았을 때"});

		ENCHANT_FIELD.put("ENCHANT", new String[] {"인첸트를 했을 때"});
		// 과정
		// 1. 플러그인 켜짐
		// 2. config.yml파일과 userdata폴더 체크 후 없으면 생성, 있으면 userdata의 유저들 static해시맵 변수로 불러오기 후 기본값은 null (키는 uuid, 값은 null)
		// 3. 새로운 유저 입장 시, userdata폴더에 데이터 있는지 체크 후 없으면 새로 생성, 기본 값 저장 후 static변수에 등록, 있으면 불러와서 static변수에 등록
		// 4. 유저 퇴장 시, 해시맵에서 값을 null로 변경
		
		
		// userdata의 정보를 기반으로 RandomEvent객체 생성
		// config파일에는 플러그인 활성화 여부
		// userdata에는 적용할 이벤트, 이벤트 별 바뀌게하지 않을 아이템, 이벤트 별 바꾸지 않을 아이템
		
		// config파일, userdata폴더 체크
		
		// config에서 Enable_plugin이 false이면 플러그인 실행 안함

		boolean isPluginOn = loadConfigFile(); // config파일을 생성 및 불러오고 성공 시 true 반환
		
		if (!isPluginOn) {
			return;
		}
		
		// config 파일 불러오기 성공 시
		PLUGIN = Bukkit.getPluginManager().getPlugin("RandomWorld"); // 플러그인 객체
		EVENTS = new HashMap<String, Listener>();
		EVENTS.put("pickup", new PickupItem());
		EVENTS.put("brew", new BrewPotion());
		EVENTS.put("enchant", new EnchantItem());
		EVENTS.put("inventoryclick", new CreateItem());
		REGISTED_PLAYER = new HashMap<UUID, RandomEvent>();
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();

		// 현재 접속 중인 플레이어들
		for (Player p : players) {
			RandomEvent re = new RandomEvent(p.getUniqueId().toString());
			REGISTED_PLAYER.put(p.getUniqueId(), re);
		}

		// 1. 이벤트를 아무도 안쓰는 경우 언레지스트기능 구현
		Bukkit.getPluginManager().registerEvents(EVENTS.get("pickup"), this);
		Bukkit.getPluginManager().registerEvents(EVENTS.get("brew"), this);
		Bukkit.getPluginManager().registerEvents(EVENTS.get("enchant"), this);
		Bukkit.getPluginManager().registerEvents(EVENTS.get("inventoryclick"), this);
		Bukkit.getPluginManager().registerEvents(new PlayerIO(), this);
		Bukkit.getPluginManager().registerEvents(new GivePotionEffect(), this);
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
			CONFIG.set("Enable_Plugin", true, "플러그인 활성화 여부");
			CONFIG.set("Enable_Entity", true, "유저 외 엔티티 랜덤 영향 여부");
			CONFIG.set("Max_effect_count", 5, "거북이모자 또는 돌고래");
			CONFIG.set("Disable_World", "", "랜덤효과 비활성화 맵 목록");
			CONFIG.saveConfig();
		}
		
		COMMON = new RandomEvent("COMMON"); // 공통 랜덤효과
		ENTITY = null; // 엔티티 랜덤효과
		MAX_EFFECT_COUNT = CONFIG.getInt("Max_effect_count");
		
		// 엔티티도 랜덤효과를 허용한 경우
		if (CONFIG.getBoolean("Enable_Entity")) {
			ENTITY = new RandomEvent("ENTITY");
		}
		// DISABLE_WORLD = CONFIG.getString("Disable_World");
		
		
		return CONFIG.getBoolean("Enable_Plugin");
	}
}
