package dcrew.rs2.entity.player.net.out.impl;

import dcrew.core.network.StreamBuffer;
import dcrew.rs2.entity.player.net.Client;
import dcrew.rs2.entity.player.net.out.OutgoingPacket;

public class SendLogout extends OutgoingPacket {

	@Override
	public void execute(Client client) {
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(1);
		out.writeHeader(client.getEncryptor(), 109);
		client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
		return 109;
	}

}