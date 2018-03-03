package dcrew.rs2.content.skill.crafting;

import dcrew.core.task.Task;
import dcrew.core.task.impl.ProductionTask;
import dcrew.core.task.impl.TaskIdentifier;
import dcrew.core.util.GameDefinitionLoader;
import dcrew.core.util.Utility;
import dcrew.rs2.entity.Animation;
import dcrew.rs2.entity.Graphic;
import dcrew.rs2.entity.item.Item;
import dcrew.rs2.entity.player.Player;

public class GlassBlowing extends ProductionTask {
	private short productionCount;
	private Glass glass;

	public GlassBlowing(Player entity, short productionCount, Glass glass) {
		super(entity, 0, false, Task.StackType.NEVER_STACK,
				Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
		this.productionCount = productionCount;
		this.glass = glass;
	}

	@Override
	public boolean canProduce() {
		return true;
	}

	@Override
	public Animation getAnimation() {
		return new Animation(884);
	}

	@Override
	public Item[] getConsumedItems() {
		return new Item[] { new Item(glass.getMaterialId()) };
	}

	@Override
	public int getCycleCount() {
		return 7;
	}

	@Override
	public double getExperience() {
		return glass.getExperience();
	}

	@Override
	public Graphic getGraphic() {
		return null;
	}

	@Override
	public String getInsufficentLevelMessage() {
		return "You need a "
				+ dcrew.rs2.content.skill.Skills.SKILL_NAMES[getSkill()]
				+ " level of " + getRequiredLevel() + " to blow this glass.";
	}

	@Override
	public int getProductionCount() {
		return productionCount;
	}

	@Override
	public int getRequiredLevel() {
		return glass.getRequiredLevel();
	}

	@Override
	public Item[] getRewards() {
		return new Item[] { new Item(glass.getRewardId()) };
	}

	@Override
	public int getSkill() {
		return 12;
	}

	@Override
	public String getSuccessfulProductionMessage() {
		String itemName = GameDefinitionLoader
				.getItemDef(getRewards()[0].getId()).getName().toLowerCase();
		return "you make " + (Utility.startsWithVowel(itemName) ? "an" : "a")
				+ " " + itemName + ".";
	}

	@Override
	public String noIngredients(Item item) {
		return null;
	}

	@Override
	public void onStop() {
	}
}
