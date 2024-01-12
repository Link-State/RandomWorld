package main;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PickupItem extends RandomItem implements Listener {
	
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
		

		// 일반 포션일 경우
		// 투척 포션일 경우
		// 잔류 포션일 경우
		// 물약 화살일 경우
		material = Material.POTION;
		
		if (material.equals(Material.POTION)) {
			stack.setType(material); // 포션타입을 먼저 넣어야 밑에 코드에서 아이템메타를 포션메타로 변경 가능
			
			// 
			ItemStack temp_stack = new ItemStack(material, stack.getAmount());
			temp_stack.setItemMeta(stack.getItemMeta());
			
			PotionMeta potionmeta = (PotionMeta) temp_stack.getItemMeta();
			
			if (re.getActivate("GET_EFFECT_ITEM")) {
				
			}
			
			PotionEffectType effect_type = re.getRandomEffect("GET_EFFECT_ITEM");
			if (effect_type == null) {
				return;
			}
			
			PotionEffect effect = new PotionEffect(effect_type, 1, 1);
			
			// PotionData는 deprecated됨!!!!
			// PotionEffect 객체를 만들어서 넣어야함.
//			potionmeta.addCustomEffect(null, false);
		}

		if (material.equals(Material.SPLASH_POTION)) {
			
		}
		
		if (material.equals(Material.LINGERING_POTION)) {
			
		}

		if (material.equals(Material.TIPPED_ARROW)) {
			
		}
		// PotionMeta.setBasePotionData or PotionMeta.setBasePotionType 
		
		// 인첸트북일 경우
		// ItemStack.addEnchant()
		
		// 염소 뿔일 경우
		// MusicInstrumentMeta = (MusicInstrumentMeta) getItemMeta

		// 무작위로 선택된 아이템으로 변경
		changeRandomItem(stack, material);
		
		return;
	}
}