package dcrew.rs2.entity.mob.impl;

import dcrew.rs2.content.combat.Hit;
import dcrew.rs2.entity.Location;
import dcrew.rs2.entity.mob.Mob;
import dcrew.rs2.entity.player.Player;

public class Tentacles extends Mob {
	
	public Tentacles(Player player, Location location) {
		super(player, 5535, false, false, false, location);
		getCombat().setAttack(getOwner());
		getOwner().tentacles.add(this);
	}

	@Override
	public void hit(Hit hit) {
	
		if (isDead() || getOwner() == null) {
			return;
		}

		super.hit(hit);


	}

}
