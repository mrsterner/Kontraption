import net.illuc.kontraption.KontraptionBlocks
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

object RingHelper {
    private val validInteriorBlocks: Set<Block> =
        setOf(
            KontraptionBlocks.LARGE_ION_THRUSTER_CASING.block,
            KontraptionBlocks.LARGE_ION_THRUSTER_CASING.block,
        )

    fun isValidInterior(blockState: BlockState): Boolean = validInteriorBlocks.contains(blockState.block)
}
