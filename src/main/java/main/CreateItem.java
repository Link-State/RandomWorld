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

public class CreateItem extends RandomItem implements Listener {
	
	private final HashMap<String, InventoryType> toInventoryType;
	private final HashMap<InventoryType, Integer> RESULT_SLOT;
	
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
	public void prepareItem(PrepareItemCraftEvent e) {
		System.out.println("1234");
		// 밑에 긴 코드를 얘로 치환할 수 있을것 같다.
		// 제작대 매트릭스에 아이템을 올릴때마다 호출됨.
	}
	
	@EventHandler
	public void inventoryClick(InventoryClickEvent e) {
		if (e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			if (e.getSlotType().equals(InventoryType.SlotType.RESULT)) {
				RandomEvent re = Main.REGISTED_PLAYER.get(p.getUniqueId());
				if (re != null && re.getActivate(p.getOpenInventory().getType().name())) {
					ItemStack stack = e.getCurrentItem();
					Material material = re.getRandomItem(p.getOpenInventory().getType().name());
					if (material != null) {
						if (!re.isBan(p.getOpenInventory().getType().name(), stack.getType())) {
							if (e.getCursor() != null && e.getCursor().getType().equals(Material.AIR)) {
								changeRandomItem(stack, material);
							} else {
								prepareItem(e.getCurrentItem());
							}
						}
					}
				}
			} else if (e.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
				InventoryType invType = e.getWhoClicked().getOpenInventory().getType();
				if (RESULT_SLOT.get(invType) != null) {
					int rawSlotID = RESULT_SLOT.get(invType);
					ItemStack resultStack = e.getWhoClicked().getOpenInventory().getItem(rawSlotID);
					if (e.getCursor() != null) {
						if (e.getCursor().getType().equals(resultStack.getType())) {
							RandomEvent re = Main.REGISTED_PLAYER.get(p.getUniqueId());
							if (re != null && re.getActivate(p.getOpenInventory().getType().name())) {
								if (!re.isBan(p.getOpenInventory().getType().name(), resultStack.getType())) {
									prepareItem(resultStack);
								}
							}
						}
					}
				}
			}
		}
	}
}
