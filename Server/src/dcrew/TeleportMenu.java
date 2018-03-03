package dcrew;

import java.lang.Math;
import java.util.HashMap;

import dcrew.rs2.content.skill.magic.MagicSkill.TeleportTypes;
import dcrew.rs2.entity.Location;
import dcrew.rs2.entity.player.Player;
import dcrew.rs2.entity.player.net.out.impl.SendString;
import dcrew.rs2.entity.player.net.out.impl.SendInterface;

public class TeleportMenu {
	static final Tab[] tabs = {
			new Tab("Training/Dungeons", new Tab.Teleport[] {
					new Tab.Teleport("Cows", new Location(3257, 3263, 0)),
					new Tab.Teleport("Yaks", new Location(2321, 3804, 0)),
					new Tab.Teleport("Al Kharid", new Location(3293, 3182, 0)),
					new Tab.Teleport("Rock Crabs", new Location(2674, 3710, 0)),
					new Tab.Teleport("Hill Giants", new Location(3117, 9856, 0)),
					new Tab.Teleport("Taverly Dungeon", new Location(2884, 9798, 0)),
					new Tab.Teleport("Brimhaven Dungeon", new Location(2710, 9466, 0))
				}),
			new Tab("Skilling", new Tab.Teleport[] {
					new Tab.Teleport("Thieving", new Location(3047, 4976, 1)),
					new Tab.Teleport("Crafting", new Location(2747, 3444, 0)),
					new Tab.Teleport("Mining", new Location(3044, 9785, 0)),
					new Tab.Teleport("Smithing", new Location(3186, 3425, 0)),
					new Tab.Teleport("Fishing", new Location(2840, 3437, 0)),
					new Tab.Teleport("Woodcutting", new Location(2722, 3473, 0)),
					new Tab.Teleport("Farming", new Location(2806, 3463, 0), false, true)
				}),
			new Tab("PvP", new Tab.Teleport[] {
					new Tab.Teleport("Edgeville", new Location(3087, 3515, 0)),
					new Tab.Teleport("Varrock", new Location(3244, 3512, 0)),
					new Tab.Teleport("East Dragons", new Location(3333, 3666, 0), true),
					new Tab.Teleport("Castle", new Location(3002, 3626, 0)),
					new Tab.Teleport("Mage Bank", new Location(2540, 4717, 0))
				})
	};

	static HashMap<Integer, Tab.Teleport> tpButtonIds;
	static Tab selectedTab;
	static Tab.Teleport selectedTeleport;
	static int selectedTeleportIndex = -1;

	static {
		tpButtonIds = new HashMap<Integer, Tab.Teleport>();
		for (Tab tab : tabs) {
			int index = 0;
			for (Tab.Teleport teleport : tab.getTeleports())
				teleport.index = index++;
		}
	}

	public static boolean isTPButton(int id) { return tpButtonIds.containsKey(id); }

	public static void setTab(int index, Player player) {
		if (index >= tabs.length)
			return;
		Tab tab = tabs[index];
		if (selectedTab != null)
			for (int i = tab.getTeleports().length; i < selectedTab.getTeleports().length; i++)
				player.send(new SendString("", (61051 + i)));
		selectedTab = tab;
		tpButtonIds.clear();
		int loops = Math.min(tabs.length, 6);
		for (int j = 0; j < loops; j++)
			if (j == index)
				player.send(new SendString(("@gre@" + tabs[j].name), (61024 + j)));
			else
				player.send(new SendString(tabs[j].name, (61024 + j)));
		loops = Math.min(tab.getTeleports().length, 40);
		for (int i = 0; i < loops; i++) {
			tpButtonIds.put((238123 + i), tab.getTeleports()[i]);
			player.send(new SendString(tab.getTeleports()[i].getText(), (61051 + i)));
		}
		selectedTeleportIndex = -1;
		setTeleport(-1, player);
		player.send(new SendInterface(61000));
	}
	public static void setTeleport(int buttonId, Player player) {
		Tab.Teleport teleport = tpButtonIds.get(buttonId);
		selectedTeleport = teleport;
		if (selectedTeleportIndex != -1)
			player.send(new SendString(selectedTab.getTeleports()[selectedTeleportIndex].getText(), (61051 + selectedTeleportIndex)));
		if (teleport != null) {
			player.send(new SendString(("@gr2@" + teleport.getText() + ""), (61051 + teleport.getIndex())));
			selectedTeleportIndex = teleport.getIndex();
		}
		player.send(new SendString(((teleport == null) ? "" : teleport.text), 61030));
		if ((teleport == null) || !teleport.getPvPWarning()) {
			player.send(new SendString("", 61031));
			player.send(new SendString("", 61039));
			player.send(new SendString("", 61032));
			player.send(new SendString("", 61033));
			player.send(new SendString("", 61034));
			return;
		}
		player.setTeleportTo(buttonId);
		if (teleport.getPvPWarning()) {
			player.send(new SendString("@red@WARNING:@or1@ This teleport leads", 61031));
			player.send(new SendString("into a @red@PvP-enabled@or1@ zone, be", 61039));
			player.send(new SendString("careful!", 61032));
			player.send(new SendString("", 61033));
			player.send(new SendString("", 61034));
		}
	}

	public static void teleport(Player player) {
		if (selectedTeleport == null)
			return;
		player.setTeleportTo(0);
		player.getMagic().teleport(selectedTeleport.getLocation().getX(), selectedTeleport.getLocation().getY(), (selectedTeleport.getInstanced() ? (player.getIndex() << 2) : selectedTeleport.getLocation().getZ()), TeleportTypes.SPELL_BOOK);
		selectedTeleport = null;
	}

	public static class Tab {
		String name;
		Teleport[] teleports;

		public Tab(String name, Teleport[] teleports) {
			this.name = name;
			this.teleports = teleports;
		}

		public String getName() { return name; }
		public Teleport[] getTeleports() { return teleports; }

		public static class Teleport {
			protected int index;
			String text;
			Location location;
			boolean pvpWarning,
				instanced;

			public Teleport(String text, Location location) {
				this.text = text;
				this.location = location;
			}
			public Teleport(String text, Location location, boolean pvpWarning) {
				this.text = text;
				this.location = location;
				this.pvpWarning = pvpWarning;
			}
			public Teleport(String text, Location location, boolean pvpWarning, boolean instanced) {
				this.text = text;
				this.location = location;
				this.pvpWarning = pvpWarning;
				this.instanced = instanced;
			}

			public int getIndex() { return index; }
			public String getText() { return text; }
			public Location getLocation() { return location; }
			public boolean getPvPWarning() { return pvpWarning; }
			public boolean getInstanced() { return instanced; }
		}
	}
}