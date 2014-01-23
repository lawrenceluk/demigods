package com.WildAmazing.marinating.Demigods;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.WildAmazing.marinating.Demigods.Deities.Deity;
import com.WildAmazing.marinating.Demigods.Deities.Gods.Apollo;
import com.WildAmazing.marinating.Demigods.Deities.Gods.Ares;
import com.WildAmazing.marinating.Demigods.Deities.Gods.Athena;
import com.WildAmazing.marinating.Demigods.Deities.Gods.Hades;
import com.WildAmazing.marinating.Demigods.Deities.Gods.Hephaestus;
import com.WildAmazing.marinating.Demigods.Deities.Gods.Poseidon;
import com.WildAmazing.marinating.Demigods.Deities.Gods.Zeus;
import com.WildAmazing.marinating.Demigods.Deities.Titans.Atlas;
import com.WildAmazing.marinating.Demigods.Deities.Titans.Cronus;
import com.WildAmazing.marinating.Demigods.Deities.Titans.Hyperion;
import com.WildAmazing.marinating.Demigods.Deities.Titans.Oceanus;
import com.WildAmazing.marinating.Demigods.Deities.Titans.Prometheus;
import com.WildAmazing.marinating.Demigods.Deities.Titans.Rhea;

public class DebugManager {

	/**
	 * Prints data for "p" in-game to "cm".
	 * @param cm
	 * @param p
	 */
	public static void printData(Player cm, String p) {
		try {
			cm.sendMessage("Name: "+DUtil.getDemigodsPlayer(p));
		} catch (NullPointerException ne) {
			cm.sendMessage(ChatColor.RED+"Name is missing/null.");
		}
		try {
			cm.sendMessage("Alliance: "+DUtil.getAllegiance(p));
		} catch (NullPointerException ne) {
			cm.sendMessage(ChatColor.RED+"Alliance is missing/null.");
		}
		try {
			cm.sendMessage("Current HP: "+DUtil.getHP(p));
		} catch (NullPointerException ne) {
			cm.sendMessage(ChatColor.RED+"HP is missing/null.");
		}
		try {
			cm.sendMessage("Max HP: "+DUtil.getMaxHP(p));
		} catch (NullPointerException ne) {
			cm.sendMessage(ChatColor.RED+"Max HP is missing/null.");
		}
		try {
			cm.sendMessage("Current Favor: "+DUtil.getFavor(p));
		} catch (NullPointerException ne) {
			cm.sendMessage(ChatColor.RED+"Favor is missing/null.");
		}
		try {
			cm.sendMessage("Max Favor: "+DUtil.getFavorCap(p));
		} catch (NullPointerException ne) {
			cm.sendMessage(ChatColor.RED+"Max Favor is missing/null.");
		}
		try {
			String s = "";
			for (Deity d : DUtil.getDeities(p)) {
				String name = d.getName();
				try {
					s += " "+name+";"+DUtil.getDevotion(p, name);
				} catch (Exception e) {
					cm.sendMessage(ChatColor.RED+"Error loading "+name+".");
				}
			}
			cm.sendMessage("Deities:"+s);
		} catch (NullPointerException ne) {
			cm.sendMessage(ChatColor.RED+"Deities are missing/null.");
		}
		try {
			cm.sendMessage("Ascensions: "+DUtil.getAscensions(p));
		} catch (NullPointerException ne) {
			cm.sendMessage(ChatColor.RED+"Ascensions are missing/null.");
		}
		try {
			cm.sendMessage("Kills: "+DUtil.getKills(p));
		} catch (NullPointerException ne) {
			cm.sendMessage(ChatColor.RED+"Kills are missing/null.");
		}
		try {
			cm.sendMessage("Deaths: "+DUtil.getDeaths(p));
		} catch (NullPointerException ne) {
			cm.sendMessage(ChatColor.RED+"Deaths are missing/null.");
		}
		try {
			cm.sendMessage("Accessible:");
			for (WriteLocation w : DUtil.getAccessibleShrines(p)) {
				String name = DUtil.getShrineName(w);
				try {
					cm.sendMessage(name+" "+w.getX()+" "+w.getY()+" "+w.getZ()+" "+w.getWorld());
				} catch (Exception e) {
					cm.sendMessage(ChatColor.RED+"Error loading "+name+".");
				}
			}
		} catch (NullPointerException ne) {
			cm.sendMessage(ChatColor.RED+"Accessible shrines list is missing/null.");
		}
		//Bindings will be cleared
		//Effects will be cleared
		try {
			cm.sendMessage("Shrines:");
			for (String name : DUtil.getShrines(p).keySet()) {
				try {
					WriteLocation w = DUtil.getShrines(p).get(name);
					String names = "";
					for (String player : DUtil.getShrineGuestlist(w))
						names += player+" ";
					names = names.trim();
					cm.sendMessage(name+" "+w.getX()+" "+w.getY()+" "+w.getZ()+" "+w.getWorld()+" "+names);
				} catch (Exception e) {
					cm.sendMessage(ChatColor.RED+"Error loading shrine \""+name+"\".");
				}
			}
		} catch (NullPointerException ne) {
			cm.sendMessage(ChatColor.RED+"Shrines are missing/null.");
		}
		//All keys
		String keys = "";
		for (String key : DSave.getAllData(p).keySet())
			keys += key+", ";
		if (keys.length() > 0) keys = keys.substring(0, keys.length()-2);
		cm.sendMessage(ChatColor.YELLOW+"All keys in save: "+keys);
	}
	/**
	 * Used to print a player's data to console.
	 * @param cm
	 * @param p
	 */
	public static void printData(Logger cm, String p) {
		try {
			cm.info("Name: "+DUtil.getDemigodsPlayer(p));
		} catch (NullPointerException ne) {
			cm.warning(ChatColor.RED+"Name is missing/null.");
		}
		try {
			cm.info("Alliance: "+DUtil.getAllegiance(p));
		} catch (NullPointerException ne) {
			cm.warning(ChatColor.RED+"Alliance is missing/null.");
		}
		try {
			cm.info("Current HP: "+DUtil.getHP(p));
		} catch (NullPointerException ne) {
			cm.warning(ChatColor.RED+"HP is missing/null.");
		}
		try {
			cm.info("Max HP: "+DUtil.getMaxHP(p));
		} catch (NullPointerException ne) {
			cm.warning(ChatColor.RED+"Max HP is missing/null.");
		}
		try {
			cm.info("Current Favor: "+DUtil.getFavor(p));
		} catch (NullPointerException ne) {
			cm.warning(ChatColor.RED+"Favor is missing/null.");
		}
		try {
			cm.info("Max Favor: "+DUtil.getFavorCap(p));
		} catch (NullPointerException ne) {
			cm.warning(ChatColor.RED+"Max Favor is missing/null.");
		}
		try {
			String s = "";
			for (Deity d : DUtil.getDeities(p)) {
				String name = d.getName();
				try {
					s += " "+name+";"+DUtil.getDevotion(p, name);
				} catch (Exception e) {
					cm.warning(ChatColor.RED+"Error loading "+name+".");
				}
			}
			cm.info("Deities:"+s);
		} catch (NullPointerException ne) {
			cm.warning(ChatColor.RED+"Deities are missing/null.");
		}
		try {
			cm.info("Ascensions: "+DUtil.getAscensions(p));
		} catch (NullPointerException ne) {
			cm.warning(ChatColor.RED+"Ascensions are missing/null.");
		}
		try {
			cm.info("Kills: "+DUtil.getKills(p));
		} catch (NullPointerException ne) {
			cm.warning(ChatColor.RED+"Kills are missing/null.");
		}
		try {
			cm.info("Deaths: "+DUtil.getDeaths(p));
		} catch (NullPointerException ne) {
			cm.warning(ChatColor.RED+"Deaths are missing/null.");
		}
		try {
			cm.info("Accessible:");
			for (WriteLocation w : DUtil.getAccessibleShrines(p)) {
				String name = DUtil.getShrineName(w);
				try {
					cm.info(name+" "+w.getX()+" "+w.getY()+" "+w.getZ()+" "+w.getWorld());
				} catch (Exception e) {
					cm.info(ChatColor.RED+"Error loading "+name+".");
				}
			}
		} catch (NullPointerException ne) {
			cm.warning(ChatColor.RED+"Accessible shrines list is missing/null.");
		}
		//Bindings will be cleared
		//Effects will be cleared
		try {
			cm.info("Shrines:");
			for (String name : DUtil.getShrines(p).keySet()) {
				try {
					WriteLocation w = DUtil.getShrines(p).get(name);
					String names = "";
					for (String player : DUtil.getShrineGuestlist(w))
						names += player+" ";
					names = names.trim();
					cm.info(name+" "+w.getX()+" "+w.getY()+" "+w.getZ()+" "+w.getWorld()+" "+names);
				} catch (Exception e) {
					cm.warning(ChatColor.RED+"Error loading shrine \""+name+"\".");
				}
			}
		} catch (NullPointerException ne) {
			cm.warning(ChatColor.RED+"Shrines are missing/null.");
		}
		//All keys
		String keys = "";
		for (String key : DSave.getAllData(p).keySet())
			keys += key+", ";
		if (keys.length() > 0) keys = keys.substring(0, keys.length()-2);
		cm.info(ChatColor.YELLOW+"All keys in save: "+keys);
	}
	/**
	 * Saves a player's data as a text file.
	 * @param p
	 * @throws IOException
	 */
	public static void writeData(String p) throws IOException {
		Logger.getLogger("Minecraft").info("[Demigods] Writing debug data for "+p+"...");
		FileWriter f = new FileWriter(new File(DSave.getPlayerSavePath()+p+".txt"));
		try {
			f.write("Name: "+DUtil.getDemigodsPlayer(p)+"\r\n");
		} catch (NullPointerException ne) {
			f.write("Name: "+p+"/r/n");
		}
		try {
			f.write("Alliance: "+DUtil.getAllegiance(p)+"\r\n");
		} catch (NullPointerException ne) {
			f.write("Alliance: NULL"+"\r\n");
		}
		try {
			f.write("Current_HP: "+DUtil.getHP(p)+"\r\n");
		} catch (NullPointerException ne) {
			f.write("Current_HP: NULL"+"\r\n");
		}
		try {
			f.write("Max_HP: "+DUtil.getMaxHP(p)+"\r\n");
		} catch (NullPointerException ne) {
			f.write("Max_HP: NULL"+"\r\n");
		}
		try {
			f.write("Current_Favor: "+DUtil.getFavor(p)+"\r\n");
		} catch (NullPointerException ne) {
			f.write("Current_Favor: NULL"+"\r\n");
		}
		try {
			f.write("Max_Favor: "+DUtil.getFavorCap(p)+"\r\n");
		} catch (NullPointerException ne) {
			f.write("Max_Favor: NULL"+"\r\n");
		}
		try {
			String s = "";
			for (Deity d : DUtil.getDeities(p)) {
				String name = d.getName();
				try {
					s += " "+name+";"+DUtil.getDevotion(p, name);
				} catch (Exception e) {
					s += " "+name+";NULL";
				}
			}
			f.write("Deities:"+s+"\r\n");
		} catch (NullPointerException ne) {
			f.write("Deities:\r\n");
		}
		try {
			f.write("Ascensions: "+DUtil.getAscensions(p)+"\r\n");
		} catch (NullPointerException ne) {
			f.write("Ascensions: NULL"+"\r\n");
		}
		try {
			f.write("Kills: "+DUtil.getKills(p)+"\r\n");
		} catch (NullPointerException ne) {
			f.write("Kills: NULL"+"\r\n");
		}
		try {
			f.write("Deaths: "+DUtil.getDeaths(p)+"\r\n");
		} catch (NullPointerException ne) {
			f.write("Deaths: NULL"+"\r\n");
		}
		try {
			String s = "Accessible:\r\n";
			for (WriteLocation wr : DUtil.getAccessibleShrines(p)) {
				String name = DUtil.getShrineName(wr);
				try {
					s += name+" "+wr.getX()+" "+wr.getY()+" "+wr.getZ()+" "+wr.getWorld()+"\r\n";
				} catch (Exception e) {
					s += name+" "+"ERROR"+"\r\n";
				}
			}
			f.write(s);
		} catch (NullPointerException ne) {
			f.write("Accessible:\r\n");
		}
		//Bindings will be cleared on load
		//Effects will be cleared on load
		try {
			String s = "Shrines:\r\n";
			for (String name : DUtil.getShrines(p).keySet()) {
				try {
					WriteLocation w = DUtil.getShrines(p).get(name);
					String names = "";
					for (String player : DUtil.getShrineGuestlist(w))
						names += player+" ";
					names = names.trim();
					s += (name+" "+w.getX()+" "+w.getY()+" "+w.getZ()+" "+w.getWorld()+" "+names+"\r\n");
				} catch (Exception e) {
					s += (name+" "+"ERROR"+"\r\n");
				}
			}
			f.write(s);
		} catch (NullPointerException ne) {
			f.write("Shrines:\r\n");
		}
		//All keys
		String keys = "";
		for (String key : DSave.getAllData(p).keySet())
			keys += key+", ";
		if (keys.length() > 0) keys = keys.substring(0, keys.length()-2);
		f.write("All keys in save: "+keys);
		f.close();
		Logger.getLogger("Minecraft").info("[Demigods] Finished writing debug data.");
	}
	public static boolean loadData(String p) throws FileNotFoundException {
		try {
			File toread = new File(DSave.getPlayerSavePath()+p+".txt");
			if ((toread == null) || !toread.exists())
				return false;
			Scanner s = new Scanner(toread);
			//clear bindings
			DSave.removeData(p, "BINDINGS");
			DSave.saveData(p, "BINDINGS", new ArrayList<Material>());
			//clear effects
			DUtil.setActiveEffects(p, new HashMap<String, Long>());
			//check name
			String sname = s.nextLine();
			if (!sname.split(" ")[1].equals(p))
				return false;
			//alliance
			String alliance = s.nextLine();
			DSave.saveData(p, "ALLEGIANCE", alliance.split(" ")[1]);
			//hp
			String hp = s.nextLine();
			DSave.saveData(p, "dHP", Integer.parseInt(hp.split(" ")[1]));
			//maxhp
			String maxhp = s.nextLine();
			DSave.saveData(p, "dmaxHP", Integer.parseInt(maxhp.split(" ")[1]));
			//favor
			String favor = s.nextLine();
			DSave.saveData(p, "FAVOR", Integer.parseInt(favor.split(" ")[1]));
			//maxfavor
			String maxfavor = s.nextLine();
			DSave.saveData(p, "FAVORCAP", Integer.parseInt(maxfavor.split(" ")[1]));
			//deities
			String deity = s.nextLine();
			String[] deities = deity.split(" ");
			if (deities.length > 1) {
				DSave.removeData(p, "DEITIES");
				DSave.saveData(p, "DEITIES", new ArrayList<Deity>());
				for (int i=1;i<deities.length;i++) {
					String[] info = deities[i].split(";");
					DUtil.removeDeity(p, info[0]);
					if (info[0].equalsIgnoreCase("zeus"))
						DUtil.giveDeitySilent(p, new Zeus(p));
					else if (info[0].equalsIgnoreCase("poseidon"))
						DUtil.giveDeitySilent(p, new Poseidon(p));
					else if (info[0].equalsIgnoreCase("hades"))
						DUtil.giveDeitySilent(p, new Hades(p));
					else if (info[0].equalsIgnoreCase("Apollo"))
						DUtil.giveDeitySilent(p, new Apollo(p));
					else if (info[0].equalsIgnoreCase("Athena"))
						DUtil.giveDeitySilent(p, new Athena(p));
					else if (info[0].equalsIgnoreCase("Hephaestus"))
						DUtil.giveDeitySilent(p, new Hephaestus(p));
					else if (info[0].equalsIgnoreCase("Atlas"))
						DUtil.giveDeitySilent(p, new Atlas(p));
					else if (info[0].equalsIgnoreCase("ares"))
						DUtil.giveDeitySilent(p, new Ares(p));
					else if (info[0].equalsIgnoreCase("Cronus"))
						DUtil.giveDeitySilent(p, new Cronus(p));
					else if (info[0].equalsIgnoreCase("Hyperion"))
						DUtil.giveDeitySilent(p, new Hyperion(p));
					else if (info[0].equalsIgnoreCase("Oceanus"))
						DUtil.giveDeitySilent(p, new Oceanus(p));
					else if (info[0].equalsIgnoreCase("Prometheus"))
						DUtil.giveDeitySilent(p, new Prometheus(p));
					else if (info[0].equalsIgnoreCase("Rhea"))
						DUtil.giveDeitySilent(p, new Rhea(p));
					DUtil.setDevotion(p, info[0], Integer.parseInt(info[1]));
				}
			}
			//ascensions
			String ascensions = s.nextLine();
			DSave.saveData(p, "ASCENSIONS", Integer.parseInt(ascensions.split(" ")[1]));
			//kills
			String kills = s.nextLine();
			DSave.saveData(p, "KILLS", Integer.parseInt(kills.split(" ")[1]));
			//deaths
			String deaths = s.nextLine();
			DSave.saveData(p, "DEATHS", Integer.parseInt(deaths.split(" ")[1]));
			//accessible (guest list)
			if (s.nextLine().trim().equals("Accessible:")) {
				String in = s.nextLine();
				while (!in.trim().equals("Shrines:")) {
					String[] info = in.split(" ");
					WriteLocation shrine = new WriteLocation(info[4], Integer.parseInt(info[1]),
							Integer.parseInt(info[2]), Integer.parseInt(info[3]));
					DUtil.removeGuest(shrine, p);
					DUtil.addGuest(shrine, p);
					in = s.nextLine();
				}
			}
			String in = s.nextLine();
			while (in.trim().substring(0, 10).equals("All keys ")) {
				String[] info = in.split(" ");
				String name = info[0];
				WriteLocation shrine = new WriteLocation(info[4], Integer.parseInt(info[1]),
						Integer.parseInt(info[2]), Integer.parseInt(info[3]));
				DUtil.removeShrine(shrine);
				DUtil.addShrine(p, name, shrine);
				if (info.length > 5) {
					for (int i=5;i<info.length;i++) {
						DUtil.addGuest(shrine, DUtil.getDemigodsPlayer(info[i]));
					}
				}
				in = s.nextLine();
			}
			//shrines
			Logger.getLogger("Minecraft").info("[Demigods] Loaded "+p+"'s data from debug file.");
			DSave.save(Demigods.mainDirectory);
		} catch (Exception e) {
			Logger.getLogger("Minecraft").warning("[Demigods] Encountered a problem while loading "+p+"'s debug file.");
			e.printStackTrace();
			Logger.getLogger("Minecraft").warning("[Demigods] End stack trace.");
		}
		return true;
	}
}
