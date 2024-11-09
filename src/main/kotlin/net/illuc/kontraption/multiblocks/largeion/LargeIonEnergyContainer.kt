package net.illuc.kontraption.multiblocks.largeion

import mekanism.api.Action
import mekanism.api.AutomationType
import mekanism.api.energy.IEnergyContainer
import mekanism.api.math.FloatingLong
import net.minecraft.nbt.CompoundTag

class LargeIonEnergyContainer(
    private val multiblock: LargeIonMultiblockData,
) : IEnergyContainer {
    private var energyStored = FloatingLong.ZERO
    private var transferCap = FloatingLong.ZERO
    private var storageCap = FloatingLong.ZERO

    private var queuedInput = FloatingLong.ZERO
    private var lastInput = FloatingLong.ZERO

    fun setStorageCapacity(capacity: FloatingLong) {
        storageCap = capacity
    }

    fun setTransferCapacity(capacity: FloatingLong) {
        transferCap = capacity
    }

    override fun getEnergy(): FloatingLong = energyStored.add(queuedInput)

    override fun setEnergy(energy: FloatingLong) {
        if (energy.smallerThan(FloatingLong.ZERO) || energy.greaterThan(storageCap)) {
            throw RuntimeException("Energy value is out of bounds.")
        }
        energyStored = energy
    }

    override fun insert(
        amount: FloatingLong,
        action: Action,
        automationType: AutomationType,
    ): FloatingLong {
        if (amount.isZero || !multiblock.isFormed) {
            return amount
        }
        val toAdd = amount.min(getRemainingInput()).min(getRemainingCapacity())
        if (toAdd.isZero) {
            return amount
        }
        if (action.execute()) {
            queuedInput = queuedInput.plusEqual(toAdd)
        }
        return amount.subtract(toAdd)
    }

    override fun extract(
        amount: FloatingLong,
        action: Action,
        automationType: AutomationType,
    ): FloatingLong {
        val currentEnergy = getEnergy()
        val toExtract = amount.min(currentEnergy).min(getRemainingOutput())
        if (!toExtract.isZero && action.execute()) {
            energyStored = energyStored.minusEqual(toExtract)
            queuedInput = queuedInput.plusEqual(toExtract)
            // unused, mostly for testing
        }
        return toExtract
    }

    override fun getMaxEnergy(): FloatingLong = storageCap

    override fun onContentsChanged() {
        // tralalala, ima suffer
    }

    override fun serializeNBT(): CompoundTag {
        val tag = CompoundTag()
        tag.putDouble("energyStored", energyStored.toDouble())
        tag.putDouble("transferCap", transferCap.toDouble())
        tag.putDouble("storageCap", storageCap.toDouble())
        return tag
    }

    override fun deserializeNBT(nbt: CompoundTag) {
        energyStored = FloatingLong.create(nbt.getDouble("energyStored"))
        transferCap = FloatingLong.create(nbt.getDouble("transferCap"))
        storageCap = FloatingLong.create(nbt.getDouble("storageCap"))
    }

    private fun getRemainingInput(): FloatingLong = transferCap.subtract(queuedInput)

    private fun getRemainingOutput(): FloatingLong = transferCap.subtract(queuedInput)

    private fun getRemainingCapacity(): FloatingLong = storageCap.subtract(energyStored)

    fun getLastInput(): FloatingLong = lastInput

    fun setLastInput(input: FloatingLong) {
        lastInput = input
    }
}
