package com.trojan.proficiency.client;

import com.trojan.proficiency.block.ModBlocks;
import com.trojan.proficiency.client.keybind.KeybindHandler;
import com.trojan.proficiency.client.screen.ProficientBrewStandScreen;
import com.trojan.proficiency.client.screen.SolarComposterScreen;
import com.trojan.proficiency.menu.ModMenus;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;

public class ProficiencyClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        KeybindHandler.register();
        ClientSkillState.register();
        CropGrowthOverlay.register();
        AnimalHusbandryOverlay.register();
        XpGainOverlay.register();
        WellRestedOverlay.register();
        AlchemyXpBuffOverlay.register();
        OneHandedTooltip.register();
        AlchemyTooltip.register();
        ParryVisualState.register();
        OffhandStrikeInput.register();
        MenuScreens.register(
                ModMenus.SOLAR_COMPOSTER,
                SolarComposterScreen::new
        );
        MenuScreens.register(
                ModMenus.PROFICIENT_BREW_STAND,
                ProficientBrewStandScreen::new
        );
        BlockRenderLayerMap.INSTANCE.putBlock(
                ModBlocks.AUTO_FARMER_PLANT_POT,
                RenderType.cutout()
        );
        BlockRenderLayerMap.INSTANCE.putBlock(
                ModBlocks.CAMELLIA_FLOWER,
                RenderType.cutout()
        );
        BlockRenderLayerMap.INSTANCE.putBlock(
                ModBlocks.POTTED_CAMELLIA_FLOWER,
                RenderType.cutout()
        );
    }
}
