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
import com.trojan.proficiency.network.AlchemyXpBuffPayload;
import com.trojan.proficiency.network.SkillStatePayload;
import com.trojan.proficiency.network.PerkUnlockRequestPayload;
import com.trojan.proficiency.network.ToggleChangeRequestPayload;
import com.trojan.proficiency.network.PrestigeRequestPayload;
import com.trojan.proficiency.network.PrestigeRosterPayload;
import com.trojan.proficiency.network.ParryVisualPayload;
import com.trojan.proficiency.network.OffhandStrikeRequestPayload;
import com.trojan.proficiency.network.SkillNetworking;
import com.trojan.proficiency.event.SaplingOwnershipTracker;
import com.trojan.proficiency.event.SkillBookEvents;
import com.trojan.proficiency.event.PrestigeEffectEvents;
import com.trojan.proficiency.event.OneHandedEvents;
import com.trojan.proficiency.event.AlchemyEvents;
import com.trojan.proficiency.event.AlchemyOilEvents;
import com.trojan.proficiency.event.AlchemyPhilosopherEvents;
import com.trojan.proficiency.event.AlchemyTastingEvents;
import com.trojan.proficiency.event.CamelliaBonemealEvents;
import com.trojan.proficiency.item.AlchemyIngredientRegistry;
import com.trojan.proficiency.item.OilRegistry;
import com.trojan.proficiency.worldgen.ModWorldgen;
import com.trojan.proficiency.item.ModItems;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.chat.Component;
public class ProficiencyMod implements ModInitializer {

	public static final String MOD_ID = "proficiency";

	public static final Logger LOGGER =
			LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		LOGGER.info("Proficiency loaded!");
		if (OilRegistry.isDebugEnabled()) {
			LOGGER.info("[OilDebug] debug enabled");
		}

		PayloadTypeRegistry.playS2C().register(
				XpGainPayload.TYPE,
				XpGainPayload.STREAM_CODEC
		);
		PayloadTypeRegistry.playS2C().register(
				WellRestedPayload.TYPE,
				WellRestedPayload.STREAM_CODEC
		);
		PayloadTypeRegistry.playS2C().register(
				AlchemyXpBuffPayload.TYPE,
				AlchemyXpBuffPayload.STREAM_CODEC
		);
		PayloadTypeRegistry.playS2C().register(
				SkillStatePayload.TYPE,
				SkillStatePayload.STREAM_CODEC
		);
		PayloadTypeRegistry.playS2C().register(
				PrestigeRosterPayload.TYPE,
				PrestigeRosterPayload.STREAM_CODEC
		);
		PayloadTypeRegistry.playS2C().register(
				ParryVisualPayload.TYPE,
				ParryVisualPayload.STREAM_CODEC
		);
		PayloadTypeRegistry.playC2S().register(
				OffhandStrikeRequestPayload.TYPE,
				OffhandStrikeRequestPayload.STREAM_CODEC
		);
		PayloadTypeRegistry.playC2S().register(
				PerkUnlockRequestPayload.TYPE,
				PerkUnlockRequestPayload.STREAM_CODEC
		);
		PayloadTypeRegistry.playC2S().register(
				ToggleChangeRequestPayload.TYPE,
				ToggleChangeRequestPayload.STREAM_CODEC
		);
		PayloadTypeRegistry.playC2S().register(
				PrestigeRequestPayload.TYPE,
				PrestigeRequestPayload.STREAM_CODEC
		);
		SkillNetworking.registerServerHandlers();

		ModBlocks.register();
		ModItems.register();
		AlchemyIngredientRegistry.registerDefaults();
		ModWorldgen.register();
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
			SkillManager.sendPrestigeRoster(server);
			if (OilRegistry.isDebugEnabled()) {
				handler.player.sendSystemMessage(
						Component.literal("[OilDebug] debug enabled")
				);
			}
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
		SkillBookEvents.register();
		PrestigeEffectEvents.register();
		OneHandedEvents.register();
		AlchemyEvents.register();
		AlchemyOilEvents.register();
		AlchemyPhilosopherEvents.register();
		AlchemyTastingEvents.register();
		CamelliaBonemealEvents.register();
		MiningPerkEffects.register();
		WoodcuttingPerkEffects.register();
		MiningDurabilityEvents.register();
		OreSenseEffects.register();
	}
}
