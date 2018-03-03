package dcrew.rs2.entity.mob;

import dcrew.Constants;

import java.util.HashMap;
import java.util.Map;

import dcrew.core.util.Utility;

public enum RandomMobChatting {

	MAKEOVER_MAGE(new int[] { 599, 2676 }, 150, "I can change your appearance.", ("Welcome to the world of " + Constants.name)),
	COMBAT_TUTOR(new int[] { 705 }, 120, ("Welcome to " + Constants.name), ("Become the number one pker of " + Constants.name));

	public static final void declare() {
		for (RandomMobChatting mob : values())
			for (Integer k : mob.getMobId())
				mobs.put(k, mob);
	}

	int[] mobId;
	String[] messages;
	int random;

	static Map<Integer, RandomMobChatting> mobs = new HashMap<Integer, RandomMobChatting>();

	public static RandomMobChatting getMob(int id) { return mobs.get(id); }

	public static void handleRandomMobChatting(Mob mob) {
		RandomMobChatting chat = getMob(mob.getId());
		if (chat == null)
			return;
		if (Utility.randomNumber(chat.getRandom()) == 1)
			mob.getUpdateFlags().sendForceMessage(chat.getMessages()[Utility.randomNumber(chat.getMessages().length - 1)]);
	}

	RandomMobChatting(int[] mobId, int random, String... messages) {
		this.mobId = mobId;
		this.random = random;
		this.messages = messages;
	}

	public String[] getMessages() { return messages; }
	public int[] getMobId() { return mobId; }
	public int getRandom() { return random; }
}