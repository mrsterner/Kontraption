package net.illuc.kontraption.multiblocks.largeion

import mekanism.common.Mekanism
import mekanism.common.content.blocktype.BlockType
import mekanism.common.lib.math.voxel.VoxelCuboid
import mekanism.common.lib.math.voxel.VoxelCuboid.CuboidSide
import mekanism.common.lib.multiblock.CuboidStructureValidator
import mekanism.common.lib.multiblock.FormationProtocol
import mekanism.common.lib.multiblock.FormationProtocol.*
import mekanism.common.lib.multiblock.StructureHelper
import mekanism.common.registries.MekanismBlockTypes
import net.illuc.kontraption.KontraptionBlockTypes
import net.illuc.kontraption.blockEntities.largeion.TileEntityIonCasing
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.core.impl.shadow.be
import java.util.*

class LargeIonValidator : CuboidStructureValidator<LargeIonMultiblockData>() {
    override fun getCasingType(state: BlockState?): FormationProtocol.CasingType {
        val block: Block = state!!.block
        if (BlockType.`is`(block, KontraptionBlockTypes.LARGE_ION_THRUSTER_CASING)) {
            Mekanism.logger.info("FRAME")
            return CasingType.FRAME
        } else if (BlockType.`is`(block, KontraptionBlockTypes.LARGE_ION_THRUSTER_VALVE)) {
            Mekanism.logger.info("VALVE")
            return CasingType.VALVE
        } else if (BlockType.`is`(block, KontraptionBlockTypes.LARGE_ION_THRUSTER_COIL)) {
            Mekanism.logger.info("COIL")
            return CasingType.OTHER
        } else if (BlockType.`is`(block, MekanismBlockTypes.STEEL_CASING)) {
            Mekanism.logger.info("CASE")
            return CasingType.FRAME
        }
        Mekanism.logger.info("INVALID")
        return CasingType.INVALID
    }

    val BOUNDS: VoxelCuboid = VoxelCuboid(9, 2, 9)

    val ALLOWED_LAYERS: Array<Array<ByteArray>> =
        arrayOf(
            arrayOf(
                byteArrayOf(1, 1, 2, 2, 2, 2, 2, 1, 1),
                byteArrayOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                byteArrayOf(2, 1, 0, 0, 0, 0, 0, 1, 2),
                byteArrayOf(2, 1, 0, 0, 0, 0, 0, 1, 2),
                byteArrayOf(2, 1, 0, 0, 0, 0, 0, 1, 2),
                byteArrayOf(2, 1, 0, 0, 0, 0, 0, 1, 2),
                byteArrayOf(2, 1, 0, 0, 0, 0, 0, 1, 2),
                byteArrayOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                byteArrayOf(1, 1, 2, 2, 2, 2, 2, 1, 1),
            ),
            arrayOf(
                byteArrayOf(4, 1, 3, 3, 3, 3, 3, 1, 4),
                byteArrayOf(1, 2, 2, 2, 2, 2, 2, 2, 1),
                byteArrayOf(3, 2, 0, 0, 0, 0, 0, 2, 3),
                byteArrayOf(3, 2, 0, 0, 0, 0, 0, 2, 3),
                byteArrayOf(3, 2, 0, 0, 0, 0, 0, 2, 3),
                byteArrayOf(3, 2, 0, 0, 0, 0, 0, 2, 3),
                byteArrayOf(3, 2, 0, 0, 0, 0, 0, 2, 3),
                byteArrayOf(1, 2, 2, 2, 2, 2, 2, 2, 1),
                byteArrayOf(4, 1, 3, 3, 3, 3, 3, 1, 4),
            ),
        )

    override fun getStructureRequirement(pos: BlockPos): StructureRequirement {
        val relativePos = pos.subtract(cuboid.minPos)
        val relative = cuboid.getWallRelative(pos)
        val be = world.getBlockEntity(pos) as? TileEntityIonCasing
        if (relative.isWall) {
            val h = relativePos.x
            val v = relativePos.z

            val layer = relativePos.y
            var allowedType = ALLOWED_LAYERS[layer][h][v]

            if (allowedType == 3.toByte()) {
                if (be != null) {
                    be.isTop = true
                }
                allowedType = 1.toByte()
            } else {
                if (be != null) {
                    be.isTop = false
                }
            }
            if (allowedType == 4.toByte()) {
                if (be != null) {
                    be.isCorner = true
                    be.isTop = true
                }
                allowedType = 1.toByte()
            } else {
                if (be != null) {
                    be.isCorner = false
                }
            }

            return StructureRequirement.REQUIREMENTS[allowedType.toInt()]
        }

        return super.getStructureRequirement(pos)
    }

    override fun precheck(): Boolean {
        cuboid = StructureHelper.fetchCuboid(structure, BOUNDS, BOUNDS, EnumSet.allOf(CuboidSide::class.java), 50)
        return cuboid != null
    }
}
