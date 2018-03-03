package dcrew.rs2.content.minigames.pestcontrol.monsters;

import dcrew.core.task.Task;
import dcrew.core.task.TaskQueue;
import dcrew.core.task.impl.FollowToEntityTask;
import dcrew.core.util.Utility;
import dcrew.rs2.content.minigames.pestcontrol.Pest;
import dcrew.rs2.content.minigames.pestcontrol.PestControlConstants;
import dcrew.rs2.content.minigames.pestcontrol.PestControlGame;
import dcrew.rs2.entity.Location;

public class Spinner extends Pest {
	
	private final Portal portal;
	private final Task heal = null;

	public Spinner(Location l, PestControlGame game, Portal portal) {
		super(game, PestControlConstants.SPINNERS[Utility.randomNumber(PestControlConstants.SPINNERS.length)], l);
		setRetaliate(false);
		this.portal = portal;
	}

	public void heal() {
		if ((heal == null) || (heal.stopped())) {
			TaskQueue.queue(new FollowToEntityTask(this, portal) {
				@Override
				public void onDestination() {
					getUpdateFlags().sendAnimation(3911, 0);
					portal.heal(5);
					stop();
				}
			});
		}
	}

	@Override
	public void tick() {
		if (portal.isDead()) {
			return;
		}

		if ((portal.isDamaged()) && (Utility.randomNumber(3) == 0)) {
			heal();
		}
	}
}
