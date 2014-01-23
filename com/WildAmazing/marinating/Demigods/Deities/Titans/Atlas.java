package com.WildAmazing.marinating.Demigods.Deities.Titans;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.WildAmazing.marinating.Demigods.DUtil;
import com.WildAmazing.marinating.Demigods.Deities.Deity;

public class Atlas implements Deity {

	/* General */
	private static final long serialVersionUID = 1898032566168889851L;
	private final int SKILLCOST = 95;
	private final int ULTIMATECOST = 6000;
	private final int ULTIMATECOOLDOWNMAX = 400;
	private final int ULTIMATECOOLDOWNMIN = 300;

	/* Specific to player */
	private String PLAYER;
	private boolean SKILL = false;
	private long  ULTIMATETIME;

	public Atlas(String name) {
		PLAYER = name;
		ULTIMATETIME = System.currentTimeMillis();
	}
	@Override
	public String getName() {
		return "Atlas";
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
		if (DUtil.hasDeity(p, "Atlas") && DUtil.isFullParticipant(p)) {
			int devotion = DUtil.getDevotion(p, getName());
			/*
			 * Calculate special values first
			 */
			int reduction = (int)Math.round(Math.pow(devotion, 0.115));
			//
			int jump = (int)Math.ceil(0.85*Math.pow(devotion, 0.08));
			int length = (int)Math.ceil(4*Math.pow(devotion, 0.2475));
			//
			int duration = (int)(Math.ceil(35.819821*Math.pow(DUtil.getAscensions(p), 0.26798863))); //seconds
			int radius = (int)(Math.ceil(4.957781*Math.pow(DUtil.getAscensions(p), 0.45901927)));
			int t = (int)(ULTIMATECOOLDOWNMAX - ((ULTIMATECOOLDOWNMAX - ULTIMATECOOLDOWNMIN)*
					((double)DUtil.getAscensions(p)/100)));
			/*
			 * The printed text
			 */
			p.sendMessage("--"+ChatColor.GOLD+"Atlas"+ChatColor.GRAY+"["+devotion+"]");
			p.sendMessage(":Reduce incoming combat damage by "+reduction+".");
			p.sendMessage(":Temporarily increase jump height.");
			p.sendMessage("Duration: "+length+" Jump multiplier: "+jump+ChatColor.GREEN+" /unburden "+ChatColor.YELLOW+"Costs "+SKILLCOST+" Favor.");
			if (((Atlas)DUtil.getDeity(p, "Atlas")).SKILL)
				p.sendMessage(ChatColor.AQUA+"    Skill is active.");
			p.sendMessage(":Atlas shields you and nearby allies from harm.");
			p.sendMessage("50% damage reduction with range "+radius+" for "+duration+" seconds.");
			p.sendMessage(ChatColor.GREEN+" /invincible"+ChatColor.YELLOW+" Costs "+ULTIMATECOST+" Favor. Cooldown time: "+t+" seconds.");
			return;
		}
		p.sendMessage("--"+ChatColor.GOLD+"Atlas");
		p.sendMessage("Passive: Reduce incoming combat damage.");
		p.sendMessage("Active: Release a great weight from your shoulders, increasing jump.");
		p.sendMessage(ChatColor.GREEN+"/unburden"+ChatColor.YELLOW+" Costs "+SKILLCOST+" Favor.");
		p.sendMessage("Ultimate: Atlas shields you and nearby allies from harm.");
		p.sendMessage(ChatColor.GREEN+"/invincible"+ChatColor.YELLOW+" Costs "+ULTIMATECOST+" Favor. Has cooldown.");
		p.sendMessage(ChatColor.YELLOW+"Select item: obsidian");
	}

	@Override
	public void onEvent(Event ee) {
		if (ee instanceof EntityDamageEvent) {
			EntityDamageEvent e = (EntityDamageEvent)ee;
			if (!(e.getEntity() instanceof Player))
				return;
			Player p = (Player)e.getEntity();
			if (!DUtil.isFullParticipant(p))
				return;
			if (DUtil.hasDeity(p, "Atlas")) {
				if (e.getCause() == DamageCause.ENTITY_ATTACK) {
					int reduction = (int)Math.round(Math.pow(DUtil.getDevotion(p, getName()), 0.115));
					if (reduction > e.getDamage()) reduction = e.getDamage();
					e.setDamage(e.getDamage()-reduction);
				}
				else if (e.getCause()== DamageCause.FALL) {
					if (DUtil.getActiveEffectsList(p.getName()).contains("Unburden"))
						e.setDamage(e.getDamage()/3);
				}
			}
		}
	}

	@Override
	public void onCommand(Player P, String str, String[] args, boolean bind) {
		final Player p = P;
		if (!DUtil.isFullParticipant(p))
			return;
		if (!DUtil.hasDeity(p, "Atlas"))
			return;
		if (str.equalsIgnoreCase("unburden")) {
			if (DUtil.getActiveEffects(p.getName()).containsKey("Unburden")) {
				SKILL = false;
				p.sendMessage(ChatColor.YELLOW+"Unburden is already active.");
			} else {
				if (DUtil.getFavor(p) < SKILLCOST) {
					p.sendMessage(ChatColor.YELLOW+"Unburden costs "+SKILLCOST+" Favor.");
					return;
				}
				int devotion = DUtil.getDevotion(p, getName());
				int jump = (int)Math.ceil(0.85*Math.pow(devotion, 0.28));
				int length = (int)Math.ceil(4*Math.pow(devotion, 0.2475));
				p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, length*20, jump));
				DUtil.addActiveEffect(p.getName(), "Unburden", length);
				SKILL = true;
				DUtil.getPlugin().getServer().getScheduler().scheduleAsyncDelayedTask(DUtil.getPlugin(), new Runnable() {
					@Override
					public void run() {
						SKILL = false;
					}
				}, length*20);
				DUtil.setFavor(p, DUtil.getFavor(p)-SKILLCOST);
				p.sendMessage(ChatColor.YELLOW+"Unburden is now active.");
				p.sendMessage(ChatColor.YELLOW+"You will jump higher for "+length+" seconds.");
			}
		} else if (str.equalsIgnoreCase("invincible")) {
			long TIME = ULTIMATETIME;
			if (System.currentTimeMillis() < TIME){
				p.sendMessage(ChatColor.YELLOW+"You cannot use Invincible again for "+((((TIME)/1000)-
						(System.currentTimeMillis()/1000)))/60+" minutes");
				p.sendMessage(ChatColor.YELLOW+"and "+((((TIME)/1000)-(System.currentTimeMillis()/1000))%60)+" seconds.");
				return;
			}
			if (DUtil.getFavor(p)>=ULTIMATECOST) {
				int t = (int)(ULTIMATECOOLDOWNMAX - ((ULTIMATECOOLDOWNMAX - ULTIMATECOOLDOWNMIN)*
						((double)DUtil.getAscensions(p)/100)));
				//
				final int seconds = (int)(Math.ceil(35.819821*Math.pow(DUtil.getAscensions(p), 0.26798863)));
				int INVINCIBLERANGE = (int)(Math.ceil(4.957781*Math.pow(DUtil.getAscensions(p), 0.45901927)));
				for (String s : DUtil.getFullParticipants()) {
					final Player pl = DUtil.getOnlinePlayer(s);
					if ((pl != null) && !pl.isDead() && (pl.getLocation().toVector().isInSphere(p.getLocation().toVector(), INVINCIBLERANGE))) {
						pl.sendMessage(ChatColor.DARK_AQUA+"Atlas"+ChatColor.GRAY+" shields you and your allies from harm.");
						DUtil.addActiveEffect(pl.getName(), "Invincible", seconds);
						DUtil.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(DUtil.getPlugin(), new Runnable() {
							@Override
							public void run() {
								pl.sendMessage(ChatColor.YELLOW+"Invincible will be in effect for "+seconds/2+" more seconds.");
							}
						}, seconds*10);
						DUtil.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(DUtil.getPlugin(), new Runnable() {
							@Override
							public void run() {
								pl.sendMessage(ChatColor.YELLOW+"Invincible is no longer in effect.");
							}
						}, seconds*20);
					}
				}
				//
				DUtil.setFavor(p, DUtil.getFavor(p)-ULTIMATECOST);
				ULTIMATETIME = System.currentTimeMillis()+t*1000;
			} else p.sendMessage(ChatColor.YELLOW+"Invincible requires "+ULTIMATECOST+" Favor.");
			return;
		}
	}
	@Override
	public void onTick(long timeSent) {

	}
}
