package dcrew.rs2.entity.player.net.in.command;

import dcrew.rs2.entity.player.Player;

public abstract interface Command {
	public abstract boolean handleCommand(Player player, CommandParser parser) throws Exception;
	public abstract boolean meetsRequirements(Player player);
}