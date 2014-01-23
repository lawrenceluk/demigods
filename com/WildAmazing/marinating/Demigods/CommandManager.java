package com.WildAmazing.marinating.Demigods;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.WildAmazing.marinating.Demigods.Deities.Deity;
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

public class CommandManager implements CommandExecutor, Listener {
	Demigods plugin;
	double ADVANTAGEPERCENT = 1.3;
	boolean BALANCETEAMS = Settings.getSettingBoolean("balance_teams");

	public CommandManager(Demigods d) {
		plugin = d;
	}
	@EventHandler (priority = EventPriority.MONITOR)
	public void chatCommands(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if (!DUtil.isFullParticipant(p))
			return;
		if (e.getMessage().contains("qd")) {
			if ((e.getMessage().charAt(0) == 'q') && (e.getMessage().charAt(1) == 'd')) {
				String str;
				if (p.getHealth() > 0) {
					ChatColor color = ChatColor.GREEN;
					if ((DUtil.getHP(p)/(double)DUtil.getMaxHP(p)) < 0.25) color = ChatColor.RED;
					else if ((DUtil.getHP(p)/(double)DUtil.getMaxHP(p)) < 0.5) color = ChatColor.YELLOW;
					str = "-- Your HP "+color+""+DUtil.getHP(p)+"/"+DUtil.getMaxHP(p)+ChatColor.YELLOW+" Favor "+DUtil.getFavor(p)+"/"+
					DUtil.getFavorCap(p);
					if (DUtil.getActiveEffects(p.getName()).size() > 0) {
						HashMap<String, Long> effects = DUtil.getActiveEffects(p.getName());
						str += ChatColor.WHITE+" Active effects:";
						for (String stt : effects.keySet())
							str += " "+stt+"["+((effects.get(stt)-System.currentTimeMillis())/1000)+"s]";
					}
					try {
						String other = e.getMessage().split(" ")[1];
						if (other != null)
							other = DUtil.getDemigodsPlayer(other);
						if ((other != null) && DUtil.isFullParticipant(other)) {
							p.sendMessage(other+" -- "+DUtil.getAllegiance(other));
							if (DUtil.hasDeity(p, "Athena")) {
								String st = ChatColor.GRAY+"Deities:";
								for (Deity d : DUtil.getDeities(other))
									st += " "+d.getName();
								p.sendMessage(st);
								p.sendMessage(ChatColor.GRAY+"HP "+DUtil.getHP(other)+"/"+DUtil.getMaxHP(other)+" Favor "+DUtil.getFavor(other)+"/"+
										DUtil.getFavorCap(other));
								if (DUtil.getActiveEffects(other).size() > 0) {
									HashMap<String, Long> fx = DUtil.getActiveEffects(other);
									str += ChatColor.GRAY+" Active effects:";
									for (String stt : fx.keySet())
										str += " "+stt+"["+((fx.get(stt)-System.currentTimeMillis())/1000)+"s]";
								}
							}
						}
					} catch (Exception invalid) {}
					p.sendMessage(str);
					e.getRecipients().clear();
					e.setCancelled(true);
				}
			}
		}
	}
	@Override
	public boolean onCommand(CommandSender sender, Command c, String label, String[] args) {
		Player p = null;
		if (sender instanceof Player) p = (Player)sender;
		if (p == null) {
			if (c.getName().equalsIgnoreCase("setfavor")) {
				if (args.length != 2) return false;
				try {
					String pl = DUtil.getDemigodsPlayer(args[0]);
					int amt = Integer.parseInt(args[1]);
					DUtil.setFavor(pl, amt);
					Logger.getLogger("Minecraft").info("[Demigods] Set "+pl+"'s Favor to "+amt+".");
				} catch (Exception error) {
					Logger.getLogger("Minecraft").warning("[Demigods] Unable to parse command.");
					return false;
				}
			} else if (c.getName().equalsIgnoreCase("getfavor")) {
				if (args.length != 1) return false;
				try {
					String pl = DUtil.getDemigodsPlayer(args[0]);
					Logger.getLogger("Minecraft").info(DUtil.getFavor(pl)+"");
				} catch (Exception error) {
					Logger.getLogger("Minecraft").warning("[Demigods] Unable to parse command.");
					return false;
				}
			} else if (c.getName().equalsIgnoreCase("addfavor")) {
				if (args.length != 2) return false;
				try {
					String pl = DUtil.getDemigodsPlayer(args[0]);
					int amt = Integer.parseInt(args[1]);
					DUtil.setFavor(pl, amt+DUtil.getFavor(pl));
					Logger.getLogger("Minecraft").info("[Demigods] Increased "+pl+"'s Favor by "+amt+" to "+DUtil.getFavor(pl)+".");
				} catch (Exception error) {
					Logger.getLogger("Minecraft").warning("[Demigods] Unable to parse command.");
					return false;
				}
			} else if (c.getName().equalsIgnoreCase("setmaxfavor")) {
				if (args.length != 2) return false;
				try {
					String pl = DUtil.getDemigodsPlayer(args[0]);
					int amt = Integer.parseInt(args[1]);
					DUtil.setFavorCap(pl, amt);
					Logger.getLogger("Minecraft").info("[Demigods] Set "+pl+"'s max Favor to "+amt+".");
				} catch (Exception error) {
					Logger.getLogger("Minecraft").warning("[Demigods] Unable to parse command.");
					return false;
				}
			} else if (c.getName().equalsIgnoreCase("getmaxfavor")) {
				if (args.length != 1) return false;
				try {
					String pl = DUtil.getDemigodsPlayer(args[0]);
					Logger.getLogger("Minecraft").info(DUtil.getFavorCap(pl)+"");
				} catch (Exception error) {
					Logger.getLogger("Minecraft").warning("[Demigods] Unable to parse command.");
					return false;
				}
			} else if (c.getName().equalsIgnoreCase("addmaxfavor")) {
				if (args.length != 2) return false;
				try {
					String pl = DUtil.getDemigodsPlayer(args[0]);
					int amt = Integer.parseInt(args[1]);
					DUtil.setFavorCap(pl, amt+DUtil.getFavor(pl));
					Logger.getLogger("Minecraft").info("[Demigods] Increased "+pl+"'s max Favor by "+amt+" to "+DUtil.getFavor(pl)+".");
				} catch (Exception error) {
					Logger.getLogger("Minecraft").warning("[Demigods] Unable to parse command.");
					return false;
				}
			} else if (c.getName().equalsIgnoreCase("getascensions")) {
				if (args.length != 1) return false;
				try {
					String pl = DUtil.getDemigodsPlayer(args[0]);
					Logger.getLogger("Minecraft").info(DUtil.getAscensions(pl)+"");
				} catch (Exception error) {
					Logger.getLogger("Minecraft").warning("[Demigods] Unable to parse command.");
					return false;
				}
			} else if (c.getName().equalsIgnoreCase("setascensions")) {
				if (args.length != 2) return false;
				try {
					String target = DUtil.getDemigodsPlayer(args[0]);
					int amt = Integer.parseInt(args[1]);
					DUtil.setAscensions(target, amt);
					long oldtotal = DUtil.getDevotion(target);
					int newtotal = DUtil.costForNextAscension(amt-1);
					for (Deity d : DUtil.getDeities(target)) {
						int devotion = DUtil.getDevotion(target, d);
						DUtil.setDevotion(target, d, (int)Math.ceil((newtotal*1.0*devotion)/oldtotal));
					}
					Logger.getLogger("Minecraft").info("[Demigods] Set "+target+"'s ascensions to "+amt+".");
				} catch (Exception error) {
					Logger.getLogger("Minecraft").warning("[Demigods] Unable to parse command.");
					return false;
				}
			} else if (c.getName().equalsIgnoreCase("addascensions")) {
				if (args.length != 2) return false;
				try {
					String target = DUtil.getDemigodsPlayer(args[0]);
					int amt = Integer.parseInt(args[1]);
					DUtil.setAscensions(target, DUtil.getAscensions(target)+amt);
					long oldtotal = DUtil.getDevotion(target);
					int newtotal = DUtil.costForNextAscension(amt-1);
					for (Deity d : DUtil.getDeities(target)) {
						int devotion = DUtil.getDevotion(target, d);
						DUtil.setDevotion(target, d, (int)Math.ceil((newtotal*1.0*devotion)/oldtotal));
					}
					Logger.getLogger("Minecraft").info("[Demigods] Increased "+target+"'s ascensions by "+amt+" to "+DUtil.getAscensions(target)+".");
				} catch (Exception error) {
					Logger.getLogger("Minecraft").severe(error.getMessage());
					Logger.getLogger("Minecraft").warning("[Demigods] Unable to parse command.");
					return false;
				}
			} else if (c.getName().equalsIgnoreCase("addhp")) {
				if (args.length != 2) return false;
				try {
					String pl = DUtil.getDemigodsPlayer(args[0]);
					int amt = Integer.parseInt(args[1]);
					DUtil.setHP(pl, amt+DUtil.getHP(pl));
					Logger.getLogger("Minecraft").info("[Demigods] Increased "+pl+"'s hp by "+amt+" to "+DUtil.getHP(pl)+".");
				} catch (Exception error) {
					Logger.getLogger("Minecraft").warning("[Demigods] Unable to parse command.");
					return false;
				}
			} else if (c.getName().equalsIgnoreCase("getdevotion")) {
				if (args.length == 1) {
					try {
						String pl = DUtil.getDemigodsPlayer(args[0]);
						Logger.getLogger("Minecraft").info(DUtil.getDevotion(pl)+"");
					} catch (Exception error) {
						Logger.getLogger("Minecraft").warning("[Demigods] Unable to parse command.");
						return false;
					}
				} else if (args.length == 2) {
					try {
						String pl = DUtil.getDemigodsPlayer(args[0]);
						Deity deity = DUtil.getDeity(pl, args[1]);
						Logger.getLogger("Minecraft").info(DUtil.getDevotion(pl, deity)+"");
					} catch (Exception error) {
						Logger.getLogger("Minecraft").warning("[Demigods] Unable to parse command.");
						return false;
					}
				} else return false;
			} else if (c.getName().equalsIgnoreCase("setdevotion")) {
				if (args.length != 3) return false;
				try {
					String pl = DUtil.getDemigodsPlayer(args[0]);
					Deity deity = DUtil.getDeity(pl, args[1]);
					int amt = Integer.parseInt(args[2]);
					DUtil.setDevotion(pl, deity, amt);
					Logger.getLogger("Minecraft").info("[Demigods] Set "+pl+"'s devotion for "+deity.getName()+" to "+amt+".");
				} catch (Exception error) {
					Logger.getLogger("Minecraft").warning("[Demigods] Unable to parse command.");
					return false;
				}
			} else if (c.getName().equalsIgnoreCase("adddevotion")) {
				if (args.length != 3) return false;
				try {
					String pl = DUtil.getDemigodsPlayer(args[0]);
					Deity deity = DUtil.getDeity(pl, args[1]);
					int amt = Integer.parseInt(args[2]);
					int before = DUtil.getDevotion(pl, deity);
					DUtil.setDevotion(pl, deity, before+amt);
					Logger.getLogger("Minecraft").info("[Demigods] Increased "+pl+"'s devotion for "+deity.getName()+" by "+amt+" to "+DUtil.getDevotion(pl, deity)+".");
				} catch (Exception error) {
					Logger.getLogger("Minecraft").warning("[Demigods] Unable to parse command.");
					return false;
				}
			} else if (c.getName().equalsIgnoreCase("debugplayer")) {
				if (args.length == 1) {
					String player = DUtil.getDemigodsPlayer(args[0]);
					if (player == null) {
						Logger.getLogger("Minecraft").info("[Demigods] Player not found.");
						return true;
					}
					DebugManager.printData(Logger.getLogger("Minecraft"), player);
				} else if (args.length == 2) {
					if (args[1].equalsIgnoreCase("print")) {
						String player = DUtil.getDemigodsPlayer(args[0]);
						if (player == null) {
							Logger.getLogger("Minecraft").info("[Demigods] Player not found.");
							return true;
						}
						DebugManager.printData(Logger.getLogger("Minecraft"), player);
					} else if (args[1].equalsIgnoreCase("write")) {
						String player = DUtil.getDemigodsPlayer(args[0]);
						if (player == null) {
							Logger.getLogger("Minecraft").info("[Demigods] Player not found.");
							return true;
						}
						try {
							DebugManager.writeData(player);
						} catch (IOException e) {
							Logger.getLogger("Minecraft").warning("[Demigods] Error writing debug for "+player+".");
							e.printStackTrace();
							Logger.getLogger("Minecraft").warning("[Demigods] End stack trace for debug.");
						}
					} else if (args[1].equalsIgnoreCase("load")) {
						String player = DUtil.getDemigodsPlayer(args[0]);
						if (player == null) {
							Logger.getLogger("Minecraft").info("[Demigods] Player not found.");
							return true;
						}
						try {
							if (DebugManager.loadData(player))
								Logger.getLogger("Minecraft").info("[Demigods] "+player+" successfully loaded into save from file.");
							else Logger.getLogger("Minecraft").info("[Demigods] "+player+"'s debug file was not found. Create one with debugplayer "+player+" write.");
						} catch (IOException e) {
							Logger.getLogger("Minecraft").warning("[Demigods] Error loading from file for "+player+".");
							e.printStackTrace();
							Logger.getLogger("Minecraft").warning("[Demigods] End stack trace for debug.");
						}
					}
				}
			}
			return true;
		} else {
			if (!Settings.getEnabledWorlds().contains(p.getWorld())) {
				p.sendMessage(ChatColor.YELLOW+"Demigods is not enabled in your world.");
				return true;
			}
			if (c.getName().equalsIgnoreCase("dg")) {
				if ((args.length == 1) && args[0].equals("db") && p.getName().equals("WildAmazing")) {
					p.sendMessage("-DEBUG-");
					p.sendMessage(p.getItemInHand().getDurability()+"");
					p.getItemInHand().setDurability((short)200);
				}
				if ((args.length == 2) || (args.length == 3)) {
					if (args[0].equalsIgnoreCase("debug") && DUtil.hasPermissionOrOP(p, "demigods.admin")) {
						String target = DUtil.getDemigodsPlayer(args[1]);
						if (args.length == 3) {
							if (args[2].equalsIgnoreCase("write")) {
								if (target == null) {
									p.sendMessage(ChatColor.YELLOW+"Player not found.");
									return true;
								}
								try {
									p.sendMessage(ChatColor.YELLOW+"Writing debug data for "+target+"...");
									DebugManager.writeData(target);
								} catch (IOException e) {
									Logger.getLogger("Minecraft").warning("[Demigods] Error writing debug data for "+target+".");
									e.printStackTrace();
									Logger.getLogger("Minecraft").warning("[Demigods] End stack trace.");
									p.sendMessage(ChatColor.RED+"Error writing data. Check the log for a stack trace.");
								}
								p.sendMessage(ChatColor.YELLOW+"Debug data for "+target+" should have been written to file.");
							} else if (args[2].equalsIgnoreCase("load")){
								if (target == null) {
									p.sendMessage(ChatColor.YELLOW+"Player not found.");
									return true;
								}
								try {
									if (DebugManager.loadData(target))
										p.sendMessage(ChatColor.YELLOW+"Loaded data for "+target+" into save from file.");
									else {
										p.sendMessage(ChatColor.YELLOW+"Failed to locate a debug file for "+target+".");
										p.sendMessage(ChatColor.YELLOW+"You can create one with /dg debug "+target+" write.");
									}
								} catch (IOException e) {
									Logger.getLogger("Minecraft").warning("[Demigods] Error writing debug data for "+target+".");
									e.printStackTrace();
									Logger.getLogger("Minecraft").warning("[Demigods] End stack trace.");
									p.sendMessage(ChatColor.RED+"Error writing data. Check the log for a stack trace.");
								}
								p.sendMessage(ChatColor.YELLOW+"Debug data for "+target+" should have been written to file.");
							}
						} else {
							if (target == null) {
								p.sendMessage(ChatColor.YELLOW+"Player not found.");
								return true;
							}
							DebugManager.printData(p, target);
						}
					}
				}
				if (args.length == 0) {
					p.sendMessage(ChatColor.YELLOW+"[Demigods] Information Directory");
					p.sendMessage(ChatColor.GRAY+"/dg god");
					p.sendMessage(ChatColor.GRAY+"/dg titan");
					p.sendMessage(ChatColor.GRAY+"/dg claim");
					p.sendMessage(ChatColor.GRAY+"/dg shrine");
					p.sendMessage(ChatColor.GRAY+"/dg tribute");
					p.sendMessage(ChatColor.GRAY+"/dg player");
					p.sendMessage(ChatColor.GRAY+"/dg pvp");
					p.sendMessage(ChatColor.GRAY+"/dg stats");
					p.sendMessage(ChatColor.GRAY+"/dg rankings");
					p.sendMessage("To see your own information, use "+ChatColor.YELLOW+"/check");
					p.sendMessage(ChatColor.DARK_AQUA+"Support this plugin with a donation at http://bit.ly/helpdemigods");
				}
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("god")) {
						p.sendMessage(ChatColor.YELLOW+"[Demigods] God Help File");
						p.sendMessage(ChatColor.GRAY+"For more information on the Gods, use /dg <name>");
						p.sendMessage(ChatColor.GOLD+"----Tier 1");
						p.sendMessage(ChatColor.GRAY+"Zeus - God of lightning and air.");
						p.sendMessage(ChatColor.GRAY+"Poseidon - God of the seas.");
						p.sendMessage(ChatColor.GRAY+"Hades - God of the underworld.");
						p.sendMessage(ChatColor.GOLD+"----Tier 2");
						p.sendMessage(ChatColor.GRAY+"Ares - God of war.");
						p.sendMessage(ChatColor.GRAY+"Athena - Goddess of wisdom.");
						p.sendMessage(ChatColor.GRAY+"Hephaestus - God of the forge.");
						/*
					p.sendMessage(ChatColor.GRAY+"Apollo - God of archery and healing.");
					p.sendMessage(ChatColor.GOLD+"----Tier 3");
					p.sendMessage(ChatColor.GRAY+"Artemis - Goddess of the hunt.");
					p.sendMessage(ChatColor.GRAY+"Demeter - Goddess of the harvest.");
					p.sendMessage(ChatColor.GRAY+"Dionysus - God of wine.");
					p.sendMessage(ChatColor.GRAY+"Hermes - God of travel and thievery.");
					p.sendMessage(ChatColor.GRAY+"Hestia - Goddess of cooking and the home.");
						 */
					} else if (args[0].equalsIgnoreCase("titan")) {
						p.sendMessage(ChatColor.YELLOW+"[Demigods] Titan Help File");
						p.sendMessage(ChatColor.GRAY+"For more information on the Titans, use /dg <name>");
						p.sendMessage(ChatColor.GOLD+"----Tier 1");
						p.sendMessage(ChatColor.GRAY+"Cronus - Titan of time.");
						p.sendMessage(ChatColor.GRAY+"Rhea - Titaness of nature.");
						p.sendMessage(ChatColor.GRAY+"Prometheus - Titan of fire.");
						p.sendMessage(ChatColor.GOLD+"----Tier 2");
						p.sendMessage(ChatColor.GRAY+"Atlas - Titan of enduring.");
						p.sendMessage(ChatColor.GRAY+"Oceanus - Titan of the oceans.");
						p.sendMessage(ChatColor.GRAY+"Hyperion - Titan of light.");
						/*
					p.sendMessage(ChatColor.GRAY+"Themis - Titaness of order and foresight.");
					p.sendMessage(ChatColor.GOLD+"----Tier 3");
					p.sendMessage(ChatColor.GRAY+"Lelantos - Titan of the predator.");
					p.sendMessage(ChatColor.GRAY+"Perses - Titan of destruction.");
					p.sendMessage(ChatColor.GRAY+"Styx - Titan of death.");
					p.sendMessage(ChatColor.GRAY+"Helios - Titan of the sun and keeper of oaths.");
					p.sendMessage(ChatColor.GRAY+"Koios - Titan of intelligence.");
						 */
					} else if (args[0].equalsIgnoreCase("claim")) {
						p.sendMessage(ChatColor.YELLOW+"[Demigods] Claim Help File");
						p.sendMessage(ChatColor.GRAY+"To claim your first deity, use "+ChatColor.YELLOW+"/claim"+ChatColor.GRAY+" with");
						p.sendMessage(ChatColor.GRAY+"a 'select item' in your hand. The 'select item' varies for each");
						p.sendMessage(ChatColor.GRAY+"deity and can be found at /dg <deity name>.");
					} else if (args[0].equalsIgnoreCase("shrine")) {
						p.sendMessage(ChatColor.YELLOW+"[Demigods] Shrine Help File");
						p.sendMessage(ChatColor.GRAY+"You may have one shrine per deity you are allied to.");
						p.sendMessage(ChatColor.GRAY+"Shrines serve two purposes: tributing and warps.");
						p.sendMessage(ChatColor.GRAY+"Read /dg tribute for more information about tributes.");
						p.sendMessage(ChatColor.GRAY+"Warp to a specific deity's shrine using /shrinewarp <deity>.");
						p.sendMessage(ChatColor.GRAY+"You may also give a shrine a specific name.");
						p.sendMessage(ChatColor.GRAY+"To create a shrine, place a sign with the following text:");
						p.sendMessage(ChatColor.GRAY+"        shrine        ");
						p.sendMessage(ChatColor.GRAY+"       dedicate       ");
						p.sendMessage(ChatColor.GRAY+"     <deity name>     ");
						p.sendMessage(ChatColor.GRAY+"<optional shrine name>");
						p.sendMessage(ChatColor.GRAY+"Then right click the sign to \"activate\" it.");
						p.sendMessage(ChatColor.GRAY+"The following commands are used when standing near a shrine:");
						p.sendMessage(ChatColor.YELLOW+"/shrinewarp"+ChatColor.GRAY+" - warp to a shrine with the given name");
						p.sendMessage(ChatColor.YELLOW+"/shrineowner add|remove|set"+ChatColor.GRAY+" - commands to allow/unallow");
						p.sendMessage(ChatColor.GRAY+"other players to warp to a shrine that you created");
						p.sendMessage(ChatColor.YELLOW+"/removeshrine"+ChatColor.GRAY+" - removes a shrine you created, costs Devotion");
						p.sendMessage(ChatColor.YELLOW+"/nameshrine"+ChatColor.GRAY+" - rename a shrine you created");
						p.sendMessage(ChatColor.GRAY+"For information about your shrines, use "+ChatColor.YELLOW+"/shrine");
					} else if (args[0].equalsIgnoreCase("tribute")) {
						p.sendMessage(ChatColor.YELLOW+"[Demigods] Tribute Help File");
						p.sendMessage(ChatColor.GRAY+"Tributing is the only way to raise your Favor cap, which");
						p.sendMessage(ChatColor.GRAY+"allows you to stockpile Favor for skills. Tributing may occur");
						p.sendMessage(ChatColor.GRAY+"at any shrine that belongs to a deity you are allied with.");
						p.sendMessage(ChatColor.GRAY+"To tribute, simply right click the gold block that marks the");
						p.sendMessage(ChatColor.GRAY+"shrine's center and place the items you wish to tribute in the");
						p.sendMessage(ChatColor.GRAY+"\"Tributes\" inventory.");
						p.sendMessage(ChatColor.GRAY+"A bonus of Devotion is given to the owner of a shrine when any");
						p.sendMessage(ChatColor.GRAY+"player makes a tribute there, so for best results tribute at");
						p.sendMessage(ChatColor.GRAY+"your own shrines.");
					} else if (args[0].equalsIgnoreCase("player")) {
						p.sendMessage(ChatColor.YELLOW+"[Demigods] Player Help File");
						p.sendMessage(ChatColor.GRAY+"As a player, you may choose to ally with the Gods or");
						p.sendMessage(ChatColor.GRAY+"the Titans. Once you have made an allegiance, you may");
						p.sendMessage(ChatColor.GRAY+"not break it without forsaking all the deities you have.");
						p.sendMessage(ChatColor.GRAY+"The three major attributes you have are:");
						p.sendMessage(ChatColor.YELLOW+"Favor "+ChatColor.GRAY+"- A measure of power and a divine currency");
						p.sendMessage(ChatColor.GRAY+"that can be spent by using skills or upgrading perks.");
						p.sendMessage(ChatColor.GRAY+"Favor regenerates whenever you are logged on.");
						p.sendMessage(ChatColor.YELLOW+"Devotion "+ChatColor.GRAY+"- A measure of how much power a deity gives you.");
						p.sendMessage(ChatColor.GRAY+"Stronger Devotion to a deity grants you increased power when.");
						p.sendMessage(ChatColor.GRAY+"using their skills.");
						p.sendMessage(ChatColor.GRAY+"Gained by dealing damage, exploring, and harvesting blocks.");
						p.sendMessage(ChatColor.YELLOW+"Ascensions "+ChatColor.GRAY+"- ");
						p.sendMessage(ChatColor.GRAY+"Ascensions unlock deities. More in progress.");
					}  else if (args[0].equalsIgnoreCase("pvp")) {
						p.sendMessage(ChatColor.YELLOW+"[Demigods] PvP Help File");
						p.sendMessage(ChatColor.GRAY+"Demigods is a player versus player centric plugin and");
						p.sendMessage(ChatColor.GRAY+"rewards players greatly for defeating members of the enemy");
						p.sendMessage(ChatColor.GRAY+"alliance. Killing an enemy player rewards you with Favor and");
						p.sendMessage(ChatColor.GRAY+"EXP. If you die in combat, your Level is instantly reduced");
						p.sendMessage(ChatColor.GRAY+"to 1, although Perks can nullify this.");
						p.sendMessage(ChatColor.GRAY+"The alliance with more overall kills receives a passive EXP");
						p.sendMessage(ChatColor.GRAY+"and Favor multiplier.");
					}
					else if (args[0].equalsIgnoreCase("stats")) {
						int titancount = 0;
						int godcount = 0;
						int othercount = 0;
						int titankills = 0;
						int godkills = 0;
						int otherkills = 0;
						int titandeaths = 0;
						int goddeaths = 0;
						int otherdeaths = 0;
						ArrayList<String> onlinegods = new ArrayList<String>();
						ArrayList<String> onlinetitans = new ArrayList<String>();
						ArrayList<String> onlineother = new ArrayList<String>();
						for (String id : DSave.getCompleteData().keySet()) {
							try {
								if (!DUtil.isFullParticipant(id)) continue;
								if (DSave.hasData(id, "LASTLOGINTIME"))
									if ((Long)DSave.getData(id, "LASTLOGINTIME") < System.currentTimeMillis()-604800000) continue;
								if (DUtil.isTitan(id)) {
									titancount++;
									titankills += DUtil.getKills(id);
									titandeaths += DUtil.getDeaths(id);
									if (DUtil.getPlugin().getServer().getPlayer(id).isOnline()) {
										onlinetitans.add(id);
									}
								} else if (DUtil.isGod(id)) {
									if (!DUtil.isFullParticipant(id)) continue;
									if (DUtil.isGod(id)) {
										godcount++;
										godkills += DUtil.getKills(id);
										goddeaths += DUtil.getDeaths(id);
										if (DUtil.getPlugin().getServer().getPlayer(id).isOnline()) {
											onlinegods.add(id);
										}
									}
								} else {
									if (!DUtil.isFullParticipant(id)) continue;
									othercount++;
									otherkills += DUtil.getKills(id);
									otherdeaths += DUtil.getDeaths(id);
									if (DUtil.getPlugin().getServer().getPlayer(id).isOnline()) {
										onlineother.add(id);
									}
								}
							} catch (NullPointerException error) {}
						}
						/*
						 * Print data
						 */
						p.sendMessage(ChatColor.GRAY+"----Stats----");
						String str1 = "";
						if (onlinegods.size()>0){
							for (String g : onlinegods){
								str1 += g+", ";
							}
							str1 = str1.substring(0, str1.length()-2);
						}
						String str2 = "";
						if (onlinetitans.size()>0){
							for (String t : onlinetitans){
								str2 += t+", ";
							}
							str2 = str2.substring(0, str2.length()-2);
						}
						String str3 = "";
						if (onlineother.size()>0){
							for (String o : onlineother) {
								str3 += o+", ";
							}
							str3 = str3.substring(0, str3.length()-2);
						}
						p.sendMessage("There are "+ChatColor.GREEN+onlinegods.size()+"/"+ChatColor.YELLOW+godcount+ChatColor.WHITE+" Gods online: "+ChatColor.GOLD+str1);
						p.sendMessage("There are "+ChatColor.GREEN+onlinetitans.size()+"/"+ChatColor.YELLOW+titancount+ChatColor.WHITE+" Titans online: "+ChatColor.GOLD+str2);
						if (othercount > 0)
							p.sendMessage("There are "+ChatColor.GREEN+onlineother.size()+"/"+ChatColor.YELLOW+othercount+ChatColor.WHITE+" other" +
									" alliance members online: "+ChatColor.GOLD+str3);
						p.sendMessage("Total God kills: "+ChatColor.GREEN+godkills+ChatColor.YELLOW+" --- "+ChatColor.WHITE+" Total Titan kills: "+ChatColor.RED+titankills);
						p.sendMessage("God K/D Ratio: "+ChatColor.GREEN+((float)godkills/goddeaths)+ChatColor.YELLOW+" --- "+ChatColor.WHITE+
								" Titan K/D Ratio: "+ChatColor.RED+((float)titankills/titandeaths));
						if (othercount > 0) {
							p.sendMessage("Total Other kills: "+ChatColor.GREEN+otherkills+ChatColor.YELLOW);
							p.sendMessage("Other K/D Ratio: "+ChatColor.YELLOW+((float)otherkills/otherdeaths));
						}
					} else if (args[0].equalsIgnoreCase("ranking") || args[0].equalsIgnoreCase("rankings")) {
						if (DUtil.getFullParticipants().size() < 1) {
							p.sendMessage(ChatColor.GRAY+"There are no players to rank.");
							return true;
						}
						//get list of gods and titans
						ArrayList<String> gods = new ArrayList<String>();
						ArrayList<String> titans = new ArrayList<String>();
						ArrayList<Long> gr = new ArrayList<Long>();
						ArrayList<Long> tr = new ArrayList<Long>();
						for (String s : DUtil.getFullParticipants()) {
							if (DUtil.isGod(s)) {
								if (DSave.hasData(s, "LASTLOGINTIME"))
									if ((Long)DSave.getData(s, "LASTLOGINTIME") < System.currentTimeMillis()-604800000) continue;
								gods.add(s);
								gr.add(DUtil.getRanking(s));
							} else if (DUtil.isTitan(s)) {
								if (DSave.hasData(s, "LASTLOGINTIME"))
									if ((Long)DSave.getData(s, "LASTLOGINTIME") < System.currentTimeMillis()-604800000) continue;
								titans.add(s);
								tr.add(DUtil.getRanking(s));
							}
						}
						String[] Gods = new String[gods.size()];
						String[] Titans = new String[titans.size()];
						Long[] GR = new Long[gods.size()];
						Long[] TR = new Long[titans.size()];
						for (int i=0;i<Gods.length;i++) {
							Gods[i] = gods.get(i);
							GR[i] = gr.get(i);
						}
						for (int i=0;i<Titans.length;i++) {
							Titans[i] = titans.get(i);
							TR[i] = tr.get(i);
						}
						//sort gods
						for (int i=0;i<Gods.length;i++) {
							int highestIndex = i;
							long highestRank = GR[i];
							for (int j=i;j<Gods.length;j++) {
								if (GR[j] > highestRank) {
									highestIndex = j;
									highestRank = GR[j];
								}
							}
							if (highestRank == GR[i])
								continue;
							String t = Gods[i];
							Gods[i] = Gods[highestIndex];
							Gods[highestIndex] = t;
							Long l = GR[i];
							GR[i] = GR[highestIndex];
							GR[highestIndex] = l;
						}
						//sort titans
						for (int i=0;i<Titans.length;i++) {
							int highestIndex = i;
							long highestRank = TR[i];
							for (int j=i;j<Titans.length;j++) {
								if (TR[j] > highestRank) {
									highestIndex = j;
									highestRank = TR[j];
								}
							}
							if (highestRank == TR[i])
								continue;
							String t = Titans[i];
							Titans[i] = Titans[highestIndex];
							Titans[highestIndex] = t;
							Long l = TR[i];
							TR[i] = TR[highestIndex];
							TR[highestIndex] = l;
						}
						//print
						p.sendMessage(ChatColor.GRAY+"----Rankings----");
						p.sendMessage(ChatColor.GRAY+"Rankings are determined by Devotion, Deities, and Kills.");
						int gp = Gods.length;
						if (gp > 5) gp = 5;
						p.sendMessage(ChatColor.GOLD+"-- Gods");
						for (int i=0;i<gp;i++) {
							p.sendMessage(ChatColor.GRAY+"  "+(i+1)+". "+Gods[i]+" :: "+GR[i]);
						}
						int tp = Titans.length;
						if (tp > 5) tp = 5;
						p.sendMessage(ChatColor.DARK_RED+"-- Titans");
						for (int i=0;i<tp;i++) {
							p.sendMessage(ChatColor.GRAY+"  "+(i+1)+". "+Titans[i]+" :: "+TR[i]);
						}
						p.sendMessage(ChatColor.GRAY+"To see the full list, use "+ChatColor.YELLOW+"/dg rankings god|titan");
					}	else {
						for (Deity deity : DSave.getGlobalList()) {
							if (deity.getName().equalsIgnoreCase(args[0]))
								deity.printInfo(p);
						}
					}
				} else if (args.length == 2) {
					if (args[0].equalsIgnoreCase("ranking") || args[0].equalsIgnoreCase("rankings")) {
						if (args[1].equalsIgnoreCase("god")) {
							//get list of gods
							ArrayList<String> gods = new ArrayList<String>();
							ArrayList<Long> gr = new ArrayList<Long>();
							for (String s : DUtil.getFullParticipants()) {
								if (DUtil.isGod(s)) {
									if (DSave.hasData(s, "LASTLOGINTIME"))
										if ((Long)DSave.getData(s, "LASTLOGINTIME") < System.currentTimeMillis()-604800000) continue;
									gods.add(s);
									gr.add(DUtil.getRanking(s));
								}
							}
							if (gods.size() < 1) {
								p.sendMessage(ChatColor.GRAY+"There are no players to rank.");
								return true;
							}
							String[] Gods = new String[gods.size()];
							Long[] GR = new Long[gods.size()];
							for (int i=0;i<Gods.length;i++) {
								Gods[i] = gods.get(i);
								GR[i] = gr.get(i);
							}
							//sort gods
							for (int i=0;i<Gods.length;i++) {
								int highestIndex = i;
								long highestRank = GR[i];
								for (int j=i;j<Gods.length;j++) {
									if (GR[j] > highestRank) {
										highestIndex = j;
										highestRank = GR[j];
									}
								}
								if (highestRank == GR[i])
									continue;
								String t = Gods[i];
								Gods[i] = Gods[highestIndex];
								Gods[highestIndex] = t;
								Long l = GR[i];
								GR[i] = GR[highestIndex];
								GR[highestIndex] = l;
							}
							p.sendMessage(ChatColor.GRAY+"----God Rankings----");
							p.sendMessage(ChatColor.GRAY+"Rankings are determined by Devotion, Deities, and Kills.");
							for (int i=0;i<Gods.length;i++) {
								p.sendMessage(ChatColor.GRAY+"  "+(i+1)+". "+Gods[i]+" :: "+GR[i]);
							}
						} else if (args[1].equalsIgnoreCase("titan")) {
							//get list of titans
							ArrayList<String> titans = new ArrayList<String>();
							ArrayList<Long> tr = new ArrayList<Long>();
							for (String s : DUtil.getFullParticipants()) {
								if (DUtil.isTitan(s)) {
									if (DSave.hasData(s, "LASTLOGINTIME"))
										if ((Long)DSave.getData(s, "LASTLOGINTIME") < System.currentTimeMillis()-604800000) continue;
									titans.add(s);
									tr.add(DUtil.getRanking(s));
								}
							}
							if (titans.size() < 1) {
								p.sendMessage(ChatColor.GRAY+"There are no players to rank.");
								return true;
							}
							String[] Titans = new String[titans.size()];
							Long[] TR = new Long[titans.size()];
							for (int i=0;i<Titans.length;i++) {
								Titans[i] = titans.get(i);
								TR[i] = tr.get(i);
							}
							//sort titans
							for (int i=0;i<Titans.length;i++) {
								int highestIndex = i;
								long highestRank = TR[i];
								for (int j=i;j<Titans.length;j++) {
									if (TR[j] > highestRank) {
										highestIndex = j;
										highestRank = TR[j];
									}
								}
								if (highestRank == TR[i])
									continue;
								String t = Titans[i];
								Titans[i] = Titans[highestIndex];
								Titans[highestIndex] = t;
								Long l = TR[i];
								TR[i] = TR[highestIndex];
								TR[highestIndex] = l;
							}
							//print
							p.sendMessage(ChatColor.GRAY+"----Titan Rankings----");
							p.sendMessage(ChatColor.GRAY+"Rankings are determined by Devotion, Deities, and Kills.");
							for (int i=0;i<Titans.length;i++) {
								p.sendMessage(ChatColor.GRAY+"  "+(i+1)+". "+Titans[i]+" :: "+TR[i]);
							}
						} else if (args[1].equalsIgnoreCase("god|titan")) {
							p.sendMessage("Try "+ChatColor.YELLOW+"/dg ranking god"+ChatColor.WHITE+" or "+ChatColor.YELLOW+"/dg ranking titan");
						}
					}
				} else if (args.length == 3) {
					if (DUtil.hasPermissionOrOP(p, "demigods.admin"))
						try {
							int one = Integer.parseInt(args[0]);
							int two = Integer.parseInt(args[1]);
							int three = Integer.parseInt(args[2]);
							p.teleport(new Location(p.getWorld(), one, two, three));
						} catch (Exception err) {}
				}
				return true;
			} else if (c.getName().equalsIgnoreCase("check")) {
				if (!DUtil.isFullParticipant(p)) {
					p.sendMessage(ChatColor.YELLOW+"--"+p.getName()+"--Mortal--");
					p.sendMessage("You are not affiliated with any Gods or Titans.");
					return true;
				}
				if (DUtil.isGod(p) || DUtil.isTitan(p)) {
					p.sendMessage(ChatColor.YELLOW+"--"+p.getName()+"--"+DUtil.getRank(p)+"");
					//HP
					ChatColor color = ChatColor.GREEN;
					if ((DUtil.getHP(p)/(double)DUtil.getMaxHP(p)) < 0.25) color = ChatColor.RED;
					else if ((DUtil.getHP(p)/(double)DUtil.getMaxHP(p)) < 0.5) color = ChatColor.YELLOW;
					p.sendMessage("HP: "+color+DUtil.getHP(p)+"/"+DUtil.getMaxHP(p));
					//List deities
					String send = "Your deities are:";
					for (Deity d : DUtil.getDeities(p)){
						send+=" "+d.getName()+" "+ChatColor.YELLOW+"<"+DUtil.getDevotion(p, d)+">"+ChatColor.WHITE;
					}
					p.sendMessage(send);
					//Display Favor/Ascensions and K/D
					//float percentage = (DUtil.getDevotion(p)-DUtil.costForNextAscension(DUtil.getAscensions(p)-1))/(float)(DUtil.costForNextAscension(p)-DUtil.costForNextAscension(DUtil.getAscensions(p)-1))*100;
					String op = ChatColor.YELLOW+"   |   "+(DUtil.costForNextAscension(DUtil.getAscensions(p))-DUtil.getDevotion(p))+" until next Ascension";
					if (DUtil.getAscensions(p) >= 100)
						op = "";
					p.sendMessage("Devotion: "+DUtil.getDevotion(p)+op);
					p.sendMessage("Favor: "+DUtil.getFavor(p)+ChatColor.YELLOW+"/"+DUtil.getFavorCap(p));
					p.sendMessage("Ascensions: "+DUtil.getAscensions(p));
					p.sendMessage("Kills: "+ChatColor.GREEN+DUtil.getKills(p)+ChatColor.WHITE+" // "+
							"Deaths: "+ChatColor.RED+DUtil.getDeaths(p));
					//Deity information
					if (DUtil.getAscensions(p) < DUtil.costForNextDeity(p))
						p.sendMessage("You may form a new alliance at "+ChatColor.GOLD+
								DUtil.costForNextDeity(p)+ChatColor.WHITE+" Ascensions.");
					else {
						p.sendMessage(ChatColor.AQUA+"You are eligible for a new alliance.");
					}
					//Effects
					if (DUtil.getActiveEffects(p.getName()).size() > 0) {
						String printout = ChatColor.YELLOW+"Active effects:";
						HashMap<String, Long> fx = DUtil.getActiveEffects(p.getName());
						for (String str : fx.keySet()) {
							printout += " "+str+"["+(Math.round(fx.get(str)-System.currentTimeMillis())/1000)+"s]";
						}
						p.sendMessage(printout);
					}
				}
				return true;
			} /*else if (c.getName().equalsIgnoreCase("transfer")) {
				if (!DUtil.isFullParticipant(p))
					return true;
				if (args.length == 1) {
					try {
						int give = Integer.parseInt(args[0]);
						if (DUtil.getFavor(p) < give) {
							p.sendMessage(ChatColor.YELLOW+"You do not have enough Favor.");
							return true;
						}
						for (Block b : p.getLineOfSight(null, 5)) {
							for (Player pl : p.getWorld().getPlayers()) {
								if (pl.getLocation().distance(b.getLocation()) < 0.8) {
									if (!DUtil.isFullParticipant(pl))
										continue;
									if (!DUtil.getAllegiance(pl).equalsIgnoreCase(DUtil.getAllegiance(p)))
										continue;
									DUtil.setFavor(pl, DUtil.getFavor(pl)+give);
									DUtil.setFavor(p, DUtil.getFavor(p)-give);
									p.sendMessage(ChatColor.YELLOW+"Successfully transferred "+give+" Favor to "+pl.getName()+".");
									pl.sendMessage(ChatColor.YELLOW+"Received "+give+" Favor from "+p.getName()+".");
									return true;
								}
							}
						}
						p.sendMessage(ChatColor.YELLOW+"No players found. You may only transfer Favor within your alliance.");
					} catch (Exception e) {
						return false;
					}
					return true;
				} else if (args.length == 2) {
					try {
						Player pl = DUtil.getOnlinePlayer(args[0]);
						if (pl.getUniqueId().equals(p.getUniqueId())) {
							p.sendMessage(ChatColor.YELLOW+"You cannot send Favor to yourself.");
							return true;
						}
						int give = Integer.parseInt(args[1]);
						int tax = (int)(TRANSFERTAX*give);
						if (DUtil.getFavor(p) < (give+tax)) {
							p.sendMessage(ChatColor.YELLOW+"You do not have enough Favor.");
							p.sendMessage(ChatColor.YELLOW+"The tax for this long-distance transfer is "+tax+".");
							return true;
						}
						if (!DUtil.isFullParticipant(pl))
							return true;
						if (!DUtil.getAllegiance(pl).equalsIgnoreCase(DUtil.getAllegiance(p)))
							return true;
						DUtil.setFavor(pl, DUtil.getFavor(pl)+give);
						DUtil.setFavor(p, DUtil.getFavor(p)-give-tax);
						p.sendMessage(ChatColor.YELLOW+"Successfully transferred "+give+" Favor to "+pl.getName()+".");
						if (tax > 0)
							p.sendMessage(ChatColor.YELLOW+"You lost "+tax+" Favor in tax for a long-distance transfer.");
						pl.sendMessage(ChatColor.YELLOW+"Received "+give+" Favor from "+p.getName()+".");
						return true;
					} catch (Exception e) {
						return false;
					}
				} else {
					return false;
				}
			}*/
			else if (c.getName().equalsIgnoreCase("alliance")) {
				if (!DUtil.isFullParticipant(p))
					return true;
				if (DSave.hasData(p, "ALLIANCECHAT")) {
					if ((Boolean)DSave.getData(p, "ALLIANCECHAT")) {
						p.sendMessage(ChatColor.YELLOW+"Alliance chat has been turned off.");
						DSave.saveData(p, "ALLIANCECHAT", false);
						return true;
					}
				}
				p.sendMessage(ChatColor.YELLOW+"Alliance chat has been turned on.");
				DSave.saveData(p, "ALLIANCECHAT", true);
				return true;
			}else if (c.getName().equalsIgnoreCase("checkplayer")) {
				if (!(DUtil.hasPermission(p, "demigods.checkplayer") || DUtil.hasPermission(p, "demigods.admin")))
					return true;
				if (args.length != 1)
					return false;
				try {
					Player ptarget = DUtil.getOnlinePlayer(args[0]);
					String target = DUtil.getDemigodsPlayer(args[0]);
					if (DUtil.isFullParticipant(target)) {
						p.sendMessage(ChatColor.YELLOW+"--"+target+"--");
						//List deities
						String send = target+"'s deities are:";
						for (Deity d : DUtil.getDeities(target)){
							send+=" "+d.getName()+" "+ChatColor.YELLOW+"<"+DUtil.getDevotion(target, d)+">"+ChatColor.WHITE;
						}
						p.sendMessage(send);
						//HP
						ChatColor color = ChatColor.GREEN;
						if ((DUtil.getHP(target)/(double)DUtil.getMaxHP(target)) < 0.25) color = ChatColor.RED;
						else if ((DUtil.getHP(target)/(double)DUtil.getMaxHP(target)) < 0.5) color = ChatColor.YELLOW;
						p.sendMessage("HP: "+color+DUtil.getHP(target)+"/"+DUtil.getMaxHP(target));
						//Display Favor/Ascensions and K/D
						p.sendMessage("Devotion: "+DUtil.getDevotion(target)+ChatColor.YELLOW+"   |   "+
								(DUtil.costForNextAscension(DUtil.getAscensions(target))-DUtil.getDevotion(target))+" until next Ascension");
						p.sendMessage("Favor: "+DUtil.getFavor(target)+ChatColor.YELLOW+"/"+DUtil.getFavorCap(target));
						p.sendMessage("Ascensions: "+DUtil.getAscensions(target));
						p.sendMessage("Kills: "+ChatColor.GREEN+DUtil.getKills(target)+ChatColor.WHITE+" // "+
								"Deaths: "+ChatColor.RED+DUtil.getDeaths(target));
						//Deity information
						if (DUtil.costForNextDeity(target) > DUtil.getAscensions(target))
							p.sendMessage(target+" may form a new alliance at "+ChatColor.GOLD+
									DUtil.costForNextDeity(target)+ChatColor.WHITE+" Ascensions.");
						else {
							p.sendMessage(ChatColor.AQUA+target+" is eligible for a new alliance.");
						}
						//Effects
						if (DUtil.getActiveEffectsList(target).size() > 0) {
							String printout = ChatColor.YELLOW+"Active effects:";
							for (String str : DUtil.getActiveEffectsList(target))
								printout += " "+str;
							p.sendMessage(printout);
						}
					} else {
						p.sendMessage(ChatColor.YELLOW+"--"+ptarget.getName()+"--Mortal--");
						p.sendMessage(ptarget.getName()+" is not affiliated with any Gods or Titans.");
					}
				} catch (NullPointerException name) {
					p.sendMessage(ChatColor.YELLOW+"Player not found.");
				}
				return true;
			} else if (c.getName().equalsIgnoreCase("shrine")) {
				if (!DUtil.isFullParticipant(p))
					return true;
				//player has shrines for these deities
				String str1 = "Shrines:";
				for (String name : DUtil.getShrines(p.getName()).keySet()) {
					if (name.charAt(0) != '#')
						str1 += " "+DUtil.getDeityAtShrine(DUtil.getShrines(p.getName()).get(name));
				}
				//player's named shrines
				String str2 = "Named shrines:";
				for (String name : DUtil.getShrines(p.getName()).keySet()) {
					if (name.charAt(0) == '#')
						str2 += " "+name.substring(1);
				}
				//player can warp to these shrines
				String str3 = "Other shrines you may warp to:";
				for (WriteLocation shrine : DUtil.getAccessibleShrines(p.getName())) {
					str3 += " "+DUtil.getShrineName(shrine);
				}
				p.sendMessage(ChatColor.YELLOW+str1);
				p.sendMessage(ChatColor.YELLOW+str2);
				p.sendMessage(ChatColor.LIGHT_PURPLE+str3);
				return true;
			} else if (c.getName().equalsIgnoreCase("shrinewarp")) {
				if (!(DUtil.hasPermission(p, "demigods.shrinewarp") || DUtil.hasPermission(p, "demigods.admin")))
					return true;
				WriteLocation target = null;
				if ((args.length != 1) && (args.length != 2))
					return false;
				if (args.length == 1)
					//try matching the name to deities the player has
					target = DUtil.getShrine(p.getName(), args[0]);
				//try matching the name to another player's warp
				if ((target == null) && (args.length == 2)) {
					if (DUtil.isFullParticipant(DUtil.getDemigodsPlayer(args[0]))) {
						target = DUtil.getShrine(DUtil.getDemigodsPlayer(args[0]), args[1]);
					}
				}
				if ((target == null) && (args.length == 1))
					target = DUtil.getShrineByKey("#"+args[0]);
				if (target == null) {
					p.sendMessage(ChatColor.YELLOW+"Target shrine not found. Shrine names are case sensitive.");
					return true;
				}
				//check for permission
				if (!DUtil.isGuest(target, p.getName()) && !DUtil.getOwnerOfShrine(target).equals(p.getName())) {
					p.sendMessage(ChatColor.YELLOW+"You do not have permission for that warp.");
					return true;
				}
				//check if warp is valid
				if (!Settings.getEnabledWorlds().contains(p.getWorld())) {
					return true;
				}
				if (!Settings.getEnabledWorlds().contains(DUtil.toLocation(target).getWorld())) {
					p.sendMessage(ChatColor.YELLOW+"Demigods is not enabled in the target world.");
					return true;
				}
				//check if warp is clear
				Block b = DUtil.toLocation(target).getBlock();
				if ((b.getRelative(BlockFace.UP).getType() != Material.AIR) || (b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getType() != Material.AIR)) {
					p.sendMessage(ChatColor.YELLOW+"The target location is blocked.");
					return true;
				}
				//warp code
				target = DUtil.toWriteLocation(b.getRelative(BlockFace.UP).getLocation());
				final WriteLocation current = DUtil.toWriteLocation(p.getLocation());
				final int hp = DUtil.getHP(p);
				final float pitch = p.getLocation().getPitch();
				final float yaw = p.getLocation().getYaw();
				final Player pt = p;
				final WriteLocation TARGET = target;
				DUtil.addActiveEffect(p.getName(), "Warping", 1000);
				p.sendMessage(ChatColor.YELLOW+"Don't move, warping in progress...");
				DUtil.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(DUtil.getPlugin(), new Runnable() {
					@Override
					public void run() {
						if (DUtil.getActiveEffectsList(pt.getName()).contains("Warping")) {
							if (!current.equalsApprox(DUtil.toWriteLocation(pt.getLocation()))) {
								pt.sendMessage(ChatColor.RED+"Warp cancelled due to movement.");
								DUtil.removeActiveEffect(pt.getName(), "Warping");
							}
							if (DUtil.getHP(pt) < hp) {
								pt.sendMessage(ChatColor.RED+"Warp cancelled due to loss of health.");
								DUtil.removeActiveEffect(pt.getName(), "Warping");
							}
						}
					}
				}, 20);
				DUtil.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(DUtil.getPlugin(), new Runnable() {
					@Override
					public void run() {
						if (DUtil.getActiveEffectsList(pt.getName()).contains("Warping")) {
							if (!current.equalsApprox(DUtil.toWriteLocation(pt.getLocation()))) {
								pt.sendMessage(ChatColor.RED+"Warp cancelled due to movement.");
								DUtil.removeActiveEffect(pt.getName(), "Warping");
							}
							if (DUtil.getHP(pt) < hp) {
								pt.sendMessage(ChatColor.RED+"Warp cancelled due to loss of health.");
								DUtil.removeActiveEffect(pt.getName(), "Warping");
							}
						}
					}
				}, 40);
				DUtil.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(DUtil.getPlugin(), new Runnable() {
					@Override
					public void run() {
						if (DUtil.getActiveEffectsList(pt.getName()).contains("Warping")) {
							if (!current.equalsApprox(DUtil.toWriteLocation(pt.getLocation()))) {
								pt.sendMessage(ChatColor.RED+"Warp cancelled due to movement.");
								DUtil.removeActiveEffect(pt.getName(), "Warping");
							}
							if (DUtil.getHP(pt) < hp) {
								pt.sendMessage(ChatColor.RED+"Warp cancelled due to loss of health.");
								DUtil.removeActiveEffect(pt.getName(), "Warping");
							}
						}
					}
				}, 60);
				DUtil.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(DUtil.getPlugin(), new Runnable() {
					@Override
					public void run() {
						if (DUtil.getActiveEffectsList(pt.getName()).contains("Warping")) {
							if (!current.equalsApprox(DUtil.toWriteLocation(pt.getLocation()))) {
								pt.sendMessage(ChatColor.RED+"Warp cancelled due to movement.");
								DUtil.removeActiveEffect(pt.getName(), "Warping");
							}
							if (DUtil.getHP(pt) < hp) {
								pt.sendMessage(ChatColor.RED+"Warp cancelled due to loss of health.");
								DUtil.removeActiveEffect(pt.getName(), "Warping");
							}
						}
					}
				}, 80);
				DUtil.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(DUtil.getPlugin(), new Runnable() {
					@Override
					public void run() {
						if (DUtil.getActiveEffectsList(pt.getName()).contains("Warping")) {
							Location newloc = DUtil.toLocation(TARGET);
							newloc.setPitch(pitch);
							newloc.setYaw(yaw);
							newloc.setX(newloc.getX()+0.5);
							newloc.setZ(newloc.getZ()+0.5);
							pt.teleport(newloc);
							pt.sendMessage(ChatColor.YELLOW+"Shrine warp successful.");
							DUtil.removeActiveEffect(pt.getName(), "Warping");
						}
					}
				}, 90);
				return true;
			} else if (c.getName().equalsIgnoreCase("shrineowner")) {
				if (!(DUtil.hasPermission(p, "demigods.shrineowner") || DUtil.hasPermission(p, "demigods.admin")))
					return true;
				if (args.length != 2)
					return false;
				WriteLocation shrine = DUtil.getNearbyShrine(p.getLocation());
				if (shrine == null) {
					p.sendMessage(ChatColor.YELLOW+"No shrine nearby.");
					return true;
				}
				if (!DUtil.getOwnerOfShrine(shrine).equals(p.getName()) && !DUtil.hasPermission(p, "demigods.admin")) {
					p.sendMessage(ChatColor.YELLOW+"Only admins and the creator of a shrine can modify it.");
					return true;
				}
				//add <name>
				if (args[0].equalsIgnoreCase("add")) {
					String toadd = DUtil.getDemigodsPlayer(args[1]);
					if (toadd == null) {
						p.sendMessage(ChatColor.YELLOW+"Player not found.");
					} else if (toadd.equals(p.getName())) {
						p.sendMessage(ChatColor.YELLOW+"You are already the shrine owner.");
					} else if (DUtil.getShrineGuestlist(shrine).contains(toadd)) {
						p.sendMessage(ChatColor.YELLOW+toadd+" already has permission to warp to this shrine.");
					} else if (!DUtil.getAllegiance(toadd).equals(DUtil.getAllegiance(p))) {
						p.sendMessage(ChatColor.YELLOW+toadd+" is not in your alliance.");
					} else {
						p.sendMessage(ChatColor.YELLOW+toadd+" now has permission to warp to this shrine.");
						DUtil.addGuest(shrine, toadd);
					}
				}
				//remove <name>
				else if (args[0].equalsIgnoreCase("remove")) {
					String remove = DUtil.getDemigodsPlayer(args[1]);
					if (remove == null) {
						p.sendMessage(ChatColor.YELLOW+"Player not found.");
					} else if (remove.equals(p.getName())) {
						p.sendMessage(ChatColor.YELLOW+"You cannot remove yourself as an owner.");
					} else if (!DUtil.getShrineGuestlist(shrine).contains(remove)) {
						p.sendMessage(ChatColor.YELLOW+remove+" is not an owner of this shrine.");
					} else {
						if (DUtil.removeGuest(shrine, remove))
							p.sendMessage(ChatColor.YELLOW+remove+" no longer has permission to warp to this shrine.");
						else p.sendMessage(ChatColor.YELLOW+"Error while removing "+remove+"'s permission.");
					}
				}
				//set <name>
				else if (args[0].equalsIgnoreCase("set")) {
					String newowner = DUtil.getDemigodsPlayer(args[1]);
					if (newowner == null) {
						p.sendMessage(ChatColor.YELLOW+"Player not found.");
					} else if (newowner.equals(DUtil.getOwnerOfShrine(shrine))) {
						p.sendMessage(ChatColor.YELLOW+newowner+" is already the shrine's owner.");
					} else {
						p.sendMessage(ChatColor.YELLOW+newowner+" is the new owner of the shrine.");
						String deity = DUtil.getDeityAtShrine(shrine);
						String shrinename = DUtil.getShrineName(shrine);
						DUtil.removeShrine(shrine);
						DUtil.addShrine(newowner, deity, shrine);
						DUtil.addShrine(newowner, shrinename, shrine);
					}
				}
				else return false;
				return true;
			} else if (c.getName().equalsIgnoreCase("fixshrine")) {
				WriteLocation shrine = DUtil.getNearbyShrine(p.getLocation());
				if (shrine == null) {
					p.sendMessage(ChatColor.YELLOW+"No shrine nearby.");
					return true;
				}
				//check if creator/admin
				if (!DUtil.getOwnerOfShrine(shrine).equals(p.getName()) && !DUtil.hasPermission(p, "demigods.admin")) {
					p.sendMessage(ChatColor.YELLOW+"Only admins and the creator of a shrine can modify it.");
					return true;
				}
				if (DUtil.toLocation(shrine).getBlock().getType() != Material.GOLD_BLOCK)
					DUtil.toLocation(shrine).getBlock().setType(Material.GOLD_BLOCK);
				p.sendMessage(ChatColor.YELLOW+"Shrine fixed.");
				return true;
			}else if (c.getName().equalsIgnoreCase("removeshrine")) {
				if (!(DUtil.hasPermission(p, "demigods.removeshrine") || DUtil.hasPermission(p, "demigods.admin")))
					return true;
				if ((args.length == 1) && DUtil.hasPermission(p, "demigods.admin") && args[0].equals("all")) {
					for (WriteLocation w : DUtil.getAllShrines()) {
						p.sendMessage("Deleting "+DUtil.getShrineName(w));
						DUtil.toLocation(w).getBlock().setType(Material.AIR);
						DUtil.removeShrine(w);
					}
					return true;
				}
				if (args.length != 0)
					return false;
				//find nearby shrine
				WriteLocation shrine = DUtil.getNearbyShrine(p.getLocation());
				if (shrine == null) {
					p.sendMessage(ChatColor.YELLOW+"No shrine nearby.");
					return true;
				}
				//check if creator/admin
				if (!DUtil.getOwnerOfShrine(shrine).equals(p.getName()) && !DUtil.hasPermission(p, "demigods.admin")) {
					p.sendMessage(ChatColor.YELLOW+"Only admins and the creator of a shrine can modify it.");
					return true;
				}
				//remove
				String deity = DUtil.getDeityAtShrine(shrine);
				DUtil.toLocation(shrine).getBlock().setType(Material.AIR);
				p.sendMessage(ChatColor.YELLOW+"The shrine "+DUtil.getShrineName(shrine)+" has been removed.");
				DUtil.removeShrine(shrine);
				if (!DUtil.hasPermission(p, "demigods.admin")) {
					//penalty
					DUtil.setDevotion(p, deity, (int)(DUtil.getDevotion(p, deity)*0.75));
					p.sendMessage(ChatColor.RED+"Your Devotion for "+deity+" has been reduced to "+DUtil.getDevotion(p, deity)+".");
				}
				return true;
			} else if (c.getName().equalsIgnoreCase("nameshrine")) {
				if (!(DUtil.hasPermission(p, "demigods.nameshrine") || DUtil.hasPermission(p, "demigods.admin")))
					return true;
				if (args.length != 1)
					return false;
				//find nearby shrine
				WriteLocation shrine = DUtil.getNearbyShrine(p.getLocation());
				if (shrine == null) {
					p.sendMessage(ChatColor.YELLOW+"No shrine nearby.");
					return true;
				}
				//check if creator/admin
				if (!DUtil.getOwnerOfShrine(shrine).equals(p.getName()) && !DUtil.hasPermission(p, "demigods.admin")) {
					p.sendMessage(ChatColor.YELLOW+"Only admins and the creator of a shrine can modify it.");
					return true;
				}
				//remove
				if (DUtil.renameShrine(shrine, args[0]))
					p.sendMessage(ChatColor.YELLOW+"The shrine has been renamed to "+args[0]+".");
				else p.sendMessage(ChatColor.YELLOW+"Error. Is there already a shrine named "+args[1]+"?");
				return true;
			} else if (c.getName().equalsIgnoreCase("givedeity")) {
				if (!(DUtil.hasPermission(p, "demigods.givedeity") || DUtil.hasPermission(p, "demigods.admin")))
					return true;
				if (args.length != 2)
					return false;
				String target = DUtil.getDemigodsPlayer(args[0]);
				if (DUtil.hasDeity(target, args[1])) {
					p.sendMessage(ChatColor.YELLOW+""+target+" already has that deity.");
					return true;
				} else {
					String s = args[1].toLowerCase();
					if (s.equals("zeus")) DUtil.giveDeity(target, new Zeus(target));
					else if (s.equals("ares")) DUtil.giveDeity(target, new Ares(target));
					else if (s.equals("cronus")) DUtil.giveDeity(target, new Cronus(target));
					else if (s.equals("prometheus")) DUtil.giveDeity(target, new Prometheus(target));
					else if (s.equals("rhea")) DUtil.giveDeity(target, new Rhea(target));
					else if (s.equals("hades")) DUtil.giveDeity(target, new Hades(target));
					else if (s.equals("poseidon")) DUtil.giveDeity(target, new Poseidon(target));
					else if (s.equals("atlas")) DUtil.giveDeity(target, new Atlas(target));
					else if (s.equals("athena")) DUtil.giveDeity(target, new Athena(target));
					else if (s.equals("oceanus")) DUtil.giveDeity(target, new Oceanus(target));
					else if (s.equals("hyperion")) DUtil.giveDeity(target, new Hyperion(target));
					else if (s.equals("hephaestus")) DUtil.giveDeity(target, new Hephaestus(target));
					p.sendMessage(ChatColor.YELLOW+"Success! "+target+" now has the deity "+args[1]+".");
					p.sendMessage(ChatColor.YELLOW+"Skills may not work if you mismatch Titans and Gods.");
				}
				return true;
			} else if (c.getName().equalsIgnoreCase("removedeity")) {
				if (!(DUtil.hasPermission(p, "demigods.removedeity") || DUtil.hasPermission(p, "demigods.admin")))
					return true;
				if (args.length != 2)
					return false;
				String target = DUtil.getDemigodsPlayer(args[0]);
				if (!DUtil.hasDeity(target, args[1])) {
					p.sendMessage(ChatColor.YELLOW+""+target+" does not have that deity.");
				} else {
					DUtil.getDeities(target).remove(DUtil.getDeity(target, args[1]));
					p.sendMessage(ChatColor.YELLOW+"Success! "+target+" no longer has that deity.");
				}
				return true;
			} else if (c.getName().equalsIgnoreCase("forsake")) {
				if (!(DUtil.hasPermission(p, "demigods.forsake") || DUtil.hasPermission(p, "demigods.admin")))
					return true;
				if (!DUtil.isFullParticipant(p))
					return true;
				if (args.length != 1)
					return false;
				if (args[0].equalsIgnoreCase("all")) {
					DUtil.getPlugin().getServer().broadcastMessage(ChatColor.RED+p.getName()+" has forsaken their deities.");
					p.sendMessage(ChatColor.RED+"You are mortal.");
					for (WriteLocation w : DUtil.getShrines(p.getName()).values())
						DUtil.removeShrine(w);
					DSave.removePlayer(p);
					DSave.addPlayer(p);
					return true;
				}
				if (!DUtil.hasDeity(p, args[0])) {
					p.sendMessage(ChatColor.YELLOW+"You do not have that deity.");
				} else {
					if (DUtil.getDeities(p).size() >= 2) {
						String str = "";
						Deity toremove = DUtil.getDeity(p, args[0]);
						LevelManager.levelProcedure(p);
						p.sendMessage(ChatColor.YELLOW+"You have forsaken "+toremove.getName()+"."+str);
						DUtil.getPlugin().getServer().broadcastMessage(ChatColor.RED+p.getName()+" has forsaken "+toremove.getName()+".");
						DUtil.getDeities(p).remove(toremove);
					} else {
						Deity toremove = DUtil.getDeity(p, args[0]);
						p.sendMessage(ChatColor.YELLOW+"You have forsaken "+toremove.getName()+".");
						p.sendMessage(ChatColor.RED+"You are mortal.");
						DUtil.getPlugin().getServer().broadcastMessage(ChatColor.RED+p.getName()+" has forsaken "+toremove.getName()+".");
						DSave.removePlayer(p);
						DSave.addPlayer(p);
					}
				}
				return true;
			} else if (c.getName().equalsIgnoreCase("setfavor")) {
				if (!(DUtil.hasPermission(p, "demigods.setfavor") || DUtil.hasPermission(p, "demigods.admin")))
					return true;
				if (args.length != 2)
					return false;
				try {
					String target = DUtil.getDemigodsPlayer(args[0]);
					int amt = Integer.parseInt(args[1]);
					if (amt < 0) {
						p.sendMessage(ChatColor.YELLOW+"The amount must be greater than 0.");
						return true;
					}
					if (DSave.hasPlayer(target)) {
						DUtil.setFavor(target, amt);
						p.sendMessage(ChatColor.YELLOW+"Success! "+target+" now has "+amt+" Favor/Power.");
						return true;
					}
				} catch (Exception e){
					return false;
				}
			} else if (c.getName().equalsIgnoreCase("setmaxfavor")) {
				if (!(DUtil.hasPermission(p, "demigods.setfavor") || DUtil.hasPermission(p, "demigods.admin")))
					return true;
				if (args.length != 2)
					return false;
				try {
					String target = DUtil.getDemigodsPlayer(args[0]);
					int amt = Integer.parseInt(args[1]);
					if (amt < 0) {
						p.sendMessage(ChatColor.YELLOW+"The amount must be greater than 0.");
						return true;
					}
					if (DSave.hasPlayer(target)) {
						DUtil.setFavorCap(target, amt);
						p.sendMessage(ChatColor.YELLOW+"Success! "+target+" now has "+amt+" max Favor/Power.");
						return true;
					}
				} catch (Exception e){
					return false;
				}
			} else if (c.getName().equalsIgnoreCase("sethp")) {
				if (!(DUtil.hasPermission(p, "demigods.sethp") || DUtil.hasPermission(p, "demigods.admin")))
					return true;
				if (args.length != 2)
					return false;
				try {
					String target = DUtil.getDemigodsPlayer(args[0]);
					int amt = Integer.parseInt(args[1]);
					if (amt < 0) {
						p.sendMessage(ChatColor.YELLOW+"The amount must be greater than 0.");
						return true;
					}
					if (DSave.hasPlayer(target)) {
						if (amt > DUtil.getMaxHP(target))
							DUtil.setMaxHP(target, amt);
						DUtil.setHP(target, amt);
						p.sendMessage(ChatColor.YELLOW+"Success! "+target+" now has "+amt+" HP.");
						return true;
					}
				} catch (Exception e){
					return false;
				}
			} else if (c.getName().equalsIgnoreCase("setmaxhp")) {
				if (!(DUtil.hasPermission(p, "demigods.setmaxhp") || DUtil.hasPermission(p, "demigods.admin")))
					return true;
				if (args.length != 2)
					return false;
				try {
					String target = DUtil.getDemigodsPlayer(args[0]);
					int amt = Integer.parseInt(args[1]);
					if (amt < 0) {
						p.sendMessage(ChatColor.YELLOW+"The amount must be greater than 0.");
						return true;
					}
					if (DSave.hasPlayer(target)) {
						DUtil.setMaxHP(target, amt);
						p.sendMessage(ChatColor.YELLOW+"Success! "+target+" now has "+amt+" max HP.");
						return true;
					}
				} catch (Exception e){
					return false;
				}
			} else if (c.getName().equalsIgnoreCase("setdevotion")) {
				if (!(DUtil.hasPermission(p, "demigods.setdevotion") || DUtil.hasPermission(p, "demigods.admin")))
					return true;
				if (args.length != 3)
					return false;
				String target = DUtil.getDemigodsPlayer(args[0]);
				if (!DUtil.isFullParticipant(target)) {
					p.sendMessage("That player is a mortal.");
					return true;
				}
				int amt = Integer.parseInt(args[2]);
				if (amt < 0) {
					p.sendMessage(ChatColor.YELLOW+"The amount must be greater than 0.");
					return true;
				}
				if (DUtil.hasDeity(target, args[1])) {
					DUtil.setDevotion(target, DUtil.getDeity(target, args[1]), amt);
					p.sendMessage(ChatColor.YELLOW+"Success! "+target+" now has "+amt+" Devotion for "+args[1].toUpperCase()+".");
					return true;
				}
			} else if (c.getName().equalsIgnoreCase("setascensions")) {
				if (!(DUtil.hasPermission(p, "demigods.setascensions") || DUtil.hasPermission(p, "demigods.admin")))
					return true;
				if (args.length != 2)
					return false;
				try {
					String target = DUtil.getDemigodsPlayer(args[0]);
					int amt = Integer.parseInt(args[1]);
					if (amt < 0) {
						p.sendMessage(ChatColor.YELLOW+"The number must be greater than 0.");
						return true;
					}
					if (DSave.hasPlayer(target)) {
						DUtil.setAscensions(target, amt);
						long oldtotal = DUtil.getDevotion(target);
						int newtotal = DUtil.costForNextAscension(amt-1);
						for (Deity d : DUtil.getDeities(target)) {
							int devotion = DUtil.getDevotion(target, d);
							DUtil.setDevotion(target, d, (int)Math.ceil((newtotal*1.0*devotion)/oldtotal));
						}
						p.sendMessage(ChatColor.YELLOW+"Success! "+target+" now has "+amt+" Ascensions.");
						return true;
					}
				} catch (Exception e){
					return false;
				}
			} else if (c.getName().equalsIgnoreCase("setkills")) {
				if (!(DUtil.hasPermission(p, "demigods.setkills") || DUtil.hasPermission(p, "demigods.admin")))
					return true;
				if (args.length != 2)
					return false;
				try {
					String target = DUtil.getDemigodsPlayer(args[0]);
					int amt = Integer.parseInt(args[1]);
					if (amt < 0) {
						p.sendMessage(ChatColor.YELLOW+"The amount must be greater than 0.");
						return true;
					}
					if (DSave.hasPlayer(target)) {
						DUtil.setKills(target, amt);
						p.sendMessage(ChatColor.YELLOW+"Success! "+target+" now has "+amt+" kills.");
						return true;
					}
				} catch (Exception e){
					return false;
				}
			} else if (c.getName().equalsIgnoreCase("setdeaths")) {
				if (!(DUtil.hasPermission(p, "demigods.setdeaths") || DUtil.hasPermission(p, "demigods.admin")))
					return true;
				if (args.length != 2)
					return false;
				try {
					String target = DUtil.getDemigodsPlayer(args[0]);
					int amt = Integer.parseInt(args[1]);
					if (amt < 0) {
						p.sendMessage(ChatColor.YELLOW+"The amount must be greater than 0.");
						return true;
					}
					if (DSave.hasPlayer(target)) {
						DUtil.setDeaths(target, amt);
						p.sendMessage(ChatColor.YELLOW+"Success! "+target+" now has "+amt+" deaths.");
						return true;
					}
				} catch (Exception e){
					return false;
				}
			} else if (c.getName().equalsIgnoreCase("setallegiance") || c.getName().equalsIgnoreCase("setalliance")) {
				if (!(DUtil.hasPermission(p, "demigods.setalliance") || DUtil.hasPermission(p, "demigods.admin")))
					return true;
				if (args.length != 2)
					return false;
				try {
					String target = DUtil.getDemigodsPlayer(args[0]);
					String allegiance = args[1];
					if (allegiance.equalsIgnoreCase("god"))
						DUtil.setGod(target);
					else if (allegiance.equalsIgnoreCase("titan"))
						DUtil.setTitan(target);
					else DUtil.setAllegiance(target, allegiance);
					p.sendMessage(ChatColor.YELLOW+"Success! "+target+" is now in the "+DUtil.getAllegiance(target)+" allegiance.");
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			} else if (c.getName().equalsIgnoreCase("removeplayer")) {
				if (!(DUtil.hasPermission(p, "demigods.removeplayer") || DUtil.hasPermission(p, "demigods.admin")))
					return true;
				if (args.length != 1)
					return false;
				Player toremove = plugin.getServer().getPlayer(args[0]);
				if (DSave.hasPlayer(toremove)) {
					p.sendMessage(ChatColor.YELLOW+toremove.getName()+ " was successfully removed from the save.");
					DSave.removePlayer(toremove);
					DSave.removeItem("plugins/Demigods/Players/"+toremove.getName()+".dem");
				} else p.sendMessage(ChatColor.YELLOW+"That player is not in the save.");
				return true;
			} else if (c.getName().equalsIgnoreCase("claim")) {
				/*
				 * Check for a new player first
				 */
				if ((DUtil.getDeities(p) == null) || (DUtil.getDeities(p).size() == 0)) {
					Deity choice = null;
					switch (p.getItemInHand().getType()) {
					case IRON_INGOT: choice = new Zeus(p.getName()); break;
					case WATER_BUCKET: choice = new Poseidon(p.getName()); break;
					case BONE: choice = new Hades(p.getName()); break;
					case SOUL_SAND: choice = new Cronus(p.getName()); break;
					case CLAY_BALL: choice = new Prometheus(p.getName()); break;
					case VINE: choice = new Rhea(p.getName()); break;
					}
					if (choice != null) {
						p.sendMessage(ChatColor.YELLOW+"The Fates ponder your decision...");
						final Deity fchoice = choice;
						final Player pl = p;
						if (BALANCETEAMS && DUtil.hasAdvantage(fchoice.getDefaultAlliance(), ADVANTAGEPERCENT)) {
							pl.sendMessage(ChatColor.RED+"The Fates have determined that your selection would");
							pl.sendMessage(ChatColor.RED+"unbalance the order of the universe. Try again");
							pl.sendMessage(ChatColor.RED+"later or select a different deity.");
							return true;
						}
						pl.sendMessage(ChatColor.YELLOW+"You have been accepted to the lineage of "+fchoice.getName()+".");
						DUtil.initializePlayer(pl.getName(), fchoice.getDefaultAlliance(), fchoice);
						pl.getWorld().strikeLightningEffect(pl.getLocation());
						for (int i=0;i<20;i++) pl.getWorld().spawn(pl.getLocation(), ExperienceOrb.class);
						return true;
					}
					p.sendMessage(ChatColor.YELLOW+"That is not a valid selection item for your first deity.");
					return true;
				}
				/*
				 * Otherwise
				 */
				if (!DUtil.isFullParticipant(p))
					return true;
				if (DUtil.getAscensions(p) < DUtil.costForNextDeity(p)) {
					p.sendMessage(ChatColor.YELLOW+"You must have "+DUtil.costForNextDeity(p)+" Ascensions to claim another deity.");
					return true;
				}
				/*
				Shrine inside = null;
				for (Shrine s : DSave.getShrines()) {
					if (s.getCenter().getWorld().equals(p.getWorld().getName()))
						if (DUtil.toLocation(s.getCenter()).distance(p.getLocation()) <= 6) { //RADIUS FOR ASCEND
							inside = s;
							break;
						}
				}
				if (inside == null) {
					p.sendMessage(ChatColor.YELLOW+"You must be near a Shrine to claim a Deity.");
					return true;
				} */
				Deity choice = null;
				switch (p.getItemInHand().getType()) {
				case IRON_INGOT: choice = new Zeus(p.getName()); break;
				case WATER_BUCKET: choice = new Poseidon(p.getName()); break;
				case BONE: choice = new Hades(p.getName()); break;
				case GOLD_SWORD: choice = new Ares(p.getName()); break;
				case BOOK: choice = new Athena(p.getName()); break;
				//
				case SOUL_SAND: choice = new Cronus(p.getName()); break;
				case CLAY_BALL: choice = new Prometheus(p.getName()); break;
				case VINE: choice = new Rhea(p.getName()); break;
				case OBSIDIAN: choice = new Atlas(p.getName()); break;
				case INK_SACK: choice = new Oceanus(p.getName()); break;
				case GLOWSTONE: choice = new Hyperion(p.getName()); break;
				case FURNACE: choice = new Hephaestus(p.getName()); break;
				}
				if (choice == null) {
					p.sendMessage(ChatColor.YELLOW+"That is not a valid selection item.");
					return true;
				}
				if (!choice.getDefaultAlliance().equalsIgnoreCase(DUtil.getAllegiance(p))) {
					p.sendMessage(ChatColor.RED+"That deity is not of your alliance.");
					return true;
				}
				if (DUtil.hasDeity(p, choice.getName())) {
					p.sendMessage(ChatColor.RED+"You are already allianced to "+choice.getName()+".");
					return true;
				}
				DUtil.giveDeity(p, choice);
				DUtil.setFavorCap(p, DUtil.getFavorCap(p)+100);
				DUtil.setFavor(p, DUtil.getFavor(p)+100);
				//	p.getWorld().strikeLightningEffect((inside.getCenter().toLocationNewWorld(p.getWorld())));
				p.sendMessage(ChatColor.YELLOW+"You have been accepted to the lineage of "+choice.getName()+".");
				return true;
			} else if (c.getName().equalsIgnoreCase("perks")) {
				p.sendMessage(ChatColor.YELLOW+"Coming soon.");
				return true;
				/*
				 * PERK STORE! TODO
				 */ /*else if (c.getName().equalsIgnoreCase("ascend")) {
				if (!DUtil.isFullParticipant(p))
					return true;
				if (DUtil.getLevel(p) < 100) {
					p.sendMessage(ChatColor.YELLOW+"You must be Level 100 in order to Ascend.");
					return true;
				}
				Shrine inside = null;
				for (Shrine s : DSave.getShrines()) {
					if (s.getCenter().getWorld().equals(p.getWorld().getName()))
						if (DUtil.toLocation(s.getCenter()).distance(p.getLocation()) <= 6) { //RADIUS FOR ASCEND
							inside = s;
							break;
						}
				}
				if (inside != null) {
					DUtil.setAscensions(p, DUtil.getAscensions(p)+1);
					DUtil.setLevel(p, 1);
					p.getWorld().strikeLightningEffect(DUtil.toLocation(inside.getCenter()));
					for (int i=0;i<20;i++) {
						p.getWorld().spawn(p.getLocation(), ExperienceOrb.class);
					}
					p.sendMessage(ChatColor.YELLOW+"You feel uplifted as a mystical energy surges through you.");
					p.sendMessage(ChatColor.YELLOW+"You now have "+DUtil.getAscensions(p)+" Ascensions.");
				} else {
					p.sendMessage(ChatColor.YELLOW+"You must be near a Shrine in order to Ascend.");
				}
			} */
			} else if (c.getName().equalsIgnoreCase("value")) {
				if (DUtil.isFullParticipant(p))
					if (p.getItemInHand() != null)
						p.sendMessage(ChatColor.YELLOW+p.getItemInHand().getType().name()+" x"+p.getItemInHand().getAmount()+" is worth "+
								(int)(DUtil.getValue(p.getItemInHand())*ShrineManager.FAVORMULTIPLIER)+" at a shrine.");
				return true;
			} else if (c.getName().equalsIgnoreCase("bindings")) {
				if (!DUtil.isFullParticipant(p))
					return true;
				if (!(DUtil.hasPermission(p, "demigods.bindings") || DUtil.hasPermission(p, "demigods.admin")))
					return true;
				ArrayList<Material> items = DUtil.getBindings(p);
				if ((items != null) && (items.size() > 0)) {
					String disp = ChatColor.YELLOW+"Bound items:";
					for (Material m : items)
						disp += " "+m.name().toLowerCase();
					p.sendMessage(disp);
				}
				else p.sendMessage(ChatColor.YELLOW+"You have no bindings.");
				return true;
			}
			else if (c.isRegistered()) {
				if (!Settings.getEnabledWorlds().contains(p.getWorld())) {
					p.sendMessage(ChatColor.YELLOW+"Demigods is not enabled in this world.");
					return true;
				}
				boolean bind = false;
				if (args.length == 1)
					if (args[0].contains("bind"))
						bind = true;
				if (DUtil.getDeities(p) != null)
					for (Deity d : DUtil.getDeities(p))
						d.onCommand(p, c.getName(), args, bind);
			}
			return false;
		}
	}

}
