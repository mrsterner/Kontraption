package net.illuc.kontraption.peripherals

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.ILuaContext
import dan200.computercraft.api.lua.MethodResult
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IDynamicPeripheral
import dan200.computercraft.api.peripheral.IPeripheral
import net.illuc.kontraption.blockEntities.TileEntityConnector

class ConnectorPeripheral(
    private val blockEntity: TileEntityConnector,
) : IDynamicPeripheral {
    override fun getType(): String = "Connector"

    override fun getMethodNames(): Array<String> = arrayOf("connect", "disconnect", "isconnected", "getconnID", "settocc", "settored") // need to figure out peripetal events, i RLLY want to have raycart one, buuut im not trying to read CC javadocs rn

    override fun callMethod(
        computer: IComputerAccess?,
        context: ILuaContext?,
        method: Int,
        arguments: IArguments?,
    ): MethodResult {
        return when (method) {
            0 -> {
                if (blockEntity.underCC) {
                    if (!blockEntity.isConnected) {
                        blockEntity.connectpass() // for now ya gonna have to either run connect in loop and wait for "trying to connect" or fire it perfectly as you hover above connector, could probably be fixed if we somehow apply redstone instead of calling it?
                        return MethodResult.of("Trying to connect")
                    } else {
                        return MethodResult.of("Already Connected")
                    }
                } else {
                    return MethodResult.of("Connector is not under CC")
                }
            }
            1 -> {
                if (blockEntity.underCC) {
                    if (blockEntity.isConnected) {
                        blockEntity.disconnect()
                    } else {
                        return MethodResult.of("Not Connected to anything")
                    }
                } else {
                    return MethodResult.of("Connector is not under CC")
                }
                return MethodResult.of(null)
            }
            2 -> {
                if (blockEntity.isConnected) {
                    return MethodResult.of(true)
                } else {
                    MethodResult.of(false)
                }
                return MethodResult.of(null)
            }
            3 -> {
                return MethodResult.of(blockEntity.conid)
            }
            4 -> {
                blockEntity.underCC = true
                return MethodResult.of(1)
            }
            5 -> {
                blockEntity.underCC = false
                return MethodResult.of(1)
            }
            else -> {
                MethodResult.of(null)
            }
        }
    }

    override fun attach(computer: IComputerAccess) {}

    override fun detach(computer: IComputerAccess) {}

    override fun equals(other: IPeripheral?): Boolean = other is ConnectorPeripheral && other.blockEntity == this.blockEntity

    override fun hashCode(): Int = blockEntity.hashCode()
}
