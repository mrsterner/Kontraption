package net.illuc.kontraption.util

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction

object OttUtils {
    /** maybe more than one use, dunnon lol
     * @param center Center Of MultiBLock
     * @param target Your Current possition
     * @return Returns direction from center to target
     * maybe lil inverted lmao
     * */
    fun getDirectionFromPositions(
        center: BlockPos,
        target: BlockPos,
    ): Direction {
        val dx = target.x - center.x
        val dy = target.y - center.y
        val dz = target.z - center.z

        val absDx = kotlin.math.abs(dx)
        val absDy = kotlin.math.abs(dy)
        val absDz = kotlin.math.abs(dz)

        return when {
            absDx >= absDy && absDx >= absDz -> {
                if (dx > 0) Direction.EAST else Direction.WEST
            }
            absDz >= absDx && absDz >= absDy -> {
                if (dz > 0) Direction.SOUTH else Direction.NORTH
            }
            // We RALLY hope that our directions are correct lmao
            else -> {
                if (dy > 0) Direction.UP else Direction.DOWN
            }
        }
    }

    /**
     * @param width Width of ring
     * @param height Ammout of layers
     * @param depth Height of ring
     * @return Returns Array of ByteArrays that represents ion ring structure of gives size*/
    fun generateAllowedLayers(
        width: Int,
        height: Int,
        depth: Int,
    ): Array<Array<ByteArray>> =
        Array(height) { layer ->
            Array(width) { x ->
                ByteArray(depth) { z ->
                    val isCorner = (x == 0 || x == width - 1) && (z == 0 || z == depth - 1)
                    val isEdge = (x == 0 || x == width - 1 || z == 0 || z == depth - 1)
                    val isInner = (x > 0 && x < width - 1) && (z > 0 && z < depth - 1)
                    val isnInner = (x > 1 && x < width - 2) && (z > 1 && z < depth - 2)
                    val isSpecialCell = (x <= 1 || x >= width - 2) && (z == 0 || z == 1 || z == depth - 1 || z == depth - 2) && !isCorner
                    when {
                        layer == height - 1 && isCorner -> 4
                        isSpecialCell && !isInner -> 1
                        layer == height - 1 && isEdge -> 3
                        isCorner && layer == 0 -> 1
                        isCorner -> 2
                        layer > 0 && isEdge -> 1
                        !isnInner && isInner && layer == 0 -> 1
                        !isnInner && isInner && layer > 0 -> 5
                        layer > 0 -> 0
                        else -> if (isEdge) 2 else 0
                    }
                }
            }
        }
}
