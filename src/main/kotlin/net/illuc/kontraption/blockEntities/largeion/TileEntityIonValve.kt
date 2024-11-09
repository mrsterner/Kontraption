package net.illuc.kontraption.blockEntities.largeion

import mekanism.api.IContentsListener
import mekanism.api.energy.IEnergyContainer
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder
import net.illuc.kontraption.KontraptionBlocks
import net.illuc.kontraption.multiblocks.largeion.LargeIonMultiblockData
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional

class TileEntityIonValve(
    pos: BlockPos?,
    state: BlockState?,
) : TileEntityIonCasing(
        KontraptionBlocks.LARGE_ION_THRUSTER_VALVE,
        pos,
        state,
    ) {
    private var energyCap: LazyOptional<IEnergyContainer> = LazyOptional.empty()

    override fun getInitialEnergyContainers(listener: IContentsListener?): IEnergyContainerHolder = IEnergyContainerHolder { side: Direction? -> multiblock!!.getEnergyContainers(side) }

    override fun onUpdateServer(multiblock: LargeIonMultiblockData?): Boolean {
        val needsPacket = super.onUpdateServer(multiblock)
        if (active && multiblock!!.isFormed) {
            energyCap = LazyOptional.of { multiblock.energyContainer } // fuck mekansim
        }
        return needsPacket
    }

    override fun <T> getCapability(
        cap: Capability<T>,
        side: Direction?,
    ): LazyOptional<T> =
        if (cap === ForgeCapabilities.ENERGY) {
            energyCap.cast()
        } else {
            super.getCapability(cap, side)
        }

    override fun invalidateCaps() {
        super.invalidateCaps()
        energyCap.invalidate()
    }
}
