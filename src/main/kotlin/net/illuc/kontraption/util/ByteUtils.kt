package net.illuc.kontraption.util

import kotlin.experimental.and
import kotlin.experimental.or

object ByteUtils {
    /**
     * @author ZeOttery
     * @return Changed Byte
     * @param b: Byte - byte to set
     * @param index: Int - where to set 0 - 7
     * @param value: Boolean - what to set true/false
     *
     * **/
    fun setBool(
        b: Byte,
        index: Int,
        value: Boolean,
    ): Byte {
        require(index in 0..7) { "Either forgot on what index java begins or fucked smt up" }
        return if (value) {
            b or (1 shl index).toByte()
        } else {
            b and (1 shl index).inv().toByte()
        }
    }

    fun getBool(
        byte: Byte,
        index: Int,
    ): Boolean {
        require(index in 0..7) { "Either forgot on what index java begins or fucked smt up" } // this exists only bc im a moron that forgets that byte can store ONLY 8 VALUES
        return (byte.toInt() and (1 shl index)) != 0
    }

    fun setBoolArg(vararg bools: Boolean): Byte {
        var res: Byte = 0
        bools.forEachIndexed { index, b ->
            if (index < 8) {
                res = setBool(res, index, b)
            }
        }
        return res
    }
}
