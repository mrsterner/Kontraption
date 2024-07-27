package net.illuc.kontraption.multiblocks.railgun

import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import mekanism.common.content.blocktype.BlockType
import mekanism.common.lib.math.voxel.VoxelCuboid
import mekanism.common.lib.math.voxel.VoxelCuboid.CuboidSide
import mekanism.common.lib.multiblock.CuboidStructureValidator
import mekanism.common.lib.multiblock.FormationProtocol
import mekanism.common.lib.multiblock.FormationProtocol.*
import mekanism.common.lib.multiblock.StructureHelper
import net.illuc.kontraption.KontraptionBlockTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.ChunkAccess
import java.util.*

class RailgunValidator : CuboidStructureValidator<RailgunMultiblockData>() {
    val MIN_CUBOID: VoxelCuboid = VoxelCuboid(3, 3, 3)

    val MAX_CUBOID: VoxelCuboid = VoxelCuboid(18, 18, 18)

    var pickedSide: Direction? = null

    override fun getCasingType(state: BlockState?): FormationProtocol.CasingType {
        val block: Block = state!!.block
        if (BlockType.`is`(block, KontraptionBlockTypes.RAILGUN_CASING)) {
            return CasingType.FRAME
        } else if (BlockType.`is`(block, KontraptionBlockTypes.RAILGUN_PORT)) {
            return CasingType.VALVE
        } else if (BlockType.`is`(block, KontraptionBlockTypes.RAILGUN_COIL)) {
            return CasingType.OTHER
        } else if (BlockType.`is`(block, KontraptionBlockTypes.RAILGUN_CONTROLLER)) {
            return CasingType.OTHER
        }
        return CasingType.INVALID
    }

    override fun getStructureRequirement(pos: BlockPos): StructureRequirement {
        val relative = cuboid.getWallRelative(pos)
        if ((pos.y == cuboid.maxPos.y) or (pos.x == cuboid.maxPos.x) or (pos.z == cuboid.maxPos.z) or (pos.y == cuboid.minPos.y) or (pos.x == cuboid.minPos.x) or (pos.z == cuboid.minPos.z)) {
            println(pos)
            return if (!relative.isOnEdge) {
                var curside: Direction? = null
                if (pos.y == cuboid.maxPos.y) {
                    curside = Direction.UP
                } else if (pos.x == cuboid.maxPos.x) {
                    curside = Direction.EAST
                } else if (pos.z == cuboid.maxPos.z) {
                    curside = Direction.SOUTH
                } else if (pos.y == cuboid.minPos.y) {
                    curside = Direction.DOWN
                } else if (pos.x == cuboid.minPos.x) {
                    curside = Direction.WEST
                } else if (pos.z == cuboid.minPos.z) {
                    curside = Direction.NORTH
                }

                if (pickedSide == null) {
                    pickedSide = curside
                    println("inner")
                    StructureRequirement.INNER
                } else {
                    println("other")
                    StructureRequirement.OTHER
                }
            } else {
                println("other")
                StructureRequirement.OTHER
            }
        }
        return super.getStructureRequirement(pos)
    }

    override fun precheck(): Boolean {
        cuboid =
            StructureHelper.fetchCuboid(
                structure,
                MIN_CUBOID,
                MAX_CUBOID,
                (CuboidSide.SIDES.toSet()),
                8,
            )
        println("cuboid: " + (cuboid != null))
        return cuboid != null
    }

    override fun postcheck(
        structure: RailgunMultiblockData?,
        chunkMap: Long2ObjectMap<ChunkAccess>?,
    ): FormationResult? {
        println("postcheck")
        return FormationResult.SUCCESS
    }
}
