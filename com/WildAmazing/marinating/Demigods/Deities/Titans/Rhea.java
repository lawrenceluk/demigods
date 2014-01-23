package com.WildAmazing.marinating.Demigods.Deities.Titans;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.WildAmazing.marinating.Demigods.DUtil;
import com.WildAmazing.marinating.Demigods.WriteLocation;
import com.WildAmazing.marinating.Demigods.Deities.Deity;


public class Rhea implements Deity {
	/* Generalized things */
	private static final long serialVersionUID = 4917938727569988533L;
	private final int POISONCOST = 50;
	private final int PLANTCOST = 100;
	private final int RHEAULTIMATECOST = 6500;
	private final int RHEAULTIMATECOOLDOWNMAX = 500;
	private final int RHEAULTIMATECOOLDOWNMIN = 80;
	private final int POISONDELAY = 1500; //milliseconds
	private final int PLANTDELAY = 2000;

	/* Specific to owner */
	private String PLAYER;
	private ArrayList<WriteLocation> TREES;
	private boolean PLANT = false;
	private boolean POISON = false;
	private long PLANTTIME, POISONTIME, RHEAULTIMATETIME;
	private Material PLANTBIND = null;
	private Material DETONATEBIND = null;
	private Material POISONBIND = null;

	public Rhea(String name) {
		PLAYER = name;
		TREES = new ArrayList<WriteLocation>();
		PLANTTIME = System.currentTimeMillis();
		POISONTIME = System.currentTimeMillis();
		RHEAULTIMATETIME = System.currentTimeMillis();
	}

	@Override
	public String getName() {
		return "Rhea";
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
		if (DUtil.hasDeity(p, "Rhea") && DUtil.isFullParticipant(p)) {
			int devotion = DUtil.getDevotion(p, getName());
			/*
			 * Calculate special values first
			 */
			//poison
			int duration = (int)Math.ceil(2.4063*Math.pow(devotion, 0.1921)); //seconds
			if (duration < 1) duration = 1;
			int strength = (int)Math.ceil(4*Math.pow(devotion, 0.09));
			//explosion
			float explosionsize = (float)(Math.ceil(3*Math.pow(devotion, 0.09)));
			//ultimate
			int range = (int)(10.84198*Math.pow(1.01926, DUtil.getAscensions(p)));
			int ultimateduration = (int)(4.95778*Math.pow(DUtil.getAscensions(p), 0.459019));
			int t = (int)(RHEAULTIMATECOOLDOWNMAX - ((RHEAULTIMATECOOLDOWNMAX - RHEAULTIMATECOOLDOWNMIN)*
					((double)DUtil.getAscensions(p)/100)));
			/*
			 * The printed text
			 */
			p.sendMessage("--"+ChatColor.GOLD+"Rhea"+ChatColor.GRAY+"["+devotion+"]");
			p.sendMessage(":Right click for a bonemeal effect.");
			p.sendMessage(":Poison a target player. "+ChatColor.GREEN+"/poison");
			p.sendMessage(ChatColor.YELLOW+"Costs "+POISONCOST+" Favor.");
			p.sendMessage("Poison power: "+strength+" for "+duration+" seconds.");
			if (((Rhea)(DUtil.getDeity(p, "Rhea"))).POISONBIND != null)
				p.sendMessage(ChatColor.AQUA+"    Bound to "+(((Rhea)(DUtil.getDeity(p, "Rhea"))).POISONBIND).name());
			else p.sendMessage(ChatColor.AQUA+"    Use /bind to bind this skill to an item.");
			p.sendMessage(":Plant and detonate exploding trees. "+ChatColor.GREEN+"/plant, /detonate");
			p.sendMessage(ChatColor.YELLOW+"Costs "+PLANTCOST+" Favor.");
			p.sendMessage("Explosion radius: "+explosionsize+". Maximum trees: "+(DUtil.getAscensions(p)+1));
			if (((Rhea)(DUtil.getDeity(p, "Rhea"))).PLANTBIND != null)
				p.sendMessage(ChatColor.AQUA+"    Plant bound to "+(((Rhea)(DUtil.getDeity(p, "Rhea"))).PLANTBIND).name());
			else p.sendMessage(ChatColor.AQUA+"    Use /bind to bind plant to an item.");
			if (((Rhea)(DUtil.getDeity(p, "Rhea"))).DETONATEBIND != null)
				p.sendMessage(ChatColor.AQUA+"    Detonate bound to "+(((Rhea)(DUtil.getDeity(p, "Rhea"))).DETONATEBIND).name());
			else p.sendMessage(ChatColor.AQUA+"    Use /bind to bind detonate to an item.");
			p.sendMessage(":Rhea entangles nearby enemies, damaging them if they move.");
			p.sendMessage("Range: "+range+" for "+ultimateduration+" seconds. "+ChatColor.GREEN+"/entangle");
			p.sendMessage(ChatColor.YELLOW+"Costs "+RHEAULTIMATECOST+" Favor. Cooldown time: "+t+" seconds.");
			return;
		}
		p.sendMessage("--"+ChatColor.GOLD+"Rhea");
		p.sendMessage("Passive: Right click for a bonemeal effect.");
		p.sendMessage("Active: Poison a target player. "+ChatColor.GREEN+"/poison");
		p.sendMessage(ChatColor.YELLOW+"Costs "+POISONCOST+" Favor. Can bind.");
		p.sendMessage("Active: Plant and detonate explosive trees.");
		p.sendMessage(ChatColor.GREEN+"/plant "+ChatColor.YELLOW+"Costs "+PLANTCOST+" Favor. Can bind.");
		p.sendMessage(ChatColor.GREEN+"/detonate "+ChatColor.YELLOW+"Can bind.");
		p.sendMessage("Ultimate: Rhea entangles nearby enemies, damaging them if they move.");
		p.sendMessage(ChatColor.GREEN+"/entangle "+ChatColor.YELLOW+"Costs "+RHEAULTIMATECOST+" Favor. Has cooldown.");
		p.sendMessage(ChatColor.YELLOW+"Select item: vines");
	}

	@Override
	public void onEvent(Event ee) {
		if (ee instanceof PlayerInteractEvent) {
			PlayerInteractEvent e = (PlayerInteractEvent)ee;
			Player p = e.getPlayer();
			if (!DUtil.isFullParticipant(p))
				return;
			if (!DUtil.hasDeity(p, "Rhea"))
				return;
			if (POISON || ((POISONBIND != null) && (p.getItemInHand().getType() == POISONBIND))) {
				if (POISONTIME > System.currentTimeMillis())
					return;
				if (DUtil.getFavor(p) >= POISONCOST) {
					if (poison(p)) {
						DUtil.setFavor(p, DUtil.getFavor(p) - POISONCOST);
						POISONTIME = System.currentTimeMillis()+POISONDELAY;
					}
				} else {
					POISON = false;
					p.sendMessage(ChatColor.YELLOW+"You don't have enough Favor to do that.");
				}
			}
			if (PLANT || ((PLANTBIND != null) && (p.getItemInHand().getType() == PLANTBIND))) {
				if (PLANTTIME > System.currentTimeMillis())
					return;
				if (DUtil.getFavor(p) >= PLANTCOST) {
					if (plant(p)) {
						DUtil.setFavor(p, DUtil.getFavor(p) - PLANTCOST);
						PLANTTIME = System.currentTimeMillis()+PLANTDELAY;
					}
				} else {
					PLANT = false;
					p.sendMessage(ChatColor.YELLOW+"You don't have enough Favor to do that.");
				}
			}
			if ((DETONATEBIND != null) && (p.getItemInHand().getType() == DETONATEBIND)) {
				detonate(p);
			}
			if (e.getClickedBlock() == null)
				return;
			Block b = e.getClickedBlock();
			if (b.getType()==Material.SAPLING){
				if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
					switch (b.getData()){
					case 0: b.setData((byte)1); break;
					case 1: b.setData((byte)2); break;
					case 2: b.setData((byte)3); break;
					case 3: b.setData((byte)0); break;
					default: b.setData((byte)0);
					}
				} else if (e.getAction() == Action.LEFT_CLICK_BLOCK){
					byte Y = b.getData();
					b.setType(Material.AIR);
					switch (Y){
					case 0: p.getWorld().generateTree(b.getLocation(), TreeType.TREE); break;
					case 1: p.getWorld().generateTree(b.getLocation(), TreeType.REDWOOD); break;
					case 2: p.getWorld().generateTree(b.getLocation(), TreeType.BIRCH); break;
					case 3: p.getWorld().generateTree(b.getLocation(), TreeType.JUNGLE); break;
					default: p.getWorld().generateTree(b.getLocation(), TreeType.TREE);
					}
					e.setCancelled(true);
				}
			} else if ((b.getType() == Material.CROPS) || (b.getType() == Material.PUMPKIN_STEM) || (b.getType() == Material.MELON_STEM)){
				if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
					b.setData((byte)0x7);
			} else if (b.getType() == Material.GRASS){
				if ((e.getAction() == Action.RIGHT_CLICK_BLOCK)&&(e.getPlayer().getItemInHand().getType()==Material.AIR))
					grow(b.getRelative(BlockFace.UP),3);
			} else if (b.getType() == Material.DIRT){
				if ((e.getAction() == Action.RIGHT_CLICK_BLOCK)&&(e.getPlayer().getItemInHand().getType()==Material.AIR))
					b.setType(Material.GRASS);
			}
		}
	}

	@Override
	public void onCommand(Player P, String str, String[] args, boolean bind) {
		final Player p = P;
		if (!DUtil.isFullParticipant(p))
			return;
		if (!DUtil.hasDeity(p, "Rhea"))
			return;
		if (str.equalsIgnoreCase("poison")) {
			if (bind) {
				if (POISONBIND == null) {
					if (DUtil.isBound(p, p.getItemInHand().getType()))
						p.sendMessage(ChatColor.YELLOW+"That item is already bound to a skill.");
					if (p.getItemInHand().getType() == Material.AIR)
						p.sendMessage(ChatColor.YELLOW+"You cannot bind a skill to air.");
					else {
						DUtil.registerBind(p, p.getItemInHand().getType());
						POISONBIND = p.getItemInHand().getType();
						p.sendMessage(ChatColor.YELLOW+"Poison is now bound to "+p.getItemInHand().getType().name()+".");
					}
				} else {
					DUtil.removeBind(p, POISONBIND);
					p.sendMessage(ChatColor.YELLOW+"Poison is no longer bound to "+POISONBIND.name()+".");
					POISONBIND = null;
				}
				return;
			}
			if (POISON) {
				POISON = false;
				p.sendMessage(ChatColor.YELLOW+"Poison is no longer active.");
			} else {
				POISON = true;
				p.sendMessage(ChatColor.YELLOW+"Poison is now active.");
			}
		} else if (str.equalsIgnoreCase("plant")) {
			if (bind) {
				if (PLANTBIND == null) {
					if (DUtil.isBound(p, p.getItemInHand().getType()))
						p.sendMessage(ChatColor.YELLOW+"That item is already bound to a skill.");
					if (p.getItemInHand().getType() == Material.AIR)
						p.sendMessage(ChatColor.YELLOW+"You cannot bind a skill to air.");
					else {
						DUtil.registerBind(p, p.getItemInHand().getType());
						PLANTBIND = p.getItemInHand().getType();
						p.sendMessage(ChatColor.YELLOW+"Plant is now bound to "+p.getItemInHand().getType().name()+".");
					}
				} else {
					DUtil.removeBind(p, PLANTBIND);
					p.sendMessage(ChatColor.YELLOW+"Plant is no longer bound to "+PLANTBIND.name()+".");
					PLANTBIND = null;
				}
				return;
			}
			if (PLANT) {
				PLANT = false;
				p.sendMessage(ChatColor.YELLOW+"Plant is no longer active.");
			} else {
				PLANT = true;
				p.sendMessage(ChatColor.YELLOW+"Plant is now active.");
			}

		} else if (str.equalsIgnoreCase("detonate")) {
			if (bind) {
				if (DETONATEBIND == null) {
					if (DUtil.isBound(p, p.getItemInHand().getType()))
						p.sendMessage(ChatColor.YELLOW+"That item is already bound to a skill.");
					if (p.getItemInHand().getType() == Material.AIR)
						p.sendMessage(ChatColor.YELLOW+"You cannot bind a skill to air.");
					else {
						DUtil.registerBind(p, p.getItemInHand().getType());
						DETONATEBIND = p.getItemInHand().getType();
						p.sendMessage(ChatColor.YELLOW+"Detonate is now bound to "+p.getItemInHand().getType().name()+".");
					}
				} else {
					DUtil.removeBind(p, DETONATEBIND);
					p.sendMessage(ChatColor.YELLOW+"Detonate is no longer bound to "+DETONATEBIND.name()+".");
					DETONATEBIND = null;
				}
				return;
			}
			detonate(p);
		} else if (str.equalsIgnoreCase("entangle")) {
			if (System.currentTimeMillis() < RHEAULTIMATETIME){
				p.sendMessage(ChatColor.YELLOW+"You cannot use entangle again for "+((((RHEAULTIMATETIME)/1000)-
						(System.currentTimeMillis()/1000)))/60+" minutes");
				p.sendMessage(ChatColor.YELLOW+"and "+((((RHEAULTIMATETIME)/1000)-(System.currentTimeMillis()/1000))%60)+" seconds.");
				return;
			}
			if (DUtil.getFavor(p)>=RHEAULTIMATECOST) {
				int t = (int)(RHEAULTIMATECOOLDOWNMAX - ((RHEAULTIMATECOOLDOWNMAX - RHEAULTIMATECOOLDOWNMIN)*
						((double)DUtil.getAscensions(p)/100)));
				int hit = entangle(p);
				if (hit > 0) {
					p.sendMessage(ChatColor.YELLOW+"Rhea has entangled "+hit+" enemies.");
					DUtil.setFavor(p, DUtil.getFavor(p)-RHEAULTIMATECOST);
					RHEAULTIMATETIME = System.currentTimeMillis()+(t*1000);
				} else p.sendMessage(ChatColor.YELLOW+"No targets found.");
			} else p.sendMessage(ChatColor.YELLOW+"Entangle requires "+RHEAULTIMATECOST+" Favor.");
			return;
		}
	}
	@Override
	public void onTick(long timeSent) {

	}
	private boolean poison(Player p) {
		int devotion = DUtil.getDevotion(p, getName());
		int duration = (int)Math.ceil(2.4063*Math.pow(devotion, 0.1921)); //seconds
		if (duration < 1) duration = 1;
		int strength = (int)Math.ceil(4*Math.pow(devotion, 0.09));
		Player target = null;
		Block b = p.getTargetBlock(null, 200);
		for (Player pl : b.getWorld().getPlayers()) {
			if (pl.getLocation().distance(b.getLocation()) < 4) {
				if (!DUtil.isTitan(pl)){
					target = pl;
					break;
				}
			}
		}
		if (target != null) {
			target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration*20, strength));
			DUtil.addActiveEffect(target.getName(), "Poison", duration);
			/*		for (int i=0;i<duration*20;i+=20) {
				DUtil.getPlugin().getServer().getScheduler().scheduleAsyncDelayedTask(DUtil.getPlugin(), new Runnable() {
					@Override
					public void run() {
						if (pt.getHealth() > 9)
							pt.setHealth((int)Math.ceil(pt.getHealth()*0.85));
					}
				}, duration*20);
			} */
			p.sendMessage(ChatColor.YELLOW+target.getName()+" has been poisoned for "+duration+" seconds.");
			target.sendMessage(ChatColor.RED+"You have been poisoned for "+duration+" seconds.");
			return true;
		} else {
			p.sendMessage(ChatColor.YELLOW+"No target found.");
			return false;
		}
	}
	private boolean plant(Player player){
		Block b = player.getTargetBlock(null, 200);
		if (b!=null) {
			if (!DUtil.isPVP(b.getLocation())) {
				player.sendMessage(ChatColor.YELLOW+"That is a protected area.");
				return false;
			}
			if (TREES.size() == (DUtil.getAscensions(player)+1)) {
				player.sendMessage(ChatColor.YELLOW+"You have reached your maximum of "+(TREES.size())+" trees.");
				return false;
			}
			if (player.getWorld().generateTree(b.getRelative(BlockFace.UP).getLocation(),TreeType.TREE)){
				player.sendMessage(ChatColor.YELLOW+"Use /detonate to create an explosion at this tree.");
				TREES.add(DUtil.toWriteLocation(b.getRelative(BlockFace.UP).getLocation()));
				return true;
			} else player.sendMessage(ChatColor.YELLOW+"A tree cannot be placed there.");
		} else player.sendMessage(ChatColor.YELLOW+"That is a protected zone or an invalid location.");
		return false;
	}
	private void detonate(Player player){
		float explosionsize = (float)(Math.ceil(3*Math.pow(DUtil.getDevotion(player.getName(), getName()), 0.09)));
		if (TREES.size()>0) {
			for (WriteLocation w : TREES){
				Location l = DUtil.toLocation(w);
				if (l.getBlock().getType()==Material.LOG) {
					removelogs(l);
					l.getWorld().createExplosion(l, explosionsize);
				}
			}
			player.sendMessage(ChatColor.YELLOW+"Successfully detonated "+TREES.size()+" tree(s).");
			TREES.clear();
		} else
			player.sendMessage(ChatColor.YELLOW+"You have not placed an exploding tree.");
	}
	private void removelogs(Location l) {
		if (l.getBlock().getType() == Material.LOG) {
			l.getBlock().setType(Material.AIR);
			removelogs(l.getBlock().getRelative(BlockFace.UP).getLocation());
		}
	}
	private void grow(Block b, int run){
		if (((b.getType()==Material.AIR) && (b.getRelative(BlockFace.DOWN).getType()==Material.GRASS)) && (run > 0)) {
			switch ((int)(Math.random()*50)){
			case 0: case 1: case 2: b.setType(Material.RED_ROSE); break;
			case 3: case 6: case 10: b.setType(Material.YELLOW_FLOWER); break;
			case 8: b.setType(Material.PUMPKIN); break;
			case 12: case 13: case 14: b.setType(Material.LONG_GRASS); break;
			case 15: case 16: case 17: case 18: case 19: case 20: case 21: case 22:
			case 23: case 24: case 25: b.setType(Material.LONG_GRASS); b.setData((byte)(0x1)); break;
			}
			grow(b.getRelative(BlockFace.NORTH),run-1);
			grow(b.getRelative(BlockFace.EAST),run-1);
			grow(b.getRelative(BlockFace.WEST),run-1);
			grow(b.getRelative(BlockFace.SOUTH),run-1);
			grow(b.getRelative(BlockFace.NORTH_EAST),run-1);
			grow(b.getRelative(BlockFace.NORTH_WEST),run-1);
			grow(b.getRelative(BlockFace.SOUTH_EAST),run-1);
			grow(b.getRelative(BlockFace.SOUTH_WEST),run-1);
		}
	}
	private int entangle(Player p) {
		int range = (int)(10.84198*Math.pow(1.01926, DUtil.getAscensions(p)));
		int duration = (int)(4.95778*Math.pow(DUtil.getAscensions(p), 0.459019));
		int count = 0;
		for (LivingEntity le : p.getWorld().getLivingEntities()) {
			if (le.getLocation().distance(p.getLocation()) < range) {
				if (le instanceof Player) {
					Player pl = (Player)le;
					if (DUtil.isFullParticipant(pl)) {
						if (!DUtil.isTitan(pl)) {
							trap(le, duration, p);
							count++;
						} else continue;
					}
				}
				if (DUtil.isPVP(le.getLocation())) {
					count++;
					trap(le, duration, p);
				}
			}
		}
		return count;
	}
	private void trap(final LivingEntity le, int durationseconds, final Player p) {
		if (le instanceof Player) {
			((Player)le).sendMessage(ChatColor.YELLOW+"You have been entangled by Rhea.");
		}
		le.setVelocity(new Vector(0, 0, 0));
		final Location originalloc = le.getLocation();
		Block corner1 = le.getLocation().getBlock().getRelative(BlockFace.SOUTH_EAST).getRelative(BlockFace.SOUTH_EAST);
		Block corner2 = le.getLocation().getBlock().getRelative(BlockFace.SOUTH_WEST).getRelative(BlockFace.SOUTH_WEST);
		Block corner3 = le.getLocation().getBlock().getRelative(BlockFace.NORTH_EAST).getRelative(BlockFace.NORTH_EAST);
		Block corner4 = le.getLocation().getBlock().getRelative(BlockFace.NORTH_WEST).getRelative(BlockFace.NORTH_WEST);
		final ArrayList<Location> toreset = new ArrayList<Location>();
		for (int i=0;i<3;i++) {
			if ((corner1.getType() == Material.AIR) || (corner1.getType() == Material.WATER)) {
				toreset.add(corner1.getLocation());
				corner1.setType(Material.LOG);
			}
			corner1 = corner1.getRelative(BlockFace.UP);
		}
		for (int i=0;i<3;i++) {
			if ((corner2.getType() == Material.AIR) || (corner2.getType() == Material.WATER)) {
				toreset.add(corner2.getLocation());
				corner2.setType(Material.LOG);
			}
			corner2 = corner2.getRelative(BlockFace.UP);
		}
		for (int i=0;i<3;i++) {
			if ((corner3.getType() == Material.AIR) || (corner3.getType() == Material.WATER)) {
				toreset.add(corner3.getLocation());
				corner3.setType(Material.LOG);
			}
			corner3 = corner3.getRelative(BlockFace.UP);
		}
		for (int i=0;i<3;i++) {
			if ((corner4.getType() == Material.AIR) || (corner4.getType() == Material.WATER)) {
				toreset.add(corner4.getLocation());
				corner4.setType(Material.LOG);
			}
			corner4 = corner4.getRelative(BlockFace.UP);
		}
		for (int x=-2;x<=2;x++) {
			for (int z=-2;z<=2;z++) {
				Block at = le.getWorld().getBlockAt(le.getEyeLocation().getBlockX()+x, le.getEyeLocation().getBlockY()+2, le.getEyeLocation().getBlockZ()+z);
				if ((at.getType() == Material.AIR) || (at.getType() == Material.WATER)) {
					toreset.add(at.getLocation());
					at.setType(Material.LEAVES);
				}
			}
		}
		for (int x=-2;x<=2;x++) {
			for (int y=-1;y<=2;y++) {
				for (int z=-2;z<=2;z++) {
					if ((x == 2) || (z == 2)) {
						Block at = le.getWorld().getBlockAt(le.getEyeLocation().getBlockX()+x, le.getEyeLocation().getBlockY()+y, le.getEyeLocation().getBlockZ()+z);
						if ((at.getType() == Material.AIR) || (at.getType() == Material.WATER)) {
							toreset.add(at.getLocation());
							at.setType(Material.VINE);
						}
					}
				}
			}
		}
		for (int i=0; i<durationseconds*20; i+=10) {
			DUtil.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(DUtil.getPlugin(), new Runnable() {
				@Override
				public void run() {
					if (le.getLocation().distance(originalloc) > 0.5) {
						if (le.isDead())
							return;
						if (le instanceof Player)
							((Player)le).sendMessage(ChatColor.YELLOW+"You take damage from moving while entangled!");
						DUtil.damageDemigods(p, le, 12);
					}
					le.teleport(originalloc);
				}
			}, i);
		}
		DUtil.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(DUtil.getPlugin(), new Runnable() {
			@Override
			public void run() {
				for (Location l : toreset)
					l.getBlock().setType(Material.AIR);
			}
		}, durationseconds*20);
	}
}
