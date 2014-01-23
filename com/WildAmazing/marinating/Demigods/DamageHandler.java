package com.WildAmazing.marinating.Demigods;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class DamageHandler implements Listener {
	/*
	 * This handler deals with non-Demigods damage (all of that will go directly to DUtil's built in damage function) and converts it
	 * to Demigods HP, using individual multipliers for balance purposes.
	 * 
	 * The adjusted value should be around/less than 1 to adjust for the increased health, but not ridiculous
	 */


	@EventHandler (priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player))
			return;
		Player p = (Player)e.getEntity();
		if (!DUtil.isFullParticipant(p))
			return;
		if (!Settings.getEnabledWorlds().contains(p.getWorld()))
			return;
		if (e instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent)e;
			if (ee.getDamager() instanceof Player)
				DUtil.damageDemigods((LivingEntity)ee.getDamager(), p, e.getDamage());
		}
		if ((e.getCause() != DamageCause.ENTITY_ATTACK) && (e.getCause() != DamageCause.PROJECTILE))
			DUtil.damageDemigodsNonCombat(p, e.getDamage());
		DUtil.damageDemigods(p, e.getDamage());
		if (DUtil.getHP(p) < 0) DUtil.setHP(p, 0);
		e.setDamage(0);
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onRespawn(PlayerRespawnEvent e) {
		if (DUtil.isFullParticipant(e.getPlayer())) {
			DUtil.setHP(e.getPlayer(), DUtil.getMaxHP(e.getPlayer()));
		}
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onHeal(EntityRegainHealthEvent e) {
		if (!(e.getEntity() instanceof Player))
			return;
		Player p = (Player)e.getEntity();
		if (!DUtil.isFullParticipant(p))
			return;
		//	int newhp = DUtil.getHP(p)+(int)((DUtil.getMaxHP(p)*e.getAmount()*1.0)/20);
		//	if (newhp > DUtil.getMaxHP(p)) newhp = DUtil.getMaxHP(p);
		//	DUtil.setHP(p, newhp);
		e.setCancelled(true);
		DUtil.setHP(p, DUtil.getHP(p)+e.getAmount());
		syncHealth(p);
	}
	public static void syncHealth(Player p) {
		int current = DUtil.getHP(p);
		if (current < 1) { //if player should be dead
			EntityDamageEvent e = p.getLastDamageCause();
			p.damage(100);
			p.setLastDamageCause(e);
			return;
		}
		double ratio = ((double)current)/DUtil.getMaxHP(p);
		int disp = (int)Math.ceil(ratio*20);
		if (disp < 1) disp = 1;
		p.setHealth(disp);
	}
	public static int armorReduction(Player p) {
		if (p.getLastDamageCause() != null)
			if ((p.getLastDamageCause().getCause() == DamageCause.FIRE) || (p.getLastDamageCause().getCause() == DamageCause.FIRE_TICK) || (p.getLastDamageCause().getCause() == DamageCause.SUFFOCATION) ||
					(p.getLastDamageCause().getCause() == DamageCause.DROWNING) || (p.getLastDamageCause().getCause() == DamageCause.STARVATION) || (p.getLastDamageCause().getCause() == DamageCause.FALL) ||
					(p.getLastDamageCause().getCause() == DamageCause.VOID) || (p.getLastDamageCause().getCause() == DamageCause.POISON) || (p.getLastDamageCause().getCause() == DamageCause.MAGIC) ||
					(p.getLastDamageCause().getCause() == DamageCause.SUICIDE)) {
				return 0;
			}
		double reduction = 0.0;
		if ((p.getInventory().getBoots() != null) && (p.getInventory().getBoots().getType() != Material.AIR)) {
			switch (p.getInventory().getBoots().getType()) {
			case LEATHER_BOOTS: reduction += 0.3; break;
			case IRON_BOOTS: reduction += 0.6; break;
			case GOLD_BOOTS: reduction += 0.5; break;
			case DIAMOND_BOOTS: reduction += 0.8; break;
			case CHAINMAIL_BOOTS: reduction += 0.7; break;
			}
			p.getInventory().getBoots().setDurability((short) (p.getInventory().getBoots().getDurability()+1));
			if (p.getInventory().getBoots().getDurability() > p.getInventory().getBoots().getType().getMaxDurability())
				p.getInventory().setBoots(null);
		}
		if ((p.getInventory().getLeggings() != null) && (p.getInventory().getLeggings().getType() != Material.AIR)) {
			switch (p.getInventory().getLeggings().getType()) {
			case LEATHER_LEGGINGS: reduction += 0.5; break;
			case IRON_LEGGINGS: reduction += 1; break;
			case GOLD_LEGGINGS: reduction += 0.8; break;
			case DIAMOND_LEGGINGS: reduction += 1.4; break;
			case CHAINMAIL_LEGGINGS: reduction += 1.1; break;
			}
			p.getInventory().getLeggings().setDurability((short) (p.getInventory().getLeggings().getDurability()+1));
			if (p.getInventory().getLeggings().getDurability() > p.getInventory().getLeggings().getType().getMaxDurability())
				p.getInventory().setLeggings(null);
		}
		if ((p.getInventory().getChestplate() != null) && (p.getInventory().getChestplate().getType() != Material.AIR)) {
			switch (p.getInventory().getChestplate().getType()) {
			case LEATHER_CHESTPLATE: reduction += 0.8; break;
			case IRON_CHESTPLATE: reduction += 1.6; break;
			case GOLD_CHESTPLATE: reduction += 1.4; break;
			case DIAMOND_CHESTPLATE: reduction += 2; break;
			case CHAINMAIL_CHESTPLATE: reduction += 1.8; break;
			}
			p.getInventory().getChestplate().setDurability((short) (p.getInventory().getChestplate().getDurability()+1));
			if (p.getInventory().getChestplate().getDurability() > p.getInventory().getChestplate().getType().getMaxDurability())
				p.getInventory().setChestplate(null);
		}
		if ((p.getInventory().getHelmet() != null) && (p.getInventory().getHelmet().getType() != Material.AIR)) {
			switch (p.getInventory().getHelmet().getType()) {
			case LEATHER_HELMET: reduction += 0.4; break;
			case IRON_HELMET: reduction += 0.8; break;
			case GOLD_HELMET: reduction += 0.7; break;
			case DIAMOND_HELMET: reduction += 1.3; break;
			case CHAINMAIL_HELMET: reduction += 1; break;
			}
			p.getInventory().getHelmet().setDurability((short) (p.getInventory().getHelmet().getDurability()+1));
			if (p.getInventory().getHelmet().getDurability() > p.getInventory().getHelmet().getType().getMaxDurability())
				p.getInventory().setHelmet(null);
		}
		return (int)(Math.round(reduction));
	}
	public static int specialReduction(Player p, int amount) {
		if (DUtil.getActiveEffectsList(p.getName()) == null)
			return amount;
		if (DUtil.getActiveEffectsList(p.getName()).contains("Invincible")) {
			amount *= 0.5;
		}
		if (DUtil.getActiveEffectsList(p.getName()).contains("Ceasefire")) {
			amount *= 0;
		}
		return amount;
	}
}
