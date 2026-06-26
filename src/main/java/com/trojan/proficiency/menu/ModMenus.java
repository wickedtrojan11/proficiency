package com.trojan.proficiency.menu;

import com.trojan.proficiency.ProficiencyMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public final class ModMenus {

    public static MenuType<SolarComposterMenu> SOLAR_COMPOSTER;

    private ModMenus() {
    }

    public static void register() {

        SOLAR_COMPOSTER =
                Registry.register(
                        BuiltInRegistries.MENU,
                        ResourceLocation.fromNamespaceAndPath(
                                ProficiencyMod.MOD_ID,
                                "solar_composter"
                        ),
                        new MenuType<>(
                                SolarComposterMenu::new,
                                FeatureFlags.VANILLA_SET
                        )
                );
    }
}
