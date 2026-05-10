package com.trojan.proficiency;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProficiencyMod implements ModInitializer {

	public static final String MOD_ID = "proficiency";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		LOGGER.info("Proficiency loaded!");

		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {

			if (state.getBlock() == Blocks.STONE) {

				boolean leveledUp =
						SkillManager.addMiningXp(player.getUUID(), 1);

				int xp =
						SkillManager.getMiningXp(player.getUUID());

				int level =
						SkillManager.getMiningLevel(player.getUUID());

				LOGGER.info(
						player.getName().getString()
								+ " Mining Level: "
								+ level
								+ " | XP: "
								+ xp
				);

				if (leveledUp) {

					player.sendSystemMessage(
							Component.literal(
									"§6Mining Level Up! → Level " + level
							)
					);
				}

				// Mining bonus at level 5+
				if (level >= 5) {

					player.addEffect(
							new MobEffectInstance(
									MobEffects.DIG_SPEED,
									40,
									0,
									false,
									false
							)
					);
				}
			}
		});
	}
}