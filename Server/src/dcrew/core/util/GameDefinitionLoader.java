package dcrew.core.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.logging.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider;
import dcrew.core.cache.map.Region;
import dcrew.core.definitions.CombatSpellDefinition;
import dcrew.core.definitions.EquipmentDefinition;
import dcrew.core.definitions.FoodDefinition;
import dcrew.core.definitions.ItemBonusDefinition;
import dcrew.core.definitions.ItemDefinition;
import dcrew.core.definitions.ItemDropDefinition;
import dcrew.core.definitions.ItemDropDefinition.ItemDrop;
import dcrew.core.definitions.NpcCombatDefinition;
import dcrew.core.definitions.NpcDefinition;
import dcrew.core.definitions.NpcSpawnDefinition;
import dcrew.core.definitions.PotionDefinition;
import dcrew.core.definitions.RangedStrengthDefinition;
import dcrew.core.definitions.RangedWeaponDefinition;
import dcrew.core.definitions.ShopDefinition;
import dcrew.core.definitions.SpecialAttackDefinition;
import dcrew.core.definitions.WeaponDefinition;
import dcrew.rs2.content.combat.impl.PlayerDrops;
import dcrew.rs2.content.gambling.FlowerGame;
import dcrew.rs2.content.shopping.Shop;
import dcrew.rs2.content.skill.Skills;
import dcrew.rs2.entity.item.Item;
import dcrew.rs2.entity.mob.Mob;

public class GameDefinitionLoader {
	static Logger logger = Logger.getLogger(GameDefinitionLoader.class.getSimpleName());
	static XStream xStream = new XStream(new Sun14ReflectionProvider());
	static int[][] alternates = new int[53000][1];
	static Map<Integer, Integer> rareDropChances = new HashMap<Integer, Integer>();
	static Map<Integer, byte[][]> itemRequirements = new HashMap<Integer, byte[][]>();
	static Map<Integer, ItemDefinition> itemDefinitions = new HashMap<Integer, ItemDefinition>();
	static Map<Integer, NpcDefinition> npcDefinitions = new HashMap<Integer, NpcDefinition>();
	static Map<Integer, SpecialAttackDefinition> specialAttackDefinitions = new HashMap<Integer, SpecialAttackDefinition>();
	static Map<Integer, RangedWeaponDefinition> rangedWeaponDefinitions = new HashMap<Integer, RangedWeaponDefinition>();
	static Map<Integer, WeaponDefinition> weaponDefinitions = new HashMap<Integer, WeaponDefinition>();
	static Map<Integer, FoodDefinition> foodDefinitions = new HashMap<Integer, FoodDefinition>();
	static Map<Integer, PotionDefinition> potionDefinitions = new HashMap<Integer, PotionDefinition>();
	static Map<Integer, EquipmentDefinition> equipmentDefinitions = new HashMap<Integer, EquipmentDefinition>();
	static Map<Integer, ItemBonusDefinition> itemBonusDefinitions = new HashMap<Integer, ItemBonusDefinition>();
	static Map<Integer, CombatSpellDefinition> combatSpellDefinitions = new HashMap<Integer, CombatSpellDefinition>();
	static Map<Integer, NpcCombatDefinition> npcCombatDefinitions = new HashMap<Integer, NpcCombatDefinition>();
	static Map<Integer, RangedStrengthDefinition> rangedStrengthDefinitions = new HashMap<Integer, RangedStrengthDefinition>();
	static Map<Integer, ItemDropDefinition> mobDropDefinitions = new HashMap<Integer, ItemDropDefinition>();

	public static final void clearAlternates() { alternates = null; }

	public static final void declare() {
		xStream.alias("ItemDropDefinition", dcrew.core.definitions.ItemDropDefinition.class);
		xStream.alias("constant", dcrew.core.definitions.ItemDropDefinition.ItemDropTable.class);
		xStream.alias("common", dcrew.core.definitions.ItemDropDefinition.ItemDropTable.class);
		xStream.alias("uncommon", dcrew.core.definitions.ItemDropDefinition.ItemDropTable.class);
		xStream.alias("itemDrop", dcrew.core.definitions.ItemDropDefinition.ItemDrop.class);
		xStream.alias("scroll", dcrew.core.definitions.ItemDropDefinition.ItemDropTable.ScrollTypes.class);

		xStream.alias("location", dcrew.rs2.entity.Location.class);
		xStream.alias("item", dcrew.rs2.entity.item.Item.class);
		xStream.alias("projectile", dcrew.rs2.entity.Projectile.class);
		xStream.alias("graphic", dcrew.rs2.entity.Graphic.class);
		xStream.alias("animation", dcrew.rs2.entity.Animation.class);

		xStream.alias("NpcCombatDefinition", dcrew.core.definitions.NpcCombatDefinition.class);
		xStream.alias("skill", dcrew.core.definitions.NpcCombatDefinition.Skill.class);
		xStream.alias("melee", dcrew.core.definitions.NpcCombatDefinition.Melee.class);
		xStream.alias("magic", dcrew.core.definitions.NpcCombatDefinition.Magic.class);
		xStream.alias("ranged", dcrew.core.definitions.NpcCombatDefinition.Ranged.class);

		xStream.alias("ItemDefinition", dcrew.core.definitions.ItemDefinition.class);
		xStream.alias("ShopDefinition", dcrew.core.definitions.ShopDefinition.class);
		xStream.alias("WeaponDefinition", dcrew.core.definitions.WeaponDefinition.class);
		xStream.alias("SpecialAttackDefinition", dcrew.core.definitions.SpecialAttackDefinition.class);
		xStream.alias("RangedWeaponDefinition", dcrew.core.definitions.RangedWeaponDefinition.class);
		xStream.alias("RangedStrengthDefinition", dcrew.core.definitions.RangedStrengthDefinition.class);
		xStream.alias("FoodDefinition", dcrew.core.definitions.FoodDefinition.class);
		xStream.alias("PotionDefinition", dcrew.core.definitions.PotionDefinition.class);
		xStream.alias("skillData", dcrew.core.definitions.PotionDefinition.SkillData.class);
		xStream.alias("ItemBonusDefinition", dcrew.core.definitions.ItemBonusDefinition.class);
		xStream.alias("CombatSpellDefinition", dcrew.core.definitions.CombatSpellDefinition.class);
		xStream.alias("NpcDefinition", dcrew.core.definitions.NpcDefinition.class);
		xStream.alias("NpcSpawnDefinition", dcrew.core.definitions.NpcSpawnDefinition.class);
		xStream.alias("EquipmentDefinition", dcrew.core.definitions.EquipmentDefinition.class);
		logger.info("All GameDefinitions have been loaded.");
		
		System.out.println("test");
		logger.info("test");
		System.out.println("test");
	}

	public static void dumpSizes() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("./NPCSizes.txt"));
			for (NpcDefinition i : npcDefinitions.values())
				if (i != null) {
					writer.write(i.getId() + ":" + i.getSize());
					writer.newLine();
				}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getAlternate(int id) { return alternates[id][0]; }
	public static CombatSpellDefinition getCombatSpellDefinition(int id) { return combatSpellDefinitions.get(id); }
	public static EquipmentDefinition getEquipmentDefinition(int id) { return equipmentDefinitions.get(id); }
	public static FoodDefinition getFoodDefinition(int id) { return foodDefinitions.get(id); }

	public static int getHighAlchemyValue(int id) {
		ItemDefinition def = getItemDef(id);
		if (def == null)
			return 1;
		Item item = new Item(id);
		if (def.isNote())
			item.unNote();
		return item.getDefinition().getHighAlch();
	}

	public static ItemBonusDefinition getItemBonusDefinition(int i) { return itemBonusDefinitions.get(i); }
	public static ItemDefinition getItemDef(int i) { return itemDefinitions.get(i); }
	public static ItemDropDefinition getItemDropDefinition(int id) { return mobDropDefinitions.get(id); }
	public static byte[][] getItemRequirements(int id) { return itemRequirements.get(id); }

	public static int getLowAlchemyValue(int id) {
		ItemDefinition def = getItemDef(id);
		if (def == null)
			return 1;
		Item item = new Item(id);
		if (def.isNote())
			item.unNote();
		return item.getDefinition().getLowAlch();
	}

	public static NpcCombatDefinition getNpcCombatDefinition(int id) { return npcCombatDefinitions.get(id); }
	public static NpcDefinition getNpcDefinition(int id) { return npcDefinitions.get(id); }
	public static PotionDefinition getPotionDefinition(int id) { return potionDefinitions.get(id); }
	public static RangedStrengthDefinition getRangedStrengthDefinition(int id) { return rangedStrengthDefinitions.get(id); }
	public static RangedWeaponDefinition getRangedWeaponDefinition(int id) { return rangedWeaponDefinitions.get(id); }

	public static int getRareDropChance(int id) {
		if (!rareDropChances.containsKey(id))
			return 80;
		return rareDropChances.get(id).intValue();
	}

	public static SpecialAttackDefinition getSpecialDefinition(int id) { return specialAttackDefinitions.get(id); }

	public static int getStoreBuyFromValue(int id) {
		ItemDefinition def = getItemDef(id);
		if (def == null)
			return 1;
		if (def.isNote()) {
			Item item = new Item(id);
			item.unNote();
			def = item.getDefinition();
		}
		double ratio = 0;
		if (def.getLowAlch() == 0 || (def.getHighAlch() == 0 && def.getLowAlch() == 0))
			ratio = 1;
		else
			ratio = def.getHighAlch() / (double) def.getLowAlch();
		return (int) Math.ceil(def.getGeneralPrice() * ratio);
	}

	public static int getStoreSellToValue(int id) {
		ItemDefinition def = getItemDef(id);
		if (def == null)
			return 1;
		Item item = new Item(id);
		if (item.getDefinition().isNote())
			item.unNote();
		return item.getDefinition().getGeneralPrice();
	}

	public static WeaponDefinition getWeaponDefinition(int id) { return weaponDefinitions.get(id); }
	public static XStream getxStream() { return xStream; }

	public static final void loadAlternateIds() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader("./data/def/ObjectAlternates.txt"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				int id = Integer.parseInt(line.substring(0, line.indexOf(":")));
				line = line.substring(line.indexOf(":") + 1);
				int alt = Integer.parseInt(line.substring(0, line.length()));
				alternates[id][0] = alt;
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("All alternative objects have been loaded.");
	}

	@SuppressWarnings("unchecked")
	public static void loadCombatSpellDefinitions() throws FileNotFoundException, IOException {
		List<CombatSpellDefinition> list = (List<CombatSpellDefinition>) xStream.fromXML(new FileInputStream("./data/def/magic/CombatSpellDefinitions.xml"));
		for (CombatSpellDefinition definition : list)
			combatSpellDefinitions.put(definition.getId(), definition);
		logger.info("Loaded " + Utility.format(list.size()) + " combat spell definitions.");
	}

	@SuppressWarnings("unchecked")
	public static void loadEquipmentDefinitions() throws FileNotFoundException, IOException {
		List<EquipmentDefinition> list = (List<EquipmentDefinition>) xStream.fromXML(new FileInputStream("./data/def/items/EquipmentDefinitions.xml"));
		for (EquipmentDefinition definition : list) {
			equipmentDefinitions.put(definition.getId(), definition);
			int size = 0;
			if (definition.getRequirements() != null) {
				byte[] array = definition.getRequirements();
				for (int i = 0; i < array.length; i++)
					if (array[i] > 1)
						size++;
			}
			if (size == 0)
				continue;
		}
		logger.info("Loaded " + Utility.format(list.size()) + " equipment definitions.");
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		declare();
		loadNpcDropDefinitions();
	}

	@SuppressWarnings("unchecked")
	public static void loadFoodDefinitions() throws FileNotFoundException, IOException {
		List<FoodDefinition> list = (List<FoodDefinition>) xStream.fromXML(new FileInputStream("./data/def/items/FoodDefinitions.xml"));
		for (FoodDefinition definition : list)
			foodDefinitions.put(definition.getId(), definition);
		logger.info("Loaded " + Utility.format(list.size()) + " food definitions.");
	}

	@SuppressWarnings("unchecked")
	public static void loadItemBonusDefinitions() throws FileNotFoundException, IOException {
		List<ItemBonusDefinition> list = (List<ItemBonusDefinition>) xStream.fromXML(new FileInputStream("./data/def/items/ItemBonusDefinitions.xml"));
		for (ItemBonusDefinition definition : list)
			itemBonusDefinitions.put(definition.getId(), definition);
		logger.info("Loaded " + Utility.format(list.size()) + " item bonus definitions.");
	}

	@SuppressWarnings("unchecked")
	public static void loadItemDefinitions() throws FileNotFoundException, IOException {
		List<ItemDefinition> list = (List<ItemDefinition>) xStream.fromXML(new FileInputStream("./data/def/items/ItemDefinitions.xml"));
		for (ItemDefinition definition : list)
			itemDefinitions.put(definition.getId(), definition);
		logger.info("Loaded " + Utility.format(list.size()) + " item definitions.");
	}
	
	public static Map<Integer, ItemDefinition> getItemDefinitions() { return itemDefinitions; }
	public static Map<Integer, ItemBonusDefinition> getItemBonusDefinitions() { return itemBonusDefinitions; }
	
	@SuppressWarnings("unchecked")
	public static void loadNpcCombatDefinitions() throws FileNotFoundException, IOException {
		List<NpcCombatDefinition> list = (List<NpcCombatDefinition>) xStream.fromXML(new FileInputStream("./data/def/npcs/NpcCombatDefinitions.xml"));
		for (NpcCombatDefinition definition : list)
			npcCombatDefinitions.put(definition.getId(), definition);
		logger.info("Loaded " + Utility.format(list.size()) + " npc combat definitions.");
	}

	@SuppressWarnings("unchecked")
	public static void loadNpcDefinitions() throws FileNotFoundException, IOException {
		List<NpcDefinition> list = (List<NpcDefinition>) xStream.fromXML(new FileInputStream("./data/def/npcs/NpcDefinitions.xml"));
		for (NpcDefinition definition : list)
			npcDefinitions.put(definition.getId(), definition);
		logger.info("Loaded " + Utility.format(list.size()) + " NPC definitions.");
	}
	
	public static Map<Integer, NpcDefinition> getNpcDefinitions() { return npcDefinitions; }
	public static Map<Integer, ItemDropDefinition> getMobDropDefinitions() { return mobDropDefinitions; }

	@SuppressWarnings("unchecked")
	public static void loadNpcDropDefinitions() throws FileNotFoundException, IOException {
		List<ItemDropDefinition> list = (List<ItemDropDefinition>) xStream.fromXML(new FileInputStream("./data/def/npcs/ItemDropDefinitions.xml"));
		for (ItemDropDefinition def : list)
			mobDropDefinitions.put(def.getId(), def);
		for (ItemDropDefinition i : mobDropDefinitions.values())
			if (npcCombatDefinitions.get(i.getId()) == null)
				mobDropDefinitions.remove(i);
		logger.info("Loaded " + Utility.format(list.size()) + " npc drops.");
	}

	@SuppressWarnings("unchecked")
	public static void loadNpcSpawns() throws FileNotFoundException, IOException {
		List<NpcSpawnDefinition> list = (List<NpcSpawnDefinition>) xStream.fromXML(new FileInputStream("./data/def/npcs/NpcSpawnDefinitions.xml"));
		for (NpcSpawnDefinition def : list) {
			if (Region.getRegion(def.getLocation().getX(), def.getLocation().getY()) == null)
				continue;
			if (npcDefinitions.get(def.getId()).isAttackable() && npcCombatDefinitions.get(def.getId()) == null)
				continue;
			Mob m = new Mob(def.getId(), def.isWalk(), def.getLocation());
			if (m.getId() == 1011)
				FlowerGame.setGambler(m);
			if (def.getFace() > 0)
				m.setFaceDir(def.getFace());
			else
				m.setFaceDir(-1);
		}
		logger.info("Loaded " + Utility.format(list.size()) + " NPC spawns.");
	}

	@SuppressWarnings("unchecked")
	public static void loadPotionDefinitions() throws FileNotFoundException, IOException {
		List<PotionDefinition> list = (List<PotionDefinition>) xStream.fromXML(new FileInputStream("./data/def/items/PotionDefinitions.xml"));
		for (PotionDefinition definition : list) {
			if (definition.getName() == null)
				definition.setName(itemDefinitions.get(definition.getId()).getName());
			potionDefinitions.put(definition.getId(), definition);
		}
		logger.info("Loaded " + Utility.format(list.size()) + " potion definitions.");
	}

	@SuppressWarnings("unchecked")
	public static void loadRangedStrengthDefinitions() throws FileNotFoundException, IOException {
		List<RangedStrengthDefinition> list = (List<RangedStrengthDefinition>) xStream.fromXML(new FileInputStream("./data/def/items/RangedStrengthDefinitions.xml"));
		for (RangedStrengthDefinition definition : list)
			rangedStrengthDefinitions.put(definition.getId(), definition);
		logger.info("Loaded " + Utility.format(list.size()) + " ranged strength bonus definitions.");
	}

	@SuppressWarnings("unchecked")
	public static void loadRangedWeaponDefinitions() throws FileNotFoundException, IOException {
		List<RangedWeaponDefinition> list = (List<RangedWeaponDefinition>) xStream.fromXML(new FileInputStream("./data/def/items/RangedWeaponDefinitions.xml"));
		for (RangedWeaponDefinition definition : list)
			rangedWeaponDefinitions.put(definition.getId(), definition);
		logger.info("Loaded " + Utility.format(list.size()) + " ranged weapon definitions.");
	}

	public static final void loadRareDropChances() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader("./data/def/npcs/DropChances.txt"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.length() == 0 || line.startsWith("//"))
					continue;
				try {
					int id = Integer.parseInt(line.substring(0, line.indexOf(":")));
					line = line.substring(line.indexOf(":") + 1);
					int value = Integer.parseInt(line.substring(0, line.indexOf("/")));
					rareDropChances.put(id, value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("Successfully loaded all rare drops.");
	}

	@SuppressWarnings("unchecked")
	public static void loadShopDefinitions() throws FileNotFoundException, IOException {
		List<ShopDefinition> list = (List<ShopDefinition>) xStream.fromXML(new FileInputStream("./data/def/items/ShopDefinitions.xml"));
		for (ShopDefinition def : list)
			Shop.getShops()[def.getId()] = new Shop(def.getId(), def.getItems(), def.isGeneral(), def.getName());
		logger.info("Loaded " + Utility.format(list.size()) + " shops.");
	}

	@SuppressWarnings("unchecked")
	public static void loadSpecialAttackDefinitions() throws FileNotFoundException, IOException {
		List<SpecialAttackDefinition> list = (List<SpecialAttackDefinition>) xStream.fromXML(new FileInputStream("./data/def/items/SpecialAttackDefinitions.xml"));
		for (SpecialAttackDefinition definition : list)
			specialAttackDefinitions.put(definition.getId(), definition);
		logger.info("Loaded " + Utility.format(list.size()) + " special attack definitions.");
	}

	@SuppressWarnings("unchecked")
	public static void loadWeaponDefinitions() throws FileNotFoundException, IOException {
		List<WeaponDefinition> list = (List<WeaponDefinition>) xStream.fromXML(new FileInputStream("./data/def/items/WeaponDefinitions.xml"));
		for (WeaponDefinition definition : list)
			weaponDefinitions.put(definition.getId(), definition);
		logger.info("Loaded " + Utility.format(list.size()) + " weapon definitions.");
	}
	
	public static Map<Integer, WeaponDefinition> getWeaponDefinitions() { return weaponDefinitions; }
	public static void setNotTradable(int id) { itemDefinitions.get(id).setUntradable(); }

	public static void setRequirements() {
		for (Object def : equipmentDefinitions.values().toArray()) {
			EquipmentDefinition definition = (EquipmentDefinition) def;
			if (definition == null || definition.getRequirements() == null)
				continue;
			byte[][] requirements = new byte[Skills.SKILL_COUNT][2];
			int count = 0;
			for (int i = 0; i < definition.getRequirements().length; i++) {
				if (definition.getRequirements()[i] == 1)
					continue;
				else {
					if (count < Skills.SKILL_COUNT) {
						requirements[count][0] = (byte) i;
						requirements[count][1] = definition.getRequirements()[i];
					}
					count++;
				}
			}
			byte[][] set = new byte[count][2];
			for (int i = 0; i < count; i++)
				if (count < Skills.SKILL_COUNT) {
					set[i][0] = requirements[i][0];
					set[i][1] = requirements[i][1];
				}
			itemRequirements.put(((EquipmentDefinition) def).getId(), set);
			((EquipmentDefinition) def).setRequirements(null);
		}
	}

	public static void writeDropPreference() {
		try {
			Queue<Item> items = new PriorityQueue<Item>(42, PlayerDrops.ITEM_VALUE_COMPARATOR);
			for (ItemDefinition i : itemDefinitions.values()) {
				if (!i.isTradable() || i.getNoteId() != -1 && items.contains(new Item(i.getNoteId())))
					continue;
				items.add(new Item(i.getId()));
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter("./DropSettings.txt"));
			Item item = null;
			while ((item = items.poll()) != null) {
				writer.write(item.getId() + ":" + item.getDefinition().getName());
				writer.newLine();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private GameDefinitionLoader() { }
}