package net.illuc.kontraption.multiblocks.largeionring

import RingHelper
import it.zerono.mods.zerocore.lib.IActivableMachine
import it.zerono.mods.zerocore.lib.block.ModBlock
import it.zerono.mods.zerocore.lib.energy.EnergyBuffer
import it.zerono.mods.zerocore.lib.energy.EnergySystem
import it.zerono.mods.zerocore.lib.energy.IWideEnergyStorage
import it.zerono.mods.zerocore.lib.energy.handler.WideEnergyStoragePolicyWrapper
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockPart
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockController
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator
import net.illuc.kontraption.KontraptionBlocks
import net.illuc.kontraption.ThrusterInterface
import net.illuc.kontraption.blockEntities.largeion.TileEntityIonCasing
import net.illuc.kontraption.particles.ThrusterParticleData
import net.illuc.kontraption.util.*
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.phys.Vec3
import net.minecraftforge.api.distmarker.Dist
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.Ship
import java.util.function.Consumer

class LargeIonRingMultiBlock(
    world: Level,
) : AbstractCuboidMultiblockController<LargeIonRingMultiBlock>(world),
    ThrusterInterface,
    IActivableMachine {
    // vars
    var exhaustDirection: Direction = Direction.UP
    var centerExhaust: BlockPos = this.boundingBox.center
    var exhaustDiameter = 0
    var offset: Vec3 = Vec3(0.0, 0.0, 0.0)
    var center: BlockPos = BlockPos(0, 0, 0)
    var innerVolume = 1
    var particleDir = exhaustDirection.normal.multiply(3 + exhaustDiameter).toJOMLD()
    var pos = centerExhaust.offset(exhaustDirection.normal.multiply(2))
    var ship: Ship? = null
    val sizeX = boundingBox.maxX - boundingBox.minX
    val sizeZ = boundingBox.maxZ - boundingBox.minZ
    val areaBIG = (sizeX * sizeZ) * 2
    val areaSMALL = ((sizeX - 2) * (sizeZ - 2)) * 2
    override var enabled = true
    override var thrusterLevel: Level? = world
    override var worldPosition: BlockPos? = center
    override var forceDirection: Direction = exhaustDirection.opposite
    override var powered: Boolean = true
    override var thrusterPower: Double = 100.0
    override val basePower: Double = 100.0

    // energy settings
    private val ENERGY_CAPACITY: Double = 10000000.0

    // Burn-related crap
    var burnRemaining = 0.0
    var lastBurnRate = 0.0

    private val energyStorage = EnergyBuffer(EnergySystem.ForgeEnergy, ENERGY_CAPACITY)
    val energyInputHandler = WideEnergyStoragePolicyWrapper.inputOnly(this.energyStorage)

    val blockMappings: Map<Byte, List<Block>> =
        mapOf(
            1.toByte() to listOf(KontraptionBlocks.LARGE_ION_THRUSTER_CASING.block),
            2.toByte() to listOf(KontraptionBlocks.LARGE_ION_THRUSTER_CASING.block),
            3.toByte() to listOf(KontraptionBlocks.LARGE_ION_THRUSTER_CASING.block),
            4.toByte() to listOf(KontraptionBlocks.LARGE_ION_THRUSTER_CASING.block),
        )
    private val ALLOWED_LAYERS: Array<Array<ByteArray>> = OttUtils.generateAllowedLayers(boundingBox.lengthX, boundingBox.lengthZ, 2)
    private val structureRequirement = StructReq(ALLOWED_LAYERS, blockMappings, this.boundingBox)

    // nukes internall stuff
    fun reset() {
        this.isMachineActive = false
        this.energyStorage.energyStored = 0.0
    }

    override fun isMachineActive(): Boolean = enabled

    override fun setMachineActive(active: Boolean) {
        if (this.isMachineActive() === active) {
            return
        }

        this.enabled = active

        if (active) {
            this.connectedParts.forEach(Consumer { obj: IMultiblockPart<LargeIonRingMultiBlock?> -> obj.onMachineActivated() })
        } else {
            this.connectedParts.forEach(Consumer { obj: IMultiblockPart<LargeIonRingMultiBlock?> -> obj.onMachineDeactivated() })
        }

        this.callOnLogicalServer(Runnable { this.markReferenceCoordForUpdate() })
    }

    fun getEnergyStorage(): IWideEnergyStorage = this.energyInputHandler

    override fun onPartAdded(p0: IMultiblockPart<LargeIonRingMultiBlock>) {
        println("added part to ION RING")
    }

    override fun onPartRemoved(p0: IMultiblockPart<LargeIonRingMultiBlock>) {
        println("removed part from ION RING")
    }

    override fun onMachineRestored() {
        println("restored ION RING")
    }

    override fun onMachinePaused() {
        println("paused ION RING")
    }

    override fun onMachineDisassembled() {
        println("disassembled ION RING")
    }

    override fun getMinimumNumberOfPartsForAssembledMachine(): Int {
        if (sizeX == sizeZ) {
            return areaBIG - areaSMALL
        }
        return 100000
    }

    override fun getMaximumXSize() = 15

    override fun getMaximumZSize() = 15

    override fun getMaximumYSize() = 2

    override fun updateServer(): Boolean {
        if (powered) {
            burnFuel(thrusterLevel as Level)
        } else {
            lastBurnRate = 0.0
        }

        if (powered && enabled) {
            if (Dist.DEDICATED_SERVER.isDedicatedServer && thrusterLevel != null) {
                particleDir =
                    if (ship == null) {
                        exhaustDirection.normal.multiply(innerVolume).toJOMLD()
                    } else {
                        ship!!.transform.shipToWorld.transformDirection(exhaustDirection.normal.multiply(innerVolume).toJOMLD())
                    }

                sendParticleData(thrusterLevel as ServerLevel, pos.toDoubles(), particleDir)
            }
        }
        return true
    }

    override fun updateClient() {
        // Client-side logic
    }

    override fun isBlockGoodForFrame(
        p0: Level,
        p1: Int,
        p2: Int,
        p3: Int,
        p4: IMultiblockValidator,
    ): Boolean {
        return false // Add your logic here
    }

    override fun isBlockGoodForTop(
        p0: Level,
        p1: Int,
        p2: Int,
        p3: Int,
        p4: IMultiblockValidator,
    ): Boolean = false

    override fun isBlockGoodForBottom(
        p0: Level,
        p1: Int,
        p2: Int,
        p3: Int,
        p4: IMultiblockValidator,
    ): Boolean = false

    override fun isBlockGoodForSides(
        p0: Level,
        p1: Int,
        p2: Int,
        p3: Int,
        p4: IMultiblockValidator,
    ): Boolean = false

    override fun isBlockGoodForInterior(
        world: Level,
        x: Int,
        y: Int,
        z: Int,
        validatorCallback: IMultiblockValidator,
    ): Boolean {
        val position = BlockPos(x, y, z)
        val blockState = world.getBlockState(position)

        if (RingHelper.isValidInterior(blockState)) {
            return true
        }

        validatorCallback.setLastError(
            position,
            "multiblock.validation.reactor.invalid_block_for_interior",
            ModBlock.getNameForTranslation(blockState.block),
        )
        return false // scrap code, to deletion
    }

    override fun onAssimilated(p0: IMultiblockController<LargeIonRingMultiBlock>) {
        println("WARNING ASSIMILATED")
    }

    override fun onAssimilate(p0: IMultiblockController<LargeIonRingMultiBlock>) {
        println("WARNING ASSIMILATING")
    }

    override fun onMachineAssembled() {
        super.onMachineAssembled()
        ship = KontraptionVSUtils.getShipObjectManagingPos((thrusterLevel as ServerLevel), centerExhaust)
            ?: KontraptionVSUtils.getShipManagingPos((thrusterLevel as ServerLevel), centerExhaust)

        offset =
            Vector3d(1.0, 1.0, 1.0)
                .add(
                    exhaustDirection.normal
                        .toJOMLD()
                        .normalize()
                        .negate(),
                ).mul(0.25 * exhaustDiameter)
                .add(
                    exhaustDirection.normal
                        .toJOMLD()
                        .mul(1.5),
                ).toMinecraft()
        pos = centerExhaust.offset(exhaustDirection.normal.multiply(1))
        thrusterPower = (100.0 * innerVolume)
        if (ship != null) {
            thrusterLevel = world
            worldPosition = center
            forceDirection = exhaustDirection.opposite
            enable()
        }
    }

    private fun burnFuel(world: Level) {
        val toBurn = (thrusterPower * 100).toInt()

        if (energyStorage.energyStored >= toBurn) {
            energyStorage.extractEnergy(EnergySystem.ForgeEnergy, toBurn.toDouble(), false)
            if (!enabled) enable()
        } else {
            if (enabled) disable()
        }

        burnRemaining = energyStorage.energyStored.toDouble() % 1
        lastBurnRate = toBurn.toDouble()
    }

    private fun sendParticleData(
        level: Level,
        pos: Vec3,
        particleDir: Vector3d,
    ) {
        if (level is ServerLevel) {
            for (player in level.players()) {
                level.sendParticles(
                    player,
                    ThrusterParticleData(particleDir.x, particleDir.y, particleDir.z, innerVolume.toDouble()),
                    true,
                    pos.x + 0.5,
                    pos.y + 0.5,
                    pos.z + 0.5,
                    2 * exhaustDiameter,
                    offset.x,
                    offset.y,
                    offset.z,
                    0.0,
                )
            }
        }
    }

    override fun isMachineWhole(validatorCallback: IMultiblockValidator): Boolean {
        val (minX, minY, minZ) = arrayOf(boundingBox.minX, boundingBox.minY, boundingBox.minZ)
        val (maxX, maxY, maxZ) = arrayOf(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ)
        for (x in minX..maxX) {
            for (y in minY..maxY) {
                for (z in minZ..maxZ) {
                    val pos = BlockPos(x, y, z).mutable()
                    val sR = structureRequirement.isValidBlock(world, pos, true)
                    if (!sR.first) {
                        validatorCallback.setLastError(pos, "Invalid block type for this position", *arrayOfNulls(0))
                        return false
                    } else {
                        val be = world.getBlockEntity(pos) as? TileEntityIonCasing
                        val aType = sR.second
                        if (aType == 3.toByte()) {
                            if (be != null) {
                                be.isTop = true // OPTIMISED :3
                            }
                        } else {
                            if (be != null) {
                                be.isTop = false
                            }
                        }
                        if (aType == 4.toByte()) {
                            if (be != null) {
                                be.isCorner = true
                                be.isTop = true
                            }
                        }
                    }
                }
            }
            return true
        }
        return false
    }
}
