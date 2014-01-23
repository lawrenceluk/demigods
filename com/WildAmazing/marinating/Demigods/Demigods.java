package com.WildAmazing.marinating.Demigods;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

import com.WildAmazing.marinating.Demigods.Deities.Deity;
import com.WildAmazing.marinating.Demigods.Deities.Gods.*;
import com.WildAmazing.marinating.Demigods.Deities.Titans.*;

public class Demigods extends JavaPlugin implements Listener {
	public static Logger log = Logger.getLogger("Minecraft");
	static String mainDirectory = "plugins/Demigods/";
	DUtil initialize;
	DSave SAVE;
	static Deity[] deities = {
		new Cronus("ADMIN"),
		new Prometheus("ADMIN"),
		new Rhea("ADMIN"),
		new Atlas("ADMIN"),
		new Oceanus("ADMIN"),
		new Hyperion("ADMIN"),
		//
		new Zeus("ADMIN"),
		new Ares("ADMIN"),
		new Hades("ADMIN"),
		new Poseidon("ADMIN"),
		new Athena("ADMIN"),
		new Hephaestus("ADMIN")
	};

	public Demigods(){
		super();
	}

	@Override
	public void onEnable() {
		long firstTime = System.currentTimeMillis();
		log.info("[Demigods] Initializing.");
		new Settings(this); // #1 (needed for DUtil to load)
		log.info("[Demigods] Updating configuration.");
		initialize = new DUtil(this); // #2 (needed for everything else to work)
		SAVE = new DSave(mainDirectory, deities); // #3 (needed to start save system)
		loadListeners(); // #4
		loadCommands(); // #5 (needed)
		initializeThreads(); // #6 (regen and etc)
		updateSave(); // #7 (updates from older versions)
		log.info("[Demigods] Preparation completed in "+((double)(System.currentTimeMillis()-firstTime)/1000)+" seconds.");
	}

	@Override
	public void onDisable() {
		try {
			DSave.save(mainDirectory);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log.severe("[Demigods] Save location error. Screenshot the stack trace and send to marinating.");
		} catch (IOException e) {
			e.printStackTrace();
			log.severe("[Demigods] Save write error. Screenshot the stack trace and send to marinating.");
		}
		int c = 0;
		for (BukkitWorker bw : getServer().getScheduler().getActiveWorkers())
			if (bw.getOwner().equals(this))
				c++;
		for (BukkitTask bt : getServer().getScheduler().getPendingTasks())
			if (bt.getOwner().equals(this))
				c++;
		this.getServer().getScheduler().cancelTasks(this);
		log.info("[Demigods] Save completed and "+c+" tasks cancelled.");
	}

	@EventHandler
	public void saveOnExit(PlayerQuitEvent e) {
		if (DUtil.isFullParticipant(e.getPlayer()))
			try {
				DSave.save(mainDirectory);
			} catch (FileNotFoundException er) {
				er.printStackTrace();
				log.severe("[Demigods] Save location error. Screenshot the stack trace and send to marinating.");
			} catch (IOException er) {
				er.printStackTrace();
				log.severe("[Demigods] Save write error. Screenshot the stack trace and send to marinating.");
			}
	}

	public void loadCommands() {
		//for help files
		CommandManager ce = new CommandManager(this);
		//general
		getServer().getPluginManager().registerEvents(ce, this);
		getCommand("dg").setExecutor(ce);
		getCommand("check").setExecutor(ce);
		getCommand("claim").setExecutor(ce);
		getCommand("alliance").setExecutor(ce);
		getCommand("perks").setExecutor(ce);
		getCommand("checkplayer").setExecutor(ce);
		getCommand("removeplayer").setExecutor(ce);
		getCommand("value").setExecutor(ce);
		getCommand("bindings").setExecutor(ce);
		getCommand("debugplayer").setExecutor(ce);

		getCommand("setallegiance").setExecutor(ce);

		getCommand("getfavor").setExecutor(ce);
		getCommand("setfavor").setExecutor(ce);
		getCommand("addfavor").setExecutor(ce);

		getCommand("getmaxfavor").setExecutor(ce);
		getCommand("setmaxfavor").setExecutor(ce);
		getCommand("addmaxfavor").setExecutor(ce);

		getCommand("givedeity").setExecutor(ce);
		getCommand("removedeity").setExecutor(ce);
		getCommand("forsake").setExecutor(ce);

		getCommand("getdevotion").setExecutor(ce);
		getCommand("setdevotion").setExecutor(ce);
		getCommand("adddevotion").setExecutor(ce);

		getCommand("addhp").setExecutor(ce);
		getCommand("sethp").setExecutor(ce);

		getCommand("setmaxhp").setExecutor(ce);

		getCommand("getascensions").setExecutor(ce);
		getCommand("setascensions").setExecutor(ce);
		getCommand("addascensions").setExecutor(ce);

		getCommand("setkills").setExecutor(ce);
		getCommand("setdeaths").setExecutor(ce);

		//shrine
		getCommand("shrine").setExecutor(ce);
		getCommand("shrinewarp").setExecutor(ce);
		getCommand("shrineowner").setExecutor(ce);
		getCommand("removeshrine").setExecutor(ce);
		getCommand("fixshrine").setExecutor(ce);
		getCommand("nameshrine").setExecutor(ce);
		//zeus
		getCommand("shove").setExecutor(ce);
		getCommand("lightning").setExecutor(ce);
		getCommand("storm").setExecutor(ce);
		//ares
		getCommand("strike").setExecutor(ce);
		getCommand("bloodthirst").setExecutor(ce);
		getCommand("crash").setExecutor(ce);
		//cronus
		getCommand("slow").setExecutor(ce);
		getCommand("cleave").setExecutor(ce);
		getCommand("timestop").setExecutor(ce);
		//prometheus
		getCommand("fireball").setExecutor(ce);
		getCommand("blaze").setExecutor(ce);
		getCommand("firestorm").setExecutor(ce);
		//rhea
		getCommand("poison").setExecutor(ce);
		getCommand("plant").setExecutor(ce);
		getCommand("detonate").setExecutor(ce);
		getCommand("entangle").setExecutor(ce);
		//hades
		getCommand("chain").setExecutor(ce);
		getCommand("entomb").setExecutor(ce);
		getCommand("tartarus").setExecutor(ce);
		//poseidon
		getCommand("reel").setExecutor(ce);
		getCommand("drown").setExecutor(ce);
		getCommand("waterfall").setExecutor(ce);
		//atlas
		getCommand("unburden").setExecutor(ce);
		getCommand("invincible").setExecutor(ce);
		//athena
		getCommand("flash").setExecutor(ce);
		getCommand("ceasefire").setExecutor(ce);
		//oceanus
		getCommand("squid").setExecutor(ce);
		getCommand("makeitrain").setExecutor(ce);
		//hyperion
		getCommand("starfall").setExecutor(ce);
		getCommand("smite").setExecutor(ce);
		//hephaestus
		getCommand("reforge").setExecutor(ce);
		getCommand("shatter").setExecutor(ce);
	}

	public void loadListeners(){
		getServer().getPluginManager().registerEvents(new ShrineManager(), this);
		getServer().getPluginManager().registerEvents(new DeityManager(), this);
		getServer().getPluginManager().registerEvents(new LevelManager(), this);
		getServer().getPluginManager().registerEvents(new PVPManager(), this);
		getServer().getPluginManager().registerEvents(new DamageHandler(), this);
		getServer().getPluginManager().registerEvents(new Hephaestus("LISTENER"), this);
	}

	private void initializeThreads() {
		int startdelay = (int)(Settings.getSettingDouble("start_delay_seconds")*20);
		int favorfrequency = (int)(Settings.getSettingDouble("favor_regen_seconds")*20);
		int hpfrequency = (int)(Settings.getSettingDouble("hp_regen_seconds")*20);
		int savefrequency = Settings.getSettingInt("save_interval_seconds")*20;
		if (hpfrequency < 0) hpfrequency = 600;
		if (favorfrequency < 0) favorfrequency = 600;
		if (startdelay <= 0) startdelay = 1;
		if (savefrequency <= 0) savefrequency = 300;
		//favor
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				for (World w : Settings.getEnabledWorlds()) {
					for (Player p : w.getPlayers())
						if (DUtil.isFullParticipant(p)) {
							int regenrate = DUtil.getAscensions(p); //TODO: PERK UPGRADES THIS
							if (regenrate < 1) regenrate = 1;
							DUtil.setFavor(p, DUtil.getFavor(p)+regenrate);
						}
				}
			}
		}, startdelay, favorfrequency);
		//health regen
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				for (World w : Settings.getEnabledWorlds()) {
					for (Player p : w.getPlayers())
						if (DUtil.isFullParticipant(p)) {
							if ((p.getHealth() < 1) || (DUtil.getHP(p) < 1)) continue;
							int heal = 1; //TODO: PERK UPGRADES THIS
							if (heal < 1) heal = 1;
							if (DUtil.getHP(p) < DUtil.getMaxHP(p))
								DUtil.setHP(p, DUtil.getHP(p)+heal);
						}
				}
			}
		}, startdelay, hpfrequency);
		//health sync
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				for (World w : Settings.getEnabledWorlds()) {
					for (Player p : w.getPlayers())
						if (DUtil.isFullParticipant(p))
							if (p.getHealth() > 0)
								DamageHandler.syncHealth(p);
				}
			}
		}, startdelay, 5);
		//data save
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				try {
					DSave.save(mainDirectory);
					log.info("[Demigods] Saved data for "+DUtil.getFullParticipants().size()+" Demigods players. "+DSave.getCompleteData().size()+" files total.");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					log.severe("[Demigods] Save location error. Screenshot the stack trace and send to marinating.");
				} catch (IOException e) {
					e.printStackTrace();
					log.severe("[Demigods] Save write error. Screenshot the stack trace and send to marinating.");
				}
			}
		}, startdelay, savefrequency);
		//information display
		int frequency = (int)(Settings.getSettingDouble("stat_display_frequency_in_seconds")*20);
		if (frequency > 0) {
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
				@Override
				public void run() {
					for (World w : Settings.getEnabledWorlds()) {
						for (Player p : w.getPlayers())
							if (DUtil.isFullParticipant(p))
								if (p.getHealth() > 0) {
									ChatColor color = ChatColor.GREEN;
									if ((DUtil.getHP(p)/(double)DUtil.getMaxHP(p)) < 0.25) color = ChatColor.RED;
									else if ((DUtil.getHP(p)/(double)DUtil.getMaxHP(p)) < 0.5) color = ChatColor.YELLOW;
									String str = "-- HP "+color+""+DUtil.getHP(p)+"/"+DUtil.getMaxHP(p)+ChatColor.YELLOW+" Favor "+DUtil.getFavor(p)+"/"+
									DUtil.getFavorCap(p);
									p.sendMessage(str);
								}
					}
				}
			}, startdelay, frequency);
		}
	}

	private void updateSave() {
		//clean things that may cause glitches
		for (String player : DUtil.getFullParticipants()) {
			for (Deity d : DUtil.getDeities(player)) {
				if (DSave.hasData(player, d.getName().toUpperCase()+"_TRIBUTE_")) {
					DSave.removeData(player, d.getName().toUpperCase()+"_TRIBUTE_");
				}
			}
		}
		//updating to 1.1
		HashMap<String, HashMap<String, Object>> copy = DSave.getCompleteData();
		String updated = "[Demigods] Updated players:";
		boolean yes = false;
		for (String player : DSave.getCompleteData().keySet()) {
			if (DSave.hasData(player, "dEXP")) { //coming from pre 1.1
				yes = true;
				copy.get(player).remove("dEXP");
				if (DSave.hasData(player, "LEVEL"))
					copy.get(player).remove("LEVEL");
				for (Deity d : DUtil.getDeities(player)) {
					copy.get(player).put(d.getName()+"_dvt", (int)Math.ceil((500*Math.pow(DUtil.getAscensions(player), 1.98))/DUtil.getDeities(player).size()));
				}
				if (!DSave.hasData(player, "A_EFFECTS"))
					DUtil.setActiveEffects(player, new HashMap<String, Long>());
				if (!DSave.hasData(player, "P_SHRINES"))
					DUtil.setShrines(player, new HashMap<String, WriteLocation>());
				updated += " "+player;
			}
		}
		if (yes)
			log.info(updated);
		DSave.overwrite(copy);
		for (String player : DSave.getCompleteData().keySet())
			LevelManager.levelProcedure(player);
	}
}
