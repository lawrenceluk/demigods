package com.WildAmazing.marinating.Demigods.Deities.Gods;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.WildAmazing.marinating.Demigods.DUtil;
import com.WildAmazing.marinating.Demigods.Deities.Deity;

/*
 * Affected by level:
 * Amount of EXP gained for overkill
 * Damage, range, slow amount, slow duration of strike
 * Duration of bloodthirst
 * Range, damage, and cooldown of ultimate
 */

public class Ares implements Deity {
	private static final long serialVersionUID = -5825867521620334951L;
	private String PLAYER;
	/*
	 * Needs to be loaded out of config
	 */
	private static final int STRIKECOST = 120;
	private static final int STRIKEDELAY = 1250; //milliseconds
	private static final int ARESULTIMATECOST = 5000;
	private static final int ARESULTIMATECOOLDOWNMAX = 180; //seconds
	private static final int ARESULTIMATECOOLDOWNMIN = 60;


	private boolean STRIKE = false;
	public Material STRIKEBIND = null;
	private long STRIKETIME;
	private long ARESULTIMATETIME;

	public Ares(String player) {
		PLAYER = player;
		ARESULTIMATETIME = System.currentTimeMillis();
		STRIKETIME = System.currentTimeMillis();
	}
	@Override
	public String getDefaultAlliance() {
		return "God";
	}
	@Override
	public void printInfo(Player p) {
		if (DUtil.hasDeity(p, "Ares") && DUtil.isFullParticipant(p)) {
			int devotion = DUtil.getDevotion(p, getName());
			/*
			 * Calculate special values first
			 */
			int dmg = (int)Math.round(0.9*Math.pow(devotion, 0.34));
			final int slowpower = (int)(Math.ceil(1.681539*Math.pow(devotion, 0.11457)));
			int duration = (int)(Math.ceil(2.9573*Math.pow(devotion, 0.138428)));
			//ultimate
			int targets = (int)Math.ceil(3.08*(Math.pow(1.05, DUtil.getAscensions(p))));
			int range = (int)Math.ceil(7.17*Math.pow(1.035, DUtil.getAscensions(p)));
			int damage = (int)Math.ceil(10*Math.pow(DUtil.getAscensions(p), 0.868));
			int confuseduration = (int)(1.0354*Math.pow(DUtil.getAscensions(p), 0.4177))*20;
			int t = (int)(ARESULTIMATECOOLDOWNMAX - ((ARESULTIMATECOOLDOWNMAX - ARESULTIMATECOOLDOWNMIN)*
					((double)DUtil.getAscensions(p)/100)));
			/*
			 * The printed text
			 */
			p.sendMessage("--"+ChatColor.GOLD+"Ares"+ChatColor.GRAY+" ["+devotion+"]");
			p.sendMessage(":Up to "+DUtil.getAscensions(p)+" additional Favor per hit on overkill.");
			p.sendMessage(":Strike an enemy from afar with your sword, slowing them down.");
			p.sendMessage("Slow: "+slowpower+" for "+duration+" seconds. Damage: "+
					dmg+ChatColor.GREEN+" /strike "+ChatColor.YELLOW+"Costs "+STRIKECOST+" Favor.");
			if (((Ares)DUtil.getDeity(p, getName())).STRIKEBIND != null)
				p.sendMessage(ChatColor.AQUA+"    Bound to "+((Ares)DUtil.getDeity(p, getName())).STRIKEBIND.name());
			else p.sendMessage(ChatColor.AQUA+"    Use /bind to bind this skill to an item.");
			p.sendMessage(":Ares flings up to "+targets+" targets within range "+range+" to you, dealing");
			p.sendMessage(damage+" damage to each and confusing them for "+confuseduration+" seconds."+ChatColor.GREEN+" /crash");
			p.sendMessage(ChatColor.YELLOW+"Costs "+ARESULTIMATECOST+" Favor. Cooldown time: "+t+" seconds.");
			return;
		}
		p.sendMessage("--"+ChatColor.GOLD+"Ares");
		p.sendMessage("Passive: Gain favor for overkill attacks.");
		p.sendMessage("Active: Strike at an enemy from afar with your sword, with");
		p.sendMessage("a slowing effect. "+ChatColor.GREEN+"/strike");
		p.sendMessage(ChatColor.YELLOW+"Costs "+STRIKECOST+" Favor. Can bind.");
		p.sendMessage("Ultimate: Ares flings nearby enemies towards you. Damages and");
		p.sendMessage("confuses targets. "+ChatColor.GREEN+"/crash");
		p.sendMessage(ChatColor.YELLOW+"Costs "+ARESULTIMATECOST+" Favor. Has cooldown.");
		p.sendMessage(ChatColor.YELLOW+"Select item: gold sword");
	}
	@Override
	public String getName() {
		return "Ares";
	}

	@Override
	public String getPlayerName() {
		return PLAYER;
	}

	@Override
	public void onEvent(Event ee) {
		if (ee instanceof PlayerInteractEvent) {
			PlayerInteractEvent e = (PlayerInteractEvent)ee;
			Player p = e.getPlayer();
			if (!DUtil.hasDeity(p, "Ares") || !DUtil.isFullParticipant(p))
				return;
			if ((p.getItemInHand() != null) && p.getItemInHand().getType().name().contains("SWORD")) {
				if (STRIKE || ((p.getItemInHand() != null) && (p.getItemInHand().getType() == STRIKEBIND))) {
					if (STRIKETIME > System.currentTimeMillis())
						return;
					STRIKETIME = System.currentTimeMillis()+STRIKEDELAY;
					if (DUtil.getFavor(p) >= STRIKECOST) {
						strike(p);
						DUtil.setFavor(p, DUtil.getFavor(p)-STRIKECOST);
						return;
					} else {
						p.sendMessage(ChatColor.YELLOW+"You do not have enough Favor.");
						STRIKE = false;
					}
				}
			}
		} else if (ee instanceof EntityDamageByEntityEvent) {
			try {
				EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)ee;
				if (e.getDamager() instanceof Player){
					Player p = (Player)e.getDamager();
					if (!DUtil.hasDeity(p, "Ares") || !DUtil.isFullParticipant(p))
						return;
					try {
						LivingEntity le = (LivingEntity) e.getEntity();
						if (le.getHealth()-e.getDamage()<=0){
							//
							if ((int)(Math.random()*3)==1){
								int reward = 1+(int)(Math.random()*DUtil.getAscensions(p));
								p.sendMessage(ChatColor.RED+"Finishing bonus: +"+reward);
								DUtil.setFavor(p, DUtil.getFavor(p)+reward);
							}
						}
					} catch (Exception notliving){}
				}
			} catch (Exception notthatevent){}
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
		if (DUtil.hasDeity(p, "Ares")) {
			if (str.equalsIgnoreCase("strike")) {
				if (bind) {
					if (STRIKEBIND == null) {
						if (DUtil.isBound(p, p.getItemInHand().getType()))
							p.sendMessage(ChatColor.YELLOW+"That item is already bound to a skill.");
						if (p.getItemInHand().getType() == Material.AIR)
							p.sendMessage(ChatColor.YELLOW+"You cannot bind a skill to air.");
						if (!p.getItemInHand().getType().name().contains("SWORD"))
							p.sendMessage(ChatColor.YELLOW+"You must bind this skill to a sword.");
						else {
							DUtil.registerBind(p, p.getItemInHand().getType());
							STRIKEBIND = p.getItemInHand().getType();
							p.sendMessage(ChatColor.YELLOW+"Strike is now bound to "+p.getItemInHand().getType().name()+".");
						}
					} else {
						DUtil.removeBind(p, STRIKEBIND);
						p.sendMessage(ChatColor.YELLOW+"Strike is no longer bound to "+STRIKEBIND.name()+".");
						STRIKEBIND = null;
					}
					return;
				}
				if (STRIKE) {
					STRIKE = false;
					p.sendMessage(ChatColor.YELLOW+"Strike is no longer active.");
				} else {
					STRIKE = true;
					p.sendMessage(ChatColor.YELLOW+"Strike is now active.");
				}
			} else if (str.equalsIgnoreCase("crash")) {
				long TIME = ARESULTIMATETIME;
				if (System.currentTimeMillis() < TIME){
					p.sendMessage(ChatColor.YELLOW+"You cannot use the power crash again for "+((((TIME)/1000)-
							(System.currentTimeMillis()/1000)))/60+" minutes");
					p.sendMessage(ChatColor.YELLOW+"and "+((((TIME)/1000)-(System.currentTimeMillis()/1000))%60)+" seconds.");
					return;
				}
				if (DUtil.getFavor(p)>=ARESULTIMATECOST) {
					int hits = crash(p);
					if (hits < 1) {
						p.sendMessage(ChatColor.YELLOW+"No targets were found, or the skill could not be used.");
						return;
					}
					int t = (int)(ARESULTIMATECOOLDOWNMAX - ((ARESULTIMATECOOLDOWNMAX - ARESULTIMATECOOLDOWNMIN)*
							((double)DUtil.getAscensions(p)/100)));
					ARESULTIMATETIME = System.currentTimeMillis()+(t*1000);
					p.sendMessage("In exchange for "+ChatColor.AQUA+ARESULTIMATECOST+ChatColor.WHITE+" Favor, ");
					p.sendMessage(ChatColor.GOLD+"Ares"+ChatColor.WHITE+" has unleashed his powers on "+hits+" non-allied entities.");
					DUtil.setFavor(p, DUtil.getFavor(p)-ARESULTIMATECOST);
				} else p.sendMessage(ChatColor.YELLOW+"Power crash requires "+ARESULTIMATECOST+" Favor.");
				return;
			}
		}
	}
	/*
	 * ---------------
	 * Helper methods
	 * ---------------
	 */
	private boolean strike(Player p) {
		/*/
		 * 
		 */
		LivingEntity target = DUtil.getTargetLivingEntity(p, 2);
		if (target == null) {
			p.sendMessage(ChatColor.YELLOW+"No target found.");
			return false;
		}
		if (!DUtil.isPVP(target.getLocation())) {
			p.sendMessage(ChatColor.YELLOW+"Can't attack in a no-PVP zone.");
			return false;
		}
		/*
		 * Calculate special values
		 */
		int devotion = DUtil.getDevotion(p, getName());
		int damage = (int)Math.round(0.9*Math.pow(devotion, 0.34));
		final int slowpower = (int)(Math.ceil(1.681539*Math.pow(devotion, 0.11457)));
		int duration = (int)(Math.ceil(2.9573*Math.pow(devotion, 0.138428)));
		duration*=20; //ticks
		/*
		 * Deal damage and slow if player
		 */
		DUtil.damageDemigods(p, target, damage);
		target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, slowpower));
		return true;
	}
	private int crash(Player p) {
		/*
		 * Calculate specials.
		 * Range: distance in a circle
		 * Damage: done instantly
		 * Confusion: how long players remain dizzied
		 */
		int range = (int)(7.17*Math.pow(1.035, DUtil.getAscensions(p)));
		int damage = (int)(1.929*Math.pow(DUtil.getAscensions(p), 0.48028));
		int confuseduration = (int)(1.0354*Math.pow(DUtil.getAscensions(p), 0.4177))*20;
		/*
		 * The ultimate
		 */
		ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>();
		ArrayList<Player> confuse = new ArrayList<Player>();
		for (LivingEntity le : p.getWorld().getLivingEntities()) {
			if (le.getLocation().distance(p.getLocation()) <= range) {
				if (le instanceof Player) {
					Player pt = (Player)le;
					if (DUtil.isGod(pt) || pt.equals(p))
						continue;
					if (!DUtil.isPVP(le.getLocation()))
						continue;
					targets.add(le);
					confuse.add(pt);
				} else targets.add(le);
			}
		}
		if (targets.size() > 0) {
			for (LivingEntity le : targets) {
				Vector v = le.getLocation().toVector();
				Vector victor = p.getLocation().toVector().subtract(v);
				le.setVelocity(victor);
				DUtil.damageDemigods(p, le, damage);
			}
		}
		if (confuse.size() > 0) {
			for (Player pl : confuse) {
				(new PotionEffect(PotionEffectType.CONFUSION, confuseduration*20, 4)).apply(pl);
			}
		}
		return targets.size();
	}
	@Override
	public void onTick(long timeSent) {

	}
}
