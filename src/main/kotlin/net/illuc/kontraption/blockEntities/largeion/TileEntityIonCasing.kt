package net.illuc.kontraption.blockEntities.largeion

import mekanism.api.providers.IBlockProvider
import mekanism.common.lib.multiblock.MultiblockManager
import mekanism.common.tile.prefab.TileEntityMultiblock
import net.illuc.kontraption.Kontraption
import net.illuc.kontraption.KontraptionBlocks
import net.illuc.kontraption.blocks.BlockIonCasing
import net.illuc.kontraption.multiblocks.largeion.LargeIonMultiblockData
import net.illuc.kontraption.util.OttUtils
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.div
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.plus

open class TileEntityIonCasing(
    blockProvider: IBlockProvider? = KontraptionBlocks.LARGE_ION_THRUSTER_CASING,
    val pos: BlockPos?,
    val state: BlockState?,
) : TileEntityMultiblock<LargeIonMultiblockData?>(blockProvider, pos, state) {
    var rotation: Direction = Direction.WEST
    var changedRotation: Boolean = false
    var isTop: Boolean = false
    var isCorner: Boolean = false
    lateinit var prevMultiblock: LargeIonMultiblockData

    var d1 = Direction.WEST
    var d2 = Direction.SOUTH
    var d3 = Direction.NORTH
    var d4 = Direction.EAST

    constructor(pos: BlockPos?, state: BlockState?) : this(
        KontraptionBlocks.LARGE_ION_THRUSTER_CASING,
        pos,
        state,
    )

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

    override fun createMultiblock(): LargeIonMultiblockData = LargeIonMultiblockData(this)

    override fun getManager(): MultiblockManager<LargeIonMultiblockData?> = Kontraption.largeIonThrusterManager

    override fun structureChanged(multiblock: LargeIonMultiblockData?) {
        if (pos == null || multiblock == null || level == null) return
        val underpos = BlockPos(pos.x, pos.y - 1, pos.z)
        val level = level ?: return
        val bbe = level.getBlockEntity(underpos)
        val blockState = level.getBlockState(pos)
        val currentStateType = blockState.getValue(BlockIonCasing.STATETYPE)
        val newStateType =
            when (bbe) {
                is TileEntityIonValve -> 1
                is TileEntityIonController -> 2
                is TileEntityIonCasing -> 0
                else -> currentStateType
            }
        if (currentStateType != newStateType) {
            val updatedState = blockState.setValue(BlockIonCasing.STATETYPE, newStateType)
            setBlockState(level, pos, updatedState)
        }
        val relativeCenter =
            BlockPos(
                (multiblock.minPos.x + multiblock.maxPos.x) / 2,
                (multiblock.minPos.y + multiblock.maxPos.y) / 2,
                (multiblock.minPos.z + multiblock.maxPos.z) / 2,
            )
        val relativePos = pos.subtract(relativeCenter)

        if (!multiblock.isFormed) {
            prevMultiblock.disable()

            val blockState = level!!.getBlockState(pos).setValue(BlockIonCasing.ASS, false)
            setBlockState(level!!, pos, blockState)
        } else {
            val blockState = level!!.getBlockState(pos).setValue(BlockIonCasing.ASS, true)
            setBlockState(level!!, pos, blockState)
            val bstate = this.blockState.setValue(BlockIonCasing.SR, isTop)
            setBlockState(level!!, pos, bstate)
            if (isCorner) {
                val bstate = this.blockState.setValue(BlockIonCasing.STATETYPE, 3)
                setBlockState(level!!, pos, bstate)
                val cornerRot = checkcornerrotation(pos)
                setRotation(cornerRot, pos)
            } else {
                checkRotation(relativePos)
                changedRotation = false
            }
            changedRotation = false
        }

        prevMultiblock = multiblock
        super.structureChanged(multiblock)
    }

    private fun checkRotation(relativePos: BlockPos) {
        if (pos != null && multiblock != null) {
            if (!isCorner) {
                rotation = OttUtils.getDirectionFromPositions(multiblock!!.center, relativePos)
                setRotation(rotation, pos)
            }
        }
    }

    private fun setRotation(
        dir: Direction,
        pos: BlockPos,
    ) {
        val bst = level!!.getBlockState(pos).setValue(BlockIonCasing.ROT, dir)
        setBlockState(level!!, pos, bst)
        changedRotation = true
    }

    private fun checkcornerrotation(pos: BlockPos): Direction {
        val isNorth = level!!.getBlockEntity(pos.north()) is TileEntityIonCasing
        val isSouth = level!!.getBlockEntity(pos.south()) is TileEntityIonCasing
        val isEast = level!!.getBlockEntity(pos.east()) is TileEntityIonCasing
        val isWest = level!!.getBlockEntity(pos.west()) is TileEntityIonCasing
        return when {
            isNorth && isEast -> d1
            isNorth && isWest -> d2
            isSouth && isEast -> d3
            isSouth && isWest -> d4
            else -> Direction.NORTH
        }
    }
}
