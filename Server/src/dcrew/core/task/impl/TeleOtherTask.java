package dcrew.core.task.impl;

import dcrew.core.task.Task;
import dcrew.rs2.content.skill.magic.MagicSkill;
import dcrew.rs2.entity.Entity;
import dcrew.rs2.entity.Graphic;
import dcrew.rs2.entity.Location;
import dcrew.rs2.entity.player.Player;

public class TeleOtherTask extends Task {
	private final Player player;
	private final Location to;

	public TeleOtherTask(Entity entity, Player player, Location to) {
		super(entity, 2);
		this.to = to;
		this.player = player;

		entity.getUpdateFlags().sendAnimation(1818, 0);
		entity.getUpdateFlags().sendGraphic(new Graphic(343, 15, true));
	}

	@Override
	public void execute() {
		player.getMagic().teleport(to.getX(), to.getY(), to.getZ(),
				MagicSkill.TeleportTypes.TELE_OTHER);
		stop();
	}

	@Override
	public void onStop() {
	}
}
