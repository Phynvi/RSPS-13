package dcrew.rs2.content.interfaces.impl;

import dcrew.Constants;
import dcrew.Server;
import dcrew.core.util.Utility;
import dcrew.rs2.content.interfaces.InterfaceHandler;
import dcrew.rs2.entity.World;
import dcrew.rs2.entity.player.Player;
import dcrew.rs2.entity.player.net.out.impl.SendColor;

public class QuestTab extends InterfaceHandler {	
	public QuestTab(Player player) {
		super(player);
		color(16, 0xC71C1C);
		color(17, 0xC71C1C);
	}
	
	public void color(int id, int color) { player.send(new SendColor(startingLine() + id, color)); }
	
	private final String[] text = {
			"@or1@        [ @lre@Game Information @or1@]",
			"@or2@Online Player(s): @whi@" + World.getActivePlayers(),		
			"@or2@Online Staff(s): @whi@" + World.getStaff(),
			"@or2@Online Player Record: @whi@" + Constants.MOST_ONLINE,
			"@or2@Time: @whi@"+ Utility.getCurrentServerTime(),
			"@or2@Date: @whi@"+ Server.vencillioDate(),
			"@or2@Uptime: @whi@"+ Server.getUptime(),
			"@or2@Votes: @whi@" + Utility.format(Constants.CURRENT_VOTES),
			"@or2@Last Voter: @whi@" + Utility.formatPlayerName(Constants.LAST_VOTER) ,
			"@or2@Vesion: @whi@" + Constants.version,
			"",
			"@or1@        [ @lre@Player Information @or1@]",		
			"@or2@Username: @whi@" + Utility.capitalizeFirstLetter(player.getUsername()),
			"@or2@Rank: " + player.determineIcon(player) + player.determineRank(player) ,
			"@or2@Money spent: @whi@$" + Utility.format(player.getMoneySpent()),
			"@or2@Credits: @whi@" + Utility.format(player.getCredits()),			
			"@or2@Log Panel [</col>View@or2@]",
			"",
			"",
	};

	@Override
	protected String[] text() { return text; }
	@Override
	protected int startingLine() { return 29501; }
}