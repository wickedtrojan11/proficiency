package com.trojan.proficiency.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class AutoFarmerPlantPotBlock extends BaseEntityBlock {

    public static final MapCodec<AutoFarmerPlantPotBlock> CODEC =
            simpleCodec(AutoFarmerPlantPotBlock::new);

    public static final IntegerProperty STAGE =
            IntegerProperty.create(
                    "stage",
                    0,
                    4
            );

    public static final IntegerProperty CROP_TYPE =
            IntegerProperty.create(
                    "crop_type",
                    0,
                    6
            );

    private static final VoxelShape SHAPE =
            Block.box(
                    5.0,
                    0.0,
                    5.0,
                    11.0,
                    6.0,
                    11.0
            );

    public AutoFarmerPlantPotBlock(
            Properties properties
    ) {

        super(properties);

        registerDefaultState(
                stateDefinition.any()
                        .setValue(
                                STAGE,
                                0
                        )
                        .setValue(
                                CROP_TYPE,
                                0
                        )
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {

        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(
            BlockState state
    ) {

        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {

        return SHAPE;
    }

    @Override
    protected VoxelShape getOcclusionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {

        return Shapes.empty();
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {

        builder.add(
                STAGE,
                CROP_TYPE
        );
    }

    @Override
    protected void onRemove(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState newState,
            boolean movedByPiston
    ) {

        if (
                !state.is(newState.getBlock())
                        && level.getBlockEntity(pos)
                        instanceof AutoFarmerPlantPotBlockEntity blockEntity
        ) {

            blockEntity.dropStoredCrop();
        }

        super.onRemove(
                state,
                level,
                pos,
                newState,
                movedByPiston
        );
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(
            BlockPos pos,
            BlockState state
    ) {

        return new AutoFarmerPlantPotBlockEntity(
                pos,
                state
        );
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {

        if (!(level instanceof ServerLevel)) {
            return null;
        }

        return createTickerHelper(
                type,
                ModBlocks.AUTO_FARMER_PLANT_POT_ENTITY,
                AutoFarmerPlantPotBlockEntity::serverTick
        );
    }
}
