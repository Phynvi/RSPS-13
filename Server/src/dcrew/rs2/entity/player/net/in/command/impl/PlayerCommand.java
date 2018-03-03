package dcrew.rs2.entity.player.net.in.command.impl;

import dcrew.Constants;
import dcrew.core.network.mysql.VoteUpdater;
import dcrew.core.util.Utility;
import dcrew.rs2.content.PlayersOnline;
import dcrew.rs2.content.Yelling;
import dcrew.rs2.content.dialogue.DialogueManager;
import dcrew.rs2.content.dialogue.OptionDialogue;
import dcrew.rs2.content.dialogue.impl.ChangePasswordDialogue;
import dcrew.rs2.content.interfaces.InterfaceHandler;
import dcrew.rs2.content.interfaces.impl.CommandInterface;
import dcrew.rs2.content.interfaces.impl.TrainingInterface;
import dcrew.rs2.content.profiles.PlayerProfiler;
import dcrew.rs2.content.skill.Skill;
import dcrew.rs2.content.skill.Skills;
import dcrew.rs2.content.skill.magic.MagicSkill.TeleportTypes;
import dcrew.rs2.entity.World;
import dcrew.rs2.entity.player.Player;
import dcrew.rs2.entity.player.net.in.command.Command;
import dcrew.rs2.entity.player.net.in.command.CommandParser;
import dcrew.rs2.entity.player.net.out.impl.SendInterface;
import dcrew.rs2.entity.player.net.out.impl.SendMessage;
import dcrew.rs2.entity.player.net.out.impl.SendRemoveInterfaces;
import dcrew.rs2.entity.player.net.out.impl.SendString;

public class PlayerCommand implements Command {

	@Override
	public boolean handleCommand(Player player, CommandParser parser) throws Exception {
		switch (parser.getCommand()) {
		case "voted":
		case "claimvote":
		case "claimvotes":
			VoteUpdater.update(player);
			return true;
		case "command":
		case "commands":
		case "commandlist":
		case "commandslist":
			player.send(new SendString((Constants.name + " Command List"), 8144));
			InterfaceHandler.writeText(new CommandInterface(player));
			player.send(new SendInterface(8134));
			return true;
		case "teleport":
		case "teleports":
		case "teleporting":
		case "teleportings":
			InterfaceHandler.writeText(new TrainingInterface(player));
			player.send(new SendInterface(61000));
			player.send(new SendString("Selected: @red@None", 61031));
			player.send(new SendString("Cost: @red@Free", 61032));
			player.send(new SendString("Requirement: @red@None", 61033));
			player.send(new SendString("Other: @red@None", 61034));
			return true;
		case "players":
			player.send(new SendMessage("There are currently @red@" + Utility.format(World.getActivePlayers()) + "</col> players online."));
			PlayersOnline.showPlayers(player, p -> { return true; });
			return true;
		case "find":
			if (parser.hasNext()) {
				String name = parser.nextString();
				while (parser.hasNext())
					name += " " + parser.nextString();
				name = name.trim();
				PlayerProfiler.search(player, name);
			}
			return true;
		case "withdrawmp":
			if (parser.hasNext()) {
				try {
					int amount = 1;
					if (parser.hasNext()) {
						long temp = Long.parseLong(parser.nextString().toLowerCase().replaceAll("k", "000").replaceAll("m", "000000").replaceAll("b", "000000000"));
						if (temp > Integer.MAX_VALUE)
							amount = Integer.MAX_VALUE;
						else
							amount = (int) temp;
					}
					player.getPouch().withdrawPouch(amount);
				} catch (Exception e) {
					player.send(new SendMessage("Something went wrong!"));
					e.printStackTrace();
				}
			}
			return true;
		case "changepassword":
		case "changepass":
			if (parser.hasNext()) {
				try {
					String password = parser.nextString();
					if ((password.length() > 4) && (password.length() < 15))
						player.start(new ChangePasswordDialogue(player, password));
					else
						DialogueManager.sendStatement(player, new String[] { "Your password must be between 4 and 15 characters." });
				} catch (Exception e) {
					player.getClient().queueOutgoingPacket(new SendMessage("Invalid password format, syntax: ::changepass password here"));
				}
			}
			return true;
		case "yelltitle":
			if (player.getRights() == 0 || player.getRights() == 5) {
				player.send(new SendMessage("You need to be a super or extreme member to do this!"));
				return true;
			}
			if (parser.hasNext()) {
				try {
					String message = parser.nextString();
					while (parser.hasNext())
						message += " " + parser.nextString();
					for (int i = 0; i < Constants.BAD_STRINGS.length; i++)
						if (message.contains(Constants.BAD_STRINGS[i])) {
							player.send(new SendMessage("You may not use that in your title!"));
							return true;
						}
					for (int i = 0; i < Constants.BAD_TITLES.length; i++)
						if (message.contains(Constants.BAD_TITLES[i])) {
							player.send(new SendMessage("You may not use that in your title!"));
							return true;
						}
					player.setYellTitle(message);
					DialogueManager.sendTimedStatement(player, "Your yell title is now @red@" + message);
				} catch (Exception e) {
					player.getClient().queueOutgoingPacket(new SendMessage("Invalid yell format, syntax: -title"));
				}
			}
			return true;
		case "yell":
			if (parser.hasNext()) {
				try {
					String message = parser.nextString();
					while (parser.hasNext())
						message += " " + parser.nextString();
					Yelling.yell(player, message.trim());
				} catch (Exception e) {
					player.getClient().queueOutgoingPacket(new SendMessage("Invalid yell format, syntax: -messsage"));
				}
			}
			return true;
		case "empty":
			if (player.getRights() == 2 || player.getRights() == 3) {
				player.getInventory().clear();
				player.send(new SendMessage("You have emptied your inventory."));
				player.send(new SendRemoveInterfaces());
				return true;
			}
			player.start(new OptionDialogue("Yes, empty my inventory.", p -> {
				p.getInventory().clear();
				p.send(new SendMessage("You have emptied your inventory."));
				p.send(new SendRemoveInterfaces());
			} , "Wait, nevermind!", p -> p.send(new SendRemoveInterfaces())));
			return true;
		case "home":
			if (player.getWildernessLevel() > 20 && player.inWilderness()) {
				player.send(new SendMessage("You cannot teleport above 20 wilderness!"));
				return true;
			}
			player.getMagic().teleport(3087, 3492, 0, TeleportTypes.SPELL_BOOK);
			return true;
		}
		return false;
	}

	@Override
	public boolean meetsRequirements(Player player) { return true; }
}