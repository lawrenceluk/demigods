package com.WildAmazing.marinating.Demigods.Deities.Gods;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import com.WildAmazing.marinating.Demigods.DUtil;
import com.WildAmazing.marinating.Demigods.Deities.Deity;

/*
 * Affected by level:
 * Fall damage reduction
 * Shove distance/range
 * Ultimate cooldown
 */

public class Zeus implements Deity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2242753324910371936L;

	private String PLAYER;
	private static final int SHOVECOST = 170;
	private static final int SHOVEDELAY = 1500; //milliseconds
	private static final int LIGHTNINGCOST = 140;
	private static final int LIGHTNINGDELAY = 1000; //milliseconds
	private static final int ZEUSULTIMATECOST = 3700;
	private static final int ZEUSULTIMATECOOLDOWNMAX = 600; //seconds
	private static final int ZEUSULTIMATECOOLDOWNMIN = 60;

	private long ZEUSULTIMATETIME;
	private long ZEUSSHOVETIME;
	private long ZEUSLIGHTNINGTIME;
	private boolean SHOVE = false;
	private boolean LIGHTNING = false;
	private Material SHOVEBIND = null;
	private Material LIGHTNINGBIND = null;

	public Zeus(String player) {
		PLAYER = player;
		ZEUSULTIMATETIME = System.currentTimeMillis();
		ZEUSSHOVETIME = System.currentTimeMillis();
		ZEUSLIGHTNINGTIME = System.currentTimeMillis();
	}
	@Override
	public String getDefaultAlliance() {
		return "God";
	}
	@Override
	public void printInfo(Player p) {
		if (DUtil.hasDeity(p, "Zeus") && DUtil.isFullParticipant(p)) {
			int devotion = DUtil.getDevotion(p, getName());
			/*
			 * Calculate special values first
			 */
			//shove
			int targets = (int)Math.ceil(1.561*Math.pow(devotion, 0.128424));
			double multiply = 0.1753*Math.pow(devotion, 0.322917);
			//ultimate
			int t = (int)(ZEUSULTIMATECOOLDOWNMAX - ((ZEUSULTIMATECOOLDOWNMAX - ZEUSULTIMATECOOLDOWNMIN)*
					((double)DUtil.getAscensions(p)/100)));
			/*
			 * The printed text
			 */
			p.sendMessage("--"+ChatColor.GOLD+"Zeus"+ChatColor.GRAY+" ["+devotion+"]");
			p.sendMessage(":Immune to fall damage.");
			p.sendMessage(":Strike lightning at a target location. "+ChatColor.GREEN+"/lightning");
			p.sendMessage(ChatColor.YELLOW+"Costs "+LIGHTNINGCOST+" Favor.");
			if (((Zeus)(DUtil.getDeity(p, "Zeus"))).LIGHTNINGBIND != null)
				p.sendMessage(ChatColor.AQUA+"    Bound to "+((Zeus)(DUtil.getDeity(p, "Zeus"))).LIGHTNINGBIND.name());
			else p.sendMessage(ChatColor.AQUA+"    Use /bind to bind this skill to an item.");
			p.sendMessage(":Use the force of wind to knock back enemies. "+ChatColor.GREEN+"/shove");
			p.sendMessage(ChatColor.YELLOW+"Costs "+SHOVECOST+" Favor.");
			p.sendMessage("Affects up to "+targets+" targets with power "+(int)(Math.round(multiply*10))+".");
			if (((Zeus)(DUtil.getDeity(p, "Zeus"))).SHOVEBIND != null)
				p.sendMessage(ChatColor.AQUA+"    Bound to "+((Zeus)(DUtil.getDeity(p, "Zeus"))).SHOVEBIND.name());
			else p.sendMessage(ChatColor.AQUA+"    Use /bind to bind this skill to an item.");
			p.sendMessage(":Zeus strikes lightning on nearby enemies as they are");
			p.sendMessage("raised in the air and dropped. "+ChatColor.GREEN+"/storm");
			p.sendMessage(ChatColor.YELLOW+"Costs "+ZEUSULTIMATECOST+" Favor. Cooldown time: "+t+" seconds.");
			return;
		}
		p.sendMessage("--"+ChatColor.GOLD+"Zeus");
		p.sendMessage("Passive: Immune to fall damage.");
		p.sendMessage("Active: Strike lightning at a target location. "+ChatColor.GREEN+"/lightning");
		p.sendMessage(ChatColor.YELLOW+"Costs "+LIGHTNINGCOST+" Favor. Can bind.");
		p.sendMessage("Active: Use the force of wind to knock back enemies. "+ChatColor.GREEN+"/shove");
		p.sendMessage(ChatColor.YELLOW+"Costs "+SHOVECOST+" Favor. Can bind.");
		p.sendMessage("Ultimate: Zeus strikes lightning on nearby enemies as they are");
		p.sendMessage("raised in the air and dropped. "+ChatColor.GREEN+"/storm");
		p.sendMessage(ChatColor.YELLOW+"Costs "+ZEUSULTIMATECOST+" Favor. Has cooldown.");
		p.sendMessage(ChatColor.YELLOW+"Select item: iron ingot");
	}
	@Override
	public String getName() {
		return "Zeus";
	}

	@Override
	public String getPlayerName() {
		return PLAYER;
	}

	@Override
	public void onEvent(Event ee) {
		if (ee instanceof EntityDamageEvent) {
			EntityDamageEvent e = (EntityDamageEvent)ee;
			if (e.getEntity() instanceof Player){
				Player p = (Player)e.getEntity();
				if (!DUtil.hasDeity(p, "Zeus") || !DUtil.isFullParticipant(p))
					return;
				if (e.getCause()==DamageCause.FALL) {
					e.setDamage(0);
				}
				else if (e.getCause()==DamageCause.LIGHTNING){
					e.setCancelled(true);
				}
			}
		}
		else if (ee instanceof PlayerInteractEvent) {
			PlayerInteractEvent e = (PlayerInteractEvent)ee;
			Player p = e.getPlayer();
			if (!DUtil.hasDeity(p, "Zeus") || !DUtil.isFullParticipant(p))
				return;
			if (SHOVE || ((p.getItemInHand() != null) && (p.getItemInHand().getType() == SHOVEBIND))) {
				if (ZEUSSHOVETIME > System.currentTimeMillis())
					return;
				ZEUSSHOVETIME = System.currentTimeMillis()+SHOVEDELAY;
				if (DUtil.getFavor(p) >= SHOVECOST) {
					shove(p);
					DUtil.setFavor(p, DUtil.getFavor(p)-SHOVECOST);
					return;
				} else {
					p.sendMessage(ChatColor.YELLOW+"You do not have enough Favor.");
					SHOVE = false;
				}
			}
			if (LIGHTNING || ((p.getItemInHand() != null) && (p.getItemInHand().getType() == LIGHTNINGBIND))) {
				if (ZEUSLIGHTNINGTIME > System.currentTimeMillis())
					return;
				ZEUSLIGHTNINGTIME = System.currentTimeMillis()+LIGHTNINGDELAY;
				if (DUtil.getFavor(p) >= LIGHTNINGCOST) {
					lightning(p);
					DUtil.setFavor(p, DUtil.getFavor(p)-LIGHTNINGCOST);
					return;
				} else {
					p.sendMessage(ChatColor.YELLOW+"You do not have enough Favor.");
					LIGHTNING = false;
				}
			}
		}
	}
	/*
	 * ---------------
	 * Commands
	 * ---------------
	 */
	@Override
	public void onCommand(Player P, String str, String[] args, boolean bind) {
		final Player p = P;
		if (!DUtil.hasDeity(p, "Zeus"))
			return;
		if (str.equalsIgnoreCase("lightning")) {
			if (bind) {
				if (LIGHTNINGBIND == null) {
					if (DUtil.isBound(p, p.getItemInHand().getType()))
						p.sendMessage(ChatColor.YELLOW+"That item is already bound to a skill.");
					if (p.getItemInHand().getType() == Material.AIR)
						p.sendMessage(ChatColor.YELLOW+"You cannot bind a skill to air.");
					else {
						DUtil.registerBind(p, p.getItemInHand().getType());
						LIGHTNINGBIND = p.getItemInHand().getType();
						p.sendMessage(ChatColor.YELLOW+"Lightning is now bound to "+p.getItemInHand().getType().name()+".");
					}
				} else {
					DUtil.removeBind(p, LIGHTNINGBIND);
					p.sendMessage(ChatColor.YELLOW+"Lightning is no longer bound to "+LIGHTNINGBIND.name()+".");
					LIGHTNINGBIND = null;
				}
				return;
			}
			if (LIGHTNING) {
				LIGHTNING = false;
				p.sendMessage(ChatColor.YELLOW+"Lightning is no longer active.");
			} else {
				LIGHTNING = true;
				p.sendMessage(ChatColor.YELLOW+"Lightning is now active.");
			}
		} else if (str.equalsIgnoreCase("shove")) {
			if (bind) {
				if (SHOVEBIND == null) {
					if (DUtil.isBound(p, p.getItemInHand().getType()))
						p.sendMessage(ChatColor.YELLOW+"That item is already bound to a skill.");
					if (p.getItemInHand().getType() == Material.AIR)
						p.sendMessage(ChatColor.YELLOW+"You cannot bind a skill to air.");
					else {
						DUtil.registerBind(p, p.getItemInHand().getType());
						SHOVEBIND = p.getItemInHand().getType();
						p.sendMessage(ChatColor.YELLOW+"Shove is now bound to "+p.getItemInHand().getType().name()+".");
					}
				} else {
					DUtil.removeBind(p, SHOVEBIND);
					p.sendMessage(ChatColor.YELLOW+"Shove is no longer bound to "+SHOVEBIND.name()+".");
					SHOVEBIND = null;
				}
				return;
			}
			if (SHOVE) {
				SHOVE = false;
				p.sendMessage(ChatColor.YELLOW+"Shove is no longer active.");
			} else {
				SHOVE = true;
				p.sendMessage(ChatColor.YELLOW+"Shove is now active.");
			}
		} else if (str.equalsIgnoreCase("storm")) {
			if (!DUtil.hasDeity(p, "Zeus"))
				return;
			long TIME = ZEUSULTIMATETIME;
			if (System.currentTimeMillis() < TIME){
				p.sendMessage(ChatColor.YELLOW+"You cannot use the lightning storm again for "+((((TIME)/1000)-
						(System.currentTimeMillis()/1000)))/60+" minutes");
				p.sendMessage(ChatColor.YELLOW+"and "+((((TIME)/1000)-(System.currentTimeMillis()/1000))%60)+" seconds.");
				return;
			}
			if (DUtil.getFavor(p)>=ZEUSULTIMATECOST) {
				int t = (int)(ZEUSULTIMATECOOLDOWNMAX - ((ZEUSULTIMATECOOLDOWNMAX - ZEUSULTIMATECOOLDOWNMIN)*
						((double)DUtil.getAscensions(p)/100)));
				int num = storm(p);
				if (num > 0) {
					p.sendMessage("In exchange for "+ChatColor.AQUA+ZEUSULTIMATECOST+ChatColor.WHITE+" Favor, ");
					p.sendMessage(ChatColor.GOLD+"Zeus"+ChatColor.WHITE+" has unloaded his wrath on "+num+" targets.");
					DUtil.setFavor(p, DUtil.getFavor(p)-ZEUSULTIMATECOST);
					ZEUSULTIMATETIME = System.currentTimeMillis()+t*1000;
				} else p.sendMessage(ChatColor.YELLOW+"There are no targets nearby.");
			} else p.sendMessage(ChatColor.YELLOW+"Lightning storm requires "+ZEUSULTIMATECOST+" Favor.");
			return;
		}
	}
	/*
	 * ---------------
	 * Helper methods
	 * ---------------
	 */
	private void shove(Player p) {
		ArrayList<LivingEntity> hit = new ArrayList<LivingEntity>();
		int devotion = DUtil.getDevotion(p, getName());
		int targets = (int)Math.ceil(1.561*Math.pow(devotion, 0.128424));
		double multiply = 0.1753*Math.pow(devotion, 0.322917);
		for (Block b : p.getLineOfSight(null, 10)) {
			for (LivingEntity le : p.getWorld().getLivingEntities()) {
				if (targets == hit.size())
					break;
				if (le instanceof Player) {
					if (DUtil.isGod((Player)le))
						continue;
				}
				if ((le.getLocation().distance(b.getLocation()) <= 5) && !hit.contains(le))
					if (DUtil.isPVP(le.getLocation()))
						hit.add(le);
			}
		}
		if (hit.size() > 0) {
			for (LivingEntity le : hit) {
				Vector v = p.getLocation().toVector();
				Vector victor = le.getLocation().toVector().subtract(v);
				victor.multiply(multiply);
				le.setVelocity(victor);
			}
		}
	}
	private void lightning(Player p) {
		Location target = null;
		Block b = p.getTargetBlock(null, 200);
		target = b.getLocation();
		if (p.getLocation().distance(target) > 2){
			try {
				strikeLightning(p, target);
			} catch (Exception nullpointer){} //ignore it if something went wrong
		} else
			p.sendMessage(ChatColor.YELLOW+"Your target is too far away, or too close to you.");
	}
	private int storm(Player p) {
		ArrayList<Entity> entitylist = new ArrayList<Entity>();
		Vector ploc = p.getLocation().toVector();
		for (Entity anEntity : p.getWorld().getEntities()){
			if (anEntity.getLocation().toVector().isInSphere(ploc, 50.0))
				entitylist.add(anEntity);
		}
		int count = 0;
		for (Entity eee : entitylist) {
			try {
				LivingEntity e1 = (LivingEntity)eee;
				if (e1 instanceof Player){
					Player ptemp = (Player)e1;
					if (!DUtil.isGod(ptemp)&&!ptemp.equals(p)){
						strikeLightning(p, ptemp.getLocation());
						strikeLightning(p, ptemp.getLocation());
						strikeLightning(p, ptemp.getLocation());
						count++;
					}
				}
				else{
					count++;
					strikeLightning(p, e1.getLocation());
					strikeLightning(p, e1.getLocation());
					strikeLightning(p, e1.getLocation());
				}
			}catch (Exception notAlive){} //ignore stuff like minecarts
		}
		return count;
	}
	private void strikeLightning(Player p, Location target) {
		if (!p.getWorld().equals(target.getWorld()))
			return;
		if (!DUtil.isPVP(target))
			return;
		p.getWorld().strikeLightning(target);
		for (Entity e : target.getBlock().getChunk().getEntities()) {
			if (e instanceof LivingEntity) {
				LivingEntity le = (LivingEntity)e;
				if (le.getLocation().distance(target) < 1.5)
					DUtil.damageDemigods(p, le, DUtil.getAscensions(p)*2);
			}
		}
	}
	@Override
	public void onTick(long timeSent) {

	}
}