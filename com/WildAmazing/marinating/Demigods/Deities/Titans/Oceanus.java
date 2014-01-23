package com.WildAmazing.marinating.Demigods.Deities.Titans;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Squid;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import com.WildAmazing.marinating.Demigods.DUtil;
import com.WildAmazing.marinating.Demigods.Deities.Deity;

public class Oceanus implements Deity {
	private static final long serialVersionUID = -2472769863144336856L;
	private String PLAYER;

	private static final String skillname = "Squid";
	private static final int SKILLCOST = 225;
	private static final int SKILLDELAY = 2000; //milliseconds
	private static final int ULTIMATECOST = 2000;
	private static final int ULTIMATECOOLDOWNMAX = 700; //seconds
	private static final int ULTIMATECOOLDOWNMIN = 400;


	private boolean SKILL = false;
	private Material SKILLBIND = null;
	private long SKILLTIME;
	private long ULTIMATETIME;
	private long LASTCHECK;

	public Oceanus(String player) {
		PLAYER = player;
		SKILLTIME = System.currentTimeMillis();
		ULTIMATETIME = System.currentTimeMillis();
		LASTCHECK = System.currentTimeMillis();
	}
	@Override
	public String getName() {
		return "Oceanus";
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
			//heal amount
			int healamt = (int)Math.ceil(0.1*Math.pow(devotion, 0.297));
			//heal interval
			int healinterval = 10-(int)(Math.round(Math.pow(devotion, 0.125))); //seconds
			if (healinterval < 1) healinterval = 1;
			//squid radius
			float radius = 1+(int)Math.round(Math.pow(devotion, 0.1142));
			//ult
			int duration = (int)Math.round(40*Math.pow(devotion, 0.15)); //seconds
			int t = (int)(ULTIMATECOOLDOWNMAX - ((ULTIMATECOOLDOWNMAX - ULTIMATECOOLDOWNMIN)*
					((double)DUtil.getAscensions(p)/100)));
			/*
			 * Print text
			 */
			p.sendMessage("--"+ChatColor.GOLD+getName()+ChatColor.GRAY+"["+devotion+"]");
			p.sendMessage(":While in the rain, heal "+healamt+" HP every "+healinterval+" seconds.");
			p.sendMessage(":Left-click to throw a squid that explodes with "+radius+" radius."+ChatColor.GREEN+" /squid "+ChatColor.YELLOW+"Costs "+SKILLCOST+" Favor.");
			if (((Oceanus)DUtil.getDeity(p, getName())).SKILLBIND != null)
				p.sendMessage(ChatColor.AQUA+"    Bound to "+((Oceanus)DUtil.getDeity(p, getName())).SKILLBIND.name());
			else p.sendMessage(ChatColor.AQUA+"    Use /bind to bind this skill to an item.");
			p.sendMessage("Oceanus causes a rainstorm lasting "+duration+" seconds."+ChatColor.GREEN+" /makeitrain");
			p.sendMessage(ChatColor.YELLOW+"Costs "+ULTIMATECOST+" Favor. Cooldown time: "+t+" seconds.");
			return;
		}
		p.sendMessage("--"+ChatColor.GOLD+getName());
		p.sendMessage("Passive: Oceanus grants increased healing while in rain.");
		p.sendMessage("Active: Launch an exploding squid at the target location. "+ChatColor.GREEN+"/squid");
		p.sendMessage(ChatColor.YELLOW+"Costs "+SKILLCOST+" Favor. Can bind.");
		p.sendMessage("Ultimate: Cause a rainstorm in the current world. "+ChatColor.GREEN+"/makeitrain");
		p.sendMessage(ChatColor.YELLOW+"Select item: ink sac");
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
					if (squidfire(p))
						DUtil.setFavor(p, DUtil.getFavor(p) - SKILLCOST);
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
			} else if (str.equalsIgnoreCase("makeitrain")) {
				long TIME = ULTIMATETIME;
				if (System.currentTimeMillis() < TIME){
					p.sendMessage(ChatColor.YELLOW+"You cannot use rain again for "+((((TIME)/1000)-
							(System.currentTimeMillis()/1000)))/60+" minutes");
					p.sendMessage(ChatColor.YELLOW+"and "+((((TIME)/1000)-(System.currentTimeMillis()/1000))%60)+" seconds.");
					return;
				}
				if (DUtil.getFavor(p)>=ULTIMATECOST) {
					int t = (int)(ULTIMATECOOLDOWNMAX - ((ULTIMATECOOLDOWNMAX - ULTIMATECOOLDOWNMIN)*
							((double)DUtil.getAscensions(p)/100)));
					ULTIMATETIME = System.currentTimeMillis()+(t*1000);
					p.getWorld().setStorm(true);
					p.getWorld().setThundering(true);
					p.getWorld().setWeatherDuration((int)Math.round(40*Math.pow(DUtil.getDevotion(p, getName()), 0.15))*20);
					p.sendMessage("In exchange for "+ChatColor.AQUA+ULTIMATECOST+ChatColor.WHITE+" Favor, ");
					p.sendMessage(ChatColor.GOLD+"Oceanus"+ChatColor.WHITE+" has started a rainstorm on your world.");
					DUtil.setFavor(p, DUtil.getFavor(p)-ULTIMATECOST);
				} else p.sendMessage(ChatColor.YELLOW+"Ceasefire requires "+ULTIMATECOST+" Favor.");
				return;
			}
		}
	}
	@Override
	public void onTick(long timeSent) {
		int healinterval = 10-(int)(Math.round(Math.pow(DUtil.getDevotion(getPlayerName(), getName()), 0.125))); //seconds
		if (healinterval < 1) healinterval = 1;
		if (timeSent > LASTCHECK+(healinterval*1000)) {
			LASTCHECK = timeSent;
			if ((DUtil.getOnlinePlayer(getPlayerName()) != null) && DUtil.getOnlinePlayer(getPlayerName()).getWorld().hasStorm()) {
				int healamt = (int)Math.ceil(0.1*Math.pow(DUtil.getDevotion(getPlayerName(), getName()), 0.297));
				if (DUtil.getHP(getPlayerName())+healamt > DUtil.getMaxHP(getPlayerName()))
					healamt = DUtil.getMaxHP(getPlayerName())-DUtil.getHP(getPlayerName());
				DUtil.setHP(getPlayerName(), DUtil.getHP(getPlayerName())+healamt);
			}
		}
	}
	private boolean squidfire(Player p) {
		Location target = DUtil.getTargetLocation(p);
		if (target == null)
			return false;
		if (!DUtil.isPVP(target))
			return false;
		Squid squid = p.getWorld().spawn(p.getLocation(), Squid.class);
		Vector v = p.getLocation().toVector();
		Vector victor = target.toVector().subtract(v);
		squid.setVelocity(victor);
		final Squid ss = squid;
		DUtil.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(DUtil.getPlugin(), new Runnable(){
			@Override
			public void run(){
				ss.setHealth(0);
				ss.getWorld().createExplosion(ss.getLocation(), 1+Math.round(Math.pow(DUtil.getDevotion(getPlayerName(), getName()), 0.1142)));
			}
		},60);
		return true;
	}
}
