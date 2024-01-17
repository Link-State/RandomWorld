package main;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerIO implements Listener {
	
	@EventHandler
	public void PlayerJoin(PlayerJoinEvent e) {
		
		Player p = e.getPlayer();
		RandomEvent re = new RandomEvent(e.getPlayer().getUniqueId().toString());
		Main.REGISTED_PLAYER.put(p.getUniqueId(), re);
	}
	

	@EventHandler
	public void PlayerLeft(PlayerQuitEvent e) {
		
		Player p = e.getPlayer();
		Main.REGISTED_PLAYER.remove(p.getUniqueId());
	}
}
