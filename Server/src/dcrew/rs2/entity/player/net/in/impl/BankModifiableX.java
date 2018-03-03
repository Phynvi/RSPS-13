package dcrew.rs2.entity.player.net.in.impl;

import dcrew.core.network.StreamBuffer;
import dcrew.core.network.StreamBuffer.ByteOrder;
import dcrew.core.network.StreamBuffer.ValueType;
import dcrew.rs2.entity.player.Player;
import dcrew.rs2.entity.player.net.in.IncomingPacket;

public class BankModifiableX extends IncomingPacket {

	@Override
	public void handle(Player player, StreamBuffer.InBuffer in, int opcode, int length) {
		in.readShort(ValueType.A, ByteOrder.BIG);
		in.readShort();
		int item = in.readShort(ValueType.A, ByteOrder.BIG);
		int amount = in.readInt();
		player.getBank().withdraw(item, amount);
	}

	@Override
	public int getMaxDuplicates() {
		return 1;
	}
}