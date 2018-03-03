package dcrew.rs2.content.skill.summoning.impl;

import dcrew.rs2.content.skill.summoning.FamiliarMob;
import dcrew.rs2.content.skill.summoning.FamiliarSpecial;
import dcrew.rs2.entity.Location;
import dcrew.rs2.entity.World;
import dcrew.rs2.entity.item.Item;
import dcrew.rs2.entity.item.impl.GroundItemHandler;
import dcrew.rs2.entity.player.Player;

public class SpiritSpider implements FamiliarSpecial {
	@Override
	public boolean execute(Player player, FamiliarMob mob) {
		mob.getUpdateFlags().sendForceMessage("Clicketyclack");

		Location a = new Location(player.getX() + 1, player.getY(),
				player.getZ());
		Location b = new Location(player.getX(), player.getY() + 1,
				player.getZ());

		GroundItemHandler.add(new Item(223), a, player);
		GroundItemHandler.add(new Item(223), b, player);

		World.sendStillGraphic(1342, 0, a);
		World.sendStillGraphic(1342, 0, b);
		return true;
	}

	@Override
	public int getAmount() {
		return 6;
	}

	@Override
	public double getExperience() {
		return 0.2D;
	}

	@Override
	public FamiliarSpecial.SpecialType getSpecialType() {
		return FamiliarSpecial.SpecialType.NONE;
	}
}
