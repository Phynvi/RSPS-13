package dcrew.rs2.entity.mob.impl;

import dcrew.core.definitions.NpcCombatDefinition.Magic;
import dcrew.core.definitions.NpcCombatDefinition.Melee;
import dcrew.core.util.Utility;
import dcrew.rs2.content.combat.Combat.CombatTypes;
import dcrew.rs2.content.skill.Skills;
import dcrew.rs2.content.skill.prayer.PrayerBook.Prayer;
import dcrew.rs2.entity.Location;
import dcrew.rs2.entity.Projectile;
import dcrew.rs2.entity.World;
import dcrew.rs2.entity.mob.Mob;
import dcrew.rs2.entity.player.Player;


public class SeaTrollQueen extends Mob {

	private static Projectile getProjectile() {
		return new Projectile(109, 1, 40, 70, 43, 31, 16);
	}

	public SeaTrollQueen() {
		super(4315, false, new Location(2342, 3702));
	}

	@Override
	public int getRespawnTime() {
		return 60;
	}

	@Override
	public void updateCombatType() {
		CombatTypes type = CombatTypes.MELEE;
		if (getCombat().getAttacking() != null) {
			if (!getCombat().getAttacking().isNpc()) {
				Player player = (Player) getCombat().getAttacking();
				if (!getCombat().withinDistanceForAttack(CombatTypes.MELEE, true)) {
					if (player.getPrayer().active(Prayer.PROTECT_FROM_MAGIC)) {
						if (player.getSkill().getLevels()[Skills.PRAYER] > 0) {
							World.sendProjectile(getProjectile(), player, this);
							int modifier = player.getSkill().getLevels()[Skills.PRAYER] - 20 > 0 ? 20 : player.getSkill().getLevels()[Skills.PRAYER];
							player.getPrayer().drain(modifier);
							type = CombatTypes.RANGED;
							getCombat().setAttackTimer(6);
						} else {
							type = CombatTypes.MAGIC;
						}
					} else {
						type = CombatTypes.MAGIC;
					}
				} else {
					if (player.getPrayer().active(Prayer.PROTECT_FROM_MAGIC)) {
						type = CombatTypes.MELEE;
					} else if (player.getPrayer().active(Prayer.PROTECT_FROM_MELEE)) {
						type = CombatTypes.MAGIC;
					} else {
						type = Utility.randomNumber(10) < 5 ? CombatTypes.MELEE : CombatTypes.MAGIC;
					}
				}
			}

			getCombat().setCombatType(type);
			getCombat().setBlockAnimation(getCombatDefinition().getBlock());
			switch (getCombat().getCombatType()) {
			case MAGIC:
				byte combatIndex = (byte) Utility.randomNumber(getCombatDefinition().getMagic().length);
				Magic magic = getCombatDefinition().getMagic()[combatIndex];
				getCombat().getMagic().setAttack(magic.getAttack(), magic.getAnimation(), magic.getStart(), magic.getEnd(), magic.getProjectile());
				break;
			case MELEE:
				combatIndex = (byte) Utility.randomNumber(getCombatDefinition().getMelee().length);
				Melee melee = getCombatDefinition().getMelee()[combatIndex];
				getCombat().getMelee().setAttack(melee.getAttack(), melee.getAnimation());
				break;
			default:
				break;

			}
		}
	}

}
