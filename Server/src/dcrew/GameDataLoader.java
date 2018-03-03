package dcrew;

import dcrew.core.cache.map.MapLoading;
import dcrew.core.cache.map.ObjectDef;
import dcrew.core.cache.map.RSInterface;
import dcrew.core.cache.map.Region;
import dcrew.core.util.FileHandler;
import dcrew.core.util.GameDefinitionLoader;
import dcrew.rs2.content.Emotes;
import dcrew.rs2.content.FountainOfRune;
import dcrew.rs2.content.GlobalMessages;
import dcrew.rs2.content.cluescroll.ClueScrollManager;
import dcrew.rs2.content.combat.impl.PoisonWeapons;
import dcrew.rs2.content.combat.special.SpecialAttackHandler;
import dcrew.rs2.content.dialogue.OneLineDialogue;
import dcrew.rs2.content.minigames.duelarena.DuelingConstants;
import dcrew.rs2.content.minigames.godwars.GodWarsData;
import dcrew.rs2.content.minigames.plunder.PlunderConstants;
import dcrew.rs2.content.minigames.plunder.PyramidPlunder;
import dcrew.rs2.content.shopping.Shop;
import dcrew.rs2.content.skill.Skills;
import dcrew.rs2.content.skill.agility.Agility;
import dcrew.rs2.content.skill.cooking.CookingData;
import dcrew.rs2.content.skill.crafting.Craftable;
import dcrew.rs2.content.skill.crafting.Glass;
import dcrew.rs2.content.skill.crafting.HideTanData;
import dcrew.rs2.content.skill.crafting.Jewelry;
import dcrew.rs2.content.skill.crafting.Spinnable;
import dcrew.rs2.content.skill.craftingnew.craftable.impl.Hide;
import dcrew.rs2.content.skill.farming.Farming;
import dcrew.rs2.content.skill.firemaking.LogData;
import dcrew.rs2.content.skill.fishing.FishableData;
import dcrew.rs2.content.skill.fishing.Fishing;
import dcrew.rs2.content.skill.fishing.ToolData;
import dcrew.rs2.content.skill.fletching.fletchable.impl.Arrow;
import dcrew.rs2.content.skill.fletching.fletchable.impl.Bolt;
import dcrew.rs2.content.skill.fletching.fletchable.impl.Carvable;
import dcrew.rs2.content.skill.fletching.fletchable.impl.Crossbow;
import dcrew.rs2.content.skill.fletching.fletchable.impl.Featherable;
import dcrew.rs2.content.skill.fletching.fletchable.impl.Stringable;
import dcrew.rs2.content.skill.herblore.FinishedPotionData;
import dcrew.rs2.content.skill.herblore.GrimyHerbData;
import dcrew.rs2.content.skill.herblore.GrindingData;
import dcrew.rs2.content.skill.herblore.UnfinishedPotionData;
import dcrew.rs2.content.skill.magic.MagicConstants;
import dcrew.rs2.content.skill.magic.MagicEffects;
import dcrew.rs2.content.skill.mining.Mining;
import dcrew.rs2.content.skill.prayer.BoneBurying;
import dcrew.rs2.content.skill.ranged.AmmoData;
import dcrew.rs2.content.skill.runecrafting.RunecraftingData;
import dcrew.rs2.content.skill.slayer.SlayerMonsters;
import dcrew.rs2.content.skill.smithing.SmeltingData;
import dcrew.rs2.content.skill.thieving.ThievingNpcData;
import dcrew.rs2.content.skill.thieving.ThievingStallData;
import dcrew.rs2.content.skill.woodcutting.WoodcuttingAxeData;
import dcrew.rs2.entity.item.EquipmentConstants;
import dcrew.rs2.entity.item.impl.GlobalItemHandler;
import dcrew.rs2.entity.mob.Mob;
import dcrew.rs2.entity.mob.MobAbilities;
import dcrew.rs2.entity.mob.MobConstants;
import dcrew.rs2.entity.object.ObjectConstants;
import dcrew.rs2.entity.object.ObjectManager;
import dcrew.rs2.entity.player.net.in.PacketHandler;

public class GameDataLoader {
	static int stage = 0;

	public static void load() {
		try {
			GameDefinitionLoader.declare();
			GlobalMessages.declare();
			new Thread() {
				@Override
				public void run() {
					try {
						ObjectDef.loadConfig();
						ObjectConstants.declare();
						MapLoading.load();
						Region.sort();
						GameDefinitionLoader.loadAlternateIds();
						MapLoading.processDoors();
						GameDefinitionLoader.clearAlternates();
						ObjectManager.declare();
						GlobalItemHandler.spawnGroundItems();
						Mob.spawnBosses();
						GameDefinitionLoader.loadNpcSpawns();
						GlobalMessages.initialize();
					} catch (Exception e) {
						e.printStackTrace();
					}

					GameDataLoader.stage += 1;
				}
			}.start();

			RSInterface.unpack();
			GameDefinitionLoader.loadNpcDefinitions();
			GameDefinitionLoader.loadItemDefinitions();
			GameDefinitionLoader.loadRareDropChances();	
			GameDefinitionLoader.loadEquipmentDefinitions();
			GameDefinitionLoader.loadShopDefinitions();
			GameDefinitionLoader.setRequirements();
			GameDefinitionLoader.loadWeaponDefinitions();
			GameDefinitionLoader.loadSpecialAttackDefinitions();
			GameDefinitionLoader.loadRangedStrengthDefinitions();
			GameDefinitionLoader.loadSpecialAttackDefinitions();
			GameDefinitionLoader.loadCombatSpellDefinitions();
			GameDefinitionLoader.loadFoodDefinitions();
			GameDefinitionLoader.loadPotionDefinitions();
			GameDefinitionLoader.loadRangedWeaponDefinitions();
			GameDefinitionLoader.loadNpcCombatDefinitions();
			GameDefinitionLoader.loadNpcDropDefinitions();
			GameDefinitionLoader.loadItemBonusDefinitions();			
			GodWarsData.declare();			
			Mining.declare();			
			PyramidPlunder.declare();
			PlunderConstants.UrnBitPosition.declare();
			PlunderConstants.DoorBitPosition.declare();		
			ClueScrollManager.declare();
			FountainOfRune.declare();
			Agility.declare();			
			Arrow.declare();
			Bolt.declare();
			Carvable.declare();
			Crossbow.declare();
			Featherable.declare();
			Stringable.declare();
			Craftable.declare();
			HideTanData.declare();
			Jewelry.declare();
			Spinnable.declare();		
			dcrew.rs2.content.skill.craftingnew.craftable.impl.Gem.declare();
			Hide.declare();		
			Farming.declare();
			Shop.declare();
			MagicConstants.declare();
			SlayerMonsters.declare();
			DuelingConstants.declare();
			MobConstants.declare();
			Emotes.declare();
			PoisonWeapons.declare();
			SpecialAttackHandler.declare();
			CookingData.declare();
			Glass.declare();
			LogData.declare();
			FishableData.Fishable.declare();
			Fishing.FishingSpots.declare();
			ToolData.Tools.declare();
			FinishedPotionData.declare();
			GrimyHerbData.declare();
			GrindingData.declare();
			UnfinishedPotionData.declare();
			MagicEffects.declare();
			BoneBurying.Bones.declare();
			AmmoData.Ammo.declare();
			RunecraftingData.declare();
			Skills.declare();
			ThievingNpcData.declare();
			ThievingStallData.declare();
			WoodcuttingAxeData.declare();
			EquipmentConstants.declare();
			PacketHandler.declare();
			MobConstants.MobDissapearDelay.declare();
			MobAbilities.declare();
			SmeltingData.declare();
			OneLineDialogue.declare();
			FileHandler.load();
			stage += 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets if the server has been successfully loaded
	 * 
	 * @return
	 */
	public static boolean loaded() {
		return stage == 2;
	}
}
