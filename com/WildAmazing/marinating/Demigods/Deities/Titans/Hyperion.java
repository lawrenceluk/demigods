package com.WildAmazing.marinating.Demigods.Deities.Titans;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.WildAmazing.marinating.Demigods.DUtil;
import com.WildAmazing.marinating.Demigods.Deities.Deity;

public class Hyperion implements Deity {
	private static final long serialVersionUID = -2472769863144336856L;
	private String PLAYER;

	private static final int SKILLCOST = 200;
	private static final int SKILLDELAY = 1250; //milliseconds
	private static final int ULTIMATECOST = 6500;
	private static final int ULTIMATECOOLDOWNMAX = 600; //seconds
	private static final int ULTIMATECOOLDOWNMIN = 300;

	private static final String skillname = "Starfall";
	private static final String ult = "Smite";

	private boolean SKILL = false;
	private Material SKILLBIND = null;
	private long SKILLTIME;
	private long ULTIMATETIME;
	private long LASTCHECK;

	public Hyperion(String player) {
		PLAYER = player;
		SKILLTIME = System.currentTimeMillis();
		ULTIMATETIME = System.currentTimeMillis();
		LASTCHECK = System.currentTimeMillis();
	}
	@Override
	public String getName() {
		return "Hyperion";
	}

	@Override
	public String getPlayerName() {
		return PLAYER;
	}

	@Override
	public String getDefaultAlliance() {
		return "Titan";
	}

	@Override
	public void printInfo(Player p) {
		if (DUtil.isFullParticipant(p) && DUtil.hasDeity(p, getName())) {
			int devotion = DUtil.getDevotion(p, getName());
			/*
			 * Calculate special values first
			 */
			//starfall
			int damage = (int)(Math.round(1.4*Math.pow(devotion, 0.1)));
			int range = (int)(Math.ceil(8*Math.pow(devotion, 0.08)));
			//ult
			int numtargets = (int)Math.round(10*Math.pow(devotion, 0.11));
			int igniteduration = (int)Math.round(5*Math.pow(devotion, 0.15));
			int ultrange = (int)Math.round(25*Math.pow(devotion, 0.09));
			int ultdamage = (int)(Math.floor(10*Math.pow(devotion, 0.105)));
			int t = (int)(ULTIMATECOOLDOWNMAX - ((ULTIMATECOOLDOWNMAX - ULTIMATECOOLDOWNMIN)*
					((double)DUtil.getAscensions(p)/100)));
			/*
			 * The printed text
			 */
			p.sendMessage("--"+ChatColor.GOLD+getName()+ChatColor.GRAY+"["+devotion+"]");
			p.sendMessage(":Move with increased speed while in a well-lit area.");
			p.sendMessage(":Left-click to call down an attack dealing "+damage+" in radius "+range+"."+ChatColor.GREEN+" /starfall "+ChatColor.YELLOW+"Costs "+SKILLCOST+" Favor.");
			if (((Hyperion)DUtil.getDeity(p, getName())).SKILLBIND != null)
				p.sendMessage(ChatColor.AQUA+"    Bound to "+((Hyperion)DUtil.getDeity(p, getName())).SKILLBIND.name());
			else p.sendMessage(ChatColor.AQUA+"    Use /bind to bind this skill to an item.");
			p.sendMessage("Ignite up to "+numtargets+" enemies in range "+ultrange+" for "+igniteduration+" seconds, then");
			p.sendMessage("attack them for "+ultdamage+" damage."+ChatColor.GREEN+" /smite");
			p.sendMessage(ChatColor.YELLOW+"Costs "+ULTIMATECOST+" Favor. Cooldown time: "+t+" seconds.");
			return;
		}
		p.sendMessage("--"+getName());
		p.sendMessage("Passive: Move with increased speed while in a bright area.");
		p.sendMessage("Active: Damage nearby enemies with strikes from above. "+ChatColor.GREEN+"/starfall");
		p.sendMessage(ChatColor.YELLOW+"Costs "+SKILLCOST+" Favor. Can bind.");
		p.sendMessage("Ultimate: Ignite nearby enemies with the power of the sun, then");
		p.sendMessage("attack for a killing blow. "+ChatColor.GREEN+"/smite "+ChatColor.YELLOW+"Costs "+ULTIMATECOST+" Favor. Has cooldown.");
		p.sendMessage(ChatColor.YELLOW+"Select item: glowstone");
	}

	@Override
	public void onEvent(Event ee) {
		if (ee instanceof PlayerInteractEvent) {
			PlayerInteractEvent e = (PlayerInteractEvent)ee;
			Player p = e.getPlayer();
			if (!DUtil.isFullParticipant(p) || !DUtil.hasDeity(p, getName()))
				return;
			if (SKILL || ((p.getItemInHand() != null) && (p.getItemInHand().getType() == SKILLBIND))) {
				if (SKILLTIME > System.currentTimeMillis())
					return;
				SKILLTIME = System.currentTimeMillis()+SKILLDELAY;
				if (DUtil.getFavor(p) >= SKILLCOST) {
					if (starfall(p) > 0)
						DUtil.setFavor(p, DUtil.getFavor(p)-SKILLCOST);
					else p.sendMessage(ChatColor.YELLOW+"No targets found.");
					return;
				} else {
					p.sendMessage(ChatColor.YELLOW+"You do not have enough Favor.");
					SKILL = false;
				}
			}
		}
	}

	@Override
	public void onCommand(Player P, String str, String[] args, boolean bind) {
		final Player p = P;
		if (DUtil.hasDeity(p, getName())) {
			if (str.equalsIgnoreCase(skillname)) {
				if (bind) {
					if (SKILLBIND == null) {
						if (DUtil.isBound(p, p.getItemInHand().getType()))
							p.sendMessage(ChatColor.YELLOW+"That item is already bound to a skill.");
						if (p.getItemInHand().getType() == Material.AIR)
							p.sendMessage(ChatColor.YELLOW+"You cannot bind a skill to air.");
						else {
							DUtil.registerBind(p, p.getItemInHand().getType());
							SKILLBIND = p.getItemInHand().getType();
							p.sendMessage(ChatColor.YELLOW+""+skillname+" is now bound to "+p.getItemInHand().getType().name()+".");
						}
					} else {
						DUtil.removeBind(p, SKILLBIND);
						p.sendMessage(ChatColor.YELLOW+""+skillname+" is no longer bound to "+SKILLBIND.name()+".");
						SKILLBIND = null;
					}
					return;
				}
				if (SKILL) {
					SKILL = false;
					p.sendMessage(ChatColor.YELLOW+""+skillname+" is no longer active.");
				} else {
					SKILL = true;
					p.sendMessage(ChatColor.YELLOW+""+skillname+" is now active.");
				}
			} else if (str.equalsIgnoreCase(ult)) {
				long TIME = ULTIMATETIME;
				if (System.currentTimeMillis() < TIME){
					p.sendMessage(ChatColor.YELLOW+"You cannot use "+ult+" again for "+((((TIME)/1000)-
							(System.currentTimeMillis()/1000)))/60+" minutes");
					p.sendMessage(ChatColor.YELLOW+"and "+((((TIME)/1000)-(System.currentTimeMillis()/1000))%60)+" seconds.");
					return;
				}
				if (DUtil.getFavor(p)>=ULTIMATECOST) {
					int t = (int)(ULTIMATECOOLDOWNMAX - ((ULTIMATECOOLDOWNMAX - ULTIMATECOOLDOWNMIN)*
							((double)DUtil.getAscensions(p)/100)));
					ULTIMATETIME = System.currentTimeMillis()+(t*1000);
					int num = smite(p);
					if (num > 0) {
						p.sendMessage("In exchange for "+ChatColor.AQUA+ULTIMATECOST+ChatColor.WHITE+" Favor, "+ChatColor.GOLD+"Hyperion"+ChatColor.WHITE+" has struck "+num+" targets.");
						DUtil.setFavor(p, DUtil.getFavor(p)-ULTIMATECOST);
					} else p.sendMessage(ChatColor.YELLOW+"No targets found.");
				} else p.sendMessage(ChatColor.YELLOW+""+ult+" requires "+ULTIMATECOST+" Favor.");
				return;
			}
		}
	}
	@Override
	public void onTick(long timeSent) {
		if (timeSent > LASTCHECK+1000) {
			LASTCHECK = timeSent;
			Player p = DUtil.getOnlinePlayer(getPlayerName());
			if ((p != null) && p.isOnline()) {
				if (p.getLocation().getBlock().getLightLevel() > 12) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 110, 0));
				}
			}
		}
	}
	private int starfall(final Player p) {
		int damage = (int)(Math.round(1.4*Math.pow(DUtil.getDevotion(p, getName()), 0.1)));
		int range = (int)(Math.ceil(8*Math.pow(DUtil.getDevotion(p, getName()), 0.08)));
		ArrayList<LivingEntity> entitylist = new ArrayList<LivingEntity>();
		Vector ploc = p.getLocation().toVector();
		for (LivingEntity anEntity : p.getWorld().getLivingEntities()){
			if (anEntity instanceof Player)
				if (DUtil.isFullParticipant((Player)anEntity))
					if (DUtil.isTitan((Player)anEntity))
						continue;
			if (!DUtil.isPVP(anEntity.getLocation()))
				continue;
			if (anEntity.getLocation().toVector().isInSphere(ploc, range))
				entitylist.add(anEntity);
		}
		for (LivingEntity le : entitylist) {
			for (int i=0;i<12;i++) {
				for (int j=0;j<3;j++) {
					Location loc = le.getLocation();
					loc.setY(le.getLocation().getBlockY()+i);
					le.getWorld().playEffect(loc, Effect.SMOKE, (int)(Math.random()*16));
				}
			}
			DUtil.damageDemigods(p, le, damage);
		}
		return entitylist.size();
	}
	private int smite(final Player p) {
		int devotion = DUtil.getDevotion(p, getName());
		int numtargets = (int)Math.round(10*Math.pow(devotion, 0.11));
		int igniteduration = (int)Math.round(5*Math.pow(devotion, 0.15));
		int ultrange = (int)Math.round(25*Math.pow(devotion, 0.09));
		final int ultdamage = (int)(Math.floor(10*Math.pow(devotion, 0.105)));
		ArrayList<LivingEntity> entitylist = new ArrayList<LivingEntity>();
		Vector ploc = p.getLocation().toVector();
		for (LivingEntity anEntity : p.getWorld().getLivingEntities()){
			if (anEntity instanceof Player)
				if (DUtil.isFullParticipant((Player)anEntity))
					if (DUtil.isTitan((Player)anEntity))
						continue;
			if (!DUtil.isPVP(anEntity.getLocation()))
				continue;
			if (anEntity.getLocation().toVector().isInSphere(ploc, ultrange) && (entitylist.size() < numtargets))
				entitylist.add(anEntity);
		}
		final Location start = p.getLocation();
		int delay = 0;
		for (final LivingEntity le : entitylist) {
			delay += 20;
			le.setFireTicks(igniteduration*20);
			DUtil.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(DUtil.getPlugin(), new Runnable() {
				@Override
				public void run() {
					p.teleport(le.getLocation());
					DUtil.damageDemigods(p, le, ultdamage);
				}
			}, delay);
		}
		DUtil.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(DUtil.getPlugin(), new Runnable() {
			@Override
			public void run() {
				p.teleport(start);
			}
		}, 20*entitylist.size()+20);
		return entitylist.size();
	}
}
