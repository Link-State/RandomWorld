package main;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.MusicInstrument;
import org.bukkit.Registry;
import org.bukkit.block.BrushableBlock;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MusicInstrumentMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class PickupItem extends RandomItem implements Listener {
	private final ArrayList<MusicInstrument> horns = new ArrayList<MusicInstrument>();
	
	public PickupItem() {
		Iterator<MusicInstrument> horns_iter = Registry.INSTRUMENT.iterator();
		while (horns_iter.hasNext()) {
			horns.add(horns_iter.next());
		}
	}
	
	// 아이템을 주웠을 때
	@EventHandler
	public void pickupItem(EntityPickupItemEvent e) {
		
		// Entity가 Player 객체가 아닌 경우
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		
		Player p = (Player) e.getEntity();
		RandomEvent re = Main.REGISTED_PLAYER.get(p.getUniqueId()); // 해당 플레이어가 등록되지 않았으면 null

		// 해당 플레이어에게 '아이템 획득 랜덤효과'가 적용 되어있는지 확인
		if (re == null || !re.getActivate("PICKUP")) {
			return;
		}
		
		ItemStack stack = e.getItem().getItemStack(); // 플레이어가 주운 아이템 스택
		Material material = re.getRandomItem("PICKUP"); // 무작위 아이템 1개 선택
		
		// 변경한 아이템이 null이거나 config에서 밴 된(필터링 처리 된) 아이템일 경우
		if (material == null) {
			return;
		}
		
		// 얻은 아이템이 밴 된 아이템일 경우,
		if (re.isItemBan("PICKUP", stack.getType())) {
			return;
		}
		
		ItemMeta item_meta = stack.getItemMeta(); // 랜덤할 아이템에 적용 할 메타 정보
		
		// 특정 아이템 메타 확인용 ItemStack, ItemMeta
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

		// 무작위로 선택된 아이템으로 변경
		changeRandomItem(stack, material, item_meta);
		
		return;
	}
	
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