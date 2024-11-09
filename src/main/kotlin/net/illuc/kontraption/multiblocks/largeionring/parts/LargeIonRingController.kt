package net.illuc.kontraption.multiblocks.largeionring.parts

import it.zerono.mods.zerocore.base.CommonConstants
import it.zerono.mods.zerocore.lib.block.TileCommandDispatcher
import net.illuc.kontraption.KontraptionTileEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.client.model.data.ModelData

class LargeIonRingController(
    position: BlockPos,
    blockState: BlockState,
) : AbstractRingEntity(KontraptionTileEntityTypes.LARGE_ION_THRUSTER_CONTROLLER.get(), position, blockState) {
    init {
        setCommandDispatcher(
            TileCommandDispatcher
                .builder<LargeIonRingController>()
                .addServerHandler(CommonConstants.COMMAND_ACTIVATE) { e -> e.setRingActive(true) }
                .addServerHandler(CommonConstants.COMMAND_DEACTIVATE) { tce -> tce.setRingActive(false) }
                .build(this),
        )
    }

    override fun getUpdatedModelData(): ModelData = ModelData.EMPTY
}
