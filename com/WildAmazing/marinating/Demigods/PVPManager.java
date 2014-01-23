package com.WildAmazing.marinating.Demigods;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import com.WildAmazing.marinating.Demigods.Deities.Deity;

public class PVPManager implements Listener {

	double MULTIPLIER = Settings.getSettingDouble("pvp_exp_bonus"); //bonus for dealing damage
	int pvpkillreward = 1500; //Devotion

	@EventHandler (priority = EventPriority.HIGHEST)
	public void pvpDamage(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player))
			return;
		if (!(e.getEntity() instanceof Player))
			return;
		Player attacker = (Player)e.getDamager();
		Player target = (Player)e.getEntity();
		if (!(DUtil.isFullParticipant(attacker) && DUtil.isFullParticipant(target)))
			return;
		if (!Settings.getEnabledWorlds().contains(attacker.getWorld()))
			return;
		if (DUtil.getAllegiance(attacker).equalsIgnoreCase(DUtil.getAllegiance(target)))
			return;
		if (!DUtil.isPVP(target.getLocation())) {
			attacker.sendMessage(ChatColor.YELLOW+"This is a no-PvP zone.");
			return;
		}
		Deity d = DUtil.getDeities(attacker).get((int)Math.floor(Math.random()*DUtil.getDeities(attacker).size()));
		DUtil.setDevotion(attacker, d, DUtil.getDevotion(attacker, d)+e.getDamage()*2);
		LevelManager.levelProcedure(attacker);
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void playerDeath(EntityDeathEvent e1) {
		if (!(e1.getEntity() instanceof Player))
			return;
		Player attacked = (Player)e1.getEntity();
		if (!Settings.getEnabledWorlds().contains(attacked.getWorld()))
			return;
		if ((attacked.getLastDamageCause() != null) && (attacked.getLastDamageCause() instanceof EntityDamageByEntityEvent)) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)attacked.getLastDamageCause();
			if (!(e.getDamager() instanceof Player))
				return;
			Player attacker = (Player)e.getDamager();
			if (!(DUtil.isFullParticipant(attacker)))
				return;
			if (DUtil.isFullParticipant(attacked)) {
				if (DUtil.getAllegiance(attacker).equalsIgnoreCase(DUtil.getAllegiance(attacked))) { //betrayal
					DUtil.getPlugin().getServer().broadcastMessage(ChatColor.YELLOW+attacked.getName()+ChatColor.GRAY+" was betrayed by "+
							ChatColor.YELLOW+attacker.getName()+ChatColor.GRAY+" of the "+DUtil.getAllegiance(attacker)+" alliance.");
					if (DUtil.getKills(attacker) > 0) {
						DUtil.setKills(attacker, DUtil.getKills(attacker)-1);
						attacker.sendMessage(ChatColor.RED+"Your number of kills has decreased to "+DUtil.getKills(attacker)+".");
					}
				} else { //PVP kill
					DUtil.setKills(attacker, DUtil.getKills(attacker)+1);
					DUtil.setDeaths(attacked, DUtil.getDeaths(attacked)+1);
					DUtil.getPlugin().getServer().broadcastMessage(ChatColor.YELLOW+attacked.getName()+ChatColor.GRAY+" of the "+
							DUtil.getAllegiance(attacked)+ " alliance was slain by "+ChatColor.YELLOW+attacker.getName()+
							ChatColor.GRAY+" of the "+DUtil.getAllegiance(attacker)+" alliance.");
					for (Deity d : DUtil.getDeities(attacker)) {
						DUtil.setDevotion(attacker, d, DUtil.getDevotion(attacker, d)+pvpkillreward);
					}
				}
			} else { //regular player
				DUtil.getPlugin().getServer().broadcastMessage(ChatColor.YELLOW+attacked.getName()+ChatColor.GRAY+" was slain by "+
						ChatColor.YELLOW+attacker.getName()+ChatColor.GRAY+" of the "+DUtil.getAllegiance(attacker)+" alliance.");
			}
		}
	}
}
