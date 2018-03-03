package dcrew.rs2.entity.player.net;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;

import dcrew.core.network.ISAACCipher;
import dcrew.core.network.ReceivedPacket;
import dcrew.core.util.Utility;
import dcrew.core.util.Utility.Stopwatch;
import dcrew.rs2.entity.World;
import dcrew.rs2.entity.mob.Mob;
import dcrew.rs2.entity.player.Player;
import dcrew.rs2.entity.player.net.in.PacketHandler;
import dcrew.rs2.entity.player.net.out.OutgoingPacket;

public class Client {
	public enum Stages { CONNECTED, LOGGING_IN, LOGGED_IN, LOGGED_OUT }

	Channel channel;
	final List<Mob> mobs = new LinkedList<Mob>();
	Queue<ReceivedPacket> incomingPackets = new ConcurrentLinkedQueue<ReceivedPacket>();
	Queue<OutgoingPacket> outgoingPackets = new ConcurrentLinkedQueue<OutgoingPacket>();
	final Utility.Stopwatch timeoutStopwatch = new Utility.Stopwatch();
	Stages stage = Stages.LOGGING_IN;
	ISAACCipher encryptor;
	ISAACCipher decryptor;
	Player player;
	PacketHandler packetHandler;
	String host;
	long hostId = 0;
	boolean logPlayer = false;
	String enteredPassword = null;
	String lastPlayerOption = "";
	long lastPacketTime = World.getCycles();

	public Client(Channel channel) {
		try {
			this.channel = channel;
			if (channel != null) {
				host = channel.getRemoteAddress().toString();
				host = host.substring(1, host.indexOf(":"));
				hostId = Utility.nameToLong(host);
			} else {
				host = "none";
				hostId = -1;
			}
			player = new Player(this);
			packetHandler = new PacketHandler(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {
		if (outgoingPackets != null)
			synchronized (outgoingPackets) {
				outgoingPackets = null;
			}
	}
	public synchronized ISAACCipher getDecryptor() { return decryptor; }
	public synchronized ISAACCipher getEncryptor() { return encryptor; }
	public String getEnteredPassword() { return enteredPassword; }
	public String getHost() { return host; }
	public long getHostId() { return hostId; }
	public long getLastPacketTime() { return lastPacketTime; }
	public String getLastPlayerOption() { return lastPlayerOption; }
	public List<Mob> getNpcs() { return mobs; }
	public Queue<OutgoingPacket> getOutgoingPackets() { return outgoingPackets; }
	public Player getPlayer() { return player; }
	public Stages getStage() { return stage; }
	public Stopwatch getTimeoutStopwatch() { return timeoutStopwatch; }
	public boolean isLogPlayer() { return logPlayer; }

	public void processIncomingPackets() {
		ReceivedPacket p = null;
		try {
			if (outgoingPackets == null)
				return;
			synchronized (incomingPackets) {
				if (outgoingPackets == null)
					return;
				while ((p = incomingPackets.poll()) != null)
					packetHandler.handlePacket(p);
			}
		} catch (Exception e) {
			e.printStackTrace();
			player.logout(true);
			return;
		}
	}

	public void processOutgoingPackets() {
		if ((channel == null) || (outgoingPackets == null))
			return;
		try {
			synchronized (channel) {
				if (channel == null)
					return;
				synchronized (outgoingPackets) {
					if (outgoingPackets == null)
						return;
					OutgoingPacket p = null;
					while ((p = outgoingPackets.poll()) != null)
						p.execute(this);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void queueIncomingPacket(ReceivedPacket packet) {
		resetLastPacketReceived();
		synchronized (incomingPackets) {
			incomingPackets.offer(packet);
		}
	}

	public void queueOutgoingPacket(OutgoingPacket o) {
		if (outgoingPackets == null)
			return;
		synchronized (outgoingPackets) {
			if (outgoingPackets == null)
				return;
			outgoingPackets.offer(o);
		}
	}

	public void reset() { packetHandler.reset(); }
	public void resetLastPacketReceived() { lastPacketTime = World.getCycles(); }

	public void send(ChannelBuffer buffer) {
		try {
			if ((channel == null) || !channel.isConnected())
				return;
			synchronized (outgoingPackets) {
				channel.write(buffer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setDecryptor(ISAACCipher decryptor) { this.decryptor = decryptor; }
	public void setEncryptor(ISAACCipher encryptor) { this.encryptor = encryptor; }
	public void setEnteredPassword(String enteredPassword) { this.enteredPassword = enteredPassword; }
	public void setHost(String host) { this.host = host; }
	public void setLastPlayerOption(String lastPlayerOption) { this.lastPlayerOption = lastPlayerOption; }
	public void setLogPlayer(boolean logPlayer) { this.logPlayer = logPlayer; }
	public void setStage(Stages stage) { this.stage = stage; }
	
	private Map<Integer, TinterfaceText> interfaceText = new HashMap<Integer, TinterfaceText>();
	
	public class TinterfaceText {
		public int id;
		public String currentState;
		
		public TinterfaceText(String s, int id) {
			this.currentState = s;
			this.id = id;
		}
	}

	public boolean checkSendString(String text, int id) {
		if (!interfaceText.containsKey(id))
			interfaceText.put(id, new TinterfaceText(text, id));
		else {
			TinterfaceText t = interfaceText.get(id);
			if (text.equals(t.currentState))
				return false;
			t.currentState = text;
		}
		return true;
	}
}