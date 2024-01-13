package main;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;


// 아이템 랜덤화 관련 클래스
public class RandomItem {
	// 아이템 태그에 사용될 키
	public static final NamespacedKey KEY = new NamespacedKey(Main.PLUGIN, "randomStatus");

	/**
	 * -ITEM STATUS-
	 * 0 - NULL
	 * 1 - INTEGRITY
	 * 2 - READY
	 * 3 - CHANGED
	 **/
	
	// 아이템 상태를 반환하는 함수
	public int getItemStatus(ItemStack stack) {
		
		// 스택이 null이 아닌 경우
		if (stack != null) {
			// 공기가 아닌 경우
			if (!stack.getType().isAir()) {
				ItemMeta meta = stack.getItemMeta();
				// 메타 정보가 없는 경우
				if (meta != null) {
					PersistentDataContainer tag = meta.getPersistentDataContainer(); // 
					
					// 해당 아이템의 태그에 번호가 부여되어있으면
					if (tag.has(KEY, PersistentDataType.STRING)) {
						if (tag.get(KEY, PersistentDataType.STRING).equals("ready")) {
							return 2;
						} else if (tag.get(KEY, PersistentDataType.STRING).equals("changed")) {
							return 3;
						}
					} else {
						return 1;
					}
				}
			}
		}
		return 0;
	}
	
	/**
	 * -Change Item Tag-
	 * Must be used only when the return value of getItemStatus() is not 0.
	 **/
	
	// 해당 아이템을 이미 랜덤화된 아이템으로 태그 부여
	public void changeTag(ItemStack stack, String value) {
		ItemMeta meta = stack.getItemMeta();
		PersistentDataContainer tag = meta.getPersistentDataContainer();
		tag.set(KEY, PersistentDataType.STRING, value);
		stack.setItemMeta(meta);
	}
	
	// 해당 아이템에 랜덤화예정 태그 부여
	public void prepareItem(ItemStack stack) {
		int status = getItemStatus(stack); // 해당 아이템의 태그 상태
		
		// 태그가 부여되어있지 않은 아이템일 경우
		if (status == 1) {
			changeTag(stack, "ready");
		}
	}
	
	// 아이템 랜덤화
	public void changeRandomItem(ItemStack stack, Material material) {
		int status = getItemStatus(stack);
		if (status == 1 || status == 2) {
			changeTag(stack, "changed");
//			System.out.println("[RandomItem.java-75] : " + stack.getType() + " => " + material);
			stack.setType(material);
		}
	}
	
	// 아이템 랜덤화
	public void changeRandomItem(ItemStack stack, Material material, ItemMeta meta) {
		int status = getItemStatus(stack);
		if (status == 1 || status == 2) {
			changeTag(stack, "changed");
//			System.out.println("[RandomItem.java-75] : " + stack.getType() + " => " + material);
			
			// 여기서 않됌.
			if (meta instanceof EnchantmentStorageMeta) {
				System.out.println((EnchantmentStorageMeta) meta);
			}
			stack.setType(material);
			stack.setItemMeta(meta);
		}
	}
}
