package main;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

// 
public class CreateItem extends RandomItem implements Listener {
	
//	private final HashMap<InventoryType, Boolean> DEACTIVATED_INVENTORY_TYPE;
//	private final HashMap<String, InventoryType> ACTIVATED_INVENTORY_TYPE; // 인벤토리 유형 해시맵
//	private final HashMap<InventoryType, Integer> RESULT_SLOT; // 결과 슬롯 해시맵
	
	public CreateItem() {
//		this.DEACTIVATED_INVENTORY_TYPE = new HashMap<InventoryType, Boolean>();
//		this.ACTIVATED_INVENTORY_TYPE = new HashMap<String, InventoryType>();
//		this.RESULT_SLOT = new HashMap<InventoryType, Integer>();
		
//		this.ACTIVATED_INVENTORY_TYPE.put("WORKBENCH", InventoryType.WORKBENCH);
//		this.ACTIVATED_INVENTORY_TYPE.put("CRAFTING", InventoryType.CRAFTING);
//		this.ACTIVATED_INVENTORY_TYPE.put("FURNACE", InventoryType.FURNACE);
//		this.ACTIVATED_INVENTORY_TYPE.put("BLAST_FURNACE", InventoryType.BLAST_FURNACE);
//		this.ACTIVATED_INVENTORY_TYPE.put("SMOKER", InventoryType.SMOKER);
//		this.ACTIVATED_INVENTORY_TYPE.put("STONECUTTER", InventoryType.STONECUTTER);
//		this.ACTIVATED_INVENTORY_TYPE.put("SMITHING", InventoryType.SMITHING);
//		this.ACTIVATED_INVENTORY_TYPE.put("CARTOGRAPHY", InventoryType.CARTOGRAPHY);
//		this.ACTIVATED_INVENTORY_TYPE.put("LOOM", InventoryType.LOOM);
//		this.ACTIVATED_INVENTORY_TYPE.put("ANVIL", InventoryType.ANVIL);
//		this.ACTIVATED_INVENTORY_TYPE.put("GRINDSTONE", InventoryType.GRINDSTONE);
//		this.ACTIVATED_INVENTORY_TYPE.put("MERCHANT", InventoryType.MERCHANT);
//		
//		this.RESULT_SLOT.put(InventoryType.CRAFTING, 0);
//		this.RESULT_SLOT.put(InventoryType.WORKBENCH, 0);
//		this.RESULT_SLOT.put(InventoryType.STONECUTTER, 1);
//		this.RESULT_SLOT.put(InventoryType.ANVIL, 2);
//		this.RESULT_SLOT.put(InventoryType.BLAST_FURNACE, 2);
//		this.RESULT_SLOT.put(InventoryType.CARTOGRAPHY, 2);
//		this.RESULT_SLOT.put(InventoryType.FURNACE, 2);
//		this.RESULT_SLOT.put(InventoryType.GRINDSTONE, 2);
//		this.RESULT_SLOT.put(InventoryType.MERCHANT, 2);
//		this.RESULT_SLOT.put(InventoryType.SMITHING, 2);
//		this.RESULT_SLOT.put(InventoryType.SMOKER, 2);
//		this.RESULT_SLOT.put(InventoryType.LOOM, 3);
		
		// 파일 불러오기
//		String deactivated = Main.CONFIG.getString("DEACTIVATED");
//		String activated = Main.CONFIG.getString("ACTIVATED");
//		InventoryType invType;
//		
//		// result타입이 없는 인벤토리들을 파일에서 불러오기
//		for (String key : deactivated.split(",")) {
//			try {
//				invType = InventoryType.valueOf(key);
//				this.DEACTIVATED_INVENTORY_TYPE.put(invType, true);
//			} catch (IllegalArgumentException err) {
//				System.out.println("[Plugin-RandomWorld] YOU NEED TO FIX 'DEACTIVATED' at config.yml\nThe wrong Inventory Name filled in.");
//			}
//		}
//		
//		// result타입이 있는 인벤토리들을 파일에서 불러오기
//		for (String key : activated.split(",")) {
//			
//			String[] tuple = key.split("@");
//			if (tuple.length != 2) {
//				continue;	
//			}
//			
//			try {
//				String name = tuple[0];
//				int slotID = Integer.parseInt(tuple[1]);
//				
//				invType = InventoryType.valueOf(name);
//				this.ACTIVATED_INVENTORY_TYPE.put(name, invType);
//				this.RESULT_SLOT.put(invType, slotID);
//			} catch (IllegalArgumentException err) {
//				System.out.println("YOU NEED TO FIX 'ACTIVATED' at config.yml\nThe wrong Inventory Name filled in.");
//			}
//		}
		
	}
	
	@EventHandler
	// 인벤토리를 클릭했을 때
	public void inventoryClick(InventoryClickEvent e) {

		HumanEntity entity = e.getWhoClicked();

		// 해당 월드 밴일 때
		if (Main.DISABLE_WORLD.get(entity.getWorld()) != null) {
			return;
		}
		
		ItemStack stack = e.getCurrentItem(); // 현재 클릭한 아이템
		InventoryType invType = e.getView().getType(); // 인벤토리 타입

		RandomEvent re;
		if (entity instanceof Player) {
			re = Main.REGISTED_PLAYER.get(entity.getUniqueId());
		} else {
			re = Main.REGISTED_ENTITY.get(entity.getType());
		}
		
		// 양조기 인벤토리일 경우
		if (invType.equals(InventoryType.BREWING)) {
			
			// 클릭한 슬롯이 양조기 제작 슬롯일 경우
			if (!e.getSlotType().equals(SlotType.CRAFTING)) {
				return;
			}

			// 클릭한 슬롯이 포션 결과 슬롯인 경우
			if (e.getRawSlot() < 0 || e.getRawSlot() > 2) {
				return;
			}

			// 클릭이 일어난 직후 커서에 있는 아이템이 null인 경우
			if (e.getCursor() == null) {
				return;
			}
			
			// 포션이 아닐 경우
			if (!(stack.getItemMeta() instanceof PotionMeta)) {
				return;
			}
			
			// 포션효과 랜덤변환
			changeRandomPotion(re, "BREWING", stack);
			return;
		}
		
		
		// result 슬롯이 없는 인벤토리일 경우, (슬롯이 있는, 처음보는 인벤토리일 경우 기록 및 Main의 static 변수에 등록)
		if (!hasResultSlot(e.getView())) {
			return;
		}
		
		if (re == null || !re.getActivate(invType.name())) {
			return;
		}
		
		// 클릭한 슬롯이 결과 슬롯일 경우
		if (e.getSlotType().equals(InventoryType.SlotType.RESULT)) {
			
			// 커서에 있는 아이템이 공기일 경우 무작위로 선택된 아이템으로 변경
			if (e.getCursor() != null && e.getCursor().getType().equals(Material.AIR)) {
				changeRandomItem(re, invType.name(), stack);
				return;
			}
			
			// 그것이 아니라면 클릭한 아이템을 랜덤화예정 태그 부여
			prepareItem(e.getCurrentItem());
			return;
		}
		
		// 더블클릭으로 커서에 같은 종류의 아이템을 모았다면,
		if (e.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
			
			// 더블클릭한 인벤토리가, 결과슬롯이 있는 인벤토리가 아닐 경우
			if (Main.RESULT_SLOT.get(invType) == null) {
				return;
			}

			int rawSlotID = Main.RESULT_SLOT.get(invType); // 결과슬롯의 번호
			ItemStack resultStack = e.getView().getItem(rawSlotID); // 클릭한 인벤토리의 결과슬롯의 아이템
			
			// 커서에 아이템이 없을 경우
			if (e.getCursor() == null) {
				return;
			}

			// 커서의 아이템이 결과 슬롯의 아이템과 성분이 같지 않으면
			if (!e.getCursor().getType().equals(resultStack.getType())) {
				return;
			}
			
			// 랜덤효과를 받지 않거나, 해당 인벤토리에 대해 랜덤효과를 받지 않는 경우
			if (re == null || !re.getActivate(invType.name())) {
				return;
			}
			
			// 해당 인벤토리에서 나온 아이템을 무작위로 변경가능한 것인지 확인
			if (re.isItemBan(invType.name(), resultStack.getType())) {
				return;
			}
			
			prepareItem(resultStack); // 결과슬롯의 아이템을 랜덤화예정 태그 부여
			
			return;
		}
	}
	
	// 해당 인벤토리에 result슬롯이 있는지 확인하는 함수
	private boolean hasResultSlot(InventoryView invView) {
		Boolean deactivated = Main.DEACTIVATED_INVENTORY_TYPE.get(invView.getType());
		if (deactivated != null && deactivated) {
			return false;
		}
		
		InventoryType inv_type = invView.getType();
		
		// result 슬롯이 있는지 모르는 경우
		if (Main.ACTIVATED_INVENTORY_TYPE.get(inv_type.name()) == null) {
			int slotID = findResultSlotID(invView); // 슬롯타입이 result인 슬롯의 위치
			
			// 슬롯타입이 result인 슬롯이 없으면
			if (slotID == -1) {
				Main.DEACTIVATED_INVENTORY_TYPE.put(inv_type, true);
				
				Set<InventoryType> keys = Main.DEACTIVATED_INVENTORY_TYPE.keySet();
				String str = "";
				
				// 저장 예시) CREATIVE,BREWING, ...
				for(InventoryType key : keys) {
					str += key.name() + ",";
				}
				
				// 해당 인벤토리는 result슬롯이 없음을 config.yml에 저장
				Main.CONFIG.set("DEACTIVATED", str);
				Main.CONFIG.saveConfig();
				
				return false;
			}
			
			// 슬롯타입이 result인 슬롯을 찾았을 경우
			Main.DEFAULT.setActivate(inv_type.name(), true);
			Main.DEFAULT.setItemFilter(inv_type.name(), "");
			Main.DEFAULT.setItemBan(inv_type.name(), "");
			
			// entity에 대해서
			Set<EntityType> entity_keys = Main.REGISTED_ENTITY.keySet();
			for (EntityType key : entity_keys) {
				if (!key.isAlive() || key.equals(EntityType.PLAYER)) {
					continue;
				}
				
				Main.REGISTED_ENTITY.get(key).setActivate(inv_type.name(), true);
				Main.REGISTED_ENTITY.get(key).setItemFilter(inv_type.name(), "");
				Main.REGISTED_ENTITY.get(key).setItemBan(inv_type.name(), "");
			}
			
			Main.ITEM_FIELD.put(inv_type.name(), true);
			Main.ACTIVATED_INVENTORY_TYPE.put(inv_type.name(), inv_type);
			Main.RESULT_SLOT.put(inv_type, slotID);
			
			Set<InventoryType> keys = Main.RESULT_SLOT.keySet();
			String str = "";
			
			// 저장 예시) WORKBENCH@1,ANVIL@1, ...
			for (InventoryType key : keys) {
				str += key.name() + "@" + slotID + ",";
			}

			// 해당 인벤토리는 result슬롯이 있고 슬롯의 위치를 config.yml에 저장
			Main.CONFIG.set("ACTIVATED", str);
			Main.CONFIG.saveConfig();

			// 플레이어 적용
			for (RandomEvent re : Main.REGISTED_PLAYER.values()) {
				re.setActivate(inv_type.name(), true);
				re.setItemFilter(inv_type.name(), "");
				re.setItemBan(inv_type.name(), "");
			}
		}
		
		return true;
	}
	
	// InventoryView의 슬롯타입이 result인 것을 슬롯 0번째 부터 선형탐색하여 위치 반환
	private int findResultSlotID(InventoryView invv) {
		int countSlots = invv.countSlots();
		for (int i = 0; i < countSlots; i++) {
			if (invv.getSlotType(i).equals(InventoryType.SlotType.RESULT)) {
				return i;
			}
		}
		
		return -1;
	}
}
