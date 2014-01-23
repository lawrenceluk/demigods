package com.WildAmazing.marinating.Demigods.Deities.Gods;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;

import com.WildAmazing.marinating.Demigods.DUtil;
import com.WildAmazing.marinating.Demigods.Deities.Deity;

public class Athena implements Deity {
	private static final long serialVersionUID = -9039521341663053625L;
	private String PLAYER;

	private static final int SKILLCOST = 100;
	private static final int SKILLDELAY = 3600; //milliseconds
	private static final int ULTIMATECOST = 4000;
	private static final int ULTIMATECOOLDOWNMAX = 500; //seconds
	private static final int ULTIMATECOOLDOWNMIN = 300;


	private boolean SKILL = false;
	public Material SKILLBIND = null;
	private long SKILLTIME;
	private long ULTIMATETIME;

	public Athena(String player) {
		PLAYER = player;
	}
	@Override
	public String getName() {
		return "Athena";
	}

	@Override
	public String getPlayerName() {
		return PLAYER;
	}

	@Override
	public String getDefaultAlliance() {
		return "God";
	}

	@Override
	public void printInfo(Player p) {
		if (DUtil.isFullParticipant(p) && DUtil.hasDeity(p, getName())) {
			int devotion = DUtil.getDevotion(p, getName());
			//flash range
			int range = (int)Math.ceil(3*Math.pow(devotion, 0.2));
			//ceasefire range
			int crange = (int)Math.floor(15*Math.pow(devotion, 0.275));
			//ceasefire duration
			int duration = (int)Math.ceil(10*Math.pow(devotion, 0.194));
			int t = (int)(ULTIMATECOOLDOWNMAX - ((ULTIMATECOOLDOWNMAX - ULTIMATECOOLDOWNMIN)*
					((double)DUtil.getAscensions(p)/100)));
			//print
			p.sendMessage("--"+ChatColor.GOLD+getName()+ChatColor.GRAY+"["+devotion+"]");
			p.sendMessage(":Use "+ChatColor.YELLOW+"qd <name>"+ChatColor.WHITE+" for detailed information about any player");
			p.sendMessage("you are looking at.");
			p.sendMessage(":Left-click to teleport with range "+range+"."+ChatColor.GREEN+" /flash "+ChatColor.YELLOW+"Costs "+SKILLCOST+" Favor.");
			if (((Athena)DUtil.getDeity(p, getName())).SKILLBIND != null)
				p.sendMessage(ChatColor.AQUA+"    Bound to "+((Athena)DUtil.getDeity(p, getName())).SKILLBIND.name());
			else p.sendMessage(ChatColor.AQUA+"    Use /bind to bind this skill to an item.");
			p.sendMessage(":Athena silences the battlefield, preventing all damage in range "+crange);
			p.sendMessage("dealt by Gods and Titans alike for "+duration+" seconds."+ChatColor.GREEN+" /ceasefire");
			p.sendMessage(ChatColor.YELLOW+"Costs "+ULTIMATECOST+" Favor. Cooldown time: "+t+" seconds.");
			return;
		}
		p.sendMessage("--"+ChatColor.GOLD+getName());
		p.sendMessage("Passive: "+ChatColor.YELLOW+"qd"+ChatColor.WHITE+" gives more detail on targets.");
		p.sendMessage("Active: Left-click to teleport forward a set distance."+ChatColor.GREEN+"/flash");
		p.sendMessage(ChatColor.YELLOW+"Costs "+SKILLCOST+" Favor. Can bind.");
		p.sendMessage("Ultimate: Athena silences the battlefield, preventing all");
		p.sendMessage("damage nearby for a short duration. "+ChatColor.GREEN+"/ceasefire");
		p.sendMessage(ChatColor.YELLOW+"Costs "+ULTIMATECOST+" Favor. Has cooldown.");
		p.sendMessage(ChatColor.YELLOW+"Select item: book");
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
					float pitch = p.getLocation().getPitch();
					float yaw = p.getLocation().getYaw();
					int range = (int)Math.ceil(3*Math.pow(DUtil.getDevotion(p, getName()), 0.2));
					List<Block> los = p.getLineOfSight(null, 100);
					Location go = null;
					if (los.size()-1 < range)
						go = los.get(los.size()-1).getLocation();
					else go = los.get(range).getLocation();
					if (go == null)
						return;
					go.setY(go.getBlockY()+1);
					go.setPitch(pitch);
					go.setYaw(yaw);
					if (go.getBlock().isLiquid() || go.getBlock().isEmpty()) {
						p.teleport(go);
						DUtil.setFavor(p, DUtil.getFavor(p) - SKILLCOST);
					}
					return;
				} else {
					p.sendMessage(ChatColor.YELLOW+"You do not have enough Favor.");
					SKILL = false;
				}
			}
		} else if (ee instanceof EntityDamageEvent) {
			EntityDamageEvent e = (EntityDamageEvent)ee;
			if (e instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent e1 = (EntityDamageByEntityEvent)e;
				if (e1.getDamager() instanceof Player) {
					Player p = (Player)e1.getDamager();
					if (DUtil.isFullParticipant(p)) {
						if (DUtil.getActiveEffectsList(p.getName()).contains("Ceasefire")) {
							e.setDamage(0);
							e.setCancelled(true);
						}
					}
				}
				return;
			}
			if ((e.getCause() == DamageCause.DROWNING) || (e.getCause() == DamageCause.LAVA) || (e.getCause() == DamageCause.FIRE_TICK))
				return;
			if (e.getEntity() instanceof Player) {
				Player p = (Player)e.getEntity();
				if (DUtil.isFullParticipant(p)) {
					if (DUtil.getActiveEffectsList(p.getName()).contains("Ceasefire")) {
						e.setDamage(0);
						e.setCancelled(true);
					}
				}
			}
		}
	}

	@Override
	public void onCommand(Player P, String str, String[] args, boolean bind) {
		final Player p = P;
		if (DUtil.hasDeity(p, getName())) {
			if (str.equalsIgnoreCase("flash")) {
				if (bind) {
					if (SKILLBIND == null) {
						if (DUtil.isBound(p, p.getItemInHand().getType()))
							p.sendMessage(ChatColor.YELLOW+"That item is already bound to a skill.");
						if (p.getItemInHand().getType() == Material.AIR)
							p.sendMessage(ChatColor.YELLOW+"You cannot bind a skill to air.");
						else {
							DUtil.registerBind(p, p.getItemInHand().getType());
							SKILLBIND = p.getItemInHand().getType();
							p.sendMessage(ChatColor.YELLOW+"Flash is now bound to "+p.getItemInHand().getType().name()+".");
						}
					} else {
						DUtil.removeBind(p, SKILLBIND);
						p.sendMessage(ChatColor.YELLOW+"Flash is no longer bound to "+SKILLBIND.name()+".");
						SKILLBIND = null;
					}
					return;
				}
				if (SKILL) {
					SKILL = false;
					p.sendMessage(ChatColor.YELLOW+"Skill is no longer active.");
				} else {
					SKILL = true;
					p.sendMessage(ChatColor.YELLOW+"Skill is now active.");
				}
			} else if (str.equalsIgnoreCase("ceasefire")) {
				long TIME = ULTIMATETIME;
				if (System.currentTimeMillis() < TIME){
					p.sendMessage(ChatColor.YELLOW+"You cannot use ceasefire again for "+((((TIME)/1000)-
							(System.currentTimeMillis()/1000)))/60+" minutes");
					p.sendMessage(ChatColor.YELLOW+"and "+((((TIME)/1000)-(System.currentTimeMillis()/1000))%60)+" seconds.");
					return;
				}
				if (DUtil.getFavor(p)>=ULTIMATECOST) {
					int devotion = DUtil.getDevotion(p, getName());
					int crange = (int)Math.floor(15*Math.pow(devotion, 0.275));
					int duration = (int)Math.ceil(10*Math.pow(devotion, 0.194));
					int t = (int)(ULTIMATECOOLDOWNMAX - ((ULTIMATECOOLDOWNMAX - ULTIMATECOOLDOWNMIN)*
							((double)DUtil.getAscensions(p)/100)));
					ULTIMATETIME = System.currentTimeMillis()+(t*1000);
					p.sendMessage("In exchange for "+ChatColor.AQUA+ULTIMATECOST+ChatColor.WHITE+" Favor, ");
					for (Player pl : P.getWorld().getPlayers()) {
						if (pl.getLocation().distance(P.getLocation()) <= crange) {
							if (DUtil.isFullParticipant(pl)) {
								pl.sendMessage(ChatColor.GOLD+"Athena"+ChatColor.WHITE+" has mandated a ceasefire for "+duration+" seconds.");
								DUtil.addActiveEffect(pl.getName(), "Ceasefire", duration);
							}
						}
					}
					DUtil.setFavor(p, DUtil.getFavor(p)-ULTIMATECOST);
				} else p.sendMessage(ChatColor.YELLOW+"Ceasefire requires "+ULTIMATECOST+" Favor.");
				return;
			}
		}
	}
	@Override
	public void onTick(long timeSent) {

	}
}