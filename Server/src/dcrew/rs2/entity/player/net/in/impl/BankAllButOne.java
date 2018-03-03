package dcrew.rs2.entity.player.net.in.impl;

import dcrew.core.network.StreamBuffer;
import dcrew.core.network.StreamBuffer.ByteOrder;
import dcrew.core.network.StreamBuffer.ValueType;
import dcrew.rs2.entity.player.Player;
import dcrew.rs2.entity.player.net.in.IncomingPacket;

public class BankAllButOne extends IncomingPacket {

	@Override
	public void handle(Player player, StreamBuffer.InBuffer in, int opcode, int length) {
		in.readShort(ValueType.A, ByteOrder.BIG);
		in.readShort();
		int item = in.readShort(ValueType.A, ByteOrder.BIG);
		if (player.getBank().hasItemId(item) && player.getBank().getItemAmount(item) > 1) {
			player.getBank().withdraw(item, player.getBank().getItemAmount(item) - 1);
		}
	}

	@Override
	public int getMaxDuplicates() {
		return 1;
	}
}