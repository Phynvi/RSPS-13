package dcrew;

import dcrew.Constants;
import dcrew.core.definitions.NpcDefinition;
import dcrew.core.network.mysql.VoteUpdater;
import dcrew.core.definitions.ItemDefinition;
import dcrew.core.util.GameDefinitionLoader;
import dcrew.core.util.Utility;
import dcrew.rs2.content.PlayersOnline;
import dcrew.rs2.content.Yelling;
import dcrew.rs2.content.dialogue.DialogueManager;
import dcrew.rs2.content.dialogue.impl.ChangePasswordDialogue;
import dcrew.core.network.StreamBuffer;
import dcrew.rs2.content.io.PlayerSave;
import dcrew.rs2.content.io.PlayerSave.PlayerContainer;
import dcrew.rs2.content.io.PlayerSave.PlayerDetails;
import dcrew.rs2.content.io.PlayerSaveUtil;
import dcrew.rs2.content.skill.Skill;
import dcrew.rs2.content.skill.Skills;
import dcrew.rs2.entity.Location;
import dcrew.rs2.entity.World;
import dcrew.rs2.entity.item.Item;
import dcrew.rs2.entity.player.Player;
import dcrew.rs2.entity.player.PlayerConstants;
import dcrew.rs2.entity.player.net.in.IncomingPacket;
import dcrew.rs2.entity.player.net.out.impl.SendInterface;
import dcrew.rs2.entity.player.net.out.impl.SendInventory;
import dcrew.rs2.entity.player.net.out.impl.SendInventoryInterface;
import dcrew.rs2.entity.player.net.out.impl.SendEquipment;
import dcrew.rs2.entity.player.net.out.impl.SendMessage;
import dcrew.rs2.entity.player.net.out.impl.SendRemoveInterfaces;
import dcrew.rs2.entity.player.net.out.impl.SendString;
import dcrew.rs2.entity.player.net.out.impl.SendUpdateItems;

public class Commands extends IncomingPacket {
	static final String[] playerCommands = {
			"@bla@::players - shows active players",
			"::changepass @dre@password @bla@- change your password",
			"::yell @dre@message @bla@- send a global chat message",
			"@bla@::empty - empty your inventory",
			"@bla@::jail - visit jail"
		};
	static final String[] modCommands = {
			"::checkbank @dre@player @bla@- check player's bank",
			"::jail @dre@player @bla@| @dre@hours @bla@- jail player for x hours (no hours for perm)",
			"::unjail @dre@player @bla@- unjail player",
			"::kick @dre@player @bla@- kick player from the server",
			"::mute @dre@player @bla@| @dre@hours @bla@- mute player for x hours (no hours for perm)",
			"::unmute @dre@player @bla@- unmute player",
			"::ban @dre@player @bla@| @dre@name @bla@- ban player for x hours (no hours for perm)",
			"::unban @dre@player @bla@- unban player"
		};
	static final String[] adminCommands = {
			"@bla@::bank - open the bank",
			"::max @dre@skill @bla@- max skill (no skill for all)",
			"::item @dre@id @bla@| @dre@amount @bla@- spawn item amount with id",
			"::copy @dre@player @bla@- copy player's gender, equipment & items",
			"::pnpc @dre@id @bla@- turn yourself into npc with id"
		};
	static final String[] devCommands = {
			"::update @dre@time @bla@| @dre@restart? @bla@- call a server update"
		};
	static final String[] ownerCommands = {
			"::makemod @dre@player @bla@- make player a moderator",
			"::makeadmin @dre@player @bla@- make player an administrator",
			"::makedev @dre@player @bla@- make player a developer"
		};

	@Override
	public int getMaxDuplicates() { return 1; }
	@Override
	public void handle(Player player, StreamBuffer.InBuffer in, int opcode, int length) { process(player, in.readString().trim()); }

	public void process(Player player, String command) {
		if (command.startsWith("/")) {
			if (player.clan != null) {
				String msg = command.substring(1);
				if (msg.contains("<img") || msg.contains("<col")) {
					player.send(new SendMessage("Those symbols have been disabled."));
					return;
				}
				player.clan.sendChat(player, msg);
				return;
			} else if (player.clan == null) {
				player.getClient().queueOutgoingPacket(new SendMessage("You can only do this while in a clan chat."));
				return;
			}
		}
		try {
			String[] args = command.split("\\s+");
			switch (args[0].toLowerCase()) {
			case "commands":
				player.send(new SendString((Constants.name + " Command List"), 8144));
				for (int i = 0; i < 53; i++)
					if (i >= (playerCommands.length + modCommands.length + adminCommands.length + devCommands.length)) {
						if (!PlayerConstants.isOwner(player) || (i >= (playerCommands.length + modCommands.length + adminCommands.length + devCommands.length + ownerCommands.length)))
							break;
						player.send(new SendString(ownerCommands[i - playerCommands.length - modCommands.length - adminCommands.length - devCommands.length], (8145 + i)));
					}
					else if (i >= (playerCommands.length + modCommands.length + adminCommands.length)) {
						if (!(PlayerConstants.isDeveloper(player) || PlayerConstants.isOwner(player)) || (i >= (playerCommands.length + modCommands.length + adminCommands.length + devCommands.length)))
							break;
						player.send(new SendString(devCommands[i - playerCommands.length - modCommands.length - adminCommands.length], (8145 + i)));
					}
					else if (i >= (playerCommands.length + modCommands.length)) {
						if (!(PlayerConstants.isAdmin(player) || PlayerConstants.isDeveloper(player) || PlayerConstants.isOwner(player)) || (i >= (playerCommands.length + modCommands.length + adminCommands.length)))
							break;
						player.send(new SendString(adminCommands[i - playerCommands.length - modCommands.length], (8145 + i)));
					}
					else if (i >= playerCommands.length) {
						if (!PlayerConstants.isStaff(player) || (i >= (playerCommands.length + modCommands.length)))
							break;
						player.send(new SendString(modCommands[i - playerCommands.length], (8145 + i)));
					}
					else if (i < playerCommands.length)
						player.send(new SendString(playerCommands[i], (8145 + i)));
				player.send(new SendInterface(8134));
				break;
				case "claim":
				case "voted":
				case "claimvote":
					VoteUpdater.update(player);
					break;
				case "players":
				case "online":
					player.send(new SendMessage("There are currently @red@" + Utility.format(World.getActivePlayers()) + "</col> players online."));
					PlayersOnline.showPlayers(player, p -> { return true; });
					break;
				case "changepass":
				case "changepassword":
					try {
						String password = args[1];
						if ((password.length() > 4) && (password.length() < 15))
							player.start(new ChangePasswordDialogue(player, password));
						else
							DialogueManager.sendStatement(player, new String[] { "Your password must be between 4 and 15 characters!" });
					} catch (Exception e) {
						player.getClient().queueOutgoingPacket(new SendMessage("Invalid password format, syntax: ::changepass [password]"));
					}
					break;
				case "yell":
					Yelling.yell(player, command.substring(1));
					break;
				case "empty":
					player.getInventory().clear();
					player.send(new SendMessage("You have emptied your inventory!"));
					player.send(new SendRemoveInterfaces());
					break;
				case "kick":
					if (!PlayerConstants.isStaff(player))
						break;
					Player target = getPlayer(args[1], true);
					if (target == null)
						player.send(new SendMessage("Player @dre@" + Utility.formatPlayerName(args[1]) + "@bla@ not found or is offline!"));
					else if (PlayerConstants.isStaff(target) && !PlayerConstants.isOwner(player))
						break;
					else
						target.logout(true);
					break;
				case "tpto":
				case "teleto":
					if (!PlayerConstants.isStaff(player))
						break;
					target = getPlayer(args[1], true);
					if (target == null)
						player.send(new SendMessage("Player @dre@" + Utility.formatPlayerName(args[1]) + "@bla@ not found or is offline!"));
					else
						player.teleport(target.getLocation());
					break;
				case "tptome":
				case "teletome":
					if (!PlayerConstants.isStaff(player))
						break;
					target = getPlayer(args[1], true);
					if (target == null)
						player.send(new SendMessage("Player @dre@" + Utility.formatPlayerName(args[1]) + "@bla@ not found or is offline!"));
					else
						target.teleport(player.getLocation());
				case "ban":
					if (!PlayerConstants.isStaff(player))
						break;
					target = getPlayer(args[1], false);
					if (target == null) {
						player.send(new SendMessage("Player @dre@" + Utility.formatPlayerName(args[1]) + "@bla@ not found!"));
						break;
					}
					if (PlayerConstants.isStaff(target) && !PlayerConstants.isOwner(player))
						break;
					int hours = 0;
					if (args.length >= 3)
						try { hours = Integer.parseInt(args[2]); }
						catch (Exception e) { }
					target.setBanned(true);
					if (hours <= 0)
						target.setBanLength(-1);
					else
						target.setBanLength(System.currentTimeMillis() + (hours * 3_600_000));
					if (!target.isLoggedIn())
						PlayerSave.save(target);
					else
						target.logout(true);
					player.send(new SendMessage("You have banned @dre@" + target.getUsername() + "@bla@ " + ((hours <= 0) ? "permanently" : (hours == 1) ? "for 1 hour" : ("for " + hours + " hours")) + "!"));
					break;
				case "unban":
					if (!PlayerConstants.isStaff(player))
						break;
					target = getPlayer(args[1], false);
					if (target == null) {
						player.send(new SendMessage("Player @dre@" + Utility.formatPlayerName(args[1]) + "@bla@ not found!"));
						break;
					}
					if (PlayerSaveUtil.unbanOfflinePlayer(target.getUsername())) {
						target.setBanned(false);
						PlayerSave.save(target);
						player.send(new SendMessage("You have unbanned @dre@" + target.getUsername() + "@bla@!"));
					}
					break;
				case "mute":
					if (!PlayerConstants.isStaff(player))
						break;
					target = getPlayer(args[1], false);
					if (target == null) {
						player.send(new SendMessage("Player @dre@" + Utility.formatPlayerName(args[1]) + "@bla@ not found!"));
						break;
					}
					if (PlayerConstants.isStaff(target) && !PlayerConstants.isOwner(player))
						break;
					hours = 0;
					if (args.length >= 3)
						try { hours = Integer.parseInt(args[2]); }
						catch (Exception e) { }
					target.setBanned(true);
					if (hours <= 0)
						target.setBanLength(-1);
					else
						target.setBanLength(System.currentTimeMillis() + (hours * 3_600_000));
					if (!target.isLoggedIn())
						PlayerSave.save(target);
					else
						target.send(new SendMessage("You have been muted " + ((hours <= 0) ? "permanently" : (hours == 1) ? "for 1 hour" : ("for " + hours + " hours")) + "!"));
					player.send(new SendMessage("You have muted @dre@" + target.getUsername() + "@bla@ " + ((hours <= 0) ? "permanently" : (hours == 1) ? "for 1 hour" : ("for " + hours + " hours")) + "!"));
					break;
				case "unmute":
					if (!PlayerConstants.isStaff(player))
						break;
					target = getPlayer(args[1], false);
					if (target == null) {
						player.send(new SendMessage("Player @dre@" + Utility.formatPlayerName(args[1]) + "@bla@ not found!"));
						break;
					}
					if (PlayerSaveUtil.unmuteOfflinePlayer(target.getUsername())) {
						target.setMuted(false);
						target.setMuteLength(0);
						if (!target.isLoggedIn())
							PlayerSave.save(target);
						else
							target.send(new SendMessage("You have been unmuted!"));
						player.send(new SendMessage("You have unmuted @dre@" + target.getUsername() + "@bla@!"));
					}
					break;
				case "jail":
					if (args.length == 1) {
						player.teleport(new Location(2767, 2795, 0));
						break;
					}
					if (!PlayerConstants.isStaff(player))
						break;
					target = getPlayer(args[1], false);
					if (target == null) {
						player.send(new SendMessage("Player @dre@" + Utility.formatPlayerName(args[1]) + "@bla@ not found!"));
						break;
					}
					if (PlayerConstants.isStaff(target) && !PlayerConstants.isOwner(player))
						break;
					hours = 0;
					if (args.length >= 3)
						try { hours = Integer.parseInt(args[2]); }
						catch (Exception e) { }
					target.setJailed(true);
					if (hours <= 0)
						target.setJailLength(-1);
					else
						target.setJailLength(System.currentTimeMillis() + (hours * 3_600_000));
					if (!target.isLoggedIn())
						PlayerSave.save(target);
					else
						target.send(new SendMessage("You have been jailed " + ((hours <= 0) ? "permanently" : (hours == 1) ? "for 1 hour" : ("for " + hours + " hours")) + "!"));
					player.send(new SendMessage("You have jailed @dre@" + target.getUsername() + "@bla@ " + ((hours <= 0) ? "permanently" : (hours == 1) ? "for 1 hour" : ("for " + hours + " hours")) + "!"));
					break;
				case "unjail":
					if (!PlayerConstants.isStaff(player))
						break;
					target = getPlayer(args[1], false);
					if (target == null) {
						player.send(new SendMessage("Player @dre@" + Utility.formatPlayerName(args[1]) + "@bla@ not found!"));
						break;
					}
					if (PlayerSaveUtil.unJailOfflinePlayer(target.getUsername())) {
						target.setJailed(false);
						target.setJailLength(0);
						if (!target.isLoggedIn())
							PlayerSave.save(target);
						else
							target.send(new SendMessage("You have been unjailed!"));
						player.send(new SendMessage("You have unjailed @dre@" + target.getUsername() + "@bla@!"));
					}
					break;
				case "checkbank":
					if (!PlayerConstants.isStaff(player))
						break;
					target = getPlayer(args[1], false);
					if (target == null) {
						player.send(new SendMessage("Player @dre@" + Utility.formatPlayerName(args[1]) + "@bla@ not found!"));
						break;
					}
					if (PlayerConstants.isStaff(target) && !PlayerConstants.isOwner(player))
						break;
					player.send(new SendMessage("@blu@" + target.getUsername() + " has " + Utility.format(target.getMoneyPouch()) + " in their pouch."));
					player.send(new SendUpdateItems(5064, target.getInventory().getItems()));
					player.send(new SendUpdateItems(5382, target.getBank().getItems(), target.getBank().getTabAmounts()));
					player.send(new SendInventory(target.getInventory().getItems()));
					player.send(new SendString("" + target.getBank().getTakenSlots(), 22033));
					player.send(new SendInventoryInterface(5292, 5063));
					break;
				case "bank":
					if (!(PlayerConstants.isAdmin(player) || PlayerConstants.isDeveloper(player) || PlayerConstants.isOwner(player)))
						break;
					player.getBank().openBank();
					break;
				case "master":
				case "max":
					if (!(PlayerConstants.isAdmin(player) || PlayerConstants.isDeveloper(player) || PlayerConstants.isOwner(player)))
						break;
					if (args.length >= 2) {
						int id = -1;
						switch (args[1]) {
							case "attack": case "atk": id = Skills.ATTACK; break;
							case "defence": case "def": id = Skills.DEFENCE; break;
							case "strength": case "str": id = Skills.STRENGTH; break;
							case "hitpoints": case "hp": id = Skills.HITPOINTS; break;
							case "ranged": case "range": id = Skills.RANGED; break;
							case "prayer": case "pray": id = Skills.PRAYER; break;
							case "magic": case "mage": id = Skills.MAGIC; break;
							case "cooking": case "cook": id = Skills.COOKING; break;
							case "woodcutting": case "wc": id = Skills.WOODCUTTING; break;
							case "fletching": case "fletch": id = Skills.FLETCHING; break;
							case "fishing": case "fish": id = Skills.FISHING; break;
							case "firemaking": case "fm": id = Skills.FIREMAKING; break;
							case "crafting": case "craft": id = Skills.CRAFTING; break;
							case "smithing": case "smith": id = Skills.SMITHING; break;
							case "mining": case "mine": id = Skills.MINING; break;
							case "herblore": case "herb": id = Skills.HERBLORE; break;
							case "agility": case "agil": id = Skills.AGILITY; break;
							case "thieving": case "thiev": id = Skills.THIEVING; break;
							case "slayer": case "slay": id = Skills.SLAYER; break;
							case "farming": case "farm": id = Skills.FARMING; break;
							case "runecrafting": case "rc": id = Skills.RUNECRAFTING; break;
							case "summoning": case "summon": id = Skills.SUMMONING; break;
							case "hunter": case "hunt": id = Skills.HUNTER; break;
							case "construction": case "cons": id = Skills.CONSTRUCTION; break;
							case "dungeoneering": case "dungeon": id = Skills.DUNGEONEERING; break;
						}
						if (id == -1) {
							player.send(new SendMessage("Could not parse skill id @dre@" + args[1] + "@bla@!"));
							break;
						} else {
		                	if (player.getLevels()[id] < 99)
		                		player.getLevels()[id] = 99;
		                    player.getMaxLevels()[id] = 99;
		                    player.getSkill().getExperience()[id] = Skill.EXP_FOR_LEVEL[99];
						}
					} else
		                for (int i = 0; i < Skills.SKILL_COUNT; i++) {
		                	if (player.getLevels()[i] < 99)
		                		player.getLevels()[i] = 99;
		                    player.getMaxLevels()[i] = 99;
		                    player.getSkill().getExperience()[i] = Skill.EXP_FOR_LEVEL[99];
		                }
	                player.getSkill().update();
	                player.setAppearanceUpdateRequired(true);
					break;
				case "item":
				case "give":
					if (!(PlayerConstants.isAdmin(player) || PlayerConstants.isDeveloper(player) || PlayerConstants.isOwner(player)))
						break;
					if (args.length >= 2)
						try {
							int id = Integer.parseInt(args[1]),
									amount = 1;
							if (args.length >= 3)
								try { amount = Integer.parseInt(args[2].replaceAll("k", "000").replaceAll("m", "000000").replaceAll("b", "000000000")); }
								catch (Exception e) { player.send(new SendMessage("Could not parse the amount @dre@" + args[1] + "@bla@!")); }
							if (player.inWGGame())
								break;
							player.getInventory().add(id, amount);
		                    ItemDefinition def = GameDefinitionLoader.getItemDef(id);
		                    player.send(new SendMessage("You have spawed @dre@" + Utility.format(amount) + "@bla@ of the item @dre@" + def.getName() + "@bla@!"));
						}
						catch (Exception e) { player.send(new SendMessage("Could not parse the item @dre@" + args[1] + "@bla@!")); }
					break;
				case "copy":
					if (!(PlayerConstants.isAdmin(player) || PlayerConstants.isDeveloper(player) || PlayerConstants.isOwner(player)))
						break;
					if (args.length >= 2)
						try {
							target = World.getPlayerByName(args[1]);
							if ((target == null) || args[1].equalsIgnoreCase(player.getUsername())) {
								target = new Player();
								target.setUsername(args[1]);
								if (!PlayerSave.load(target)) {
									player.send(new SendMessage("Player @dre@" + args[1] + "@bla@ not found!"));
									break;
								}
							}
		                    player.setGender(target.getGender());
		                	player.getInventory().clear();
		                    for (int i = 0; i < target.getEquipment().getItems().length; i++) {
		                    	if (target.getEquipment().getItems()[i] == null) {
		                    		player.getEquipment().getItems()[i] = new Item(0, 0);
		                    		continue;
		                    	}
		                    	player.getEquipment().getItems()[i] = new Item(target.getEquipment().getItems()[i].getId(), target.getEquipment().getItems()[i].getAmount());
		                    }
		                    player.getEquipment().update();
		            		player.getEquipment().calculateBonuses();
		            		player.setAppearanceUpdateRequired(true);
		                    for (int i = 0; i < target.getInventory().getItems().length; i++) {
		                    	if (target.getInventory().items[i] == null)
		                    		continue;
		                    	player.getInventory().getItems()[i] = new Item(target.getInventory().getItems()[i].getId(), target.getInventory().getItems()[i].getAmount());
		                    }
		            		player.getInventory().update();
		            		player.getCombat().reset();
		            		player.getUpdateFlags().setUpdateRequired(true);
		            		player.send(new SendMessage("You have copied @dre@" + target.getUsername() + "@bla@!"));
						}
						catch (Exception e) { player.send(new SendMessage("Could not copy the player @dre@" + args[1] + "@bla@!")); }
					break;
				case "pnpc":
					if (!(PlayerConstants.isAdmin(player) || PlayerConstants.isDeveloper(player) || PlayerConstants.isOwner(player)))
						break;
					if (args.length >= 2)
						try {
							int id = -1;
							try { id = Integer.parseInt(args[1]); }
							catch (Exception e) {
								player.send(new SendMessage("Could not parse id @dre@" + args[1] + "@bla@!"));
								break;
							}
							NpcDefinition npcDef = GameDefinitionLoader.getNpcDefinition(id);
							player.getAnimations().setWalkEmote(npcDef.getWalkAnimation());
							player.getAnimations().setRunEmote(npcDef.getWalkAnimation());
							player.getAnimations().setStandEmote(npcDef.getStandAnimation());
							player.getAnimations().setTurn180Emote(npcDef.getTurn180Animation());
							player.getAnimations().setTurn90CCWEmote(npcDef.getTurn90CCWAnimation());
							player.getAnimations().setTurn90CWEmote(npcDef.getTurn90CWAnimation());
							player.send(new SendMessage("You have turned into @dre@" + npcDef.getName() + "@bla@!"));
						}
						catch (Exception e) { }
					else {
						player.getAnimations().setWalkEmote(819);
						player.getAnimations().setRunEmote(824);
						player.getAnimations().setStandEmote(808);
						player.getAnimations().setTurn180Emote(820);
						player.getAnimations().setTurn90CCWEmote(822);
						player.getAnimations().setTurn90CWEmote(821);
						player.send(new SendMessage("You have reset your appearance!"));
					}
					break;
				case "update":
					if (!(PlayerConstants.isDeveloper(player) || PlayerConstants.isOwner(player)))
						break;
					if (args.length >= 2)
						try {
							int time = Integer.parseInt(args[1]);
							if (args.length >= 3)
								try {
									boolean restart = Boolean.parseBoolean(args[2]);
									World.initUpdate(time, restart);
									break;
								}
								catch (Exception e) { }
							World.initUpdate(time, false);
						}
						catch (Exception e) { }
					break;
				case "makemod":
					if (!PlayerConstants.isOwner(player))
						break;
					target = getPlayer(args[1], false);
					if (target == null) {
						player.send(new SendMessage("Player @dre@" + Utility.formatPlayerName(args[1]) + "@bla@ not found!"));
						break;
					}
					target.setRights(1);
					if (!target.isLoggedIn())
						PlayerSave.save(target);
					else
						target.send(new SendMessage("You have been made a moderator by @dre@" + player.getUsername() + "@bla@!"));
					player.send(new SendMessage("You have made @dre@" + target.getUsername() + " @bla@a moderator!"));
					break;
				case "makeadmin":
					if (!PlayerConstants.isOwner(player))
						break;
					target = getPlayer(args[1], false);
					if (target == null) {
						player.send(new SendMessage("Player @dre@" + Utility.formatPlayerName(args[1]) + "@bla@ not found!"));
						break;
					}
					target.setRights(2);
					if (!target.isLoggedIn())
						PlayerSave.save(target);
					else
						target.send(new SendMessage("You have been made an administrator by @dre@" + player.getUsername() + "@bla@!"));
					player.send(new SendMessage("You have made @dre@" + target.getUsername() + " @bla@an administrator!"));
					break;
				case "makedev":
					if (!PlayerConstants.isOwner(player))
						break;
					target = getPlayer(args[1], false);
					if (target == null) {
						player.send(new SendMessage("Player @dre@" + Utility.formatPlayerName(args[1]) + "@bla@ not found!"));
						break;
					}
					target.setRights(4);
					if (!target.isLoggedIn())
						PlayerSave.save(target);
					else
						target.send(new SendMessage("You have been made a developer by @dre@" + player.getUsername() + "@bla@!"));
					player.send(new SendMessage("You have made @dre@" + target.getUsername() + " @bla@a developer!"));
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	Player getPlayer(String name, boolean onlyIfOnline) {
		Player target = World.getPlayerByName(name);
		if (onlyIfOnline)
			return target;
		if (target == null) {
			target = new Player();
			target.setUsername(Utility.formatPlayerName(name));
			try {
				if (!PlayerSave.load(target))
					return null;
			}
			catch (Exception e) { return null; }
		}
		return target;
	}
}