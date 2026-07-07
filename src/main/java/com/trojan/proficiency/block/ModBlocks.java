package com.trojan.proficiency.block;

import com.trojan.proficiency.ProficiencyMod;
import com.trojan.proficiency.item.AlchemyGatedBlockItem;
import com.trojan.proficiency.item.GreenhouseGatedBlockItem;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.effect.MobEffects;

public final class ModBlocks {

    public static final AutoFarmerPlantPotBlock AUTO_FARMER_PLANT_POT =
            new AutoFarmerPlantPotBlock(
                    BlockBehaviour.Properties.ofFullCopy(
                            Blocks.FLOWER_POT
                    ).noOcclusion()
            );

    public static final SolarComposterBlock SOLAR_COMPOSTER =
            new SolarComposterBlock(
                    BlockBehaviour.Properties.ofFullCopy(
                            Blocks.COMPOSTER
                    )
            );

    public static final Block CAMELLIA_FLOWER =
            new FlowerBlock(
                    MobEffects.REGENERATION,
                    5.0f,
                    BlockBehaviour.Properties.ofFullCopy(
                            Blocks.DANDELION
                    ).noOcclusion()
            );

    public static final Block POTTED_CAMELLIA_FLOWER =
            new FlowerPotBlock(
                    CAMELLIA_FLOWER,
                    BlockBehaviour.Properties.ofFullCopy(
                            Blocks.POTTED_DANDELION
                    ).noOcclusion()
            );

    public static final ProficientBrewStandBlock PROFICIENT_BREW_STAND =
            new ProficientBrewStandBlock(
                    BlockBehaviour.Properties.ofFullCopy(
                            Blocks.BREWING_STAND
                    )
            );

    public static BlockEntityType<AutoFarmerPlantPotBlockEntity>
            AUTO_FARMER_PLANT_POT_ENTITY;

    public static BlockEntityType<SolarComposterBlockEntity>
            SOLAR_COMPOSTER_ENTITY;
    public static BlockEntityType<ProficientBrewStandBlockEntity>
            PROFICIENT_BREW_STAND_ENTITY;

    private ModBlocks() {
    }

    public static void register() {

        registerBlock(
                "auto_farmer_plant_pot",
                AUTO_FARMER_PLANT_POT,
                new GreenhouseGatedBlockItem(
                        AUTO_FARMER_PLANT_POT,
                        new Item.Properties()
                )
        );

        registerBlock(
                "solar_composter",
                SOLAR_COMPOSTER,
                new GreenhouseGatedBlockItem(
                        SOLAR_COMPOSTER,
                        new Item.Properties()
                )
        );

        registerBlock(
                "camellia_flower",
                CAMELLIA_FLOWER,
                new BlockItem(
                        CAMELLIA_FLOWER,
                        new Item.Properties()
                )
        );
        registerBlockNoItem(
                "potted_camellia_flower",
                POTTED_CAMELLIA_FLOWER
        );
        ComposterBlock.COMPOSTABLES.put(
                CAMELLIA_FLOWER.asItem(),
                0.65f
        );

        registerBlock(
                "proficient_brew_stand",
                PROFICIENT_BREW_STAND,
                new AlchemyGatedBlockItem(
                        PROFICIENT_BREW_STAND,
                        new Item.Properties(),
                        "proficient_brew_stand",
                        "Proficient Brew Stand"
                )
        );

        AUTO_FARMER_PLANT_POT_ENTITY =
                Registry.register(
                        BuiltInRegistries.BLOCK_ENTITY_TYPE,
                        id("auto_farmer_plant_pot"),
                        FabricBlockEntityTypeBuilder.create(
                                AutoFarmerPlantPotBlockEntity::new,
                                AUTO_FARMER_PLANT_POT
                        ).build()
                );

        SOLAR_COMPOSTER_ENTITY =
                Registry.register(
                        BuiltInRegistries.BLOCK_ENTITY_TYPE,
                        id("solar_composter"),
                        FabricBlockEntityTypeBuilder.create(
                                SolarComposterBlockEntity::new,
                                SOLAR_COMPOSTER
                        ).build()
                );

        PROFICIENT_BREW_STAND_ENTITY =
                Registry.register(
                        BuiltInRegistries.BLOCK_ENTITY_TYPE,
                        id("proficient_brew_stand"),
                        FabricBlockEntityTypeBuilder.create(
                                ProficientBrewStandBlockEntity::new,
                                PROFICIENT_BREW_STAND
                        ).build()
                );
    }

    private static void registerBlock(
            String name,
            Block block,
            BlockItem item
    ) {

        Registry.register(
                BuiltInRegistries.BLOCK,
                id(name),
                block
        );

        Registry.register(
                BuiltInRegistries.ITEM,
                id(name),
                item
        );
    }

    private static void registerBlockNoItem(
            String name,
            Block block
    ) {

        Registry.register(
                BuiltInRegistries.BLOCK,
                id(name),
                block
        );
    }

    private static ResourceLocation id(
            String path
    ) {

        return ResourceLocation.fromNamespaceAndPath(
                ProficiencyMod.MOD_ID,
                path
        );
    }
}
