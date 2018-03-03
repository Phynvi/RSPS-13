package dcrew.rs2.content.skill.magic.effects;

import dcrew.rs2.content.combat.impl.CombatEffect;
import dcrew.rs2.entity.Entity;
import dcrew.rs2.entity.player.Player;

public class SnareEffect implements CombatEffect {
	@Override
	public void execute(Player p, Entity e) {
		e.freeze(10, 5);
	}
}
