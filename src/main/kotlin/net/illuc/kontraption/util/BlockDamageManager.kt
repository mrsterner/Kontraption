package net.illuc.kontraption.util

import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.state.BlockState
import kotlin.math.floor


public class BlockDamageManager {

    /*

        https://github.com/Cannoneers-of-Create/CreateBigCannons/blob/1.20/dev/common/src/main/java/rbasamoyai/createbigcannons/base/PartialBlockDamageManager.java
        bwuh  ⬆️⬆️⬆️

     */

    private var blockDamage: MutableMap<ResourceKey<Level>, MutableMap<BlockPos, Int>>? = null

    fun levelLoaded(level: LevelAccessor) {
        this.cleanUp()
    }

    private fun cleanUp() {
        this.blockDamage = HashMap()
    }


    fun tick(level: Level) {
        val dimension = level.dimension()
        if (!blockDamage!!.containsKey(dimension)) return
        val levelSet: MutableMap<BlockPos, Int>? = blockDamage!![dimension]  //state.getBlock().getExplosionResistance();
        if (levelSet!!.isEmpty()) {
            blockDamage!!.remove(dimension)
            return
        }
        if (level.gameTime % 20 != 0L) return

        val newSet: MutableMap<BlockPos, Int> = HashMap()
        val iter: MutableIterator<Map.Entry<BlockPos, Int>> = levelSet!!.entries.iterator()
        while (iter.hasNext()) {
            val entry = iter.next()
            val pos = entry.key

            val state = level.getChunkAt(pos).getBlockState(pos)
            val oldProgress = entry.value
            if (state.canBeReplaced() || !state.isSolid || state.getDestroySpeed(level, pos) == -1f) {
                if (oldProgress > 0) level.destroyBlockProgress(-1, pos, -1)
                iter.remove()
            } else {
                val newProgress = oldProgress - 3
                if (newProgress <= 0) {
                    level.destroyBlockProgress(-1, pos, -1)
                    iter.remove()
                } else {
                    newSet[entry.key] = newProgress
                }
            }
        }

        levelSet!!.putAll(newSet)
    }

    fun damageBlock(pos: BlockPos, added: Int, state: BlockState?, level: Level) {

        val levelSet: MutableMap<BlockPos, Int> = this.blockDamage!!.getOrPut(level.dimension()) { HashMap() }

        val oldProgress = levelSet.getOrDefault(pos, 0)
        levelSet.merge(pos, added) { a: Int?, b: Int? -> Integer.sum(a!!, b!!) }

        val hardnessRec: Float = 1 / state!!.getBlock().explosionResistance
        val oldPart = floor(oldProgress * hardnessRec).toInt()
        val newPart = floor(levelSet[pos]!! * hardnessRec).toInt()

        if (newPart >= 10) {
            if (!level.isClientSide()) level.destroyBlock(pos, false)
            levelSet.remove(pos)
        } else if (newPart - oldPart > 0) {
            level.destroyBlockProgress(-1, pos, newPart)
        }
    }


}