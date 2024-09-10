package net.illuc.kontraption.gui

import mekanism.common.Mekanism
import net.illuc.kontraption.Kontraption
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack

class ShipTerminalMenu(
    id: Int,
    playerInventory: Inventory,
    buf: FriendlyByteBuf?,
) : AbstractContainerMenu(Kontraption.TERMINALMENU.get(), id) {
    override fun quickMoveStack(
        p0: Player,
        p1: Int,
    ): ItemStack = ItemStack.EMPTY

    data class Binds(
        var blockPos: BlockPos,
        var keyBind: Int,
    )

    var keyStones: MutableList<BlockPos> = buf?.readList(FriendlyByteBuf::readBlockPos) ?: mutableListOf()
    var keyBinds: MutableList<Int> = buf?.readList(FriendlyByteBuf::readInt) ?: mutableListOf()
    var combinedList =
        keyStones.mapIndexed { index, blockPos ->
            Binds(blockPos, keyBinds.getOrElse(index) { 0 }) // we kinda need to default lol
        }

    override fun stillValid(p0: Player): Boolean = true

    init {
        combinedList.forEach { binding ->
            Mekanism.logger.info("Menu Position: " + binding.blockPos.toString() + "KeyBind Index: " + binding.keyBind.toString())
        }
        Mekanism.logger.info("Buffer didnt null")
    }

    companion object {
        fun create(
            id: Int,
            inv: Inventory,
            buf: FriendlyByteBuf,
        ): ShipTerminalMenu = ShipTerminalMenu(id, inv, buf)
    }
}
