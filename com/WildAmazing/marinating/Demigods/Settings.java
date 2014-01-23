package com.WildAmazing.marinating.Demigods;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.World;

public class Settings {

	static Demigods plugin;

	public Settings(Demigods instance) {
		plugin = instance;
		plugin.getConfig().options().copyDefaults(true);
		plugin.saveConfig();
	}
	public static int getSettingInt(String id) {
		if (plugin.getConfig().isInt(id))
			return plugin.getConfig().getInt(id);
		else return -1;
	}
	public static String getSettingString(String id) {
		if (plugin.getConfig().isString(id))
			return plugin.getConfig().getString(id);
		else return null;
	}
	public static boolean getSettingBoolean(String id) {
		if (plugin.getConfig().isBoolean(id))
			return plugin.getConfig().getBoolean(id);
		else return true;
	}
	public static double getSettingDouble(String id) {
		if (plugin.getConfig().isDouble(id))
			return plugin.getConfig().getDouble(id);
		else return -1;
	}
	public static List<World> getEnabledWorlds() {
		if (!plugin.getConfig().isList("active_worlds"))
			enableWorlds();
		else if (plugin.getConfig().getStringList("active_worlds").contains("DEFAULT"))
			enableWorlds();
		ArrayList<World> worlds = new ArrayList<World>();
		for (String s : plugin.getConfig().getStringList("active_worlds")) {
			worlds.add(plugin.getServer().getWorld(s));
		}
		return worlds;
	}
	public static void enableWorlds() {
		ArrayList<String> worlds = new ArrayList<String>();
		for (World w : plugin.getServer().getWorlds())
			worlds.add(w.getName());
		plugin.getConfig().set("active_worlds", worlds);
		plugin.saveConfig();
	}
}