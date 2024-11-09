package net.illuc.kontraption.multiblocks.largeionring.parts

import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockEntity
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockPartTypeProvider
import it.zerono.mods.zerocore.lib.multiblock.cuboid.PartPosition
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator
import net.illuc.kontraption.multiblocks.largeionring.IIonRingPartType
import net.illuc.kontraption.multiblocks.largeionring.LargeIonRingMultiBlock
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

abstract class AbstractRingEntity(
    type: BlockEntityType<*>,
    position: BlockPos,
    blockState: BlockState,
) : AbstractMultiblockEntity<LargeIonRingMultiBlock>(type, position, blockState),
    IMultiblockPartTypeProvider<LargeIonRingMultiBlock, IIonRingPartType> {
    protected fun IsRingActive(): Boolean =
        this
            .multiblockController
            .filter(LargeIonRingMultiBlock::isAssembled)
            .map(LargeIonRingMultiBlock::enabled)
            .orElse(false)

    fun getPartDisplayName(): Component =
        Component.translatable(
            this.partType
                .map<Any>(IIonRingPartType::getTranslationKey)
                .orElse("unknown")
                .toString(),
        )

    protected fun setRingActive(active: Boolean) {
        this.multiblockController
            .filter(LargeIonRingMultiBlock::isAssembled)
            .ifPresent { c: LargeIonRingMultiBlock -> c.setMachineActive(active) }
    }

    override fun isGoodForPosition(
        p0: PartPosition,
        p1: IMultiblockValidator,
    ): Boolean {
        p1.setLastError(this.getWorldPosition(), "multiblock.validation.ring.ILLEGAL_CHECK")
        return true
    }

    override fun createController(): LargeIonRingMultiBlock {
        val myWorld = this.getLevel() ?: throw RuntimeException("Trying to create a Controller from a Part without a Level")

        return LargeIonRingMultiBlock(myWorld)
    }

    override fun getControllerType(): Class<LargeIonRingMultiBlock> = LargeIonRingMultiBlock::class.java

    override fun onMachineActivated() {
    }

    override fun onMachineDeactivated() {
    }
}
