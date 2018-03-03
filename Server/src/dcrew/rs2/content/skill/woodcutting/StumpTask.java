package dcrew.rs2.content.skill.woodcutting;

import dcrew.core.cache.map.RSObject;
import dcrew.core.cache.map.Region;
import dcrew.core.task.Task;
import dcrew.core.task.impl.TaskIdentifier;
import dcrew.rs2.entity.object.GameObject;
import dcrew.rs2.entity.object.ObjectManager;

public class StumpTask extends Task {
	private GameObject object;
	private final int treeId;

	public StumpTask(GameObject object, int treeId, int delay) {
		super(delay, false, Task.StackType.STACK, Task.BreakType.NEVER, TaskIdentifier.CURRENT_ACTION);
		this.treeId = treeId;
		this.object = object;
	}

	@Override
	public void execute() {
		ObjectManager.removeFromList(object);

		RSObject rsObject = new RSObject(object.getLocation().getX(), object.getLocation().getY(), object.getLocation().getZ(), treeId, 10, 0);
		Region.getRegion(object.getLocation().getX(), object.getLocation().getY()).addObject(rsObject);

		ObjectManager.send(new GameObject(treeId, object.getLocation().getX(), object.getLocation().getY(), object.getLocation().getZ(), 10, 0));
		stop();
	}

	@Override
	public void onStop() {
	}
}
