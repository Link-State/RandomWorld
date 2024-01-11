package main;

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
		
		// 플러그인 함수, 명령어, 우유, 일루전, 만료, 죽음, 주민좀비변환에 의한 포션효과일 때
		if (e.getCause().equals(EntityPotionEffectEvent.Cause.PLUGIN) ||
			e.getCause().equals(EntityPotionEffectEvent.Cause.COMMAND) ||
			e.getCause().equals(EntityPotionEffectEvent.Cause.MILK) ||
			e.getCause().equals(EntityPotionEffectEvent.Cause.ILLUSION) ||
			e.getCause().equals(EntityPotionEffectEvent.Cause.EXPIRATION) ||
			e.getCause().equals(EntityPotionEffectEvent.Cause.DEATH) ||
			e.getCause().equals(EntityPotionEffectEvent.Cause.CONVERSION)) {
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

		RandomEvent re = null;
		
		// 플레이어일 경우
		if (entity instanceof Player) {
			re = Main.REGISTED_PLAYER.get(entity.getUniqueId());
		}
		// 플레이어가 아닐 경우
		else {
			re = Main.ENTITY;
		}
		
		// 여기서부터 포션이펙트 세분화 할 것 ============================================================================
		
		// 해당 개체가 포션 랜덤효과를 허용하지 않았을 때
		if (re == null || !re.getActivate("POTION")) {
			return;
		}
		
		PotionEffect origin_effect = e.getNewEffect(); // 원래 포션효과
		PotionEffectType random_type = re.getRandomEffect("POTION", origin_effect.getType());

		// 특정 포션효과는 랜덤하게 바꾸는 것을 허용하지 않을 때,
		if (random_type == null) {
			return;
		}
		
		e.setCancelled(true); // 이벤트 취소
		// 특히 isCancel 이용하여 취소여부 이용하여 이벤트 취소할 것. (안그러면 버프 0초에서 안 지워짐)
		
		// 돌고래에 의해 받은 포션효과가 지정한 최대갯수에 도달했을 때,
		if (e.getCause().equals(EntityPotionEffectEvent.Cause.DOLPHIN) &&
			entity.getActivePotionEffects().size() >= 5) {
			return;
		}
		
		/// 여기까지 포션이펙트 세분화 할 것 ============================================================================
		
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
