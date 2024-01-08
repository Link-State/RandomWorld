package main;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareInventoryResultEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

// 
public class CreateItem extends RandomItem implements Listener {
	
	private final HashMap<String, InventoryType> toInventoryType; // 인벤토리 유형 해시맵
	private final HashMap<InventoryType, Integer> RESULT_SLOT; // 결과 슬롯 해시맵
	
	public CreateItem() {
		this.toInventoryType = new HashMap<String, InventoryType>();
		this.toInventoryType.put("WORKBENCH", InventoryType.WORKBENCH);
		this.toInventoryType.put("CRAFTING", InventoryType.CRAFTING);
		this.toInventoryType.put("FURNACE", InventoryType.FURNACE);
		this.toInventoryType.put("BLAST_FURNACE", InventoryType.BLAST_FURNACE);
		this.toInventoryType.put("SMOKER", InventoryType.SMOKER);
		this.toInventoryType.put("STONECUTTER", InventoryType.STONECUTTER);
		this.toInventoryType.put("SMITHING", InventoryType.SMITHING);
		this.toInventoryType.put("CARTOGRAPHY", InventoryType.CARTOGRAPHY);
		this.toInventoryType.put("LOOM", InventoryType.LOOM);
		this.toInventoryType.put("ANVIL", InventoryType.ANVIL);
		this.toInventoryType.put("GRINDSTONE", InventoryType.GRINDSTONE);
		this.toInventoryType.put("MERCHANT", InventoryType.MERCHANT);
		
		this.RESULT_SLOT = new HashMap<InventoryType, Integer>();
		this.RESULT_SLOT.put(InventoryType.CRAFTING, 0);
		this.RESULT_SLOT.put(InventoryType.WORKBENCH, 0);
		this.RESULT_SLOT.put(InventoryType.STONECUTTER, 1);
		this.RESULT_SLOT.put(InventoryType.ANVIL, 2);
		this.RESULT_SLOT.put(InventoryType.BLAST_FURNACE, 2);
		this.RESULT_SLOT.put(InventoryType.CARTOGRAPHY, 2);
		this.RESULT_SLOT.put(InventoryType.FURNACE, 2);
		this.RESULT_SLOT.put(InventoryType.GRINDSTONE, 2);
		this.RESULT_SLOT.put(InventoryType.MERCHANT, 2);
		this.RESULT_SLOT.put(InventoryType.SMITHING, 2);
		this.RESULT_SLOT.put(InventoryType.SMOKER, 2);
		this.RESULT_SLOT.put(InventoryType.LOOM, 3);
	}
	
	@EventHandler
	// 인벤토리를 클릭했을 때
	public void inventoryClick(InventoryClickEvent e) {
		
		// 인벤토리를 클릭한 사람이 플레이어일 경우
		if (e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked(); // 플레이어 변수
			
			// 클릭한 슬롯이 결과 슬롯일 경우
			if (e.getSlotType().equals(InventoryType.SlotType.RESULT)) {
				RandomEvent re = Main.REGISTED_PLAYER.get(p.getUniqueId()); // 랜던아이템효과를 받는 플레이어 목록에서 해당 플레이어를 검색
				
				// 랜덤아이템 효과를 받고, 해당 인벤토리에 대해 랜덤효과를 받는 경우,
				if (re != null && re.getActivate(p.getOpenInventory().getType().name())) {
					ItemStack stack = e.getCurrentItem(); // 현재 클릭한 아이템
					Material material = re.getRandomItem(p.getOpenInventory().getType().name()); // 마크 인게임 아이템 중 무작위로 1개 선택
					
					// 무작위로 선택한 아이템이 null이 아니고
					if (material != null) {
						// 해당 인벤토리에서 결과로 나온 아이템은 무작위로 변경가능한 것인지 확인
						if (!re.isBan(p.getOpenInventory().getType().name(), stack.getType())) {
							// 커서에 있는 아이템이 null이 아니고 공기도 아닐 경우
							if (e.getCursor() != null && e.getCursor().getType().equals(Material.AIR)) {
								changeRandomItem(stack, material); // 무작위로 선택된 아이템으로 변경
							}
							// 그것이 아니라면 클릭한 아이템을 랜덤화예정 태그 부여
							else {
								prepareItem(e.getCurrentItem());
							}
						}
					}
				}
			}
			// 더블클릭으로 커서에 같은 종류의 아이템을 모았다면,
			else if (e.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
				InventoryType invType = e.getWhoClicked().getOpenInventory().getType(); // 인벤토리 타입
				
				// 해당 인벤토리는 결과슬롯이 있는 인벤토리일 경우
				if (RESULT_SLOT.get(invType) != null) {
					int rawSlotID = RESULT_SLOT.get(invType); // 결과슬롯의 번호
					ItemStack resultStack = e.getWhoClicked().getOpenInventory().getItem(rawSlotID); // 클릭한 인벤토리의 결과슬롯의 아이템
					
					// 커서에 아이템이 있을 경우
					if (e.getCursor() != null) {
						// 커서의 아이템이 결과 슬롯의 아이템과 성분이 같으면
						if (e.getCursor().getType().equals(resultStack.getType())) {
							RandomEvent re = Main.REGISTED_PLAYER.get(p.getUniqueId()); // 랜덤아이템 효과를 받는 유저인지 불러옴
							
							// 랜덤효과를 받고, 해당 인벤토리에 대해 랜덤효과를 받는 경우,
							if (re != null && re.getActivate(p.getOpenInventory().getType().name())) {
								// 해당 인벤토리에서 나온 아이템을 무작위로 변경가능한 것인지 확인
								if (!re.isBan(p.getOpenInventory().getType().name(), resultStack.getType())) {
									prepareItem(resultStack); // 결과슬롯의 아이템을 랜덤화예정 태그 부여
								}
							}
						}
					}
				}
			}
		}
	}
}
