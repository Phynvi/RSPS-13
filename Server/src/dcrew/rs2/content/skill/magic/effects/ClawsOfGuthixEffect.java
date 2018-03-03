package dcrew.rs2.content.skill.magic.effects;

import dcrew.core.util.Utility;
import dcrew.rs2.content.combat.impl.CombatEffect;
import dcrew.rs2.entity.Entity;
import dcrew.rs2.entity.player.Player;

public class ClawsOfGuthixEffect implements CombatEffect {
	@Override
	public void execute(Player p, Entity e) {
		if ((Utility.randomNumber(4) == 0) && (p.getLastDamageDealt() > 0)
				&& (!e.isNpc())) {
			Player other = dcrew.rs2.entity.World.getPlayers()[e.getIndex()];

			if (other != null) {
				int tmp39_38 = 1;
				short[] tmp39_35 = other.getLevels();
				tmp39_35[tmp39_38] = ((short) (int) (tmp39_35[tmp39_38] - other
						.getLevels()[1] * 0.05D));

				if (other.getLevels()[1] < 0) {
					other.getLevels()[1] = 0;
				}

				other.getSkill().update(1);
			}
		}
	}
}
