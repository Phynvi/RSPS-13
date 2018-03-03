package dcrew.rs2.content.skill.magic.effects;

import dcrew.rs2.content.combat.impl.CombatEffect;
import dcrew.rs2.entity.Entity;
import dcrew.rs2.entity.player.Player;
import dcrew.rs2.entity.player.net.out.impl.SendMessage;

public class IceRushEffect implements CombatEffect {
	@Override
	public void execute(Player p, Entity e) {
		if (!e.isNpc() && !e.isFrozen()) {
			Player p2 = dcrew.rs2.entity.World.getPlayers()[e.getIndex()];
			if (p2 == null) {
				return;
			}
			p2.getClient().queueOutgoingPacket(
					new SendMessage("You have been frozen."));
		}
		e.freeze(5, 5);
	}
}
