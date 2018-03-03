package dcrew.rs2.content.skill.herblore;

import dcrew.core.task.Task;
import dcrew.core.task.TaskQueue;
import dcrew.core.task.impl.TaskIdentifier;
import dcrew.rs2.entity.Animation;
import dcrew.rs2.entity.item.Item;
import dcrew.rs2.entity.player.Player;

public class HerbloreGrindingTask extends Task {
	public static void handleGrindingIngredients(Player player, Item used,
			Item usedWith) {
		int itemId = used.getId() != 233 ? used.getId() : usedWith.getId();
		GrindingData data = GrindingData.forId(itemId);
		if (data == null)
			return;
		player.getUpdateFlags().sendAnimation(new Animation(364));
		TaskQueue.queue(new HerbloreGrindingTask(player, data));
	}

	private final Player player;

	private final GrindingData data;

	public HerbloreGrindingTask(Player player, GrindingData data) {
		super(player, 1, true, Task.StackType.NEVER_STACK,
				Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
		this.player = player;
		this.data = data;
	}

	private void createGroundItem() {
		player.getInventory().remove(data.getItemId(), 1);
		player.getInventory().add(new Item(data.getGroundId(), 1));
	}

	@Override
	public void execute() {
		createGroundItem();
		stop();
	}

	@Override
	public void onStop() {
	}
}
