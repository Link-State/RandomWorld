package main;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffect;

// 포션효과 이벤트 관련 클래스
public class GivePotionEffect implements Listener {
	@EventHandler
	// 포션효과가 일어났을 때,
	public void PotionEffect(EntityPotionEffectEvent e) {
		
		// 플러그인 함수, 명령어에 의한 포션효과일 때
		if (e.getCause().equals(EntityPotionEffectEvent.Cause.PLUGIN) ||
			e.getCause().equals(EntityPotionEffectEvent.Cause.COMMAND)) {
			return;
		}
		
		// 포션효과가 사라졌을 때,
		if (e.getNewEffect() == null) {
			return;
		}
		
		// 포션효과를 받을 수 없는 엔티티일 때
		if (!(e.getEntity() instanceof LivingEntity)) {
			return;
		}

		LivingEntity entity = (LivingEntity) e.getEntity(); // 랜덤효과를 부여할 엔티티

		// 플레이어가 아니고 엔티티는 영향을 받지 않을 때
		if (!(entity instanceof Player) && !Main.EFFECT_ENTITY) {
			return;
		}
		
		// 플레이어일 경우, 포션 랜덤효과를 사용하지 않을 때
		RandomEvent re = Main.REGISTED_PLAYER.get(e.getEntity().getUniqueId());
		if (re != null && !re.getActivate("POTION")) {
			return;
		}
		
		// 해당 플레이어가 특정 포션효과는 랜덤하게 바꾸지 않을 때,
		PotionEffect origin_effect = e.getNewEffect(); // 원래 포션효과
		// 구현 필요
//		if (re != null && ) {
//			
//		}
		
		e.setCancelled(true); // 이벤트 취소
		
		// 거북모자, 돌고래에 의한 포션효과일 때
		if (e.getCause().equals(EntityPotionEffectEvent.Cause.TURTLE_HELMET) ||
			e.getCause().equals(EntityPotionEffectEvent.Cause.DOLPHIN)) {
			// 활성화중인 포션효과가 5개 이상일 때
			if (entity.getActivePotionEffects().size() >= 5) {
				return;
			}
		}
		
		PotionEffectType random_type = re.getRandomEffect("POTION");
		
		// 먼가 안됨
		if (random_type == null) {
			return;
		}
		
		PotionEffect random_effect = new PotionEffect(
			random_type,
			origin_effect.getDuration(),
			origin_effect.getAmplifier(),
			origin_effect.isAmbient(),
			origin_effect.hasParticles(),
			origin_effect.hasIcon()
		); // 랜덤 포션 효과
		
		// 해당 엔티티에게 포션효과 부여
		entity.addPotionEffect(random_effect);
	}
}
