package main;

import java.lang.reflect.Field;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffectType;

public class GivePotionEffect implements Listener {

	@EventHandler
	public void PotionEffect(EntityPotionEffectEvent e) {
		Field[] i = PotionEffectType.class.getDeclaredFields();
		
		for (Field idx : i) {
			try {
				System.out.println((PotionEffectType) idx.get(i));
			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				System.out.println("일리갈알규멘트익셉션");
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				System.out.println("일리갈엑세스익셉션");
			}
		}
	}
}
