package com.WildAmazing.marinating.Demigods;

import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.WildAmazing.marinating.Demigods.Deities.Deity;

public class ShrineManager implements Listener {
	protected static double FAVORMULTIPLIER = Settings.getSettingDouble("globalfavormultiplier");
	protected static int RADIUS = 8;

	@EventHandler (priority = EventPriority.HIGH)
	public void createShrine(PlayerInteractEvent e) {
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		if (!Settings.getEnabledWorlds().contains(e.getClickedBlock().getWorld()))
			return;
		if (!DUtil.isFullParticipant(e.getPlayer()))
			return;
		if ((e.getClickedBlock().getType() != Material.SIGN) && (e.getClickedBlock().getType() != Material.SIGN_POST))
			return;
		Sign s = (Sign)e.getClickedBlock().getState();
		if (!s.getLines()[0].trim().equalsIgnoreCase("shrine"))
			return;
		if (!s.getLines()[1].trim().equalsIgnoreCase("dedicate"))
			return;
		String deityname = null;
		Player p = e.getPlayer();
		for (String name : DUtil.getDeityNames(p))
			if (s.getLines()[2].trim().equalsIgnoreCase(name)) {
				deityname = name;
				break;
			}
		if (deityname == null)
			return;
		if (DUtil.getShrine(p.getName(), deityname) != null) {
			p.sendMessage(ChatColor.YELLOW+"You already have a shrine dedicated to "+deityname+".");
			return;
		}
		String shrinename = "";
		if (s.getLines()[3].trim().length() > 0) {
			if (s.getLines()[3].trim().charAt(0) == '#') {
				p.sendMessage(ChatColor.YELLOW+"The shrine's name cannot begin with an invalid character.");
				return;
			}
			if (s.getLines()[3].trim().contains(" ")) {
				p.sendMessage(ChatColor.YELLOW+"The shrine's name cannot contain a space.");
				return;
			}
			for (Deity d : Demigods.deities) {
				if (s.getLines()[3].trim().equalsIgnoreCase(d.getName())) {
					p.sendMessage(ChatColor.YELLOW+"The shrine's name cannot be the same as a deity.");
					return;
				}
			}
			for (WriteLocation w : DUtil.getAllShrines()) {
				if (DUtil.getShrineName(w).equals(s.getLines()[3].trim())) {
					p.sendMessage(ChatColor.YELLOW+"A shrine with that name already exists.");
					return;
				}
			}
			shrinename = "#"+s.getLines()[3].trim();
		}
		for (WriteLocation center : DUtil.getAllShrines())
			if (DUtil.toLocation(center).getWorld().equals(e.getClickedBlock().getWorld()))
				if (e.getClickedBlock().getLocation().distance(DUtil.toLocation(center)) < (RADIUS+1)) {
					p.sendMessage(ChatColor.YELLOW+"Too close to an existing shrine.");
					return;
				}
		//conditions cleared
		DUtil.addShrine(p.getName(), deityname, DUtil.toWriteLocation(e.getClickedBlock().getLocation()));
		if (shrinename.length() > 1)
			DUtil.addShrine(p.getName(), shrinename, DUtil.toWriteLocation(e.getClickedBlock().getLocation())); //accessible by two names
		e.getClickedBlock().setType(Material.GOLD_BLOCK);
		e.getClickedBlock().getWorld().strikeLightningEffect(e.getClickedBlock().getLocation());
		p.sendMessage(ChatColor.AQUA+"You have dedicated this shrine to "+deityname+".");
		p.sendMessage(ChatColor.YELLOW+"Warp here at any time with /shrinewarp "+deityname.toLowerCase()+".");
		if ((shrinename.length() > 0) && (shrinename.charAt(0) == '#'))
			p.sendMessage(ChatColor.YELLOW+"You may also warp here using /shrinewarp "+shrinename.substring(1).toLowerCase()+".");
	}

	@EventHandler (priority = EventPriority.HIGH)
	public void destroyShrine(BlockBreakEvent e) {
		if (!Settings.getEnabledWorlds().contains(e.getBlock().getWorld()))
			return;
		for (WriteLocation center : DUtil.getAllShrines())
			if ((DUtil.toWriteLocation(e.getBlock().getLocation())).equalsApprox(center)) {
				e.getPlayer().sendMessage(ChatColor.YELLOW+"Shrines cannot be broken by hand.");
				e.setCancelled(true);
				return;
			}
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onBlockDamage(BlockDamageEvent e) {
		if (!Settings.getEnabledWorlds().contains(e.getBlock().getWorld()))
			return;
		for (WriteLocation center : DUtil.getAllShrines())
			if ((DUtil.toWriteLocation(e.getBlock().getLocation())).equalsApprox(center))
				e.setCancelled(true);
	}
	@EventHandler (priority = EventPriority.MONITOR)
	public void onBlockIgnite(BlockIgniteEvent e) {
		if (!Settings.getEnabledWorlds().contains(e.getBlock().getWorld()))
			return;
		for (WriteLocation center : DUtil.getAllShrines())
			if ((DUtil.toWriteLocation(e.getBlock().getLocation())).equalsApprox(center))
				e.setCancelled(true);
	}
	@EventHandler (priority = EventPriority.MONITOR)
	public void onBlockBurn(BlockBurnEvent e) {
		if (!Settings.getEnabledWorlds().contains(e.getBlock().getWorld()))
			return;
		for (WriteLocation center : DUtil.getAllShrines())
			if ((DUtil.toWriteLocation(e.getBlock().getLocation())).equalsApprox(center))
				e.setCancelled(true);
	}
	@EventHandler (priority = EventPriority.MONITOR)
	public void explosionManaging(final EntityExplodeEvent e) {
		if (!Settings.getEnabledWorlds().contains(e.getLocation().getWorld()))
			return;
		try {
			//removes shrine blocks from explosions
			Iterator<Block> i = e.blockList().iterator();
			while (i.hasNext()) {
				Block b = i.next();
				if (!DUtil.isPVP(b.getLocation()))
					i.remove();
				for (WriteLocation center : DUtil.getAllShrines())
					if ((DUtil.toWriteLocation(b.getLocation())).equalsApprox(center))
						i.remove();
			}
		} catch (Exception er) {}
	}
	@EventHandler (priority = EventPriority.HIGH) //tribute
	public void playerTribute(PlayerInteractEvent e) {
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		if (!Settings.getEnabledWorlds().contains(e.getClickedBlock().getWorld()))
			return;
		if (e.getClickedBlock().getType() != Material.GOLD_BLOCK)
			return;
		if (!DUtil.isFullParticipant(e.getPlayer()))
			return;
		//check if block is shrine
		String deityname = DUtil.getDeityAtShrine(DUtil.toWriteLocation(e.getClickedBlock().getLocation()));
		if (deityname == null)
			return;
		//check if player has deity
		Player p = e.getPlayer();
		for (Deity d : DUtil.getDeities(p))
			if (d.getName().equalsIgnoreCase(deityname)) {
				//open the tribute inventory
				Inventory ii = DUtil.getPlugin().getServer().createInventory(p, 27, "Tributes");
				p.openInventory(ii);
				DSave.saveData(p, deityname.toUpperCase()+"_TRIBUTE_", DUtil.getOwnerOfShrine(DUtil.toWriteLocation(e.getClickedBlock().getLocation())));
				return;
			}
		p.sendMessage(ChatColor.YELLOW+"You must be allianced to "+deityname+" in order to tribute here.");
	}
	@EventHandler (priority = EventPriority.HIGH)
	public void shrineAlerts(PlayerMoveEvent e) {
		if (e.getFrom().distance(e.getTo()) < 0.1)
			return;
		for (String player : DUtil.getFullParticipants()) {
			for (WriteLocation center : DUtil.getShrines(player).values()) {
				if (!DUtil.toLocation(center).getWorld().equals(e.getPlayer().getWorld()))
					return;
				/*
				 * Outside coming in
				 */
				if (e.getFrom().distance(DUtil.toLocation(center)) > RADIUS) {
					if (DUtil.toLocation(center).distance(e.getTo()) <= RADIUS) {
						e.getPlayer().sendMessage(ChatColor.GRAY+"You have entered "+player+"'s shrine to "+ChatColor.YELLOW+DUtil.getDeityAtShrine(center)+ChatColor.GRAY+".");
						return;
					}
				}
				/*
				 * Leaving
				 */
				else if (e.getFrom().distance(DUtil.toLocation(center)) <= RADIUS) {
					if (DUtil.toLocation(center).distance(e.getTo()) > RADIUS) {
						e.getPlayer().sendMessage(ChatColor.GRAY+"You have left a shrine.");
						return;
					}
				}
			}
		}
	}
	@EventHandler (priority = EventPriority.MONITOR)
	public void tributeSuccess(InventoryCloseEvent e) {
		if (!Settings.getEnabledWorlds().contains(e.getPlayer().getWorld()))
			return;
		if (!(e.getPlayer() instanceof Player))
			return;
		Player p = (Player)e.getPlayer();
		if (!DUtil.isFullParticipant(p))
			return;
		//continue if tribute chest
		if (!e.getInventory().getName().equals("Tributes"))
			return;
		//get which deity tribute goes to
		String togive = null;
		for (String player : DUtil.getFullParticipants()) {
			for (Deity d : DUtil.getDeities(player)) {
				if (DSave.hasData(player, d.getName().toUpperCase()+"_TRIBUTE_")) {
					togive = d.getName();
					break;
				}
			}
		}
		if (togive == null)
			return;
		String creator = (String) DSave.removeData(p, togive.toUpperCase()+"_TRIBUTE_"); //get the creator of the shrine
		//calculate value of chest
		int value = 0;
		int items = 0;
		for (ItemStack ii : e.getInventory().getContents()) {
			if (ii != null) {
				value += DUtil.getValue(ii);
				items ++;
			}
		}
		value *= FAVORMULTIPLIER;
		//give devotion
		int dbefore = DUtil.getDevotion(p, togive);
		DUtil.setDevotion(p, togive, DUtil.getDevotion(p, togive)+value);
		DUtil.setDevotion(creator, togive, DUtil.getDevotion(creator, togive)+value/7);
		//give favor
		int fbefore = DUtil.getFavorCap(p);
		DUtil.setFavorCap(p, DUtil.getFavorCap(p)+value/5);
		//devotion lock TODO
		if (dbefore < DUtil.getDevotion(p, togive))
			p.sendMessage(ChatColor.YELLOW+"Your Devotion for "+togive+" has increased to "+DUtil.getDevotion(p, togive)+".");
		if (fbefore < DUtil.getFavorCap(p))
			p.sendMessage(ChatColor.YELLOW+"Your Favor Cap has increased to "+DUtil.getFavorCap(p)+".");
		if ((fbefore == DUtil.getFavorCap(p)) && (dbefore == DUtil.getDevotion(p, togive)) && (items > 0))
			p.sendMessage(ChatColor.YELLOW+"Your tributes were insufficient for "+togive+"'s blessings.");
		LevelManager.levelProcedure(p);
		//clear inventory
		e.getInventory().clear();
	}
}
