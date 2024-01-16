package main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
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
import org.bukkit.potion.PotionType;


// 아이템 랜덤화 관련 클래스
public class RandomItem {
	// 아이템 태그에 사용될 키
	public static final NamespacedKey KEY = new NamespacedKey(Main.PLUGIN, "randomStatus");
	private final ArrayList<MusicInstrument> horns = new ArrayList<MusicInstrument>();

	/**
	 * -ITEM STATUS-
	 * 0 - NULL
	 * 1 - INTEGRITY
	 * 2 - READY
	 * 3 - CHANGED
	 **/
	
	// 생성자
	public RandomItem() {
		Iterator<MusicInstrument> horns_iter = Registry.INSTRUMENT.iterator();
		while (horns_iter.hasNext()) {
			horns.add(horns_iter.next());
		}
	}
	
	// 아이템 상태를 반환하는 함수
	public int getItemStatus(ItemStack stack) {
		
		// 스택이 null, 공기, 메타정보가 null일 경우
		if (stack == null || stack.getType().isAir() || stack.getItemMeta() == null) {
			return 0;	
		}
		
		ItemMeta meta = stack.getItemMeta();
		PersistentDataContainer tag = meta.getPersistentDataContainer(); // 
		
		// 해당 아이템의 태그에 번호가 부여되어있으면
		if (!tag.has(KEY, PersistentDataType.STRING)) {
			return 1;
		}

		if (tag.get(KEY, PersistentDataType.STRING).equals("ready")) {
			return 2;
		}
		
		if (tag.get(KEY, PersistentDataType.STRING).equals("changed")) {
			return 3;
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
		
		// 태그가 부여된 아이템일 경우
		if (status != 1) {
			return;
		}
		
		changeTag(stack, "ready");
	}
	
	// 아이템 랜덤화
	public void changeRandomItem(RandomEvent re, String eventName, ItemStack stack) {
		
		// 해당 아이템이 바뀔 수 있는 상태일 때,
		int status = getItemStatus(stack);
		if (status != 1 && status != 2) {
			return;
		}
		
		// 해당 플레이어에게 해방 이벤트가 적용 되어있는지 확인
		if (re == null || !re.getActivate(eventName)) {
			return;
		}
		
		// 아이템 관련 이벤트가 아닐 경우
		if (Main.ITEM_FIELD.get(eventName) == null) {
			return;
		}

		Material material = re.getRandomItem(eventName, stack.getType()); // 무작위 아이템 1개 선택
		
		// 변경한 아이템이 null이거나 config에서 밴 된(필터링 처리 된) 아이템일 경우
		if (material == null) {
			return;
		}
		
		// 랜덤한 아이템에 적용 할 메타 정보 (특정 아이템일 경우 메타데이터 덮어씌우기 용도)
		ItemMeta item_meta = stack.getItemMeta();
		
		// 특정 아이템 메타정보 확인용 ItemStack / ItemMeta
		ItemStack test_stack = new ItemStack(material, 1);
		ItemMeta test_meta = test_stack.getItemMeta();
		
		// 포션효과가 부여 가능한 아이템일 경우
		if (test_meta instanceof PotionMeta) {
			stack.setType(material);
			prepareItem(stack);
			changeRandomPotion(re, "GET_EFFECT_ITEM", stack);
			return;
		}
		else if (test_meta instanceof SuspiciousStewMeta) {
			stack.setType(material);
			changeRandomStew(re, "GET_EFFECT_ITEM", stack);
			return;
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
		// 발굴 가능한 블럭일 경우
		else if (test_meta instanceof BlockStateMeta &&
				((BlockStateMeta) test_meta).getBlockState() instanceof BrushableBlock) {
			
			// 랜덤아이템이 붓질가능한 아이템일 경우 붓질가능한 아이템에 대해 랜덤효과부여 여부 검사
			Material brush = re.getRandomItem("GET_BRUSHABLE_ITEM");
			
			if (brush != null) {
				item_meta = createBlockMeta(stack, material, brush);
			}
		}
		
		stack.setType(material);
		stack.setItemMeta(item_meta);
		
		changeTag(stack, "changed");
	}
	
	
	// 포션 랜덤화
	public void changeRandomPotion(RandomEvent re, String eventName, ItemStack stack) {
		
		// 해당 아이템이 바뀔 수 있는 상태일 때,
		if (getItemStatus(stack) != 2) {
			return;
		}
		
		// 해당 플레이어에게 해방 이벤트가 적용 되어있는지 확인
		if (re == null || !re.getActivate(eventName)) {
			return;
		}
		
		// 아이템 관련 이벤트가 아닐 경우
		if (Main.POTION_FIELD.get(eventName) == null) {
			return;
		}
		
		PotionMeta potion_meta = (PotionMeta) stack.getItemMeta();
		
		// 랜덤 아이템 결과가 포션인 경우,
		if (potion_meta.getBasePotionType().equals(PotionType.UNCRAFTABLE)) {
			
			// 랜덤 포션효과 하나를 저장
			PotionEffectType random_effect = re.getRandomEffect(eventName);
			if (random_effect == null) {
				return;
			}
			
			// 랜덤효과를 포션에 적용
			addRandomEffect(potion_meta, random_effect);
		}
		// 양조기 등, 포션을 제작한 경우
		else {
			List<PotionEffect> base_effects = potion_meta.getBasePotionType().getPotionEffects();
			ArrayList<PotionEffect> new_effects = new ArrayList<PotionEffect>();
			Color color = null;
			
			// 각 포션마다 각각의 효과들에 대해 변환을 허용하는지 유무를 검사
			for (PotionEffect effect : base_effects) {
				// 하나라도 변환을 허용하지 않는 경우 랜덤변환 안함
				PotionEffectType random_effect = re.getRandomEffect(eventName, effect.getType());
				if (random_effect == null) {
					return;
				}
				
				// 변환을 허용하는 경우, 기존효과에서 타입만 바꾼 후, 임시 저장
				PotionEffect new_effect = new PotionEffect(random_effect, effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.hasIcon());
				new_effects.add(new_effect);
				
				// 색깔 섞기
				if (color == null) {
					color = random_effect.getColor();
				} else {
					color.mixColors(random_effect.getColor());
				}
			}
			
			// 포션효과가 없는 물약일 때, (어색한물약, 평범한물약, 등)
			if (new_effects.size() <= 0) {
				return;
			}
			
			// 기존효과를 수정해야하므로 베이스포션 없애기
			potion_meta.setBasePotionType(PotionType.UNCRAFTABLE);
			potion_meta.setColor(color);
			
			// 임시저장된 랜덤포션효과들을 메타정보에 적용
			for (PotionEffect new_effect : new_effects) {
				potion_meta.addCustomEffect(new_effect, true);
			}
		}

		stack.setItemMeta(potion_meta);
		
		changeTag(stack, "changed");
	}
	
	
	public void changeRandomStew(RandomEvent re, String eventName, ItemStack stack) {
		
		// 해당 아이템이 바뀔 수 있는 상태일 때,
		int status = getItemStatus(stack);
		if (status != 1 && status != 2) {
			return;
		}
		
		// 해당 플레이어에게 해방 이벤트가 적용 되어있는지 확인
		if (re == null || !re.getActivate(eventName)) {
			return;
		}
		
		// 포션 관련 이벤트가 아닐 경우
		if (Main.POTION_FIELD.get(eventName) == null) {
			return;
		}
		
		SuspiciousStewMeta stew_meta = (SuspiciousStewMeta) stack.getItemMeta();
		
		PotionEffectType random_effect = re.getRandomEffect(eventName);
		if (random_effect == null) {
			return;
		}
		
		addRandomEffect(stew_meta, random_effect);
		
		stack.setItemMeta(stew_meta);
		
		changeTag(stack, "changed");
	}

	
	// 포션메타 생성
	private PotionMeta addRandomEffect(PotionMeta origin_meta, PotionEffectType type) {
		
		// 지속시간 생성 (10초 ~ 8분)
		int duration = ((int) (Math.random() * 47) + 1) * 200;
		
		// 중폭수준 생성 (0 or 1 or 2)
		int amplifier = (int) (Math.random() * 2);
		
		// 포션효과 객체 생성
		PotionEffect effect = new PotionEffect(type, duration, amplifier);
		
		origin_meta.addCustomEffect(effect, true);
		origin_meta.setColor(type.getColor());
		
		return origin_meta;
	}
	
	// 수상한 스튜 메타
	private SuspiciousStewMeta addRandomEffect(SuspiciousStewMeta origin_meta, PotionEffectType type) {
		
		// 지속시간 생성 (2초 ~ 10초)
		int duration = ((int) (Math.random() * 7) + 1) * 20;
		
		// 포션효과 객체 생성
		PotionEffect effect = new PotionEffect(type, duration, 1);

		origin_meta.addCustomEffect(effect, true);
		
		return origin_meta;
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
