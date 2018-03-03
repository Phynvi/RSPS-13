package dcrew.rs2.entity.player.net.out.impl;

import dcrew.core.network.StreamBuffer;
import dcrew.rs2.entity.player.net.Client;
import dcrew.rs2.entity.player.net.out.OutgoingPacket;

public class SendModelAnimation extends OutgoingPacket {

	private final int model;

	private final int anim;

	public SendModelAnimation(int model, int anim) {
		super();
		this.model = model;
		this.anim = anim;
	}

	@Override
	public void execute(Client client) {
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(5);
		out.writeHeader(client.getEncryptor(), 200);
		out.writeShort(model);
		out.writeShort(anim);
		client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
		return 200;
	}

}
