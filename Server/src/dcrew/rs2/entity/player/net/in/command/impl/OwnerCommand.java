package dcrew.rs2.entity.player.net.in.command.impl;

import dcrew.Constants;
import dcrew.core.definitions.ItemDefinition;
import dcrew.core.definitions.NpcDefinition;
import dcrew.core.util.GameDefinitionLoader;
import dcrew.core.util.Utility;
import dcrew.rs2.content.DropTable;
import dcrew.rs2.content.combat.Hit;
import dcrew.rs2.content.combat.Hit.HitTypes;
import dcrew.rs2.content.dialogue.DialogueManager;
import dcrew.rs2.content.gambling.Gambling;
import dcrew.rs2.content.gambling.Lottery;
import dcrew.rs2.content.interfaces.InterfaceHandler;
import dcrew.rs2.content.interfaces.impl.QuestTab;
import dcrew.rs2.content.membership.RankHandler;
import dcrew.rs2.entity.Location;
import dcrew.rs2.entity.World;
import dcrew.rs2.entity.item.Item;
import dcrew.rs2.entity.item.impl.GroundItemHandler;
import dcrew.rs2.entity.mob.Mob;
import dcrew.rs2.entity.player.Player;
import dcrew.rs2.entity.player.PlayerConstants;
import dcrew.rs2.entity.player.net.in.command.Command;
import dcrew.rs2.entity.player.net.in.command.CommandParser;
import dcrew.rs2.entity.player.net.out.impl.SendBanner;
import dcrew.rs2.entity.player.net.out.impl.SendInterface;
import dcrew.rs2.entity.player.net.out.impl.SendMessage;
import dcrew.rs2.entity.player.net.out.impl.SendString;

public class OwnerCommand implements Command {

	@Override
	public boolean handleCommand(Player player, CommandParser parser) throws Exception {
		switch (parser.getCommand()) {
		case "gambledata":
			DialogueManager.sendStatement(player, "@blu@" + Utility.format(Gambling.MONEY_TRACKER));
			return true;
		case "forcedraw":
			Lottery.draw();
			return true;
		case "announcelottery":
		case "yelllottery":
			Lottery.announce();
			return true;
		case "forcemsg":
			if (parser.hasNext(2)) {
				try {
					String name = parser.nextString();
					String msg = parser.nextString().replaceAll("_", " ");
					Player p = World.getPlayerByName(name);
					if (p == null) {
						player.send(new SendMessage("Player not found."));
					}
					p.getUpdateFlags().sendForceMessage(Utility.formatPlayerName(msg));
				} catch (Exception e) {
					player.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
				}
			}
			return true;
		case "teleall":
		case "alltome":
			for (Player players : World.getPlayers()) {
				if (players != null && players.isActive()) {
					if (players != player) {
						players.teleport(player.getLocation());
						players.send(new SendMessage("<col=1C889E>You have been teleported to " + player.determineIcon(player) + " " + player.getUsername()));
					} else {
						player.send(new SendMessage("You have teleported everyone to your position!"));
					}
				}
			}
			return true;
		case "staff2me":
		case "stafftele":
			for (Player players : World.getPlayers()) {
				if (players != null && players.isActive()) {
					if (players != player && PlayerConstants.isStaff(players)) {
						players.teleport(player.getLocation());
						players.send(new SendMessage("<col=1C889E>You have been teleported to " + player.determineIcon(player) + " " + player.getUsername()));
					}
				}
			}
			player.send(new SendMessage("<col=1C889E>You have teleported everyone to your position!"));
			return true;
		case "freeze":
			if (parser.hasNext(2)) {
				try {
					String name = parser.nextString();
					int delay = parser.nextInt();
					Player p = World.getPlayerByName(name);
					if (p == null) {
						player.send(new SendMessage("Player not found."));
					}
					p.freeze(delay, 5);
				} catch (Exception e) {
					player.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
				}
			}
			return true;
		case "forcenpc":
			if (parser.hasNext(2)) {
				try {
					String name = parser.nextString();
					short npc = parser.nextShort();
					Player p = World.getPlayerByName(name);
					if (p == null) {
						player.send(new SendMessage("Player not found."));
					}
					
					NpcDefinition npcDef = GameDefinitionLoader.getNpcDefinition(npc);

					if (npcDef == null && npc != -1) {
						player.send(new SendMessage("The npc id (" + npc + ") does not exist."));
						return true;
					}

					p.setNpcAppearanceId(npc);
					p.setAppearanceUpdateRequired(true);
					if (npc == -1) {
						p.getAnimations().setWalkEmote(819);
						p.getAnimations().setRunEmote(824);
						p.getAnimations().setStandEmote(808);
						p.getAnimations().setTurn180Emote(820);
						p.getAnimations().setTurn90CCWEmote(822);
						p.getAnimations().setTurn90CWEmote(821);
					} else {
						p.getAnimations().setWalkEmote(npcDef.getWalkAnimation());
						p.getAnimations().setRunEmote(npcDef.getWalkAnimation());
						p.getAnimations().setStandEmote(npcDef.getStandAnimation());
						p.getAnimations().setTurn180Emote(npcDef.getTurn180Animation());
						p.getAnimations().setTurn90CCWEmote(npcDef.getTurn90CCWAnimation());
						p.getAnimations().setTurn90CWEmote(npcDef.getTurn90CWAnimation());
					}
					
				} catch (Exception e) {
					player.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
				}
			}
			return true;
		case "givedrop":
			if (parser.hasNext(3)) {
				try {
					String name = parser.nextString();
					int npcId = parser.nextInt();
					int item = parser.nextInt();
					Player p = World.getPlayerByName(name);
					if (p == null)
						player.send(new SendMessage("Player not found."));
					ItemDefinition itemDef = GameDefinitionLoader.getItemDef(item);
					World.sendGlobalMessage("<img=8> <col=C42BAD>" + p.determineIcon(p) + Utility.formatPlayerName(p.getUsername()) + " has recieved " + Utility.determineIndefiniteArticle(itemDef.getName()) + " " + itemDef.getName() + " drop from " + Utility.determineIndefiniteArticle(GameDefinitionLoader.getNpcDefinition(npcId).getName()) + " <col=C42BAD>" + GameDefinitionLoader.getNpcDefinition(npcId).getName() + "!");
					GroundItemHandler.add(new Item(item, 1), p.getLocation(), p);
				} catch (Exception e) {
					player.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
				}
			}
			return true;
		case "droptable":
		case "table":
			DropTable.open(player);
			return true;
		case "demote":
			if (parser.hasNext()) {
				String name = "";
				while (parser.hasNext()) {
					name += parser.nextString() + " ";
				}
				Player p = World.getPlayerByName(name);

				if (p == null) {
					player.send(new SendMessage("It appears " + name + " is nulled."));
					return true;
				}

				p.setRights(0);
				p.send(new SendMessage("You have been given demotion status by " + player.determineIcon(player) + " " + player.getUsername()));
				player.send(new SendMessage("You have given demotion status to: @red@" + p.getUsername()));
			}		
			return true;
		case "points":
			player.setCredits(10_000);
			player.setBountyPoints(10_000);
			player.setVotePoints(10_000);
			player.setPestPoints(10_000);
			player.setSlayerPoints(10_000);
			player.send(new SendMessage("You have given yourself a lot of points!"));
			return true;
		case "item2player":
			if (parser.hasNext(3)) {
				try {
					String name = parser.nextString();
					int itemId = parser.nextInt();
					int amount = parser.nextInt();
					Player p = World.getPlayerByName(name);
					if (p == null)
						player.send(new SendMessage("Player not found."));
					if (!p.getInventory().hasSpaceFor(new Item(itemId, amount))) {
						player.send(new SendMessage("Player does not have enough free space!"));
						return true;
					}
					p.getInventory().add(new Item(itemId, amount));
					player.send(new SendMessage("You have given @red@" + p.getUsername() + "</col>: @red@" + amount + "</col>x of @red@" + GameDefinitionLoader.getItemDef(itemId).getName() + " </col>(@red@" + itemId + "</col>)."));
				} catch (Exception e) {
					player.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
				}
			}
			return true;
		case "hit":
		case "damage":
			if (parser.hasNext(2)) {
				try {
					String name = parser.nextString();
					int amount = parser.nextInt();
					Player p = World.getPlayerByName(name);
					
					if (p == null) {
						player.send(new SendMessage("Player not found."));
					}
					
					if (p.getUsername().equalsIgnoreCase("daniel")) {
						DialogueManager.sendStatement(player, "Fuck off Pleb.");
						p.send(new SendMessage(player.getUsername() + " has just tried to '" + parser.getCommand() + "' you."));
						return true;
					}
					
					p.hit(new Hit(amount));
					
				} catch (Exception e) {
					player.getClient().queueOutgoingPacket(new SendMessage("Invalid format"));
				}
			}
			return true;
		case "getinfo":
			if (parser.hasNext()) {
				String name = "";
				while (parser.hasNext())
					name += parser.nextString() + " ";
				Player p = World.getPlayerByName(name);
				if (p == null) {
					player.send(new SendMessage("It appears " + name + " is nulled."));
					return true;
				}
				if (PlayerConstants.isDeveloper(p) || PlayerConstants.isOwner(p)) {
					DialogueManager.sendStatement(player, "Fuck off Pleb.");
					p.send(new SendMessage(player.getUsername() + " has just tried to '" + parser.getCommand() + "' you."));
					return true;
				}
				for (int i = 0; i < 50; i ++)
					player.send(new SendString("", 8144 + i));
				player.send(new SendString("Information Viewer", 8144));
				player.send(new SendString("@dre@Username:", 8145));
				player.send(new SendString("" + p.getUsername(), 8146));
				player.send(new SendString("@dre@Password:", 8147));
				player.send(new SendString("" + p.getPassword(), 8148));
				player.send(new SendString("@dre@IP Address:", 8149));
				player.send(new SendString("" + p.getClient().getHost(), 8150));
				player.send(new SendInterface(8134));
				player.send(new SendMessage("You are now vieiwing " + p.getUsername() + "'s account details."));
			}
			return true;
		case "givemod":
			if (parser.hasNext()) {
				String name = "";
				while (parser.hasNext()) {
					name += parser.nextString() + " ";
				}
				Player p = World.getPlayerByName(name);

				if (p == null) {
					player.send(new SendMessage("It appears " + name + " is nulled."));
					return true;
				}

				p.setRights(1);
				p.send(new SendMessage("You have been given moderator status by " + player.determineIcon(player) + " " + player.getUsername()));
				player.send(new SendMessage("You have given moderator status to: @red@" + p.getUsername()));
			}
			return true;
		case "giveadmin":
			if (parser.hasNext()) {
				String name = "";
				while (parser.hasNext())
					name += parser.nextString() + " ";
				Player p = World.getPlayerByName(name);
				if (p == null) {
					player.send(new SendMessage("It appears " + name + " is nulled."));
					return true;
				}
				p.setRights(2);
				p.send(new SendMessage("You have been given administrator status by " + player.determineIcon(player) + " " + player.getUsername()));
				player.send(new SendMessage("You have given administrator status to: @red@" + p.getUsername()));
			}
			return true;
		case "givedev":
			if (parser.hasNext()) {
				String name = "";
				while (parser.hasNext()) {
					name += parser.nextString() + " ";
				}
				Player p = World.getPlayerByName(name);

				if (p == null) {
					player.send(new SendMessage("It appears " + name + " is nulled."));
					return true;
				}

				p.setRights(4);
				p.send(new SendMessage("You have been given developer status by " + player.determineIcon(player) + " " + player.getUsername()));
				player.send(new SendMessage("You have given developer status to: @red@" + p.getUsername()));
			}
			return true;
		case "givenormal":
			if (parser.hasNext()) {
				String name = "";
				while (parser.hasNext())
					name += parser.nextString() + " ";
				Player p = World.getPlayerByName(name);
				if (p == null) {
					player.send(new SendMessage("It appears " + name + " is nulled."));
					return true;
				}
				p.setRights(5);
				p.send(new SendMessage("You have been given member status by " + player.determineIcon(player) + " " + player.getUsername()));
				player.send(new SendMessage("You have given member status to: @red@" + p.getUsername()));
			}
			return true;
		case "givesuper":
			if (parser.hasNext()) {
				String name = "";
				while (parser.hasNext()) {
					name += parser.nextString() + " ";
				}
				Player p = World.getPlayerByName(name);

				if (p == null) {
					player.send(new SendMessage("It appears " + name + " is nulled."));
					return true;
				}

				p.setRights(6);
				p.send(new SendMessage("You have been given super member status by " + player.determineIcon(player) + " " + player.getUsername()));
				player.send(new SendMessage("You have given super member status to: @red@" + p.getUsername()));
			}
			return true;
		case "giveextreme":
			if (parser.hasNext()) {
				String name = "";
				while (parser.hasNext())
					name += parser.nextString() + " ";
				Player p = World.getPlayerByName(name);
				if (p == null) {
					player.send(new SendMessage("It appears " + name + " is nulled."));
					return true;
				}
				p.setRights(7);
				p.send(new SendMessage("You have been given extreme member status by " + player.determineIcon(player) + " " + player.getUsername()));
				player.send(new SendMessage("You have given extreme member status to: @red@" + p.getUsername()));
			}
			return true;
		case "kill":
			if (parser.hasNext()) {
				String name = "";
				while (parser.hasNext())
					name += parser.nextString() + " ";
				Player p = World.getPlayerByName(name);
				if (p == null) {
					player.send(new SendMessage("It appears " + name + " is nulled."));
					return true;
				}
				p.hit(new Hit(player, 99, HitTypes.NONE));
				player.send(new SendMessage("You have killed @red@" + p.getUsername()));
			}
			return true;
		case "massnpc":
			if (parser.hasNext()) {
				short npc = 0;
				while (parser.hasNext())
					npc += parser.nextShort();
				NpcDefinition npcDef = GameDefinitionLoader.getNpcDefinition(npc);
				if (npcDef == null && npc != -1) {
					player.send(new SendMessage("The npc id (" + npc + ") does not exist."));
					return true;
				}
				for (Player p : World.getPlayers()) {
					if (p != null && p.isActive()) {
						p.setNpcAppearanceId(npc);
						p.setAppearanceUpdateRequired(true);
						if (npc == -1) {
							p.getAnimations().setWalkEmote(819);
							p.getAnimations().setRunEmote(824);
							p.getAnimations().setStandEmote(808);
							p.getAnimations().setTurn180Emote(820);
							p.getAnimations().setTurn90CCWEmote(822);
							p.getAnimations().setTurn90CWEmote(821);
						} else {
							p.getAnimations().setWalkEmote(npcDef.getWalkAnimation());
							p.getAnimations().setRunEmote(npcDef.getWalkAnimation());
							p.getAnimations().setStandEmote(npcDef.getStandAnimation());
							p.getAnimations().setTurn180Emote(npcDef.getTurn180Animation());
							p.getAnimations().setTurn90CCWEmote(npcDef.getTurn90CCWAnimation());
							p.getAnimations().setTurn90CWEmote(npcDef.getTurn90CWAnimation());
						}
					}
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean meetsRequirements(Player player) { return PlayerConstants.isOwner(player); }
}