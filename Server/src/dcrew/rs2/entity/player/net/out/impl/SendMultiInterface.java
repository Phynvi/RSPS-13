package dcrew.rs2.entity.player.net.out.impl;

import dcrew.core.network.StreamBuffer;
import dcrew.rs2.entity.player.net.Client;
import dcrew.rs2.entity.player.net.out.OutgoingPacket;

public class SendMultiInterface extends OutgoingPacket {

	private final boolean multi;

	public SendMultiInterface(boolean multi) {
		this.multi = multi;
	}

	@Override
	public void execute(Client client) {
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(5);
		out.writeHeader(client.getEncryptor(), 61);
		out.writeByte(multi ? 1 : 0);
		client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
		return 61;
	}

}
