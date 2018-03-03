package dcrew.rs2.content.combat.special;

import dcrew.rs2.entity.player.Player;

public abstract interface Special {
	
	public abstract boolean checkRequirements(Player player);

	public abstract int getSpecialAmountRequired();

	public abstract void handleAttack(Player player);
}
