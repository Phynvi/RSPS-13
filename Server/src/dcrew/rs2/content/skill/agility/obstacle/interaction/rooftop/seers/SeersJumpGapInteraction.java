package dcrew.rs2.content.skill.agility.obstacle.interaction.rooftop.seers;

import dcrew.core.task.Task;
import dcrew.core.task.TaskQueue;
import dcrew.rs2.content.skill.agility.obstacle.interaction.ObstacleInteraction;
import dcrew.rs2.entity.Animation;
import dcrew.rs2.entity.Location;
import dcrew.rs2.entity.player.Player;

public interface SeersJumpGapInteraction extends ObstacleInteraction {
	
	@Override
	public default void start(Player player) {
		player.getUpdateFlags().sendFaceToDirection(player.getX() - 1, player.getY());
	}
	
	@Override
	public default void onExecution(Player player, Location start, Location end) {
		TaskQueue.queue(new Task(player, 1, true) {
			int ticks = 0;
			
			@Override
			public void execute() {
				switch (ticks++) {
				case 1:
					player.getUpdateFlags().sendAnimation(new Animation(2586));
					break;
					
				case 2:
					player.getUpdateFlags().sendAnimation(new Animation(2588));
					player.teleport(new Location(start.getX() - 2, start.getY(), 2));
					break;
				
				case 4:
					player.teleport(end);
					player.getUpdateFlags().sendAnimation(new Animation(2588));
					stop();
					break;
				}
			}

			@Override
			public void onStop() {
			}
		});
	}
	
	@Override
	public default void onCancellation(Player player) {
		// Climbing has nothing special on cancellation.
	}
}