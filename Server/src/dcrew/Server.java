package dcrew;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;

import dcrew.core.GameThread;
import dcrew.core.network.mysql.HiscoreUpdater;
import dcrew.core.network.mysql.MembershipRewards;
import dcrew.core.network.mysql.VoteUpdater;
import dcrew.core.util.logger.PlayerLogger;
import dcrew.rs2.content.clanchat.ClanManager;
import dcrew.rs2.content.io.PlayerSave;
import dcrew.rs2.entity.World;
import dcrew.rs2.entity.player.Player;

public class Server {
	static Logger logger = Logger.getLogger(Server.class.getSimpleName());

	public static ClanManager clanManager = new ClanManager();

	public static String vencillioTime() { return new SimpleDateFormat("HH:mm aa").format(new Date()); }
	public static String vencillioDate() { return new SimpleDateFormat("EEEE MMM dd yyyy ").format(new Date()); }
	public static String getUptime() {
		RuntimeMXBean mx = ManagementFactory.getRuntimeMXBean();
		DateFormat df = new SimpleDateFormat("DD 'D', HH 'H', mm 'M'");
		df.setTimeZone(TimeZone.getTimeZone("GMT+0"));
		return "" + df.format(new Date(mx.getUptime()));
	}

	public static void main(String[] args) {
		if (args != null && args.length > 0)
			Constants.DEV_MODE = Boolean.valueOf(args[0]);
		if (!Constants.DEV_MODE) {
			try {
				MembershipRewards.prepare();
				HiscoreUpdater.prepare();
				VoteUpdater.prepare();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				for (Player players : World.getPlayers())
					if ((players != null) && players.isActive())
						PlayerSave.save(players);
				MembershipRewards.shutdown();
				HiscoreUpdater.shutdown();
				VoteUpdater.shutdown();
				PlayerLogger.SHUTDOWN_LOGGER.log("Logs", String.format("Server shutdown with %s online.", World.getActivePlayers()));
			}));
		}
		GameThread.init();
	}
}