package net.illuc.kontraption.blocks

import mekanism.common.block.prefab.BlockBasicMultiblock
import mekanism.common.block.states.BlockStateHelper
import mekanism.common.content.blocktype.BlockTypeTile
import net.illuc.kontraption.blockEntities.largeion.TileEntityIonController
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

class BlockIonController(
    type: BlockTypeTile<TileEntityIonController>,
) : BlockBasicMultiblock<TileEntityIonController>(
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
                .setValue(STATETYPE, 0)
                .setValue(facing, Direction.UP),
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(ASS, SR, ROT, STATETYPE, facing)
    }

    override fun getRenderShape(pState: BlockState): RenderShape {
        if (pState.getValue(ASS) == true) {
            return RenderShape.ENTITYBLOCK_ANIMATED
        } else {
            return RenderShape.MODEL
        }
    }

    companion object {
        val facing: DirectionProperty = DirectionProperty.create("facing")
        val ASS: BooleanProperty = BooleanProperty.create("ass")
        val SR: BooleanProperty = BooleanProperty.create("srender")
        val ROT: DirectionProperty = DirectionProperty.create("rotation")
        val STATETYPE: IntegerProperty = IntegerProperty.create("statetype", 0, 10)
    }
}
