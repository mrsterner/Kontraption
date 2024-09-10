package net.illuc.kontraption.network.to_server

import mekanism.common.network.IMekanismPacket
import net.illuc.kontraption.blockEntities.TileEntityKey
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Player
import net.minecraftforge.network.NetworkEvent

// HAHA SANITY GONE, simplified A LOT, soo im better :3
class PacketKontraptionScreen(
    val index: Int,
    val blockPos: BlockPos,
) : IMekanismPacket {
    override fun handle(context: NetworkEvent.Context) {
        val player: Player? = context.sender
        if (player != null) {
            context.enqueueWork {
                val level = player.level()
                val blockEntity = level.getBlockEntity(blockPos)
                if (blockEntity is TileEntityKey) {
                    blockEntity.getsetKeybind(true, index)
                }
            }
        }
    }

    override fun encode(buffer: FriendlyByteBuf) {
        buffer.writeInt(index)
        buffer.writeBlockPos(blockPos)
    }

    companion object {
        fun decode(buffer: FriendlyByteBuf) = PacketKontraptionScreen(buffer.readInt(), buffer.readBlockPos())
    }
}
