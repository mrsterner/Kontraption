package net.illuc.kontraption.blockEntities
import dan200.computercraft.shared.Capabilities
import mekanism.common.tile.base.TileEntityMekanism
import net.illuc.kontraption.KontraptionBlocks
import net.illuc.kontraption.peripherals.ConnectorPeripheral
import net.illuc.kontraption.util.KontraptionVSUtils
import net.illuc.kontraption.util.KontraptionVSUtils.getShipObjectManagingPos
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.LazyOptional
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.apigame.constraints.VSAttachmentConstraint

// Comment for CEO, im not even throwing that BAD model i did
class TileEntityConnector(
    pos: BlockPos?,
    state: BlockState?,
) : TileEntityMekanism(KontraptionBlocks.CONNECTOR, pos, state) {
    private val ship: ServerShip? get() = getShipObjectManagingPos((level as ServerLevel), this.blockPos)
    private val sLevel: ServerLevel? get() = level as? ServerLevel
    var conid: Int = 666 // placeholder as i dont want to play with nulls, rlly hope it wont cause errors in future UPDATE: it wont, may troll CC users a bit xd
    var isConnected = false
    var underCC = false
    val x = this.blockPos.x.toDouble()
    val y = this.blockPos.y.toDouble()
    val z = this.blockPos.z.toDouble()
    val vectorBlocpos = Vector3d(x, y, z) // stupid solution but most of .to conversion are somewhy broken to me
    private val peripheral = ConnectorPeripheral(this)
    private val peripheralCapability = LazyOptional.of { peripheral }

    override fun <T> getCapability(
        capability: Capability<T>,
        side: Direction?,
    ): LazyOptional<T> =
        if (capability == Capabilities.CAPABILITY_PERIPHERAL as Capability<T>) {
            peripheralCapability as LazyOptional<T>
        } else {
            super.getCapability(capability, side)
        }

    fun enable() {
        // as ceo intended
    }

    override fun onLoad() {
        super.onLoad()
        if (!level?.isClientSide!!) {
            if (this.isConnected) {
                this.isConnected = false
                this.connect()
            } // unfortunly connections dont save on load
        }
    }

    private fun rayTrace(distance: Double): BlockEntity? {
        val blockState = sLevel!!.getBlockState(blockPos)
        val fdirection = blockState.getValue(BlockStateProperties.FACING)
        var startVec = Vec3.atCenterOf(blockPos).add(Vec3.atLowerCornerOf(fdirection.normal).scale(0.7))
        val directionVec = Vec3(fdirection.stepX.toDouble(), fdirection.stepY.toDouble(), fdirection.stepZ.toDouble())
        var endVec = startVec.add(directionVec.scale(distance))
        if (ship?.id != null) {
            val tempstartVec = ship!!.transform.shipToWorld.transformPosition(fV3V3d(startVec))
            val tempendVec = ship!!.transform.shipToWorld.transformPosition(fV3V3d(endVec))
            endVec = fV3dV3(tempendVec)
            startVec = fV3dV3(tempstartVec)
            // Mekanism.logger.info("Transformed raycast")
        }

        val context =
            ClipContext(
                startVec,
                endVec,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                null,
            )
        val bhitresul = sLevel!!.clip(context)
        if (bhitresul.type === HitResult.Type.BLOCK) {
            val hitPos: BlockPos = bhitresul.blockPos
            val btype = sLevel!!.getBlockEntity(hitPos) // 50/50 its my serverLevel fault
            if (btype != null) {
                if (btype::class == this::class) {
                    return btype
                }
            }
        } else {
            // Mekanism.logger.info("No block hit.")
        }
        return null
    }

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        tag.putInt("ConID", conid)
        tag.putBoolean("isconnected", isConnected)
        tag.putBoolean("undercc", underCC)
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)
        conid = tag.getInt("ConID")
        isConnected = tag.getBoolean("isconnected")
        underCC = tag.getBoolean("undercc")
    }

    fun check(): Pair<BlockEntity?, ServerShip?>? {
        val world = this.level ?: return null // kinda needed lol
        if (world.isClientSide) {
            return null // mf crash
        }
        val bp2 = rayTrace(2.5) ?: return null
        val tileEntity2c: TileEntityConnector? = bp2 as? TileEntityConnector
        val ship2 = tileEntity2c?.ship
        return Pair(tileEntity2c, ship2)
    }

    fun fVdVdc(vector3d: Vector3d): Vector3dc {
        val vectors: Vector3dc = vector3d
        return vectors
    }

    fun fV3V3d(vec3: Vec3): Vector3d {
        val xx = vec3.x
        val xy = vec3.y
        val xz = vec3.z
        return Vector3d(xx, xy, xz)
    }

    fun fV3dV3(vector3d: Vector3d): Vec3 {
        val xx = vector3d.x
        val xy = vector3d.y
        val xz = vector3d.z
        return Vec3(xx, xy, xz)
    } // if i ever need to use those again ima just put them in seperate files, NO IDEA why normal .toVector3d or .toVec3 didnt want to work here

    fun getDimID(): ShipId? {
        val dimID = KontraptionVSUtils.dimensionID(sLevel)
        val dimshipID = KontraptionVSUtils.getShipObjectWorld(sLevel).dimensionToGroundBodyIdImmutable[dimID]
        return dimshipID
    }

    fun connectpass() {
        this.connect() // for some VERY WEIRD REASON, no matter if i use this weird passthru or normal .connect it CANNOT get tileEntity for some reason like WTF? Raycast gets correct possition(same as redstone eq) but doesnt get tileentity
    }

    fun connect() {
        var shipid2: ShipId?
        val getPaz = check()
        val (tileEntity2, ship2) = getPaz ?: Pair(null, null)
        val tileEntity2c: TileEntityConnector? = tileEntity2 as? TileEntityConnector
        if (ship2 == null && tileEntity2c != null) {
            // Mekanism.logger.info("Didnt find SHIP2 but found TE2, so static connection")
        }
        val shipid1 = ship?.id
        shipid2 =
            if (ship2?.id != null) {
                ship2.id
            } else {
                tileEntity2c?.getDimID()
            }
        if (shipid2 == null || shipid1 == null) {
            // Mekanism.logger.info("SHIPID NULLED AND CONNECTOR NULLED  ID1:$shipid1 ID2:$shipid2")
            return
        }
        println(shipid2)
        // Mekanism.logger.info("Attempting connection")
        if (!this.isConnected && tileEntity2c?.isConnected == false) {
            // Mekanism.logger.info("both are not connected")
            val sypos1 = vectorBlocpos
            val sypos2 = tileEntity2.vectorBlocpos
            println("SX1: ${fVdVdc(sypos1).x()} SY1: ${fVdVdc(sypos1).y()} SZ1: ${fVdVdc(sypos1).z()}")
            println("SX2: ${fVdVdc(sypos2).x()} SY2: ${fVdVdc(sypos2).y()} SZ2: ${fVdVdc(sypos2).z()}")
            val constraint =
                VSAttachmentConstraint(
                    shipid1,
                    shipid2,
                    compliance = 1e-10,
                    localPos0 = fVdVdc(sypos1),
                    localPos1 = fVdVdc(sypos2),
                    maxForce = 1e10,
                    fixedDistance = 0.5,
                )
            conid = KontraptionVSUtils.getShipObjectWorld(sLevel).createNewConstraint(constraint)!!
            isConnected = true
        }
    }

    fun tick() {
        if (!underCC) {
            if (canConnect()) {
                connect()
            }
        }
    }

    fun canConnect(): Boolean {
        if (this.level?.hasNeighborSignal(blockPos) == true) {
            if (!isConnected) {
                return true
            }
            return false
        } else {
            if (isConnected && !underCC) {
                KontraptionVSUtils.getShipObjectWorld(sLevel).removeConstraint(conid)
                isConnected = false
                return false
            }
            return false
        }
    }

    fun disconnect() {
        KontraptionVSUtils.getShipObjectWorld(sLevel).removeConstraint(conid)
    }
}
