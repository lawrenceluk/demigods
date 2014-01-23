package com.WildAmazing.marinating.Demigods;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.WildAmazing.marinating.Demigods.Deities.Deity;

public class DeityManager implements Listener {
	/*
	 * Distributes all events to deities
	 */
	public DeityManager() {
		DUtil.getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(DUtil.getPlugin(), new Runnable() {
			@Override
			public void run() {
				for (String name : DUtil.getFullParticipants()) {
					Player p = DUtil.getOnlinePlayer(name);
					if ((p != null) && p.isOnline()) {
						if (Settings.getEnabledWorlds().contains(p.getWorld())) {
							for (Deity d : DUtil.getDeities(p))
								d.onTick(System.currentTimeMillis());
						}
					}
				}
			}
		}, 20, 5);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (!Settings.getEnabledWorlds().contains(e.getBlock().getWorld()))
			return;
		//Player
		Player p = e.getPlayer();
		if ((DUtil.getDeities(p)!=null) && (DUtil.getDeities(p).size()>0)) {
			for (Deity d : DUtil.getDeities(p))
				d.onEvent(e);
		}
	}
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if (!Settings.getEnabledWorlds().contains(e.getBlock().getWorld()))
			return;
		//Player
		Player p = e.getPlayer();
		if ((DUtil.getDeities(p)!=null) && (DUtil.getDeities(p).size()>0)) {
			for (Deity d : DUtil.getDeities(p))
				d.onEvent(e);
		}
	}
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e){
		if (!Settings.getEnabledWorlds().contains(e.getEntity().getWorld()))
			return;
		for (Player pl : e.getEntity().getWorld().getPlayers()) {
			if (DUtil.isFullParticipant(pl)) {
				if ((DUtil.getDeities(pl)!=null) && (DUtil.getDeities(pl).size()>0)) {
					for (Deity d : DUtil.getDeities(pl))
						d.onEvent(e);
				}
			}
		}
	}
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e){
		if (!Settings.getEnabledWorlds().contains(e.getEntity().getWorld()))
			return;
		if (e.getEntity() instanceof Player) {
			Player p = (Player)e.getEntity();
			if ((DUtil.getDeities(p)!=null) && (DUtil.getDeities(p).size()>0)) {
				for (Deity d : DUtil.getDeities(p))
					d.onEvent(e);
			}
		}
	}
	@EventHandler
	public void onEntityTarget(EntityTargetEvent e){
		if (!Settings.getEnabledWorlds().contains(e.getEntity().getWorld()))
			return;
		if (e.isCancelled())
			return;
		if (e.getTarget() instanceof Player) {
			Player p = (Player)e.getTarget();
			if ((DUtil.getDeities(p)!=null) && (DUtil.getDeities(p).size()>0)) {
				for (Deity d : DUtil.getDeities(p))
					d.onEvent(e);
			}
		}
	}
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){ //sync to master file
		final Player p = e.getPlayer();
		if (!Settings.getEnabledWorlds().contains(p.getWorld()))
			return;
		if (Settings.getSettingBoolean("motd")) {
			p.sendMessage("This server is running Demigods v"+ChatColor.YELLOW+DUtil.getPlugin().getDescription().getVersion()+ChatColor.WHITE+".");
			p.sendMessage(ChatColor.GRAY+"Type "+ChatColor.GREEN+"/dg"+ChatColor.GRAY+" for more info.");
		}
		if (!DSave.hasPlayer(p)) {
			Logger.getLogger("Minecraft").info("[Demigods] "+p.getName()+" joined and no save was detected. Creating new file.");
			DSave.addPlayer(p);
		}
		DSave.saveData(p, "LASTLOGINTIME", System.currentTimeMillis());
	}
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent e){
		Player p = e.getPlayer();
		if (!Settings.getEnabledWorlds().contains(p.getWorld()))
			return;
		if ((DUtil.getDeities(p)!=null) && (DUtil.getDeities(p).size()>0)) {
			for (Deity d : DUtil.getDeities(p))
				d.onEvent(e);
		}
	}
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e){
		Player p = e.getPlayer();
		if (DUtil.isFullParticipant(p))
			if (DSave.hasData(p, "ALLIANCECHAT")) {
				if ((Boolean)DSave.getData(p, "ALLIANCECHAT")) {
					e.setCancelled(true);
					Logger.getLogger("Minecraft").info("["+DUtil.getAllegiance(p)+"] "+p.getName()+": "+e.getMessage());
					for (Player pl : DUtil.getPlugin().getServer().getOnlinePlayers()) {
						if (DUtil.isFullParticipant(pl) && DUtil.getAllegiance(pl).equalsIgnoreCase(DUtil.getAllegiance(p)))
							pl.sendMessage(ChatColor.GREEN+"["+DUtil.getAscensions(p)+"] "+p.getName()+": "+e.getMessage());
					}
				}
			}
		if (!Settings.getEnabledWorlds().contains(p.getWorld()))
			return;
		if ((DUtil.getDeities(p)!=null) && (DUtil.getDeities(p).size()>0)) {
			for (Deity d : DUtil.getDeities(p))
				d.onEvent(e);
		}
	}
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e){
		Player p = e.getPlayer();
		if (!Settings.getEnabledWorlds().contains(p.getWorld()))
			return;
		if ((DUtil.getDeities(p)!=null) && (DUtil.getDeities(p).size()>0)) {
			for (Deity d : DUtil.getDeities(p))
				d.onEvent(e);
		}
	}
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		if (!Settings.getEnabledWorlds().contains(p.getWorld()))
			return;
		if ((DUtil.getDeities(p)!=null) && (DUtil.getDeities(p).size()>0)) {
			for (Deity d : DUtil.getDeities(p))
				d.onEvent(e);
		}
	}
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		if (!Settings.getEnabledWorlds().contains(p.getWorld()))
			return;
		if ((DUtil.getDeities(p)!=null) && (DUtil.getDeities(p).size()>0)) {
			for (Deity d : DUtil.getDeities(p))
				d.onEvent(e);
		}
	}
}
