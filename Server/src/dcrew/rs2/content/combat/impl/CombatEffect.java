package dcrew.rs2.content.combat.impl;

import dcrew.rs2.entity.Entity;
import dcrew.rs2.entity.player.Player;

public abstract interface CombatEffect {

	public abstract void execute(Player paramPlayer, Entity paramEntity);
}
