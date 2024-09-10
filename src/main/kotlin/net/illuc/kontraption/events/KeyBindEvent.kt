package net.illuc.kontraption.events

import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.eventbus.api.Event
import org.valkyrienskies.core.api.ships.ServerShip

class KeyBindEvent(
    val keybindIndex: Int,
    val updown: Boolean,
    val player: ServerPlayer,
    val ship: ServerShip,
) : Event()
