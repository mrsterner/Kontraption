package net.illuc.kontraption.util

import it.zerono.mods.zerocore.lib.data.geometry.CuboidBoundingBox
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block

class StructReq(
    private val allowedLayers: Array<Array<ByteArray>>,
    private val blockMappings: Map<Byte, List<Block>>,
    private val cbb: CuboidBoundingBox,
) {
    /**
     * Checks if the block at a given position satisfies the structure requirement for this Array of Layers.
     * @param world The world in which the multiblock is located(CANNOT BE RELATIVE)
     * @param pos The position of the block
     * @param airc If 0 should by default be Air
     * @return True if the block at the position meets the requirement, false otherwise
     */
    fun isValidBlock(
        world: Level,
        pos: BlockPos,
        airc: Boolean,
    ): Pair<Boolean, Byte> {
        if (cbb.lengthY == allowedLayers.size) {
            val relativePos = pos.subtract(Vec3i(cbb.minX, cbb.minY, cbb.minZ))
            val layer = relativePos.y
            val h = relativePos.x
            val v = relativePos.z
            if (layer < 0 || layer >= allowedLayers.size) return Pair(false, 0.toByte())
            val requiredType = allowedLayers[layer][h][v]
            val validBlocks = blockMappings[requiredType] ?: return Pair(false, 0.toByte())
            val blockAtPosition = world.getBlockState(pos)
            return if (airc) {
                if (requiredType == 0.toByte()) {
                    Pair(blockAtPosition.isAir, requiredType)
                } else {
                    Pair(validBlocks.contains(blockAtPosition.block), requiredType)
                }
            } else {
                Pair(validBlocks.contains(blockAtPosition.block), requiredType)
            }
        } else {
            return Pair(false, 0.toByte())
        }
    }
}
