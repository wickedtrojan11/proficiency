package com.trojan.proficiency;

import com.trojan.proficiency.event.MiningEvents;
import com.trojan.proficiency.event.WoodcuttingEvents;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProficiencyMod implements ModInitializer {

	public static final String MOD_ID = "proficiency";

	public static final Logger LOGGER =
			LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		LOGGER.info("Proficiency loaded!");

		MiningEvents.register();
		WoodcuttingEvents.register();
	}
}