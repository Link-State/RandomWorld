package main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

public class Main extends JavaPlugin {
	public static Plugin PLUGIN; // 해당 플러그인
	public static SimpleConfigManager MANAGER;
	public static SimpleConfig CONFIG;
	public static SimpleConfig LANGUAGE;
	public static HashMap<String, Listener> EVENTS; // 이벤트 목록 해시맵
	public static HashMap<UUID, RandomEvent> REGISTED_PLAYER; // 플레이어에게 적용할 랜덤이벤트 해시맵
	public static HashMap<Integer, String> LANGUAGE_DATA;

	public static final HashMap<InventoryType, Boolean> DEACTIVATED_INVENTORY_TYPE = new HashMap<InventoryType, Boolean>(); // result슬롯 없는 인벤토리 해시맵
	public static final HashMap<String, InventoryType> ACTIVATED_INVENTORY_TYPE = new HashMap<String, InventoryType>(); // 인벤토리 유형 해시맵
	public static final HashMap<InventoryType, Integer> RESULT_SLOT = new HashMap<InventoryType, Integer>(); // 결과 슬롯 해시맵
	public static final HashMap<String, Boolean> ITEM_FIELD = new HashMap<String, Boolean>(); // 아이템 관련 이벤트 명
	public static final HashMap<String, Boolean> POTION_FIELD = new HashMap<String, Boolean>(); // 포션 관련 이벤트 명
	public static final HashMap<String, Boolean> ENCHANT_FIELD = new HashMap<String, Boolean>(); // 인첸트 관련 이벤트 명
	
	public static RandomEvent DEFAULT; // 공통으로 적용 할 랜덤이벤트 해시맵
	public static HashMap<EntityType, RandomEvent> REGISTED_ENTITY; // 엔티티에게 적용 할 랜덤이벤트 해시맵
	public static HashMap<World, Boolean> DISABLE_WORLD; // 월드 제한 수
	
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
		EVENTS.put("gui", new InventoryGUI_Listener());
		
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
		Bukkit.getPluginManager().registerEvents(EVENTS.get("playerIO"), this);

		// 명령어 자동완성 등록
		this.getCommand("randomworld").setTabCompleter(new RandomWorldCommand());
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
//		/randomworld <add | remove | set> <user | entity> <이름> <설정 이름> <설정... | *>
//		/randomworld setting
//		/randomworld permission <이름> <user | admin | super>
		
		if (args[0].equals("Link-State:test")) {
			/*
			 * 
			 * TEST-CODE
			 *
			 */
			Player p = (Player) sender;
			InventoryGUI gui = new InventoryGUI(p);
			
			return true;
		}

		if (!label.equals("randomworld")) {
			return false;
		}

		boolean isSuccess = false;
		
		switch (args.length) {
			case 1 : {
				// 설정 GUI창 열기 명령어
				if (args[0].equals("setting")) {
					// 명령어 실행자가 플레이어가 아니면 실행하지 않음
					if (!(sender instanceof Player)) {
						break;
					}
					
					Player p = (Player) sender;
					
					// 권한 검사 (op이거나, config.yml에 목록에 있거나)
					int rank = RandomWorldCommand.getRank(p);
					
					// 일반유저 또는 유저가 아닌 경우
					if (rank <= 1) {
						p.sendMessage(ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "권한이 없습니다.");
						break;
					}
					
					isSuccess = RandomWorldCommand.openSettingGUI(p, rank);
				}
				break;
			}
			case 3 : {
				// 권한 설정
				if (args[0].equals("permission")) {
					// 권한 검사 (op이거나, config.yml에 목록에 있거나)
					if (sender instanceof Player) {
						Player p = (Player) sender;
						int rank = RandomWorldCommand.getRank(p.getUniqueId());
						
						if (rank <= 2) {
							p.sendMessage(ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "권한이 없습니다.");
							return false;
						}
					}
					
					String player_name = args[1]; // 권한 수정 할 유저 이름
					
					// 해당 유저 권한 추가
					isSuccess = RandomWorldCommand.setPermission(sender, args[2], player_name);
				}
				
				break;
			}
			case 4 : {
				break;
			}
			default : {
				if (args.length < 5) {
					break;
				}

				// 해당 이벤트를 설정
				if (args[0].equals("add") || args[0].equals("remove") || args[0].equals("set")) {
					// 권한 검사 (op이거나, config.yml에 목록에 있거나)
					if (sender instanceof Player) {
						Player p = (Player) sender;
						int rank = RandomWorldCommand.getRank(p.getUniqueId());
						
						if ((rank <= 1 && args[1].equals("user") && p.getName().equals(args[2])) || (rank <= 2)) {
							p.sendMessage(ChatColor.GREEN + "[RandomWorld] : " + ChatColor.RED + "권한이 없습니다.");
							return false;
						}
						
					}
					
					String cmd_option = args[0];
					String entityType = args[1];
					String entityName = args[2];
					String eventName = args[3];
					ArrayList<String> settings = null;
					
					// 해당 이벤트를 공란으로 설정
					if (args[4].equals("\"\"") || args[4].equals("''")) {
						settings = new ArrayList<String>();
					}
					// 명령어길이가 5 이상일 때
					else {
						settings = new ArrayList<String>(Arrays.asList(args));
						settings.remove(0); // add|remove|set 삭제
						settings.remove(0); // entity | player 삭제
						settings.remove(0); // 엔티티 이름 삭제
						settings.remove(0); // 이벤트명 삭제
						
						for (int i = 0; i < settings.size(); i++) {
							settings.set(i, settings.get(i).toUpperCase());
						}
					}
					
					// 변경
					isSuccess = RandomWorldCommand.setEvents(sender, cmd_option, entityType, entityName, eventName, settings);
				}
			
				
				break;
			}
		}
		
		return isSuccess;
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
			CONFIG.set("Language", "ko-kr");
			CONFIG.set("DEACTIVATED", "");
			CONFIG.set("ACTIVATED", "");
			CONFIG.saveConfig();
		}
		
		loadSetting();
		
//		loadLanguage();
		
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
		REGISTED_ENTITY = new HashMap<EntityType, RandomEvent>(); // 엔티티 랜덤효과
		DISABLE_WORLD = new HashMap<World, Boolean>();
		
		// 엔티티도 랜덤효과를 허용한 경우
		if (CONFIG.getBoolean("Enable_Entity")) {
			
			// 각각의 엔티티에 대해 랜덤이벤트 객체 생성
			EntityType[] entities = EntityType.values();
			for (EntityType entity : entities) {
				// 플레이어를 제외한 살아있는 엔티티 중에서
				if (!entity.isAlive() || entity.equals(EntityType.PLAYER)) {
					continue;
				}
				
				// 등록
				RandomEvent re = new RandomEvent(entity.name());
				REGISTED_ENTITY.put(entity, re);
			}
		}
		
		// 월드 밴
		String worlds_str = CONFIG.getString("Disable_World").replaceAll(" ", "").replaceAll("\n", "");
		if (worlds_str.isEmpty()) {
			return;
		}
		
		String[] worlds = worlds_str.split(",");
		for (String world_str : worlds) {
			
			if (world_str == null) {
				continue;
			}
			
			// 월드 이름으로 월드 객체 가져오기
			World world = Bukkit.getWorld(world_str);
			
			if (world == null) {
				continue;
			}
			
			DISABLE_WORLD.put(world, true);
		}
		
	}
	
	// 언어정보 가져오기
	private void loadLanguage() {
		String country = CONFIG.getString("Language").replaceAll(" ", "").replaceAll("\n", "");
		if (country.isEmpty()) {
			country = "ko_kr";
		}

		File default_lang = new File(this.getDataFolder() + File.separator + "lang" + File.separator + country + ".yml");
		File korean = new File(this.getDataFolder() + File.separator + "lang" + File.separator + "ko_kr.yml");
		File english = new File(this.getDataFolder() + File.separator + "lang" + File.separator + "en_us.yml");
		
		// 적용하려는 언어 파일이 없는 경우
		if (!default_lang.exists()) {
			default_lang = korean;
		}
		
		// 한국어 파일이 없는 경우 생성
		if (!korean.exists()) {
			SimpleConfig korean_yml = MANAGER.getNewConfig(File.separator + "lang" + File.separator + "ko_kr.yml");
			
			// 한국어 생성
			
		}

		// 영어 파일이 없는 경우 생성
		SimpleConfig english_yml;
		if (!english.exists()) {
			english_yml = MANAGER.getNewConfig(File.separator + "lang" + File.separator + "en_us.yml");
			
			// 영어 생성
			
		}
		
		LANGUAGE = MANAGER.getNewConfig(File.separator + default_lang + ".yml"); // config 파일
		LANGUAGE_DATA = new HashMap<Integer, String>();
		int number_of_lines = LANGUAGE.getInt("number_of_lines"); // 총 갯수
		
		for (int line = 0; line < number_of_lines; line++) {
			// hash맵으로 가져오기
			// 만약 없는게 있다면 english_yml로 가져오기
			// 그래도 없으면 ... 어쩌지?
			
		}
	}
}