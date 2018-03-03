package dcrew.rs2.content.minigames.pestcontrol.monsters;

import dcrew.core.util.Utility;
import dcrew.rs2.GameConstants;
import dcrew.rs2.content.minigames.pestcontrol.Pest;
import dcrew.rs2.content.minigames.pestcontrol.PestControlConstants;
import dcrew.rs2.content.minigames.pestcontrol.PestControlGame;
import dcrew.rs2.entity.Location;

public class Shifter extends Pest {

	private byte delay = 0;

	public Shifter(Location location, PestControlGame game) {
		super(game, PestControlConstants.SHIFTERS[Utility.randomNumber(PestControlConstants.SHIFTERS.length)], location);
	}

	@Override
	public void tick() {
		if (++delay == 7) {
			if (Utility.getManhattanDistance(getLocation(), getGame().getVoidKnight().getLocation()) > 2) {
				if (!isMovedLastCycle() && getCombat().getAttackTimer() == 0) {
					if (getCombat().getAttacking() != null) {
						if (getCombat().getAttacking().equals(getGame().getVoidKnight())) {
							Location l = GameConstants.getClearAdjacentLocation(getGame().getVoidKnight().getLocation(), getSize(), getGame().getVirtualRegion());

							if (l != null) {
								teleport(l);
							}
						} else {
							getCombat().setAttack(getGame().getVoidKnight());
						}
					} else {
						getCombat().setAttack(getGame().getVoidKnight());
					}
				}
			}

			delay = 0;
		}
	}
}
