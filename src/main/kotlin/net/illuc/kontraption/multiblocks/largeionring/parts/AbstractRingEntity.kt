package net.illuc.kontraption.multiblocks.largeionring.parts

import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockEntity
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockPartTypeProvider
import it.zerono.mods.zerocore.lib.data.geometry.CuboidBoundingBox
import it.zerono.mods.zerocore.lib.energy.IWideEnergyStorage
import it.zerono.mods.zerocore.lib.energy.NullEnergyHandlers
import it.zerono.mods.zerocore.lib.multiblock.cuboid.PartPosition
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator
import net.illuc.kontraption.multiblocks.largeionring.IIonRingPartType
import net.illuc.kontraption.multiblocks.largeionring.LargeIonRingMultiBlock
import net.illuc.kontraption.util.OttUtils
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

abstract class AbstractRingEntity(
    type: BlockEntityType<*>,
    position: BlockPos,
    blockState: BlockState,
) : AbstractMultiblockEntity<LargeIonRingMultiBlock>(type, position, blockState),
    IMultiblockPartTypeProvider<LargeIonRingMultiBlock, IIonRingPartType> {
    var isTop: Boolean = false
    var isCorner: Boolean = false
    var rotation: Direction = Direction.WEST
    var changedRotation: Boolean = false
    private var mbsize = 0

    fun getNBT(): Int = mbsize

    fun setNBT(mbsized: Int) {
        this.mbsize = mbsized
        setChanged()
        if (level != null && !level!!.isClientSide) {
            level!!.sendBlockUpdated(worldPosition, blockState, blockState, Block.UPDATE_CLIENTS)
        }
    }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket = ClientboundBlockEntityDataPacket.create(this)

    override fun getUpdateTag(): CompoundTag {
        val tag = super.getUpdateTag()
        tag.putInt("mbsize", this.mbsize)
        return tag
    }

    override fun handleUpdateTag(tag: CompoundTag) {
        super.handleUpdateTag(tag)
        this.mbsize = tag.getInt("mbsize")
    }

    private fun setBlockState(
        level: Level,
        pos: BlockPos,
        newState: BlockState,
    ) {
        if (!level.isClientSide) {
            level.setBlockAndUpdate(pos, newState)
            level.sendBlockUpdated(pos, newState, newState, 3)
        }
    }

    private fun setRotation(
        dir: Direction,
        pos: BlockPos,
    ) {
        val bst = level!!.getBlockState(pos).setValue(LargeIonMultiblockPartBlockTemplate.ROT, dir)
        setBlockState(level!!, pos, bst)
        changedRotation = true
    }

    private fun changeState() {
        val level = level ?: return
        val pos = blockPos
        val underPos = BlockPos(pos.x, pos.y - 1, pos.z)
        val bbe = level.getBlockEntity(underPos)
        val blockState = level.getBlockState(pos)
        val currentStateType = blockState.getValue(LargeIonMultiblockPartBlockTemplate.STATETYPE)
        val newStateType =
            when (bbe) {
                is LargeIonRingPowerPortEntity -> 1
                is LargeIonRingController -> 2
                is LargeIonRingCasingEntity -> 0
                else -> currentStateType
            }
        if (currentStateType != newStateType) {
            val updatedState = blockState.setValue(LargeIonMultiblockPartBlockTemplate.STATETYPE, newStateType)
            setBlockState(level, pos, updatedState)
        }
        val boundbox = multiblockCenter()
        val relativeCenter =
            BlockPos(
                (boundbox.minX + boundbox.maxX) / 2,
                (boundbox.maxY),
                (boundbox.minZ + boundbox.maxZ) / 2,
            )
        setNBT(boundbox.lengthX)
        if (!this.isMachineAssembled) {
            val bStateNonAss = level.getBlockState(pos).setValue(LargeIonMultiblockPartBlockTemplate.ASS, false)
            setBlockState(level, pos, bStateNonAss)
        } else {
            val bState = level.getBlockState(pos).setValue(LargeIonMultiblockPartBlockTemplate.ASS, true).setValue(LargeIonMultiblockPartBlockTemplate.SR, isTop)
            setBlockState(level, pos, bState)
            if (isCorner) {
                val bStateCorner = this.blockState.setValue(LargeIonMultiblockPartBlockTemplate.STATETYPE, 3)
                setBlockState(level, pos, bStateCorner)
                val cornerRot = checkcornerrotation(pos)
                setRotation(cornerRot, pos)
            } else {
                checkRotation(pos, relativeCenter)
                changedRotation = false
            }
            changedRotation = false
        }
    }

    private fun checkcornerrotation(pos: BlockPos): Direction {
        val isNorth = level!!.getBlockEntity(pos.north()) is AbstractRingEntity
        val isSouth = level!!.getBlockEntity(pos.south()) is AbstractRingEntity
        val isEast = level!!.getBlockEntity(pos.east()) is AbstractRingEntity
        val isWest = level!!.getBlockEntity(pos.west()) is AbstractRingEntity
        return when {
            isNorth && isEast -> Direction.WEST
            isNorth && isWest -> Direction.SOUTH
            isSouth && isEast -> Direction.NORTH
            isSouth && isWest -> Direction.EAST
            else -> Direction.NORTH
        }
    }

    private fun checkRotation(
        Pos: BlockPos,
        Center: BlockPos,
    ) {
        if (this.isMachineAssembled) {
            if (!isCorner) {
                rotation = OttUtils.getDirectionFromPositions(Center, Pos)
                setRotation(rotation, blockPos)
            }
        }
    }

    protected fun multiblockCenter(): CuboidBoundingBox =
        this.multiblockController
            .filter(LargeIonRingMultiBlock::isAssembled)
            .map(LargeIonRingMultiBlock::getBoundingBox)
            .orElse(CuboidBoundingBox.EMPTY)

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

    fun getEnergyStorage(): IWideEnergyStorage = this.evalOnController(LargeIonRingMultiBlock::getEnergyStorage, NullEnergyHandlers.STORAGE)

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

    override fun onPostMachineAssembled(controller: LargeIonRingMultiBlock) {
        this.changeState()
        super.onPostMachineAssembled(controller)
    }

    override fun onPostMachineBroken() {
        this.changeState()
        super.onPostMachineBroken()
    }

    override fun getControllerType(): Class<LargeIonRingMultiBlock> = LargeIonRingMultiBlock::class.java

    override fun onMachineActivated() {
    }

    override fun onMachineDeactivated() {
    }
}
