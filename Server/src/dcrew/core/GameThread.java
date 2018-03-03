package dcrew.core;

import dcrew.Constants;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import dcrew.GameDataLoader;
import dcrew.Constants;
import dcrew.core.network.PipelineFactory;
import dcrew.core.task.TaskQueue;
import dcrew.core.util.LineCounter;
import dcrew.core.util.Stopwatch;
import dcrew.core.util.SystemLogger;
import dcrew.rs2.entity.World;
import dcrew.rs2.entity.item.impl.GroundItemHandler;
import dcrew.rs2.entity.object.ObjectManager;

public class GameThread extends Thread {
	private static Logger logger = Logger.getLogger(GameThread.class.getSimpleName());

	public static void init() {
		try {
			try {
				startup();
			} catch (Exception e) {
				e.printStackTrace();
			}
			new GameThread();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void startup() throws Exception {
		Stopwatch timer = new Stopwatch().reset();
		logger.info("Launching " + Constants.name + "..");
		if (!Constants.DEV_MODE)
			System.setErr(new SystemLogger(System.err, new File("./data/logs/err")));
		if (Constants.DEV_MODE)
			LineCounter.run();
		logger.info("Loading game data..");
		GameDataLoader.load();
		while (!GameDataLoader.loaded())
			Thread.sleep(200);
		logger.info("Binding port and initializing threads..");
		ServerBootstrap serverBootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		serverBootstrap.setPipelineFactory(new PipelineFactory());
		new LoginThread();
		new NetworkThread();
		while (true) {
			try {
				serverBootstrap.bind(new InetSocketAddress(43594));
				break;
			} catch (ChannelException e2) {
				logger.info("Server could not bind port - sleeping..");
				Thread.sleep(2000);
			}
		}
		logger.info("Server successfully launched. (took " + (timer.elapsed() / 1000) + " seconds)");
	}

	private GameThread() {
		setName("Main Thread");
		setPriority(Thread.MAX_PRIORITY);
		start();
	}

	private void cycle() {
		try {
			TaskQueue.process();
			GroundItemHandler.process();
			ObjectManager.process();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			World.process();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			while (!Thread.interrupted()) {
				long s = System.nanoTime();
				cycle();
				long e = (System.nanoTime() - s) / 1_000_000;
				if (e < 600)
					if (e < 400) {
						for (int i = 0; i < 30; i++) {
							long sleep = (600 - e) / 30;
							Thread.sleep(sleep);
						}
					} else
						Thread.sleep(600 - e);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}