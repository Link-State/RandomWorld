package main;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

public class InventoryGUI {
	public static final TreeMap<String, OfflinePlayer> SORTED_PLAYERS = new TreeMap<String, OfflinePlayer>();
	private Player player;
	
	public InventoryGUI(Player p) {
		this.player = p;
		
		OfflinePlayer[] all_player = Bukkit.getOfflinePlayers();
		for (OfflinePlayer player : all_player) {
			if (RandomEvent.hasEntity(player.getUniqueId().toString())) {
				SORTED_PLAYERS.put(player.getName(), player);
			}
		}
	}

	// 설정창0 : 플레이어/엔티티/공통 선택 (해당 플레이어가 op 또는 super일 경우에만 활성화)
	// 설정창1 : (플레이어/엔티티 선택 시) 플레이어/엔티티 선택 (해당 플레이어가 op 또는 super일 경우에만 활성화)
	// 설정창2 : 아이템/포션/인첸트 선택 (뒤로가기 버튼) (해당 플레이어가 Admin일 경우에만 활성화)
	// 설정창3 : 이벤트(e.g : PICKUP_BAN) 아이콘 선택 (뒤로가기 버튼, 이전/다음페이지 버튼)
	// 설정창4 : 책 GUI open, 각 라인하나 당 옵션 하나 (open시 엔티티 설정 불러오기)
	// 설정창5 : 책 닫을 시, 저장 및 적용 여부 창 (강제종료 시 저장안함)
	
	
	// 개체 타입을 선택하는 GUI
	public boolean openEntityTypeSelect() {

		Inventory inv = Bukkit.createInventory(null, 45, "개체 종류 선택");
		
		// 닫기 버튼
		ItemStack close_icon = new ItemStack(Material.BARRIER, 1);
		ItemMeta close_icon_meta = close_icon.getItemMeta();
		close_icon_meta.setDisplayName(ChatColor.RED + "닫기");
		close_icon.setItemMeta(close_icon_meta);
		
		// 플레이어
		ItemStack player_icon = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta player_icon_meta = (SkullMeta) player_icon.getItemMeta();
		player_icon_meta.setDisplayName(ChatColor.WHITE + "플레이어");
		player_icon.setItemMeta(player_icon_meta);
		
		// 엔티티
		ItemStack entity_icon = new ItemStack(Material.SKELETON_SKULL, 1);
		SkullMeta entity_icon_meta = (SkullMeta) entity_icon.getItemMeta();
		entity_icon_meta.setDisplayName(ChatColor.WHITE + "엔티티");
		entity_icon.setItemMeta(entity_icon_meta);
		
		// 공통
		ItemStack default_icon = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta default_icon_meta = (SkullMeta) default_icon.getItemMeta();
		PlayerProfile default_icon_profile = createProfile("c69196b330c6b8962f23ad5627fb6ecce472eaf5c9d44f791f6709c7d0f4dece");
		default_icon_meta.setOwnerProfile(default_icon_profile);
		default_icon_meta.setDisplayName(ChatColor.WHITE + "공통");
		default_icon.setItemMeta(default_icon_meta);
		
		
		inv.setItem(8, close_icon);
		inv.setItem(20, player_icon);
		inv.setItem(22, entity_icon);
		inv.setItem(24, default_icon);
		
		this.player.openInventory(inv);
		return true;
	}
	
	// 개체 선택 GUI
	public boolean openEntitySelect(Boolean isPlayer, int start) {
		Inventory inv = Bukkit.createInventory(null, 54, "개체 선택");

		// 검색 버튼
		
		
		// 뒤로가기 버튼
		ItemStack backward_icon = new ItemStack(Material.ACACIA_BOAT, 1);
		ItemMeta backward_icon_meta = backward_icon.getItemMeta();
		backward_icon_meta.setDisplayName(ChatColor.GRAY + "뒤로가기");
		backward_icon.setItemMeta(backward_icon_meta);
		
		// 닫기 버튼
		ItemStack close_icon = new ItemStack(Material.BARRIER, 1);
		ItemMeta close_icon_meta = close_icon.getItemMeta();
		close_icon_meta.setDisplayName(ChatColor.RED + "닫기");
		close_icon.setItemMeta(close_icon_meta);

		// 이전페이지 버튼
		ItemStack prev_icon = new ItemStack(Material.ACACIA_BOAT, 1);
		ItemMeta prev_icon_meta = prev_icon.getItemMeta();
		prev_icon_meta.setDisplayName(ChatColor.GRAY + "이전페이지");
		prev_icon_meta.setLore(Arrays.asList(ChatColor.GOLD + "(" + ChatColor.GREEN + "0" + ChatColor.GOLD + " / " + "0" + ")"));
		prev_icon.setItemMeta(prev_icon_meta);
		
		// 다음페이지 버튼
		ItemStack next_icon = new ItemStack(Material.ACACIA_BOAT, 1);
		ItemMeta next_icon_meta = next_icon.getItemMeta();
		next_icon_meta.setDisplayName(ChatColor.GRAY + "다음페이지");
		next_icon_meta.setLore(Arrays.asList(ChatColor.GOLD + "(" + ChatColor.GREEN + "0" + ChatColor.GOLD + " / " + "0" + ")"));
		next_icon.setItemMeta(next_icon_meta);
		
		int end = start + 27;
		
		if (isPlayer) {
			ArrayList<String> all_player = new ArrayList<String>( new TreeSet<String>(SORTED_PLAYERS.keySet()) );
			
			if (end > all_player.size()) {
				end = all_player.size();
			}
			
			for (int i = start; i < end; i++) {
				String player = all_player.get(i);
				
				ItemStack player_icon = new ItemStack(Material.PLAYER_HEAD, 1);
				SkullMeta player_icon_meta = (SkullMeta) player_icon.getItemMeta();
				player_icon_meta.setOwningPlayer(SORTED_PLAYERS.get(player));
				player_icon_meta.setDisplayName(ChatColor.WHITE + player);
				player_icon.setItemMeta(player_icon_meta);
				inv.setItem(i - start + 9, player_icon);
			}
			
		}
		else {
			EntityType[] all_entity = EntityType.values();
			
			if (end > all_entity.length) {
				end = all_entity.length;
			}
			
			for (int i = start; i < end; i++) {
				EntityType entity = all_entity[i];
				
				// 무한루프문 됨, i가 1 증가했다가 1 감소했다가 무한반복 
				if (!entity.isAlive() || entity.equals(EntityType.PLAYER)) {
					i--;
					continue;
				}
				
				ItemStack entity_icon = new ItemStack(Material.SKELETON_SKULL, 1);
				SkullMeta entity_icon_meta = (SkullMeta) entity_icon.getItemMeta();
				entity_icon_meta.setDisplayName(ChatColor.WHITE + entity.getKey().getKey().toUpperCase());
				entity_icon.setItemMeta(entity_icon_meta);
				inv.setItem(i - start + 8, entity_icon);
			}
		}

		inv.setItem(0, backward_icon);
		inv.setItem(8, close_icon);
		inv.setItem(52, prev_icon);
		inv.setItem(53, next_icon);
		
		this.player.openInventory(inv);
		return true;
	}
	
	// 이벤트 타입을 선택하는 GUI
	public boolean openEventTypeSelect() {
		
		Inventory inv = Bukkit.createInventory(null, 45, "이벤트 종류 선택");
		
		// 뒤로가기 버튼
		ItemStack backward_icon = new ItemStack(Material.ACACIA_BOAT, 1);
		ItemMeta backward_icon_meta = backward_icon.getItemMeta();
		backward_icon_meta.setDisplayName(ChatColor.GRAY + "뒤로가기");
		backward_icon.setItemMeta(backward_icon_meta);
		
		// 닫기 버튼
		ItemStack close_icon = new ItemStack(Material.BARRIER, 1);
		ItemMeta close_icon_meta = close_icon.getItemMeta();
		close_icon_meta.setDisplayName(ChatColor.RED + "닫기");
		close_icon.setItemMeta(close_icon_meta);
		
		// 플레이어
		ItemStack item_icon = new ItemStack(Material.CRAFTING_TABLE, 1);
		ItemMeta item_icon_meta = item_icon.getItemMeta();
		item_icon_meta.setDisplayName(ChatColor.WHITE + "아이템");
		item_icon.setItemMeta(item_icon_meta);
		
		// 엔티티
		ItemStack potion_icon = new ItemStack(Material.POTION, 1);
		ItemMeta potion_icon_meta = potion_icon.getItemMeta();
		potion_icon_meta.setDisplayName(ChatColor.WHITE + "포션효과");
		potion_icon.setItemMeta(potion_icon_meta);
		
		// 공통
		ItemStack enchant_icon = new ItemStack(Material.ENCHANTED_BOOK, 1);
		ItemMeta enchant_icon_meta = enchant_icon.getItemMeta();
		enchant_icon_meta.setDisplayName(ChatColor.WHITE + "인첸트");
		enchant_icon.setItemMeta(enchant_icon_meta);
		

		inv.setItem(0, backward_icon);
		inv.setItem(8, close_icon);
		inv.setItem(20, item_icon);
		inv.setItem(22, potion_icon);
		inv.setItem(24, enchant_icon);
		
		this.player.openInventory(inv);
		return true;
	}
	
	
	
	
	
	private PlayerProfile createProfile(String url) {
		PlayerProfile profile = Bukkit.createPlayerProfile("test");
		PlayerTextures texture = profile.getTextures();
		
		URL skin_url;
		try {
			skin_url = new URL("https://textures.minecraft.net/texture/" + url);
		} catch (MalformedURLException e) {
			OfflinePlayer[] all_player = Bukkit.getOfflinePlayers();
			int randIdx = (int) (Math.random() * all_player.length);
			return all_player[randIdx].getPlayerProfile();
		}
		
		texture.setSkin(skin_url);
		profile.setTextures(texture);
		
		return profile;
	}
}
