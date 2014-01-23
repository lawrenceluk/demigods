package com.WildAmazing.marinating.Demigods.Deities.Gods;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import com.WildAmazing.marinating.Demigods.DUtil;
import com.WildAmazing.marinating.Demigods.Deities.Deity;

public class Poseidon implements Deity {

	/* General */
	private static final long serialVersionUID = 2319323778421842381L;
	private final int REELCOST = 120;
	private final int REELDELAY = 1100;
	private final int drownCOST = 240;
	private final int DROWNDELAY = 15000;
	private final int ULTIMATECOST = 5000;
	private final int ULTIMATECOOLDOWNMAX = 800;
	private final int ULTIMATECOOLDOWNMIN = 220;

	/* Specific to player */
	private String PLAYER;
	public boolean REEL = false;
	private boolean drown = false;
	private long REELTIME, drownTIME, ULTIMATETIME, LASTCHECK;
	private Material drownBIND = null;

	public Poseidon(String name) {
		PLAYER = name;
		REELTIME = System.currentTimeMillis();
		drownTIME = System.currentTimeMillis();
		ULTIMATETIME = System.currentTimeMillis();
		LASTCHECK = System.currentTimeMillis();
	}
	@Override
	public String getName() {
		return "Poseidon";
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
		if (DUtil.hasDeity(p, "Poseidon") && DUtil.isFullParticipant(p)) {
			int devotion = DUtil.getDevotion(p, getName());
			/*
			 * Calculate special values first
			 */
			//heal amount
			int healamt = (int)Math.ceil(0.1*Math.pow(devotion, 0.297));
			//heal interval
			int healinterval = 10-(int)(Math.round(Math.pow(devotion, 0.125))); //seconds
			if (healinterval < 1) healinterval = 1;
			//drown
			int radius = (int)(Math.ceil(1.6955424*Math.pow(devotion, 0.189349)));
			int duration = (int)Math.ceil(2.80488*Math.pow(devotion, 0.2689)); //seconds
			//reel
			int damage = (int)Math.ceil(0.37286*Math.pow(devotion, 0.371238));
			//ult
			int numtargets = (int)Math.round(5*Math.pow(devotion, 0.15));
			int ultduration = (int)Math.round(30*Math.pow(devotion, 0.09));
			int t = (int)(ULTIMATECOOLDOWNMAX - ((ULTIMATECOOLDOWNMAX - ULTIMATECOOLDOWNMIN)*
					((double)DUtil.getAscensions(p)/100)));
			/*
			 * The printed text
			 */
			p.sendMessage("--"+ChatColor.GOLD+"Poseidon"+ChatColor.GRAY+" ["+devotion+"]");
			p.sendMessage(":Heal "+healamt+" every "+healinterval+" seconds while in contact with water.");
			p.sendMessage("Immune to drowning.");
			p.sendMessage(":Deal "+damage+" damage and soak an enemy from a distance. "+ChatColor.GREEN+"/reel");
			p.sendMessage(ChatColor.YELLOW+"Costs "+REELCOST+" Favor. Must have fishing rod in hand.");
			if (((Poseidon)(DUtil.getDeity(p, "Poseidon"))).REEL)
				p.sendMessage(ChatColor.AQUA+"    Reel is active.");
			p.sendMessage(":Create a temporary flood of water. "+ChatColor.GREEN+"/drown");
			p.sendMessage(ChatColor.YELLOW+"Costs "+drownCOST+" Favor.");
			p.sendMessage("Water has radius of "+radius+" for "+duration+" seconds.");
			if (((Poseidon)(DUtil.getDeity(p, "Poseidon"))).drownBIND != null)
				p.sendMessage(ChatColor.AQUA+"    drown bound to "+(((Poseidon)(DUtil.getDeity(p, "Poseidon"))).drownBIND).name());
			else p.sendMessage(ChatColor.AQUA+"    Use /bind to bind this skill to an item.");
			p.sendMessage(":Trap nearby enemies in a cascade of water.");
			p.sendMessage("Max targets: "+numtargets+". Duration: "+ultduration+ChatColor.GREEN+" /waterfall");
			p.sendMessage(ChatColor.YELLOW+"Costs "+ULTIMATECOST+" Favor. Cooldown time: "+t+" seconds.");
			return;
		}
		p.sendMessage("--"+ChatColor.GOLD+"Poseidon");
		p.sendMessage("Passive: Immune to drowning, with increased healing while in water.");
		p.sendMessage("Active: Deal damage and soak an enemy with a fishing rod. "+ChatColor.GREEN+"/reel");
		p.sendMessage(ChatColor.YELLOW+"Costs "+REELCOST+" Favor.");
		p.sendMessage("Active: Create a temporary flood of water.");
		p.sendMessage(ChatColor.GREEN+"/drown "+ChatColor.YELLOW+"Costs "+drownCOST+" Favor. Can bind.");
		p.sendMessage("Ultimate: Trap nearby enemies in a cascade of water.");
		p.sendMessage(ChatColor.GREEN+"/waterfall "+ChatColor.YELLOW+"Costs "+ULTIMATECOST+" Favor. Has cooldown.");
		p.sendMessage(ChatColor.YELLOW+"Select item: water bucket");
	}

	@Override
	public void onEvent(Event ee) {
		if (ee instanceof EntityDamageEvent) {
			EntityDamageEvent e = (EntityDamageEvent)ee;
			if (e.getEntity() instanceof Player) {
				if (e.getCause() == DamageCause.DROWNING) {
					Player p = (Player)e.getEntity();
					if (!DUtil.isFullParticipant(p))
						return;
					if (!DUtil.hasDeity(p, "Poseidon"))
						return;
					e.setDamage(0);
					e.setCancelled(true);
				}
			}
		} else if (ee instanceof PlayerInteractEvent) {
			PlayerInteractEvent e = (PlayerInteractEvent)ee;
			Player p = e.getPlayer();
			if (!DUtil.isFullParticipant(p))
				return;
			if (!DUtil.hasDeity(p, "Poseidon"))
				return;
			if (REEL) {
				if (p.getItemInHand().getType() == Material.FISHING_ROD) {
					if (REELTIME > System.currentTimeMillis())
						return;
					if (DUtil.getFavor(p) > REELCOST) {
						if (reel(p)) {
							DUtil.setFavor(p, DUtil.getFavor(p)-REELCOST);
							REELTIME = System.currentTimeMillis()+REELDELAY;
						}
					} else {
						p.sendMessage(ChatColor.YELLOW+"You do not have enough Favor.");
						REEL = false;
					}
				}
			}
			if ((p.getItemInHand().getType() == drownBIND) || drown) {
				if (drownTIME > System.currentTimeMillis()) {
					p.sendMessage(ChatColor.YELLOW+"You may not use this skill yet.");
					return;
				}
				if (DUtil.getFavor(p) > drownCOST) {
					drown(p);
					DUtil.setFavor(p, DUtil.getFavor(p)-drownCOST);
					drownTIME = System.currentTimeMillis()+DROWNDELAY;
				} else {
					p.sendMessage(ChatColor.YELLOW+"You do not have enough Favor.");
					drown = false;
				}
			}
		}
	}

	@Override
	public void onCommand(Player P, String str, String[] args, boolean bind) {
		final Player p = P;
		if (!DUtil.isFullParticipant(p))
			return;
		if (!DUtil.hasDeity(p, "Poseidon"))
			return;
		if (str.equalsIgnoreCase("reel")) {
			if (REEL) {
				REEL = false;
				p.sendMessage(ChatColor.YELLOW+"Reel is no longer active.");
			} else {
				REEL = true;
				p.sendMessage(ChatColor.YELLOW+"Reel is now active.");
				p.sendMessage(ChatColor.YELLOW+"It can only be used with a fishing rods.");
			}
		} else if (str.equalsIgnoreCase("drown")) {
			if (bind) {
				if (drownBIND == null) {
					if (DUtil.isBound(p, p.getItemInHand().getType()))
						p.sendMessage(ChatColor.YELLOW+"That item is already bound to a skill.");
					if (p.getItemInHand().getType() == Material.AIR)
						p.sendMessage(ChatColor.YELLOW+"You cannot bind a skill to air.");
					else {
						DUtil.registerBind(p, p.getItemInHand().getType());
						drownBIND = p.getItemInHand().getType();
						p.sendMessage(ChatColor.YELLOW+"Drown is now bound to "+p.getItemInHand().getType().name()+".");
					}
				} else {
					DUtil.removeBind(p, drownBIND);
					p.sendMessage(ChatColor.YELLOW+"Drown is no longer bound to "+drownBIND.name()+".");
					drownBIND = null;
				}
				return;
			}
			if (drown) {
				drown = false;
				p.sendMessage(ChatColor.YELLOW+"Drown is no longer active.");
			} else {
				drown = true;
				p.sendMessage(ChatColor.YELLOW+"Drown is now active.");
			}
		} else if (str.equalsIgnoreCase("waterfall")) {
			long TIME = ULTIMATETIME;
			if (System.currentTimeMillis() < TIME){
				p.sendMessage(ChatColor.YELLOW+"You cannot use Waterfall again for "+((((TIME)/1000)-
						(System.currentTimeMillis()/1000)))/60+" minutes");
				p.sendMessage(ChatColor.YELLOW+"and "+((((TIME)/1000)-(System.currentTimeMillis()/1000))%60)+" seconds.");
				return;
			}
			if (DUtil.getFavor(p)>=ULTIMATECOST) {
				for (String s : DUtil.getFullParticipants()) {
					if (DUtil.isFullParticipant(s) && DUtil.getActiveEffectsList(s).contains("Waterfall")) {
						p.sendMessage(ChatColor.YELLOW+"Another player's Waterfall is already in effect.");
						return;
					}
				}
				int t = (int)(ULTIMATECOOLDOWNMAX - ((ULTIMATECOOLDOWNMAX - ULTIMATECOOLDOWNMIN)*
						((double)DUtil.getAscensions(p)/100)));
				int num = waterfall(p);
				if (num > 0) {
					p.sendMessage("In exchange for "+ChatColor.AQUA+ULTIMATECOST+ChatColor.WHITE+" Favor, ");
					p.sendMessage(ChatColor.GOLD+"Poseidon"+ChatColor.WHITE+" has drowned "+num+" targets.");
					DUtil.setFavor(p, DUtil.getFavor(p)-ULTIMATECOST);
					ULTIMATETIME = System.currentTimeMillis()+t*1000;
					DUtil.addActiveEffect(p.getName(), "Waterfall", (int)Math.round(30*Math.pow(DUtil.getDevotion(p, getName()), 0.09)));
				} else p.sendMessage(ChatColor.YELLOW+"There are no targets nearby.");
			} else p.sendMessage(ChatColor.YELLOW+"Waterfall requires "+ULTIMATECOST+" Favor.");
			return;
		}
	}
	@Override
	public void onTick(long timeSent) {
		int healinterval = 10-(int)(Math.round(Math.pow(DUtil.getDevotion(getPlayerName(), getName()), 0.125))); //seconds
		if (healinterval < 1) healinterval = 1;
		if (timeSent > LASTCHECK+(healinterval*1000)) {
			LASTCHECK = timeSent;
			Player p = DUtil.getOnlinePlayer(getPlayerName());
			if ((p != null) && p.isOnline()) {
				if ((p.getLocation().getBlock().getType() == Material.WATER) || (p.getEyeLocation().getBlock().getType() == Material.WATER)) {
					int healamt = (int)Math.ceil(0.1*Math.pow(DUtil.getDevotion(getPlayerName(), getName()), 0.297));
					if (DUtil.getHP(getPlayerName())+healamt > DUtil.getMaxHP(getPlayerName()))
						healamt = DUtil.getMaxHP(getPlayerName())-DUtil.getHP(getPlayerName());
					DUtil.setHP(getPlayerName(), DUtil.getHP(getPlayerName())+healamt);
				}
			}
		}
	}
	private boolean reel(Player p) {
		LivingEntity le = DUtil.getTargetLivingEntity(p, 3);
		if ((le == null) || le.isDead())
			return false;
		if (le.getLocation().getBlock().getType() == Material.AIR) {
			le.getLocation().getBlock().setType(Material.WATER);
			le.getLocation().getBlock().setData((byte)0x8);
		}
		int damage = (int)Math.ceil(0.37286*Math.pow(DUtil.getDevotion(p, getName()), 0.371238));
		if (le instanceof Player) {
			if (DUtil.isFullParticipant((Player)le))
				if (DUtil.isGod((Player)le))
					return false;
		}
		DUtil.damageDemigods(p, le, damage);
		REELTIME = System.currentTimeMillis();
		return true;
	}
	private void drown(Player p) {
		//special values
		int devotion = DUtil.getDevotion(p, getName());
		int radius = (int)(Math.ceil(1.6955424*Math.pow(devotion, 0.189349)));
		int duration = (int)Math.ceil(2.80488*Math.pow(devotion, 0.2689)); //seconds
		//
		Location target = DUtil.getTargetLocation(p);
		if (target == null) return;
		if (!DUtil.isPVP(target)) return;
		drown(target, radius, duration*20);
	}
	private void drown(Location target, int radius, int duration) {
		final ArrayList<Block> toreset = new ArrayList<Block>();
		for (int x=-radius; x<=radius;x++) {
			for (int y=-radius; y<=radius;y++) {
				for (int z=-radius; z<=radius;z++) {
					Block block = target.getWorld().getBlockAt(target.getBlockX()+x, target.getBlockY()+y, target.getBlockZ()+z);
					if (block.getLocation().distance(target) <= radius) {
						if ((block.getType() == Material.AIR) || (block.getType() == Material.WATER) || (block.getType() == Material.LAVA)) {
							block.setType(Material.WATER);
							block.setData((byte)(0x8));
							toreset.add(block);
						}
					}
				}
			}
		}
		DUtil.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(DUtil.getPlugin(), new Runnable() {
			@Override
			public void run() {
				for (Block b : toreset)
					if ((b.getType() == Material.WATER) || (b.getType() == Material.STATIONARY_WATER))
						b.setType(Material.AIR);
			}
		}, duration);
	}
	private int waterfall(Player p) {
		int numtargets = (int)Math.round(15*Math.pow(DUtil.getDevotion(p, getName()), 0.15));
		final int ultduration = (int)Math.round(30*Math.pow(DUtil.getDevotion(p, getName()), 0.09)*20);
		ArrayList<LivingEntity> entitylist = new ArrayList<LivingEntity>();
		Vector ploc = p.getLocation().toVector();
		for (LivingEntity anEntity : p.getWorld().getLivingEntities()){
			if (anEntity instanceof Player)
				if (DUtil.isFullParticipant((Player)anEntity))
					if (DUtil.isGod((Player)anEntity))
						continue;
			if (!DUtil.isPVP(anEntity.getLocation()))
				continue;
			if (anEntity.getLocation().toVector().isInSphere(ploc, 50.0) && (entitylist.size() < numtargets))
				entitylist.add(anEntity);
		}
		for (LivingEntity le : entitylist) {
			final LivingEntity fl = le;
			for (int i=0;i<ultduration;i+=9) {
				final int ii = i;
				DUtil.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(DUtil.getPlugin(), new Runnable() {
					@Override
					public void run() {
						if ((fl != null) && !fl.isDead()) {
							drown(fl.getLocation(), 3, ultduration - ii);
						}
					}
				}, i);
			}
		}
		return entitylist.size();
	}
}
