package main;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BrushableBlock;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MusicInstrumentMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


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
	public void changeRandomItem(ItemStack stack, Material material, RandomEvent re) {
		// 
		
		// 랜덤한 아이템에 적용 할 메타 정보 (특정 아이템일 경우 메타데이터 덮어씌우기 용도)
		ItemMeta item_meta = stack.getItemMeta();
		
		ItemStack test_stack = new ItemStack(material, 1);
		ItemMeta test_meta = test_stack.getItemMeta();
		
		// 포션효과가 부여 가능한 아이템일 경우
		if (test_meta instanceof PotionMeta) {
			
			// 랜덤아이템이 포션효과 관련 아이템일 경우 포션효과부여 허용여부 검사
			PotionEffectType effect_type = re.getRandomEffect("GET_EFFECT_ITEM");
			
			if (effect_type != null) {
				item_meta = createPotionMeta(stack, material, effect_type);
			}
		}
		// 수상한 스튜일 경우 
		else if (test_meta instanceof SuspiciousStewMeta) {
			
			// 랜덤아이템이 포션효과 관련 아이템일 경우 포션효과부여 허용여부 검사
			PotionEffectType effect_type = re.getRandomEffect("GET_EFFECT_ITEM");
			
			if (effect_type != null) {
				item_meta = createStewMeta(stack, material, effect_type);
			}
		}
		// 인첸트를 저장할 수 있는 아이템일 경우
		else if (test_meta instanceof EnchantmentStorageMeta) {

			// 랜덤아이템이 인첸트 관련 아이템일 경우 인첸트부여 허용여부 검사
			Enchantment enchant_type = re.getRandomEnchant("GET_ENCHANT_ITEM");
			
			if (enchant_type != null) {
				item_meta = createEnchantMeta(stack, material, enchant_type);
			}
		}
		// 염소 뿔일 경우
		else if (test_meta instanceof MusicInstrumentMeta) {
			item_meta = createHornMeta(stack, material);
		}
		// 붓질이 가능한 블럭일 경우
		else if (test_meta instanceof BlockStateMeta &&
				((BlockStateMeta) test_meta).getBlockState() instanceof BrushableBlock) {
			
			// 랜덤아이템이 붓질가능한 아이템일 경우 붓질가능한 아이템에 대해 랜덤효과부여 여부 검사
			Material brush = re.getRandomItem("GET_BRUSHABLE_ITEM");
			
			if (brush != null) {
				item_meta = createBlockMeta(stack, material, brush);
			}
		}
		
		int status = getItemStatus(stack);
		if (status == 1 || status == 2) {
			changeTag(stack, "changed");
//			System.out.println("[RandomItem.java-75] : " + stack.getType() + " => " + material);
			stack.setType(material);
		}
	}
	
	// 아이템 랜덤화
//	public void changeRandomItem(ItemStack stack, Material material, ItemMeta meta) {
//		int status = getItemStatus(stack);
//		if (status == 1 || status == 2) {
//			changeTag(stack, "changed");
////			System.out.println("[RandomItem.java-75] : " + stack.getType() + " => " + material);
//			
//			// 여기서 않됌.
//			if (meta instanceof EnchantmentStorageMeta) {
//				System.out.println((EnchantmentStorageMeta) meta);
//			}
//			
//			stack.setType(material);
//			stack.setItemMeta(meta);
//		}
//	}
	

	// 포션메타 생성
	private PotionMeta createPotionMeta(ItemStack origin_stack, Material material, PotionEffectType type) {
		ItemStack copy_stack = new ItemStack(origin_stack);
		copy_stack.setType(material);
		PotionMeta potion_meta = (PotionMeta) copy_stack.getItemMeta();
		
		// 지속시간 생성 (10초 ~ 8분)
		int duration = ((int) (Math.random() * 47) + 1) * 200;
		
		// 중폭수준 생성 (1 or 2)
		int amplifier = (int) (Math.random() + 1);
		
		// 포션색깔 생성
		int red = (int) (Math.random() * 255);
		int green = (int) (Math.random() * 255);
		int blue = (int) (Math.random() * 255);
		Color color = Color.fromRGB(red, green, blue);
		
		// 포션효과 객체 생성
		PotionEffect effect = new PotionEffect(type, duration, amplifier);
		
		potion_meta.addCustomEffect(effect, true);
		potion_meta.setColor(color);
		
		return potion_meta;
	}
	
	// 수상한 스튜 메타
	private SuspiciousStewMeta createStewMeta(ItemStack origin_stack, Material material, PotionEffectType type) {
		ItemStack copy_stack = new ItemStack(origin_stack);
		copy_stack.setType(material);
		SuspiciousStewMeta stew_meta = (SuspiciousStewMeta) copy_stack.getItemMeta();
		
		// 지속시간 생성 (2초 ~ 10초)
		int duration = ((int) (Math.random() * 7) + 1) * 20;
		
		// 포션효과 객체 생성
		PotionEffect effect = new PotionEffect(type, duration, 2);

		stew_meta.addCustomEffect(effect, true);
		
		return stew_meta;
	}
	
	// 저장용아이템 인첸트 메타 생성
	private EnchantmentStorageMeta createEnchantMeta(ItemStack origin_stack, Material material, Enchantment ench) {
		ItemStack copy_stack = new ItemStack(origin_stack);
		copy_stack.setType(material);
		EnchantmentStorageMeta enchant_meta = (EnchantmentStorageMeta) copy_stack.getItemMeta();
		
		// 인첸트 레벨 생성
		int start = ench.getStartLevel();
		int max = ench.getMaxLevel();
		int level = (int) ((Math.random() * Math.abs(max - start)) + start);
		
		enchant_meta.addStoredEnchant(ench, level, true);
		
		return enchant_meta;
	}
	
	private MusicInstrumentMeta createHornMeta(ItemStack origin_stack, Material material) {
		ItemStack copy_stack = new ItemStack(origin_stack);
		copy_stack.setType(material);
		MusicInstrumentMeta inst_meta = (MusicInstrumentMeta) copy_stack.getItemMeta();
		
		int randIdx = (int) (Math.random() * horns.size());
		MusicInstrument horn = horns.get(randIdx);
		
		inst_meta.setInstrument(horn);
		
		return inst_meta;
	}
	
	private BlockStateMeta createBlockMeta(ItemStack origin_stack, Material material, Material brush) {
		ItemStack copy_stack = new ItemStack(origin_stack);
		copy_stack.setType(material);
		BlockStateMeta block_meta = (BlockStateMeta) copy_stack.getItemMeta();
		BrushableBlock brushable = (BrushableBlock) block_meta.getBlockState();
		
		// 갯수 (1개 ~ 10개)
		int count = ((int) (Math.random() * 10)) + 1;
		
		ItemStack hideItem = new ItemStack(brush, count);
		
		brushable.setItem(hideItem);
		block_meta.setBlockState(brushable);
		
		return block_meta;
	}	
}
