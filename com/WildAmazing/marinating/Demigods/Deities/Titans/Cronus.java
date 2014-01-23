package com.WildAmazing.marinating.Demigods.Deities.Titans;



import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.WildAmazing.marinating.Demigods.DUtil;
import com.WildAmazing.marinating.Demigods.Deities.Deity;

public class Cronus implements Deity {

	private static final long serialVersionUID = -6160291350540472542L;

	//global vars
	private static final int CLEAVECOST = 100;
	private static final int SLOWCOST = 350;
	private static final int CRONUSULTIMATECOST = 7000;
	private static final int CRONUSULTIMATECOOLDOWNMAX = 360;
	private static final int CRONUSULTIMATECOOLDOWNMIN = 80;

	//per player
	String PLAYER;
	boolean CLEAVE = false;
	Material CLEAVEITEM = null;
	boolean SLOW = false;
	Material SLOWITEM = null;
	long CRONUSULTIMATETIME;
	private long CLEAVETIME;

	public Cronus(String player) {
		PLAYER = player;
		CRONUSULTIMATETIME = System.currentTimeMillis();
		CLEAVETIME = System.currentTimeMillis();
	}

	@Override
	public String getName() {
		return "Cronus";
	}
	@Override
	public String getDefaultAlliance() {
		return "Titan";
	}
	@Override
	public void printInfo(Player p) {
		if (DUtil.hasDeity(p, "Cronus") && DUtil.isFullParticipant(p)) {
			int devotion = DUtil.getDevotion(p, getName());
			/*
			 * Calculate special values first
			 */
			//cleave
			int damage = (int)Math.ceil(Math.pow(devotion, 0.35));
			int hungerdamage = (int)Math.ceil(Math.pow(devotion, 0.1776));
			//slow
			int duration = (int)Math.ceil(3.635*Math.pow(devotion, 0.2576)); //seconds
			int strength = (int)Math.ceil(2.757*Math.pow(devotion, 0.097));
			//ultimate
			int slowamount = (int)Math.round(.77179 * Math.pow(DUtil.getAscensions(p), 0.17654391));
			int stopduration = (int)Math.round(9.9155621 * Math.pow(DUtil.getAscensions(p), 0.459019));
			int t = (int)(CRONUSULTIMATECOOLDOWNMAX - ((CRONUSULTIMATECOOLDOWNMAX - CRONUSULTIMATECOOLDOWNMIN)*
					((double)DUtil.getAscensions(p)/100)));
			/*
			 * The printed text
			 */
			p.sendMessage("--"+ChatColor.GOLD+"Cronus"+ChatColor.GRAY+"["+devotion+"]");
			p.sendMessage(":Slow your enemy when attacking with a scythe (hoe).");
			p.sendMessage(":Attack with a scythe to deal "+damage+" damage and "+hungerdamage+" hunger. "+ChatColor.GREEN+"/cleave");
			p.sendMessage(ChatColor.YELLOW+"Costs "+CLEAVECOST+" Favor.");
			if (((Cronus)(DUtil.getDeity(p, "Cronus"))).CLEAVEITEM != null)
				p.sendMessage(ChatColor.AQUA+"    Bound to "+(((Cronus)(DUtil.getDeity(p, "Cronus"))).CLEAVEITEM).name());
			else p.sendMessage(ChatColor.AQUA+"    Use /bind to bind this skill to an item.");
			p.sendMessage(":Slow time to reduce movement speed of an enemy player. "+ChatColor.GREEN+"/slow");
			p.sendMessage(ChatColor.YELLOW+"Costs "+SLOWCOST+" Favor.");
			p.sendMessage("Slow power: "+strength+" for "+duration+" seconds.");
			if (((Cronus)(DUtil.getDeity(p, "Cronus"))).SLOWITEM != null)
				p.sendMessage(ChatColor.AQUA+"    Bound to "+(((Cronus)(DUtil.getDeity(p, "Cronus"))).SLOWITEM).name());
			else p.sendMessage(ChatColor.AQUA+"    Use /bind to bind this skill to an item.");
			p.sendMessage(":Cronus slows enemies' perception of time, slowing their");
			p.sendMessage("movement by "+slowamount+" for "+stopduration+" seconds. "+ChatColor.GREEN+"/timestop");
			p.sendMessage(ChatColor.YELLOW+"Costs "+CRONUSULTIMATECOST+" Favor. Cooldown time: "+t+" seconds.");
			return;
		}
		p.sendMessage("--"+ChatColor.GOLD+"Cronus");
		p.sendMessage("Passive: Slow your enemy when attacking with a scythe (hoe).");
		p.sendMessage("Active: Cause extra damage and hunger with a scythe (hoe). "+ChatColor.GREEN+"/cleave");
		p.sendMessage(ChatColor.YELLOW+"Costs "+CLEAVECOST+" Favor. Can bind.");
		p.sendMessage("Active: Slow time to reduce movement speed of an enemy player.");
		p.sendMessage(ChatColor.GREEN+"/slow "+ChatColor.YELLOW+"Costs "+SLOWCOST+" Favor. Can bind.");
		p.sendMessage("Ultimate: Cronus slows enemies' perception of time,");
		p.sendMessage("slowing their movement drastically. "+ChatColor.GREEN+"/timestop");
		p.sendMessage(ChatColor.YELLOW+"Costs "+CRONUSULTIMATECOST+" Favor. Has cooldown.");
		p.sendMessage(ChatColor.YELLOW+"Select item: soul sand");
	}
	@Override
	public String getPlayerName() {
		return PLAYER;
	}
	@Override
	public void onEvent(Event ee) {
		if (ee instanceof EntityDamageEvent) {
			if ((EntityDamageEvent)ee instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)ee;
				if (e.getDamager() instanceof Player) {
					Player p = (Player)e.getDamager();
					if (DUtil.isFullParticipant(p)) {
						if (!DUtil.hasDeity(p, "Cronus"))
							return;
						if (!p.getItemInHand().getType().name().contains("_HOE"))
							return;
						/*
						 * Passive ability (stop movement)
						 */
						if (e.getEntity() instanceof Player) {
							Player attacked = (Player)e.getEntity();
							if (!DUtil.isFullParticipant(attacked) ||
									(DUtil.isFullParticipant(attacked) && !(DUtil.getAllegiance(p).equalsIgnoreCase(DUtil.getAllegiance(attacked))))) {
								attacked.setVelocity(new Vector(0,0,0));
							}
						}
						/*
						 * Cleave
						 */
						if (CLEAVE || ((CLEAVEITEM != null) && (p.getItemInHand().getType() == CLEAVEITEM))) {
							if (DUtil.getFavor(p) >= CLEAVECOST) {
								if (!(e.getEntity() instanceof LivingEntity))
									return;
								if (System.currentTimeMillis() < CLEAVETIME+100)
									return;
								if (!DUtil.isPVP(e.getEntity().getLocation()))
									return;
								DUtil.setFavor(p, DUtil.getFavor(p) - CLEAVECOST);
								for (int i=1;i<=31;i+=4)
									e.getEntity().getWorld().playEffect(e.getEntity().getLocation(), Effect.SMOKE, i);
								DUtil.damageDemigods(p, (LivingEntity)e.getEntity(), (int)Math.ceil(Math.pow(DUtil.getDevotion(p, getName()), 0.35)));
								CLEAVETIME = System.currentTimeMillis();
								if ((LivingEntity)e.getEntity() instanceof Player) {
									Player otherP = (Player)((LivingEntity)e.getEntity());
									otherP.setFoodLevel(otherP.getFoodLevel()-(e.getDamage()/2));
									if (otherP.getFoodLevel() < 0) otherP.setFoodLevel(0);
								}
							} else {
								p.sendMessage(ChatColor.YELLOW+"You don't have enough Favor to do that.");
								CLEAVE = false;
							}
						}
					}
				}
			}
		}
		else if (ee instanceof PlayerInteractEvent) {
			PlayerInteractEvent e = (PlayerInteractEvent)ee;
			Player p = e.getPlayer();
			if (!DUtil.hasDeity(p, "Cronus"))
				return;
			if (SLOW || ((SLOWITEM != null) && (p.getItemInHand().getType() == SLOWITEM))) {
				if (DUtil.getFavor(p) >= SLOWCOST) {
					if (slow(p))
						DUtil.setFavor(p, DUtil.getFavor(p) - SLOWCOST);
				} else {
					SLOW = false;
					p.sendMessage(ChatColor.YELLOW+"You don't have enough Favor to do that.");
				}
			}
		}
	}
	@Override
	public void onCommand(Player P, String str, String[] args, boolean bind) {
		final Player p = P;
		if (!DUtil.hasDeity(p, "Cronus"))
			return;
		if (str.equalsIgnoreCase("cleave")) {
			if (bind) {
				if (CLEAVEITEM == null) {
					if (DUtil.isBound(p, p.getItemInHand().getType()))
						p.sendMessage(ChatColor.YELLOW+"That item is already bound to a skill.");
					if (p.getItemInHand().getType() == Material.AIR)
						p.sendMessage(ChatColor.YELLOW+"You cannot bind a skill to air.");
					if (!p.getItemInHand().getType().name().contains("_HOE"))
						p.sendMessage(ChatColor.YELLOW+"Cleave can only be bound to a scythe (hoe).");
					else {
						DUtil.registerBind(p, p.getItemInHand().getType());
						CLEAVEITEM = p.getItemInHand().getType();
						p.sendMessage(ChatColor.YELLOW+"Cleave is now bound to "+p.getItemInHand().getType().name()+".");
					}
				} else {
					DUtil.removeBind(p, CLEAVEITEM);
					p.sendMessage(ChatColor.YELLOW+"Cleave is no longer bound to "+CLEAVEITEM.name()+".");
					CLEAVEITEM = null;
				}
				return;
			}
			if (CLEAVE) {
				CLEAVE = false;
				p.sendMessage(ChatColor.YELLOW+"Cleave is no longer active.");
			} else {
				CLEAVE = true;
				p.sendMessage(ChatColor.YELLOW+"Cleave is now active.");
			}
		} else if (str.equalsIgnoreCase("slow")) {
			if (bind) {
				if (SLOWITEM == null) {
					if (DUtil.isBound(p, p.getItemInHand().getType()))
						p.sendMessage(ChatColor.YELLOW+"That item is already bound to a skill.");
					if (p.getItemInHand().getType() == Material.AIR)
						p.sendMessage(ChatColor.YELLOW+"You cannot bind a skill to air.");
					else {
						DUtil.registerBind(p, p.getItemInHand().getType());
						SLOWITEM = p.getItemInHand().getType();
						p.sendMessage(ChatColor.YELLOW+"Slow is now bound to "+p.getItemInHand().getType().name()+".");
					}
				} else {
					DUtil.removeBind(p, SLOWITEM);
					p.sendMessage(ChatColor.YELLOW+"Slow is no longer bound to "+SLOWITEM.name()+".");
					SLOWITEM = null;
				}
				return;
			}
			if (SLOW) {
				SLOW = false;
				p.sendMessage(ChatColor.YELLOW+"Slow is no longer active.");
			} else {
				SLOW = true;
				p.sendMessage(ChatColor.YELLOW+"Slow is now active.");
			}
		} else if (str.equalsIgnoreCase("timestop")) {
			if (!DUtil.hasDeity(p, "Cronus"))
				return;
			if (System.currentTimeMillis() < CRONUSULTIMATETIME){
				p.sendMessage(ChatColor.YELLOW+"You cannot stop time again for "+((((CRONUSULTIMATETIME)/1000)-
						(System.currentTimeMillis()/1000)))/60+" minutes");
				p.sendMessage(ChatColor.YELLOW+"and "+((((CRONUSULTIMATETIME)/1000)-(System.currentTimeMillis()/1000))%60)+" seconds.");
				return;
			}
			if (DUtil.getFavor(p)>=CRONUSULTIMATECOST) {
				int t = (int)(CRONUSULTIMATECOOLDOWNMAX - ((CRONUSULTIMATECOOLDOWNMAX - CRONUSULTIMATECOOLDOWNMIN)*
						((double)DUtil.getAscensions(p)/100)));
				CRONUSULTIMATETIME = System.currentTimeMillis()+(t*1000);
				timeStop(p);
				DUtil.setFavor(p, DUtil.getFavor(p)-CRONUSULTIMATECOST);
			} else p.sendMessage(ChatColor.YELLOW+"Stopping time requires "+CRONUSULTIMATECOST+" Favor.");
			return;
		}
	}
	@Override
	public void onTick(long timeSent) {

	}
	private boolean slow(Player p) {
		int devotion = DUtil.getDevotion(p, getName());
		int duration = (int)Math.ceil(3.635*Math.pow(devotion, 0.2576)); //seconds
		int strength = (int)Math.ceil(2.757*Math.pow(devotion, 0.097));
		Player target = null;
		Block b = p.getTargetBlock(null, 200);
		for (Player pl : b.getWorld().getPlayers()) {
			if (pl.getLocation().distance(b.getLocation()) < 4) {
				if (!DUtil.isTitan(pl) && DUtil.isPVP(pl.getLocation())){
					target = pl;
					break;
				}
			}
		}
		if ((target != null) && (target.getEntityId() != p.getEntityId())) {
			target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration*20, strength));
			p.sendMessage(ChatColor.YELLOW+target.getName()+" has been slowed.");
			target.sendMessage(ChatColor.RED+"You have been slowed for "+duration+" seconds.");
			DUtil.addActiveEffect(target.getName(), "Slow", duration);
			return true;
		} else {
			p.sendMessage(ChatColor.YELLOW+"No target found.");
			return false;
		}
	}
	private void timeStop(Player p) {
		int slowamount = (int)Math.round(4.77179 * Math.pow(DUtil.getAscensions(p), 0.17654391));
		int duration = (int)Math.round(9.9155621 * Math.pow(DUtil.getAscensions(p), 0.459019));
		int count = 0;
		for (Player pl : p.getWorld().getPlayers()) {
			if (DUtil.isFullParticipant(pl)) {
				if (DUtil.getAllegiance(pl).equalsIgnoreCase(DUtil.getAllegiance(p)))
					continue;
				if (!DUtil.isPVP(pl.getLocation()))
					continue;
			}
			pl.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration*20, slowamount));
			DUtil.addActiveEffect(pl.getName(), "Time Stop", duration);
			count++;
		}
		p.sendMessage(ChatColor.RED+"Cronus has slowed time for "+count+" players in your world.");
	}
}
