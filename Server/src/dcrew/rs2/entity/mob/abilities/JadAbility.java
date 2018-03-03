package dcrew.rs2.entity.mob.abilities;

import dcrew.rs2.content.combat.Combat.CombatTypes;
import dcrew.rs2.content.combat.CombatEffect;
import dcrew.rs2.entity.Entity;
import dcrew.rs2.entity.Graphic;

public class JadAbility implements CombatEffect {
	@Override
	public void execute(Entity e1, Entity e2) {
		if (e1.getCombat().getCombatType() == CombatTypes.RANGED) {
			e2.getUpdateFlags().sendGraphic(new Graphic(451, 0, 0));
		}
	}
}