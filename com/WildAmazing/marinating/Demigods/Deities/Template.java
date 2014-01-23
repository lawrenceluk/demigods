package com.WildAmazing.marinating.Demigods.Deities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

import com.WildAmazing.marinating.Demigods.DUtil;

public class Template implements Deity {
	private static final long serialVersionUID = -2472769863144336856L;
	private String PLAYER;

	private static final int SKILLCOST = 120;
	private static final int SKILLDELAY = 1250; //milliseconds
	private static final int ULTIMATECOST = 10000;
	private static final int ULTIMATECOOLDOWNMAX = 180; //seconds
	private static final int ULTIMATECOOLDOWNMIN = 60;

	private static final String skillname = "";
	private static final String ult = "";

	private boolean SKILL = false;
	private Material SKILLBIND = null;
	private long SKILLTIME;
	private long ULTIMATETIME;
	private long LASTCHECK;

	public Template(String player) {
		PLAYER = player;
		SKILLTIME = System.currentTimeMillis();
		ULTIMATETIME = System.currentTimeMillis();
		LASTCHECK = System.currentTimeMillis();
	}
	@Override
	public String getName() {
		return "Name";
	}

	@Override
	public String getPlayerName() {
		return PLAYER;
	}

	@Override
	public String getDefaultAlliance() {
		return "";
	}

	@Override
	public void printInfo(Player p) {
		if (DUtil.isFullParticipant(p) && DUtil.hasDeity(p, getName())) {
			int devotion = DUtil.getDevotion(p, getName());
			p.sendMessage("--"+ChatColor.GOLD+getName()+ChatColor.GRAY+"["+devotion+"]");
			return;
		}
		p.sendMessage("--"+getName());
		p.sendMessage("Passive: ");
		p.sendMessage("Active: ");
		p.sendMessage("Ultimate: ");
		p.sendMessage(ChatColor.YELLOW+"Select item: ");
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
					/*
					 * Skill
					 */
					DUtil.setFavor(p, DUtil.getFavor(p)-SKILLCOST);
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
					/*
					 * Ultimate code
					 */
					DUtil.setFavor(p, DUtil.getFavor(p)-ULTIMATECOST);
				} else p.sendMessage(ChatColor.YELLOW+""+ult+" requires "+ULTIMATECOST+" Favor.");
				return;
			}
		}
	}

	@Override
	public void onTick(long timeSent) {
		if (timeSent > LASTCHECK+1000) {
			LASTCHECK = timeSent;
		}
	}
}
