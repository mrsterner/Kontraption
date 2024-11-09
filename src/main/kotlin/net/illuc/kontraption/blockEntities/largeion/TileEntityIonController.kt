package net.illuc.kontraption.blockEntities.largeion

import net.illuc.kontraption.KontraptionBlocks
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

class TileEntityIonController(
    pos: BlockPos?,
    state: BlockState?,
) : TileEntityIonCasing(
        KontraptionBlocks.LARGE_ION_THRUSTER_COIL,
        pos,
        state,
    )
