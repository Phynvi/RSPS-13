package dcrew.rs2.content.combat.special.specials;

import dcrew.rs2.content.combat.special.Special;
import dcrew.rs2.entity.Animation;
import dcrew.rs2.entity.Graphic;
import dcrew.rs2.entity.player.Player;

public class DragonLongswordSpecialAttack implements Special {
	@Override
	public boolean checkRequirements(Player player) {
		return true;
	}

	@Override
	public int getSpecialAmountRequired() {
		return 25;
	}

	@Override
	public void handleAttack(Player player) {
		player.getCombat().getMelee().setAnimation(new Animation(1058, 0));
		player.getUpdateFlags().sendGraphic(Graphic.highGraphic(248, 3));
	}
}
