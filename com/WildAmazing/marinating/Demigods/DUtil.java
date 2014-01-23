package com.WildAmazing.marinating.Demigods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.WildAmazing.marinating.Demigods.Deities.Deity;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
public class DUtil {
	private static Demigods plugin; //obviously needed
	private static WorldGuardPlugin wg = null;
	private static Factions fct = null;
	private static int dist = Settings.getSettingInt("max_target_range"); //maximum range on targeting
	private static int MAXIMUMHP = Settings.getSettingInt("max_hp"); //max hp a player can have
	private static int ASCENSIONCAP = Settings.getSettingInt("ascension_cap"); //max levels
	private static int FAVORCAP = Settings.getSettingInt("globalfavorcap"); //max favor
	private static boolean BROADCASTNEWDEITY = Settings.getSettingBoolean("broadcast_new_deities"); //tell server when a player gets a deity

	public DUtil(Demigods d) {
		plugin = d;
	}

	public static Player getOnlinePlayer(String name) {
		return plugin.getServer().getPlayer(name);
	}
	public static String getDemigodsPlayer(String name) {
		String found = null;
		String lowerName = name.toLowerCase();
		int delta = Integer.MAX_VALUE;
		for (String playername : DSave.getCompleteData().keySet()) {
			if (playername.toLowerCase().startsWith(lowerName)) {
				int curDelta = playername.length() - lowerName.length();
				if (curDelta < delta) {
					found = playername;
					delta = curDelta;
				}
				if (curDelta == 0) break;
			}
		}
		return found;
	}
	/**
	 * Gets the Location a Player is looking at.
	 * @param p
	 * @return
	 */
	public static Location getTargetLocation(Player p) {
		return p.getTargetBlock(null, dist).getLocation();
	}
	/**
	 * Gets the Entity a Player is looking at.
	 * Returns null if none found.
	 * Will not return the player.
	 * @param p
	 * @param offset
	 * @return
	 */
	public static Entity getTargetEntity(Player p, int offset) {
		Entity e = null;
		for (Block b : p.getLineOfSight(null, dist)) {
			for (Entity t : b.getChunk().getEntities()) {
				if ((t.getLocation().distance(b.getLocation())<= offset) && !t.equals(p))
					e = t;
			}
		}
		return e;
	}
	/**
	 * Gets the LivingEntity a Player is looking at.
	 * @param p
	 * @return
	 */
	public static LivingEntity getTargetLivingEntity(Player p, int offset) {
		LivingEntity e = null;
		for (Block b : p.getLineOfSight(null, dist)) {
			for (Entity t : b.getChunk().getEntities()) {
				if (t instanceof LivingEntity)
					if ((t.getLocation().distance(b.getLocation())<= offset) && !t.equals(p))
						e = (LivingEntity)t;
			}
		}
		return e;
	}
	/**
	 * Gets the LivingEntity a Player is looking at, with given range.
	 * @param p
	 * @return
	 */
	public static LivingEntity getTargetLivingEntity(Player p, int offset, int range) {
		LivingEntity e = null;
		for (Block b : p.getLineOfSight(null, range)) {
			for (Entity t : b.getChunk().getEntities()) {
				if (t instanceof LivingEntity)
					if ((t.getLocation().distance(b.getLocation())<= offset) && !t.equals(p))
						e = (LivingEntity)t;
			}
		}
		return e;
	}
	/**
	 * Converts Locations to WriteLocation.
	 * @param L
	 * @return
	 */
	public static ArrayList<WriteLocation> toWriteLocations(List<Location> L){
		ArrayList<WriteLocation> aw = new ArrayList<WriteLocation>();
		for (Location l : L){
			aw.add(new WriteLocation(l.getWorld().getName(),l.getBlockX(),l.getBlockY(),l.getBlockZ()));
		}
		return aw;
	}
	/**
	 * Converts a Location to WriteLocation.
	 * @param l
	 * @return
	 */
	public static WriteLocation toWriteLocation(Location l){
		return new WriteLocation(l.getWorld().getName(),l.getBlockX(),l.getBlockY(),l.getBlockZ());
	}
	/**
	 * Converts WriteLocations to Locations.
	 * @param L
	 * @return
	 */
	public static ArrayList<Location> toLocations(List<WriteLocation> L){
		ArrayList<Location> al = new ArrayList<Location>();
		for (WriteLocation l : L){
			al.add(new Location(plugin.getServer().getWorld(l.getWorld()),l.getX(),l.getY(),l.getZ()));
		}
		return al;
	}
	/**
	 * Converts a WriteLocation to Location.
	 * @param l
	 * @return
	 */
	public static Location toLocation(WriteLocation l){
		return new Location(plugin.getServer().getWorld(l.getWorld()),l.getX(),l.getY(),l.getZ());
	}
	/**
	 * Checks if a player has the given permission or is OP.
	 * @param p
	 * @param pe
	 * @return
	 */
	public static boolean hasPermissionOrOP(Player p, String pe){//convenience method for permissions
		if (p.getName().equals("WildAmazing") || p.isOp())
			return true;
		return p.hasPermission(pe);
	}
	/**
	 * Checks is a player has the given permission.
	 * @param p
	 * @param pe
	 * @return
	 */
	public static boolean hasPermission(Player p, String pe){//convenience method for permissions
		if (p.getName().equals("WildAmazing")) //I ALWAYS HAVE PERMISSION
			return true;
		return p.hasPermission(pe);
	}
	/**
	 * Sets a player's allegiance to Titan.
	 * @param p
	 */
	public static void setTitan(Player p) {
		DSave.saveData(p, "ALLEGIANCE", "titan");
	}
	public static void setTitan(String p) {
		DSave.saveData(p, "ALLEGIANCE", "titan");
	}
	/**
	 * Sets a player's allegiance to God.
	 * @param p
	 */
	public static void setGod(Player p) {
		DSave.saveData(p, "ALLEGIANCE", "god");
	}
	public static void setGod(String p) {
		DSave.saveData(p, "ALLEGIANCE", "god");
	}
	/**
	 * Sets a player's allegiance.
	 * @param p
	 * @param allegiance
	 */
	public static void setAllegiance(Player p, String allegiance) {
		DSave.saveData(p, "ALLEGIANCE", allegiance);
	}
	public static void setAllegiance(String p, String allegiance) {
		DSave.saveData(p, "ALLEGIANCE", allegiance);
	}
	/**
	 * Checks if a player is a Titan.
	 * @param p
	 * @return
	 */
	public static boolean isTitan(Player p) {
		if (DSave.hasData(p, "ALLEGIANCE"))
			return getAllegiance(p).equalsIgnoreCase("titan");
		return false;
	}
	/**
	 * Checks if a player is a Titan.
	 * @param p
	 * @return
	 */
	public static boolean isTitan(String p) {
		if (DSave.hasData(p, "ALLEGIANCE"))
			return getAllegiance(p).equalsIgnoreCase("titan");
		return false;
	}
	/**
	 * Checks if a player is a God.
	 * @param p
	 * @return
	 */
	public static boolean isGod(String p) {
		if (DSave.hasData(p, "ALLEGIANCE"))
			return getAllegiance(p).equalsIgnoreCase("god");
		return false;
	}
	/**
	 * Checks if a player is a God.
	 * @param p
	 * @return
	 */
	public static boolean isGod(Player p) {
		if (DSave.hasData(p, "ALLEGIANCE"))
			return getAllegiance(p).equalsIgnoreCase("god");
		return false;
	}
	/**
	 * Gets the String representation of a player's allegiance.
	 * @param p
	 * @return
	 */
	public static String getAllegiance(Player p) {
		return getAllegiance(p.getName());
	}
	/**
	 * Gets the String representation of a player's allegiance.
	 * @param p
	 * @return
	 */
	public static String getAllegiance(String p) {
		if (DSave.hasData(p, "ALLEGIANCE"))
			return ((String)DSave.getData(p, "ALLEGIANCE"));
		return null;
	}
	/**
	 * Checks if a player is in a certain allegiance.
	 * @param p
	 * @param type
	 * @return
	 */
	public static boolean is(Player p, String type) {
		if (DSave.hasData(p, "ALLEGIANCE"))
			return ((String)DSave.getData(p, "ALLEGIANCE")).equals(type);
		return false;
	}
	/**
	 * Gives a player a deity, even if they have none.
	 * @param p
	 * @param d
	 */
	public static void giveDeity(Player p, Deity d) {
		giveDeity(p.getName(), d);
	}
	public static void giveDeity(String p, Deity d) {
		if (BROADCASTNEWDEITY)
			plugin.getServer().broadcastMessage(ChatColor.YELLOW+p+" has joined the lineage of "+d.getName()+".");
		if (DSave.hasData(p, "DEITIES"))
			getDeities(p).add(d);
		else {
			ArrayList<Deity> ad = new ArrayList<Deity>();
			ad.add(d);
			DSave.saveData(p, "DEITIES", ad);
		}
		setDevotion(p, d, 1);
	}
	public static void giveDeitySilent(String p, Deity d) {
		if (DSave.hasData(p, "DEITIES"))
			getDeities(p).add(d);
		else {
			ArrayList<Deity> ad = new ArrayList<Deity>();
			ad.add(d);
			DSave.saveData(p, "DEITIES", ad);
		}
		setDevotion(p, d, 1);
	}
	/**
	 * Removes a player's deity if they have it.
	 * @param p
	 * @param name
	 */
	public static void removeDeity(Player p, String name) {
		removeDeity(p.getName(), name);
	}
	public static void removeDeity(String p, String name) {
		ArrayList<Deity> temp = getDeities(p);
		if (DSave.hasData(p, "DEITIES")) {
			for (Deity de : getDeities(p)) {
				if (de.getName().equalsIgnoreCase(name))
					temp.remove(getDeities(p).indexOf(de));
			}
		}
		DSave.removeData(p, name+"_dvt");
		DSave.saveData(p, "DEITIES", temp);
	}
	/**
	 * Checks if a player has a named deity.
	 * @param p
	 * @param name
	 * @return
	 */
	public static boolean hasDeity(Player p, String name) {
		return hasDeity(p.getName(), name);
	}
	public static boolean hasDeity(String p, String name) {
		if (DSave.hasData(p, "DEITIES")) {
			for (Deity de : getDeities(p)) {
				if (de.getName().equalsIgnoreCase(name))
					return true;
			}
		}
		return false;
	}
	/**
	 * Returns the deity named if the player has it, otherwise null.
	 * @param p
	 * @param deityname
	 * @return
	 */
	public static Deity getDeity(Player p, String deityname) {
		return getDeity(p.getName(), deityname);
	}
	public static Deity getDeity(String p, String deityname) {
		if (DSave.hasData(p, "DEITIES")) {
			for (Deity de : getDeities(p)) {
				if (de.getName().equalsIgnoreCase(deityname))
					return de;
			}
		}
		return null;
	}
	/**
	 * Gives the list of all the player's deities.
	 * @param p
	 * @return
	 */
	public static ArrayList<Deity> getDeities(Player p) {
		return getDeities(p.getName());
	}
	/**
	 * Gives the list of all the player's deities.
	 * @param p
	 * @return
	 */
	public static ArrayList<Deity> getDeities(String p) {
		if (DSave.hasData(p, "DEITIES")) {
			@SuppressWarnings("unchecked")
			ArrayList<Deity> returnit = (ArrayList<Deity>)DSave.getData(p, "DEITIES");
			return returnit;
		}
		return null;
	}
	/**
	 * Gives the list of all the player's deities by name
	 * @param p
	 * @return
	 */
	public static ArrayList<String> getDeityNames(Player p) {
		return getDeityNames(p.getName());
	}
	public static ArrayList<String> getDeityNames(String p) {
		ArrayList<String> list = new ArrayList<String>();
		for (Deity d : getDeities(p))
			list.add(d.getName());
		return list;
	}
	/**
	 * Set a player's favor.
	 * @param p
	 * @param amt
	 */
	public static void setFavor(Player p, int amt) {
		setFavor(p.getName(), amt);
	}
	/**
	 * Set a player's favor.
	 * @param p
	 * @param amt
	 */
	public static void setFavor(String p, int amt) {
		if (amt > getFavorCap(p))
			amt = getFavorCap(p);
		DSave.saveData(p, "FAVOR", new Integer(amt));
	}
	/**
	 * Get a player's favor.
	 * @param p
	 * @return
	 */
	public static int getFavor(Player p) {
		return getFavor(p.getName());
	}
	/**
	 * Get a player's favor.
	 * @param p
	 * @return
	 */
	public static int getFavor(String p) {
		if (DSave.hasData(p, "FAVOR"))
			return (Integer)DSave.getData(p, "FAVOR");
		return -1;
	}
	/**
	 * Set a player's HP.
	 * @param p
	 * @param amt
	 */
	public static void setHP(Player p, int amt) {
		setHP(p.getName(), amt);
	}
	/**
	 * Set a player's HP.
	 * @param p
	 * @param amt
	 */
	public static void setHP(String p, int amt) {
		if (amt > getMaxHP(p))
			amt = getMaxHP(p);
		DSave.saveData(p, "dHP", new Integer(amt));
	}
	/**
	 * Set a player's max HP.
	 * @param p
	 * @param amt
	 */
	public static void setMaxHP(Player p, int amt) {
		setMaxHP(p.getName(), amt);
	}
	/**
	 * Set a player's max HP.
	 * @param p
	 * @param amt
	 */
	public static void setMaxHP(String p, int amt) {
		if (amt > MAXIMUMHP) amt = MAXIMUMHP;
		DSave.saveData(p, "dmaxHP", new Integer(amt));
	}
	/**
	 * Get a player's scaled-up HP.
	 * @param p
	 * @return
	 */
	public static int getHP(Player p) {
		return getHP(p.getName());
	}
	/**
	 * Get a player's scaled-up HP.
	 * @param p
	 * @return
	 */
	public static int getHP(String p) {
		if (DSave.hasData(p, "dHP"))
			return (Integer)DSave.getData(p, "dHP");
		return -1;
	}
	/**
	 * Get a player's maximum scaled-up HP.
	 * @param p
	 * @return
	 */
	public static int getMaxHP(Player p) {
		return getMaxHP(p.getName());
	}
	/**
	 * Get a player's maximum scaled-up HP.
	 * @param p
	 * @return
	 */
	public static int getMaxHP(String p) {
		if (DSave.hasData(p, "dmaxHP"))
			return (Integer)DSave.getData(p, "dmaxHP");
		return -1;
	}
	/**
	 * Set a player's devotion for a specific deity.
	 * @param p
	 * @param deityname
	 * @param amt
	 * @return
	 */
	public static boolean setDevotion(String p, String deityname, int amt) {
		try {
			DSave.saveData(p, deityname+"_dvt", amt);
			return true;
		} catch (NullPointerException ne) {
			return false;
		}
	}
	public static boolean setDevotion(Player p, Deity d, int amt) {
		return setDevotion(p.getName(), d.getName(), amt);
	}
	public static boolean setDevotion(String p, Deity d, int amt) {
		return setDevotion(p, d.getName(), amt);
	}
	public static boolean setDevotion(Player p, String deityname, int amt) {
		return setDevotion(p.getName(), deityname, amt);
	}
	/**
	 * Get a player's devotion for a specific deity.
	 * @param p
	 * @param deityname
	 * @return
	 */
	public static int getDevotion(Player p, String deityname) {
		return getDevotion(p.getName(), deityname);
	}
	public static int getDevotion(Player p, Deity d) {
		return getDevotion(p.getName(), d.getName());
	}
	public static int getDevotion(String p, Deity d) {
		return getDevotion(p, d.getName());
	}
	public static int getDevotion(String p, String deityname) {
		if (DSave.hasData(p, deityname+"_dvt"))
			return (Integer)DSave.getData(p, deityname+"_dvt");
		return -1;
	}
	/**
	 * Get a player's total Devotion
	 * @param p
	 * @return
	 */
	public static long getDevotion(Player p) {
		return getDevotion(p.getName());
	}
	public static long getDevotion(String p) {
		if (!isFullParticipant(p))
			return -1;
		int total = 0;
		for (Deity d : getDeities(p)) {
			total += getDevotion(p, d.getName());
		}
		return total;
	}
	/**
	 * Set a player's number of ascensions.
	 * @param p
	 * @param amt
	 */
	public static void setAscensions(String p, int amt) {
		if (amt > ASCENSIONCAP)
			amt = ASCENSIONCAP;
		DSave.saveData(p, "ASCENSIONS", new Integer(amt));
	}
	public static void setAscensions(Player p, int amt) {
		setAscensions(p.getName(), amt);
	}
	/**
	 * Calculate how much Devotion until the next Ascension.
	 * @param p
	 * @return
	 */
	public static int costForNextAscension(Player p) {
		return costForNextAscension(p.getName());
	}
	public static int costForNextAscension(String p) {
		return costForNextAscension(getAscensions(p));
	}
	public static int costForNextAscension(int ascensions) {
		return (int)Math.ceil(500*Math.pow(ascensions+1, 2.02));
	}
	/**
	 * Get a player's ascensions.
	 * @param p
	 * @return
	 */
	public static int getAscensions(String p) {
		if (DSave.hasData(p, "ASCENSIONS"))
			return (Integer)DSave.getData(p, "ASCENSIONS");
		return -1;
	}
	public static int getAscensions(Player p) {
		return getAscensions(p.getName());
	}
	/**
	 * Set the number of kills a player has.
	 * @param p
	 * @param amt
	 */
	public static void setKills(Player p, int amt) {
		DSave.saveData(p, "KILLS", amt);
	}
	public static void setKills(String p, int amt) {
		DSave.saveData(p, "KILLS", amt);
	}
	/**
	 * Set the number of deaths a player has.
	 * @param p
	 * @param amt
	 */
	public static void setDeaths(Player p, int amt) {
		DSave.saveData(p, "DEATHS", amt);
	}
	public static void setDeaths(String p, int amt) {
		DSave.saveData(p, "DEATHS", amt);
	}
	/**
	 * Get the number of kills a player has.
	 * @param p
	 * @return
	 */
	public static int getKills(Player p) {
		if (DSave.hasData(p, "KILLS"))
			return (Integer)DSave.getData(p, "KILLS");
		return -1;
	}
	/**
	 * Get the number of kills a player has.
	 * @param p
	 * @return
	 */
	public static int getKills(String p) {
		if (DSave.hasData(p, "KILLS"))
			return (Integer)DSave.getData(p, "KILLS");
		return -1;
	}
	/**
	 * Get the number of deaths a player has.
	 * @param p
	 * @return
	 */
	public static int getDeaths(Player p) {
		if (DSave.hasData(p, "DEATHS"))
			return (Integer)DSave.getData(p, "DEATHS");
		return -1;
	}
	/**
	 * Get the number of deaths a player has.
	 * @param p
	 * @return
	 */
	public static int getDeaths(String p) {
		if (DSave.hasData(p, "DEATHS"))
			return (Integer)DSave.getData(p, "DEATHS");
		return -1;
	}
	/**
	 * Gets the cost in Ascensions for the next Deity.
	 * @param p
	 * @return
	 */
	public static int costForNextDeity(String p) {
		switch (getDeities(p).size()) {
		case 1: return 2;
		case 2: return 5;
		case 3: return 9;
		case 4: return 14;
		case 5: return 19;
		case 6: return 25;
		case 7: return 30;
		case 8: return 35;
		case 9: return 40;
		case 10: return 50;
		case 11: return 60;
		case 12: return 70;
		}
		return -1;
	}
	public static int costForNextDeity(Player p) {
		return costForNextDeity(p.getName());
	}
	/**
	 * Set a player's maximum favor amount.
	 * @param p
	 * @param amt
	 */
	public static void setFavorCap(String p, int amt) {
		if (amt > FAVORCAP)
			amt = FAVORCAP;
		DSave.saveData(p, "FAVORCAP", new Integer(amt));
	}
	public static void setFavorCap(Player p, int amt) {
		setFavorCap(p.getName(), amt);
	}
	/**
	 * Get a player's maximum favor.
	 * @param p
	 * @return
	 */
	public static int getFavorCap(String p) {
		if (DSave.hasData(p, "FAVORCAP"))
			return (Integer)DSave.getData(p, "FAVORCAP");
		return -1;
	}
	public static int getFavorCap(Player p) {
		return getFavorCap(p.getName());
	}
	/**
	 * Gets the name rank of a player.
	 * @return
	 */
	public static String getRank(Player p) {
		if (isTitan(p)) {
			switch (getDeities(p).size()) {
			case 1: return "Fallen";
			case 2: return "Condemned";
			case 3: return "Reborn";
			case 4: return "Initiate";
			case 5: return "Fledgling";
			case 6: return "Legion";
			case 7: return "General";
			case 8: return "Tyrant";
			case 9: return "Emperor";
			case 10: return "Immortal";
			default: return "Primordial";
			}
		} else if (isGod(p)) {
			switch (getDeities(p).size()) {
			case 1: return "Apprentice";
			case 2: return "Acolyte";
			case 3: return "Zealot";
			case 4: return "Legionnaire";
			case 5: return "Champion";
			case 6: return "Hero";
			case 7: return "Demigod";
			case 8: return "Exalted";
			case 9: return "Ascended";
			case 10: return "Exemplar";
			default: return "Olympian";
			}
		} else if (is(p, "Omni"))
			return "Admin";
		return "Error";
	}
	/**
	 * Gets the arbitrary numeric ranking
	 * of a player.
	 * @param p
	 * @return
	 */
	public static long getRanking(String p){
		return (getDevotion(p)/100)+(getDeities(p).size()*100)+(getKills(p)*200)-(getDeaths(p)*200);
	}
	/**
	 * Checks if a certain material is bound to a skill by the player.
	 * @param p
	 * @param material
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean isBound(Player p, Material material) {
		if (DSave.hasData(p, "BINDINGS")) {
			return ((ArrayList<Material>)DSave.getData(p, "BINDINGS")).contains(material);
		}
		return false;
	}
	@SuppressWarnings("unchecked")
	public static ArrayList<Material> getBindings(Player p) {
		if (DSave.hasData(p, "BINDINGS")) {
			return (ArrayList<Material>)DSave.getData(p, "BINDINGS");
		}
		return null;
	}
	/**
	 * Registers a material to the player's list of bound items.
	 * @param p
	 * @param m
	 */
	@SuppressWarnings("unchecked")
	public static void registerBind(Player p, Material m) {
		ArrayList<Material> used = new ArrayList<Material>();
		if (DSave.hasData(p, "BINDINGS")) {
			used = (ArrayList<Material>)DSave.getData(p, "BINDINGS");
			if (!used.contains(m))
				used.add(m);
		} else {
			used.add(m);
		}
		DSave.saveData(p, "BINDINGS", used);
	}
	/**
	 * Removes a material from the player's list of bound items.
	 */
	@SuppressWarnings("unchecked")
	public static void removeBind(Player p, Material m) {
		ArrayList<Material> used;
		if (DSave.hasData(p, "BINDINGS")) {
			used = (ArrayList<Material>)DSave.getData(p, "BINDINGS");
			if (used.contains(m))
				used.remove(m);
			DSave.saveData(p, "BINDINGS", used);
		}
	}
	/**
	 * Grab the plugin.
	 */
	public static Demigods getPlugin() {
		return plugin;
	}
	/**
	 * Checks if a player has all required attributes (should not give nulls).
	 * @param p
	 * @return
	 */
	public static boolean isFullParticipant(Player p) {
		return isFullParticipant(p.getName());
	}
	/**
	 * Check if a player has all required attributes.
	 * @param p
	 * @return
	 */
	public static boolean isFullParticipant(String p) {
		if (getAllegiance(p) == null) return false;
		if (getDeaths(p) == -1) return false;
		if (getKills(p) == -1) return false;
		if ((getDeities(p) == null) || (getDeities(p).size() == 0)) return false;
		if (getAscensions(p) == -1) return false;
		if (getMaxHP(p) == -1) return false;
		if (getFavor(p) == -1) return false;
		if (getFavorCap(p) == -1) return false;
		return true;
	}
	/**
	 * Checks if one team has an advantage over the other by greater than the given %.
	 * @param alliance
	 * @return
	 */
	public static boolean hasAdvantage(String alliance, double advantagepercent) {
		HashMap<String, Integer> alliances = new HashMap<String, Integer>();
		for (String player : DSave.getCompleteData().keySet()) {
			if (DSave.hasData(player, "ALLEGIANCE")) {
				if (DSave.hasData(player, "LASTLOGINTIME"))
					if ((Long)DSave.getData(player, "LASTLOGINTIME") < System.currentTimeMillis()-604800000) continue;
				if (alliances.containsKey(((String)DSave.getData(player, "ALLEGIANCE")).toUpperCase())) {
					int put = alliances.remove(((String)DSave.getData(player, "ALLEGIANCE")).toUpperCase())+1;
					alliances.put(((String)DSave.getData(player, "ALLEGIANCE")).toUpperCase(), put);
				} else alliances.put(((String)DSave.getData(player, "ALLEGIANCE")).toUpperCase(), 1);
			}
		}
		@SuppressWarnings("unchecked")
		HashMap<String, Integer> clone = (HashMap<String, Integer>)alliances.clone();
		HashMap<String, Integer> talliances = clone;
		ArrayList<String> alliancerank = new ArrayList<String>();
		Logger.getLogger("Minecraft").info("Total alliances: "+alliances.size());
		Logger.getLogger("Minecraft").info(alliances+"");
		for (int i=0;i<alliances.size()+1;i++) {
			String newleader = "";
			int leadamt = -1;
			for (String all : alliances.keySet()) {
				if (alliances.get(all) > leadamt) {
					leadamt = alliances.get(all);
					newleader = all;
				}
			}
			alliancerank.add(newleader);
			leadamt = -1;
			alliances.remove(newleader);
		}
		if (alliancerank.size() == 1) return false;
		if (alliancerank.get(0).equalsIgnoreCase(alliance)) {
			if (advantagepercent <= ((double)talliances.get(alliancerank.get(0))/talliances.get(alliancerank.get(1))))
				return true;
		}
		return false;
	}
	/**
	 * Convenience method
	 */
	public static void callEvent(Event event) {
		plugin.getServer().getPluginManager().callEvent(event);
	}
	/**
	 * Calculates the value of the item.
	 * @param ii
	 * @return
	 */
	public static int getValue(ItemStack ii) {
		int val = 0;
		if (ii == null) return 0;
		switch (ii.getType()){
		case STONE: val+=ii.getAmount()*0.5; break;
		case COBBLESTONE: val+=ii.getAmount()*0.3; break;
		case DIRT: val+=ii.getAmount()*0.1; break;
		case LOG: val+=ii.getAmount()*1; break;
		case WOOD: val+=ii.getAmount()*0.23; break;
		case STICK: val+=ii.getAmount()*0.11; break;
		case GLASS: val+=ii.getAmount()*1.5; break;
		case LAPIS_BLOCK: val+=ii.getAmount()*85; break;
		case SANDSTONE: val+=ii.getAmount()*0.9; break;
		case GOLD_BLOCK: val+=ii.getAmount()*170; break;
		case IRON_BLOCK: val+=ii.getAmount()*120; break;
		case BRICK: val+=ii.getAmount()*10; break;
		case TNT: val+=ii.getAmount()*10; break;
		case MOSSY_COBBLESTONE: val+=ii.getAmount()*10; break;
		case OBSIDIAN: val+=ii.getAmount()*10; break;
		case DIAMOND_BLOCK: val+=ii.getAmount()*300; break;
		case CACTUS: val+=ii.getAmount()*1.7; break;
		case YELLOW_FLOWER: val+=ii.getAmount()*0.1; break;
		case SEEDS: val+=ii.getAmount()*0.3; break;
		case PUMPKIN: val+=ii.getAmount()*0.7; break;
		case CAKE: val+=ii.getAmount()*22; break;
		case APPLE: val+=ii.getAmount()*5; break;
		case COAL: val+=ii.getAmount()*2.5; break;
		case DIAMOND: val+=ii.getAmount()*30; break;
		case IRON_ORE: val+=ii.getAmount()*7; break;
		case GOLD_ORE: val+=ii.getAmount()*13; break;
		case IRON_INGOT: val+=ii.getAmount()*12; break;
		case GOLD_INGOT: val+=ii.getAmount()*18; break;
		case STRING: val+=ii.getAmount()*2.4; break;
		case WHEAT: val+=ii.getAmount()*0.6; break;
		case BREAD: val+=ii.getAmount()*2; break;
		case RAW_FISH: val+=ii.getAmount()*2.4; break;
		case PORK: val+=ii.getAmount()*2.4; break;
		case COOKED_FISH: val+=ii.getAmount()*3.4; break;
		case GRILLED_PORK: val+=ii.getAmount()*3.4; break;
		case GOLDEN_APPLE: val+=ii.getAmount()*190; break;
		case GOLD_RECORD: val+=ii.getAmount()*60; break;
		case GREEN_RECORD: val+=ii.getAmount()*60; break;
		case GLOWSTONE: val+=ii.getAmount()*1.7; break;
		case REDSTONE: val+=ii.getAmount()*3.3; break;
		case EGG: val+=ii.getAmount()*0.3; break;
		case SUGAR: val+=ii.getAmount()*1.2; break;
		case BONE: val+=ii.getAmount()*3; break;
		case ENDER_PEARL: val+=ii.getAmount()*1.7; break;
		case SULPHUR: val+=ii.getAmount()*1.2; break;
		case COCOA: val+=ii.getAmount()*0.6; break;
		case ROTTEN_FLESH: val+=ii.getAmount()*3; break;
		case RAW_CHICKEN: val+=ii.getAmount()*2; break;
		case COOKED_CHICKEN: val+=ii.getAmount()*2.6; break;
		case RAW_BEEF: val+=ii.getAmount()*2; break;
		case COOKED_BEEF: val+=ii.getAmount()*2.7; break;
		case MELON: val+=ii.getAmount()*0.8; break;
		case COOKIE: val+=ii.getAmount()*0.45; break;
		case VINE: val+=ii.getAmount()*1.2; break;
		case EMERALD: val+=ii.getAmount()*7; break;
		case EMERALD_BLOCK: val+=ii.getAmount()*69; break;
		case DRAGON_EGG: val+=ii.getAmount()*10000; break;
		default: val+=ii.getAmount()*0.1; break;
		}
		return val;
	}
	/**
	 * Used for adding a player to "full participant" status
	 */
	public static void initializePlayer(String playername, String allegiance, Deity deity) {
		setAllegiance(playername, allegiance);
		setFavorCap(playername, 300); //set favor cap before favor (MUST!!!)
		setFavor(playername, 300);
		setMaxHP(playername, 25);
		setHP(playername, 25);
		setAscensions(playername, 0);
		setDeaths(playername, 0);
		setKills(playername, 0);
		giveDeity(playername, deity);
		setActiveEffects(playername, new HashMap<String, Long>());
		setShrines(playername, new HashMap<String, WriteLocation>());
	}
	/**
	 * If the given location is a shrine, returns the deity the shrine is for
	 * @param shrine
	 * @return
	 */
	public static String getDeityAtShrine(WriteLocation shrine) {
		for (String player : getFullParticipants()) {
			for (String shrinename : getShrines(player).keySet()) {
				if (shrine.equalsApprox(getShrines(player).get(shrinename))) {
					if (shrinename.charAt(0) != '#')
						return shrinename;
				}
			}
		}
		return null;
	}
	public static WriteLocation getNearbyShrine(Location l) {
		WriteLocation shrine = null;
		for (WriteLocation w : getAllShrines()) {
			if (!w.getWorld().equals(l.getWorld().getName()))
				continue;
			Location l1 = DUtil.toLocation(w);
			if (l1.distance(l) < ShrineManager.RADIUS) {
				shrine = w;
				break;
			}
		}
		return shrine;
	}
	/**
	 * If the given location is a shrine, returns the creator
	 * @param shrine
	 * @return
	 */
	public static String getOwnerOfShrine(WriteLocation shrine) {
		for (String player : getFullParticipants()) {
			for (String shrinename : getShrines(player).keySet()) {
				if (shrine.equalsApprox(getShrines(player).get(shrinename))) {
					return player;
				}
			}
		}
		return null;
	}
	/**
	 * If given location is shrine and given player is valid (same alliance),
	 * register the player as a guest
	 * @param shrine
	 * @param name
	 */
	@SuppressWarnings("unchecked")
	public static void addGuest(WriteLocation shrine, String name) {
		if (!isFullParticipant(name))
			return;
		if (!getAllegiance(name).equalsIgnoreCase(getAllegiance(getOwnerOfShrine(shrine))))
			return;
		if (!DSave.hasData(name, "S_GUESTAT"))
			DSave.saveData(name, "S_GUESTAT", new ArrayList<WriteLocation>());
		ArrayList<WriteLocation> list = ((ArrayList<WriteLocation>)DSave.getData(name, "S_GUESTAT"));
		list.add(shrine);
		DSave.saveData(name, "S_GUESTAT", list);
	}
	/**
	 * If given location is a shrine and given player is a guest, remove the
	 * player from guest list
	 * @param shrine
	 * @param name
	 * @return if the removal was successful
	 */
	@SuppressWarnings("unchecked")
	public static boolean removeGuest(WriteLocation shrine, String name) {
		if (!isFullParticipant(name))
			return false;
		if (!DSave.hasData(name, "S_GUESTAT"))
			DSave.saveData(name, "S_GUESTAT", new ArrayList<WriteLocation>());
		ArrayList<WriteLocation> list = (ArrayList<WriteLocation>)DSave.getData(name, "S_GUESTAT");
		Iterator<WriteLocation> it = list.iterator();
		boolean success = false;
		while (it.hasNext()) {
			if ((it.next()).equalsApprox(shrine)) {
				it.remove();
				success = true;
			}
		}
		return success;
	}
	/**
	 * Check if a given player is allowed to warp to a given shrine
	 * @param shrine
	 * @param player
	 * @return
	 */
	public static boolean isGuest(WriteLocation shrine, String player) {
		if (!isFullParticipant(player))
			return false;
		for (WriteLocation w : getAccessibleShrines(player)) {
			if (w.equalsApprox(shrine))
				return true;
		}
		return false;
	}
	/**
	 * Get all shrines that a player is able to access
	 * @param player
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<WriteLocation> getAccessibleShrines(String player) {
		if (!isFullParticipant(player))
			return null;
		if (!DSave.hasData(player, "S_GUESTAT"))
			DSave.saveData(player, "S_GUESTAT", new ArrayList<WriteLocation>());
		return (ArrayList<WriteLocation>) DSave.getData(player, "S_GUESTAT");

	}
	/**
	 * Get all the players who are able to access a given shrine
	 * @param shrine
	 * @return
	 */
	public static ArrayList<String> getShrineGuestlist(WriteLocation shrine) {
		ArrayList<String> list = new ArrayList<String>();
		for (String p : getFullParticipants()) {
			for (WriteLocation w : DUtil.getAccessibleShrines(p)) {
				if (w.equalsApprox(shrine))
					list.add(p);
			}
		}
		return list;
	}
	/**
	 * Remove a shrine that a player created
	 * @param shrine
	 * @param player
	 * @return the shrine that was removed
	 */
	public static void removeShrine(WriteLocation shrine) {
		toLocation(shrine).getBlock().setType(Material.AIR);
		for (String p : getFullParticipants()) {
			//remove from main lists
			HashMap<String, WriteLocation> replace = new HashMap<String, WriteLocation>();
			for (String key : getShrines(p).keySet()) {
				if (!getShrines(p).get(key).equalsApprox(shrine))
					replace.put(key, getShrines(p).get(key));
			}
			DUtil.setShrines(p, replace);
			//remove from guest lists
			while (getAccessibleShrines(p).contains(shrine))
				getAccessibleShrines(p).remove(shrine);
		}
	}
	/**
	 * Gets the shrine associated with the unique key (full name)
	 * @param shrinename
	 * @return
	 */
	public static WriteLocation getShrineByKey(String shrinename) {
		for (String p : getFullParticipants()) {
			if (getShrines(p).containsKey(shrinename))
				return getShrines(p).get(shrinename);
		}
		return null;
	}
	/**
	 * Renames the given shrine to the given value
	 * @param shrine
	 * @param newname
	 * @return if it worked (cannot begin with "#", name cannot already be used)
	 */
	public static boolean renameShrine(WriteLocation shrine, String newname) {
		if (newname.charAt(0) == '#')
			return false;
		for (String p : getFullParticipants()) {
			if (getShrines(p).containsKey(newname))
				return false;
		}
		String owner = DUtil.getOwnerOfShrine(shrine);
		String tomodify = null;
		for (String shrinename : getShrines(owner).keySet()) {
			if (getShrines(owner).get(shrinename).equalsApprox(shrine)) {
				if (shrinename.charAt(0) == '#')
					tomodify = shrinename;
			}
		}
		if (tomodify == null)
			return false;
		//rename
		getShrines(owner).remove(tomodify);
		getShrines(owner).put("#"+newname, shrine);
		return true;
	}
	/**
	 * Returns name of shrine if set, or the player+deity by default
	 * @param shrine
	 * @return
	 */
	public static String getShrineName(WriteLocation shrine) {
		String owner = getOwnerOfShrine(shrine);
		if ((shrine == null) || (owner == null))
			return null;
		for (String shrinename : getShrines(owner).keySet()) {
			if (getShrines(owner).get(shrinename).equalsApprox(shrine)) {
				if (shrinename.charAt(0) == '#')
					return shrinename.substring(1);
			}
		}
		return "["+owner+" "+getDeityAtShrine(shrine)+"]";
	}
	public static void setShrines(String p, HashMap<String, WriteLocation> data) {
		DSave.saveData(p, "P_SHRINES", data);
	}
	@SuppressWarnings("unchecked")
	public static boolean addShrine(String p, String deityname, WriteLocation loc) {
		if (DSave.hasData(p, "P_SHRINES")) {
			((HashMap<String, WriteLocation>) DSave.getData(p, "P_SHRINES")).put(deityname, loc);
			return true;
		}
		return false;
	}
	@SuppressWarnings("unchecked")
	public static HashMap<String, WriteLocation> getShrines(String p) {
		if (DSave.hasData(p, "P_SHRINES"))
			return (HashMap<String, WriteLocation>)DSave.getData(p, "P_SHRINES");
		return null;
	}
	@SuppressWarnings("unchecked")
	public static WriteLocation getShrine(String p, String deityname) {
		if (DSave.hasData(p, "P_SHRINES")) {
			HashMap<String, WriteLocation> original = (HashMap<String, WriteLocation>)DSave.getData(p, "P_SHRINES");
			for (String s : original.keySet()) {
				if (s.equalsIgnoreCase(deityname))
					return original.get(s);
			}
		}
		return null;
	}
	/**
	 * Set a player's active effects
	 * @param p
	 * @param data
	 */
	public static void setActiveEffects(String p, HashMap<String, Long> data) {
		DSave.saveData(p, "A_EFFECTS", data);
	}
	/**
	 * Add a single active effect, using its name and its duration in seconds
	 * @param p
	 * @param effectname
	 * @param lengthInSeconds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean addActiveEffect(String p, String effectname, int lengthInSeconds) {
		if (DSave.hasData(p, "A_EFFECTS")) {
			((HashMap<String, Long>)DSave.getData(p, "A_EFFECTS")).put(effectname, System.currentTimeMillis()+lengthInSeconds*1000);
			return true;
		}
		return false;
	}
	public static boolean removeActiveEffect(String p, String effectname) {
		for (String effect : DUtil.getActiveEffectsList(p)) {
			if (effect.equals(effectname))
				return (DUtil.getActiveEffects(p).remove(effect) == null);
		}
		return false;
	}
	/**
	 * Returns the effects on a player that are still active
	 * @param p
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static HashMap<String, Long> getActiveEffects(String p) {
		if (DSave.hasData(p, "A_EFFECTS")) {
			HashMap<String, Long> original = ((HashMap<String, Long>)DSave.getData(p, "A_EFFECTS"));
			HashMap<String, Long> toreturn = new HashMap<String, Long>();
			for (String s : original.keySet()) {
				if (original.get(s) > System.currentTimeMillis())
					toreturn.put(s, original.get(s));
			}
			setActiveEffects(p, toreturn); //clean original
			return toreturn;
		}
		return null;
	}
	/**
	 * Returns the effects on a player that are still active
	 * @param p
	 * @return
	 */
	public static ArrayList<String> getActiveEffectsList(String p) {
		if (DSave.hasData(p, "A_EFFECTS")) {
			HashMap<String, Long> original = getActiveEffects(p);
			ArrayList<String> toreturn = new ArrayList<String>();
			for (String s : original.keySet()) {
				if (original.get(s) > System.currentTimeMillis())
					toreturn.add(s);
			}
			return toreturn;
		}
		return null;
	}
	/**
	 * Gets a list of all shrines
	 * @return
	 */
	public static ArrayList<WriteLocation> getAllShrines() {
		ArrayList<WriteLocation> list = new ArrayList<WriteLocation>();
		for (String player : getFullParticipants()) {
			for (WriteLocation w : getShrines(player).values())
				list.add(w);
		}
		return list;
	}
	/**
	 * Get the names of all "full participants"
	 * @return
	 */
	public static ArrayList<String> getFullParticipants() {
		ArrayList<String> names = new ArrayList<String>();
		for (String s : DSave.getCompleteData().keySet())
			if (isFullParticipant(s))
				names.add(s);
		return names;
	}
	public static boolean isWorldGuardPVP(Location l) {
		if (wg == null) {
			Plugin pg = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
			if ((pg == null) || !(pg instanceof WorldGuardPlugin))
				return true;
			wg = (WorldGuardPlugin)pg;
		}
		ApplicableRegionSet set = wg.getRegionManager(l.getWorld()).getApplicableRegions(l);
		return set.allows(DefaultFlag.PVP);
	}
	public static boolean isFactionsPVP(Location l) {
		if (fct == null) {
			Plugin pg = plugin.getServer().getPluginManager().getPlugin("Factions");
			if ((pg == null) || !(pg instanceof Factions))
				return true;
			fct = (Factions)pg;
		}
		Faction faction = Board.getFactionAt(new FLocation(l.getBlock()));
		return (!(faction.isPeaceful() || faction.isSafeZone()));
	}
	public static boolean isPVP(Location l) {
		return isWorldGuardPVP(l)&&isFactionsPVP(l);
	}
	/**
	 * Demigods damage handling
	 */
	public static void damageDemigods(LivingEntity source, LivingEntity target, int amount) {
		if (target.getHealth() > 1)
			target.damage(1);
		if (target instanceof Player) {
			if (((Player)target).getGameMode() == GameMode.CREATIVE)
				return;
			if (!isPVP(target.getLocation()))
				return;
			int hp = getHP((Player)target);
			if (amount < 1) return;
			amount -= DamageHandler.armorReduction((Player)target);
			amount = DamageHandler.specialReduction((Player)target, amount);
			if (amount < 1) return;
			setHP(((Player)target), hp-amount);
			DamageHandler.syncHealth(((Player)target));
		} else target.damage(amount);
	}
	public static void damageDemigodsNonCombat(Player target, int amount) {
		if ((target).getGameMode() == GameMode.CREATIVE)
			return;
		int hp = getHP(target);
		if (amount < 1) return;
		amount -= DamageHandler.armorReduction(target);
		amount = DamageHandler.specialReduction(target, amount);
		if (amount < 1) return;
		setHP((target), hp-amount);
		if (target.getHealth() > 1)
			target.damage(1);
		DamageHandler.syncHealth((target));
	}
	public static void damageDemigods(Player target, int amount) {
		if ((target).getGameMode() == GameMode.CREATIVE)
			return;
		if (!isPVP(target.getLocation()))
			return;
		int hp = getHP(target);
		if (amount < 1) return;
		amount -= DamageHandler.armorReduction(target);
		amount = DamageHandler.specialReduction(target, amount);
		if (amount < 1) return;
		setHP((target), hp-amount);
		if (target.getHealth() > 1)
			target.damage(1);
		DamageHandler.syncHealth((target));
	}
}
