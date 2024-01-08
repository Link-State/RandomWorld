package main;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerIO implements Listener {
	
	@EventHandler
	public void PlayerJoin(PlayerJoinEvent e) {
		
		Main.REGISTED_PLAYER.put(e.getPlayer().getUniqueId(), new RandomEvent(e.getPlayer()));
	}
	

	@EventHandler
	public void PlayerLeft(PlayerQuitEvent e) {
		Main.REGISTED_PLAYER.remove(e.getPlayer().getUniqueId());
	}
}
