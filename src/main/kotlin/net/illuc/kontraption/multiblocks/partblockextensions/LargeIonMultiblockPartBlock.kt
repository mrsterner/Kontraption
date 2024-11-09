package net.illuc.kontraption.multiblocks.partblockextensions

import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockPartType
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartBlock
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController
import net.illuc.kontraption.blocks.BlockIonCasing
import net.minecraft.core.Direction
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.level.block.state.properties.IntegerProperty

class LargeIonMultiblockPartBlock<Controller : IMultiblockController<Controller>, PartType : IMultiblockPartType>(
    properties: Properties,
) : MultiblockPartBlock<Controller, PartType>(),
    EntityBlock {
    init {
        registerDefaultState(
            stateDefinition
                .any()
                .setValue(BlockIonCasing.ASS, false)
                .setValue(BlockIonCasing.SR, false)
                .setValue(BlockIonCasing.ROT, Direction.UP)
                .setValue(BlockIonCasing.STATETYPE, 0),
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockIonCasing.ASS, BlockIonCasing.SR, BlockIonCasing.ROT, BlockIonCasing.STATETYPE)
    }

    override fun getRenderShape(pState: BlockState): RenderShape =
        if (pState.getValue(ASS) == true) {
            RenderShape.ENTITYBLOCK_ANIMATED
        } else {
            RenderShape.MODEL
        }

    companion object {
        val ASS: BooleanProperty = BooleanProperty.create("ass")
        val SR: BooleanProperty = BooleanProperty.create("srender")
        val ROT: DirectionProperty = DirectionProperty.create("rotation")
        val STATETYPE: IntegerProperty = IntegerProperty.create("statetype", 0, 10)
    }
}
