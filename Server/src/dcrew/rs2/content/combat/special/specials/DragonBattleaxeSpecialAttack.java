package dcrew.rs2.content.combat.special.specials;

import dcrew.rs2.content.combat.special.Special;
import dcrew.rs2.entity.player.Player;

public class DragonBattleaxeSpecialAttack implements Special {
	@Override
	public boolean checkRequirements(Player player) {
		return false;
	}

	@Override
	public int getSpecialAmountRequired() {
		return 0;
	}

	@Override
	public void handleAttack(Player player) {
	}
}
