package com.trojan.proficiency.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SolarComposterBlock extends BaseEntityBlock {

    public static final MapCodec<SolarComposterBlock> CODEC =
            simpleCodec(SolarComposterBlock::new);

    public SolarComposterBlock(
            Properties properties
    ) {

        super(properties);
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
                        instanceof SolarComposterBlockEntity blockEntity
        ) {

            Containers.dropContents(
                    level,
                    pos,
                    blockEntity
            );

            level.updateNeighbourForOutputSignal(
                    pos,
                    this
            );
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

        return new SolarComposterBlockEntity(
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
                ModBlocks.SOLAR_COMPOSTER_ENTITY,
                SolarComposterBlockEntity::serverTick
        );
    }
}
