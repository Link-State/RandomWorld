package main;

import java.util.Set;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class RandomPlayer extends RandomEvent {
	private final LivingEntity entity; // 플레이어
	private final Player p;
	
	public RandomPlayer() {
		super("");
		this.p = null;
		this.entity = null;
	}
}
