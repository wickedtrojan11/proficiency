package com.trojan.proficiency;
import com.trojan.proficiency.block.ModBlocks;
import com.trojan.proficiency.event.MiningEvents;
import com.trojan.proficiency.event.WoodcuttingEvents;
import com.trojan.proficiency.event.WellRestedEvents;
import com.trojan.proficiency.event.FarmingEvents;
import com.trojan.proficiency.event.FarmingAnimalEffects;
import com.trojan.proficiency.event.FarmingAnimalDropEffects;
import com.trojan.proficiency.event.FarmingBeekeepingEffects;
import com.trojan.proficiency.event.FarmingUtilityEvents;
import com.trojan.proficiency.menu.ModMenus;
import com.trojan.proficiency.perk.MiningPerkEffects;
import com.trojan.proficiency.perk.WoodcuttingPerkEffects;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import com.trojan.proficiency.event.MiningDurabilityEvents;
import com.trojan.proficiency.save.PlayerDataStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.trojan.proficiency.perk.OreSenseEffects;
import com.trojan.proficiency.network.XpGainPayload;
import com.trojan.proficiency.network.WellRestedPayload;
import com.trojan.proficiency.network.SkillStatePayload;
import com.trojan.proficiency.network.PerkUnlockRequestPayload;
import com.trojan.proficiency.network.ToggleChangeRequestPayload;
import com.trojan.proficiency.network.SkillNetworking;
import com.trojan.proficiency.event.SaplingOwnershipTracker;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
public class ProficiencyMod implements ModInitializer {

	public static final String MOD_ID = "proficiency";

	public static final Logger LOGGER =
			LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		LOGGER.info("Proficiency loaded!");

		PayloadTypeRegistry.playS2C().register(
				XpGainPayload.TYPE,
				XpGainPayload.STREAM_CODEC
		);
		PayloadTypeRegistry.playS2C().register(
				WellRestedPayload.TYPE,
				WellRestedPayload.STREAM_CODEC
		);
		PayloadTypeRegistry.playS2C().register(
				SkillStatePayload.TYPE,
				SkillStatePayload.STREAM_CODEC
		);
		PayloadTypeRegistry.playC2S().register(
				PerkUnlockRequestPayload.TYPE,
				PerkUnlockRequestPayload.STREAM_CODEC
		);
		PayloadTypeRegistry.playC2S().register(
				ToggleChangeRequestPayload.TYPE,
				ToggleChangeRequestPayload.STREAM_CODEC
		);
		SkillNetworking.registerServerHandlers();

		ModBlocks.register();
		ModMenus.register();

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {

			PlayerDataStorage.configureForServer(server);
			SkillManager.clearPlayerDataCache();
		});

		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {

			LOGGER.info("Saving all loaded proficiency player data on server stopping");
			SkillManager.saveAllPlayerData();
		});

		ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
			SkillManager.clearPlayerDataCache();
			SaplingOwnershipTracker.clear();
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			SkillManager.loadPlayerData(
					handler.player.getUUID()
			);
			SkillManager.sendSkillState(handler.player);
		});

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {

			SkillManager.saveLoadedPlayerData(
					handler.player.getUUID(),
					"disconnect"
			);
			SkillManager.unloadPlayerData(
					handler.player.getUUID()
			);
		});

		MiningEvents.register();
		WoodcuttingEvents.register();
		WellRestedEvents.register();
		FarmingEvents.register();
		FarmingAnimalEffects.register();
		FarmingAnimalDropEffects.register();
		FarmingBeekeepingEffects.register();
		FarmingUtilityEvents.register();
		SaplingOwnershipTracker.register();
		MiningPerkEffects.register();
		WoodcuttingPerkEffects.register();
		MiningDurabilityEvents.register();
		OreSenseEffects.register();
	}
}
