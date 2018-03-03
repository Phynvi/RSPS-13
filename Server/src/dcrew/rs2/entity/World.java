package dcrew.rs2.entity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dcrew.core.task.Task;
import dcrew.core.task.TaskQueue;
import dcrew.core.util.Utility;
import dcrew.core.util.MobUpdateList;
import dcrew.rs2.content.combat.CombatConstants;
import dcrew.rs2.content.dwarfcannon.DwarfCannon;
import dcrew.rs2.content.gambling.Lottery;
import dcrew.rs2.content.io.PlayerSave;
import dcrew.rs2.content.minigames.pestcontrol.PestControl;
import dcrew.rs2.content.minigames.weapongame.WeaponGame;
import dcrew.rs2.entity.mob.Mob;
import dcrew.rs2.entity.mob.MobUpdateFlags;
import dcrew.rs2.entity.player.Player;
import dcrew.rs2.entity.player.PlayerConstants;
import dcrew.rs2.entity.player.PlayerUpdateFlags;
import dcrew.rs2.entity.player.net.Client;
import dcrew.rs2.entity.player.net.out.impl.SendGameUpdateTimer;
import dcrew.rs2.entity.player.net.out.impl.SendMessage;
import dcrew.rs2.entity.player.net.out.impl.SendNPCUpdate;
import dcrew.rs2.entity.player.net.out.impl.SendPlayerUpdate;
import dcrew.rs2.entity.player.net.out.impl.SendProjectile;
import dcrew.rs2.entity.player.net.out.impl.SendStillGraphic;

public class World {
	public static final short MAX_PLAYERS = 2048;
	public static final short MAX_MOBS = 8192;
	public static boolean worldUpdating = false;
	public static long getCycles() { return cycles; }
	public static Mob[] getNpcs() { return mobs; }

	static final Player[] players = new Player[MAX_PLAYERS];
	static final Mob[] mobs = new Mob[MAX_MOBS];
	static long cycles = 0L;
	static MobUpdateList mobUpdateList = new MobUpdateList();
	static List<DwarfCannon> cannons = new ArrayList<DwarfCannon>();
	static short updateTimer = -1;
	static boolean updating = false;
	static boolean ignoreTick = false;

	public static void addCannon(DwarfCannon cannon) { cannons.add(cannon); }

	public static int getActivePlayers() {
		int r = 0;
		for (Player p : players)
			if (p != null)
				r++;
		return r;
	}

	public static Player getPlayerByName(long n) {
		for (Player p : players)
			if ((p != null) && p.isActive() && (p.getUsernameToLong() == n))
				return p;
		return null;
	}

	public static Player getPlayerByName(String username) {
		if (username == null)
			return null;
		long n = Utility.nameToLong(username.toLowerCase());
		for (Player p : players)
			if ((p != null) && p.isActive() && (p.getUsernameToLong() == n))
				return p;
		return null;
	}

	public static Player[] getPlayers() { return players; }

	public static void initUpdate(int time, boolean reboot) {
		Lottery.draw();
		worldUpdating = true;
		for (Player p : players)
			if (p != null)
				p.getClient().queueOutgoingPacket(new SendGameUpdateTimer(time));
		TaskQueue.queue(new Task((int) Math.ceil((time * 5) / 3.0)) {
			@Override
			public void execute() {
				for (Player p : players)
					if (p != null) {
						p.logout(true);
						PlayerSave.save(p);
					}
				stop();
			}
			@Override
			public void onStop() {
				if (reboot)
					try {
						ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "run.bat");
						processBuilder.directory(new File("./"));
						processBuilder.start();
					} catch (IOException e) {
						e.printStackTrace();
					}
				System.exit(0);
			}
		});
	}

	public static boolean isIgnoreTick() { return ignoreTick; }
	public static boolean isMobWithinRange(int mobIndex) { return ((mobIndex > -1) && (mobIndex < mobs.length)); }
	public static boolean isPlayerWithinRange(int playerIndex) { return ((playerIndex > -1) && (playerIndex < players.length)); }
	public static boolean isUpdating() { return updating; }

	public static int npcAmount() {
		int amount = 0;
		for (int i = 1; i < mobs.length; i++)
			if (mobs[i] != null)
				amount++;
		return amount;
	}

	public static void process() {
		PlayerUpdateFlags[] pFlags = new PlayerUpdateFlags[players.length];
		MobUpdateFlags[] nFlags = new MobUpdateFlags[mobs.length];
		try {
			//FightPits.tick();
			PestControl.tick();
			WeaponGame.tick();
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (DwarfCannon c : cannons)
			c.tick();
		for (int i = 1; i < 2048; i++) {
			Player player = players[i];
			try {
				if (player != null) {
					if (!player.isActive())
						if (player.getClient().getStage() == Client.Stages.LOGGED_IN) {
							player.setActive(true);
							player.start();
							player.getClient().resetLastPacketReceived();
						} else if (getCycles() - player.getClient().getLastPacketTime() > 30) {
							player.logout(true);
						}
					player.getClient().processIncomingPackets();
					player.process();
					player.getClient().reset();
					for (DwarfCannon c : cannons)
						if (c.getLoc().isViewableFrom(player.getLocation()))
							c.rotate(player);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				if (player != null)
					player.logout(true);
			}
		}
		for (int i = 0; i < mobs.length; i++) {
			Mob mob = mobs[i];
			if (mob != null)
				try {
					mob.process();
				} catch (Exception e) {
					e.printStackTrace();
					mob.remove();
				}
		}
		for (int i = 1; i < 2048; i++) {
			Player player = players[i];
			if ((player == null) || !player.isActive())
				pFlags[i] = null;
			else
				try {
					player.getMovementHandler().process();
					pFlags[i] = new PlayerUpdateFlags(player);
				} catch (Exception ex) {
					ex.printStackTrace();
					player.logout(true);
				}
		}
		for (int i = 0; i < mobs.length; i++) {
			Mob mob = mobs[i];
			if (mob != null)
				try {
					mob.processMovement();
					nFlags[mob.getIndex()] = new MobUpdateFlags(mob);
				} catch (Exception e) {
					e.printStackTrace();
					mob.remove();
				}
		}
		for (int i = 1; i < 2048; i++) {
			Player player = players[i];
			if ((player != null) && (pFlags[i] != null) && player.isActive())
				try {
					player.getClient().queueOutgoingPacket(new SendPlayerUpdate(pFlags));
					player.getClient().queueOutgoingPacket(new SendNPCUpdate(nFlags, pFlags[i]));
				} catch (Exception ex) {
					ex.printStackTrace();
					player.logout(true);
				}
		}
		for (int i = 1; i < 2048; i++) {
			Player player = players[i];
			if ((player != null) && player.isActive())
				try {
					player.reset();
				} catch (Exception ex) {
					ex.printStackTrace();
					player.logout(true);
				}
		}
		for (int i = 0; i < mobs.length; i++) {
			Mob mob = mobs[i];
			if (mob != null)
				try {
					mob.reset();
				} catch (Exception e) {
					e.printStackTrace();
					mob.remove();
				}
		}
		if ((updateTimer > -1) && ((World.updateTimer = (short)(updateTimer - 1)) == 0))
			update();
		if (ignoreTick)
			ignoreTick = false;
		cycles += 1L;
	}

	public static int register(Mob mob) {
		for (int i = 1; i < mobs.length; i++)
			if (mobs[i] == null) {
				mobs[i] = mob;
				mob.setIndex(i);
				return i;
			}
		return -1;
	}

	public static int register(Player player) {
		int[] ids = new int[players.length];
		int c = 0;
		for (int i = 1; i < players.length; i++)
			if (players[i] == null) {
				ids[c] = i;
				c++;
			}
		if (c == 0)
			return -1;
		int index = ids[Utility.randomNumber(c)];
		players[index] = player;
		player.setIndex(index);
		for (int k = 1; k < players.length; k++)
			if ((players[k] != null) && players[k].isActive())
				players[k].getPrivateMessaging().updateOnlineStatus(player, true);
		if (updateTimer > -1)
			player.getClient().queueOutgoingPacket(new SendGameUpdateTimer(updateTimer));
		return c;
	}

	public static void remove(List<Mob> local) {
	}

	public static void removeCannon(DwarfCannon cannon) { cannons.remove(cannon); }

	public static void resetUpdate() {
		updateTimer = -1;
		synchronized (players) {
			for (Player p : players)
				if (p != null)
					p.getClient().queueOutgoingPacket(new SendGameUpdateTimer(0));
		}
	}

	public static void sendGlobalMessage(String message, boolean format) {
		message = (format ? "<col=255>" : "") + message + (format ? "</col>" : "");
		for (Player p : players)
			if ((p != null) && (p.isActive()))
				p.getClient().queueOutgoingPacket(new SendMessage(message));
	}

	public static void sendGlobalMessage(String message) {
		for (Player i : World.getPlayers())
			if (i != null)
				i.getClient().queueOutgoingPacket(new SendMessage(message));
	}

	public static void sendGlobalMessage(String message, Player exceptions) {
		for (Player i : World.getPlayers())
			if (i != null)
				if (i != exceptions)
					i.getClient().queueOutgoingPacket(new SendMessage(message));
	}

	public static void sendProjectile(Projectile p, Entity e1, Entity e2) {
		int lockon = e2.isNpc() ? e2.getIndex() + 1 : -e2.getIndex() - 1;
		byte offsetX = (byte) ((e1.getLocation().getY() - e2.getLocation().getY()) * -1);
		byte offsetY = (byte) ((e1.getLocation().getX() - e2.getLocation().getX()) * -1);
		sendProjectile(p, CombatConstants.getOffsetProjectileLocation(e1), lockon, offsetX, offsetY);
	}

	public static void sendProjectile(Projectile projectile, Location pLocation, int lockon, byte offsetX, byte offsetY) {
		for (Player player : players)
			if (player != null)
				if (pLocation.isViewableFrom(player.getLocation()))
					player.getClient().queueOutgoingPacket(new SendProjectile(player, projectile, pLocation, lockon, offsetX, offsetY));
	}

	public static void sendStillGraphic(int id, int delay, Location location) {
		for (Player player : players)
			if ((player != null) && (location.isViewableFrom(player.getLocation())))
				player.getClient().queueOutgoingPacket(new SendStillGraphic(id, location, delay));
	}

	public static void sendRegionMessage(String message, Location location) {
		for (Player player : players)
			if (player != null && location.isViewableFrom(player.getLocation()))
				player.send(new SendMessage(message));
	}

	public static void setIgnoreTick(boolean ignore) { ignoreTick = ignore; }

	public static void unregister(Mob mob) {
		if (mob.getIndex() == -1)
			return;
		mobs[mob.getIndex()] = null;
		mobUpdateList.toRemoval(mob);
	}

	public static void unregister(Player player) {
		if ((player.getIndex() == -1) || (players[player.getIndex()] == null))
			return;
		players[player.getIndex()] = null;
		for (int i = 0; i < players.length; i++)
			if ((players[i] != null) && players[i].isActive())
				players[i].getPrivateMessaging().updateOnlineStatus(player, false);
	}

	public static void update() {
		updating = true;
		for (Player p : players)
			if (p != null)
				p.logout(true);
	}
	
	public static int getStaff() {
		int amount = 0;
		for (Player players : World.getPlayers())
			if (players != null)
				if (PlayerConstants.isStaff(players))
					amount++;
		return amount;
	}
}