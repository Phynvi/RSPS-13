package dcrew.rs2.content.combat;

import dcrew.rs2.entity.Entity;

public abstract interface CombatEffect {
	
	public abstract void execute(Entity paramEntity1, Entity paramEntity2);
}
