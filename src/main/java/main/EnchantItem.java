package main;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

public class EnchantItem extends RandomItem implements Listener {
	
	@EventHandler
	public void enchantItem(EnchantItemEvent e) {
		// #가끔 적용 안됨 => 왜? = 자연생성 아이템이 아니어서.
		changeRandomItem(e.getItem());
	}
}
