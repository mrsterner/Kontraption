package net.illuc.kontraption.blocks

import mekanism.common.block.prefab.BlockBasicMultiblock
import mekanism.common.block.states.BlockStateHelper
import mekanism.common.content.blocktype.BlockTypeTile
import net.illuc.kontraption.blockEntities.largeion.TileEntityIonCasing
import net.minecraft.core.Direction
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument

class BlockIonCasing(
    type: BlockTypeTile<TileEntityIonCasing>,
) : BlockBasicMultiblock<TileEntityIonCasing>(
        type,
        BlockBehaviour.Properties
            .of()
            .sound(SoundType.METAL)
            .strength(3.5F, 4.8F)
            .requiresCorrectToolForDrops()
            .noOcclusion()
            .isSuffocating(BlockStateHelper.NEVER_PREDICATE)
            .isViewBlocking(BlockStateHelper.NEVER_PREDICATE)
            .instrument(NoteBlockInstrument.HAT),
    ) {
    init {
        registerDefaultState(
            stateDefinition
                .any()
                .setValue(ASS, false)
                .setValue(SR, false)
                .setValue(ROT, Direction.UP)
                .setValue(STATETYPE, 0),
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(ASS, SR, ROT, STATETYPE)
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
