package net.illuc.kontraption.blockEntities

import mekanism.api.Action
import mekanism.api.AutomationType
import mekanism.api.IContentsListener
import mekanism.api.RelativeSide
import mekanism.api.chemical.ChemicalTankBuilder
import mekanism.api.chemical.gas.Gas
import mekanism.api.chemical.gas.GasStack
import mekanism.api.chemical.gas.IGasTank
import mekanism.api.recipes.cache.CachedRecipe
import mekanism.api.recipes.inputs.IInputHandler
import mekanism.api.recipes.inputs.InputHelper
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder
import mekanism.common.lib.transmitter.TransmissionType
import mekanism.common.registries.MekanismGases
import mekanism.common.tile.component.TileComponentConfig
import mekanism.common.tile.component.TileComponentEjector
import mekanism.common.tile.prefab.TileEntityConfigurableMachine
import mekanism.common.util.MekanismUtils.canFunction
import net.illuc.kontraption.Kontraption
import net.illuc.kontraption.KontraptionBlocks
import net.illuc.kontraption.KontraptionSounds
import net.illuc.kontraption.particles.MuzzleFlashParticleData
import net.illuc.kontraption.util.*
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import org.jetbrains.annotations.NotNull
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.Ship
import java.util.function.LongSupplier
import java.util.function.Supplier
import kotlin.random.Random

class TileEntityCannon(pos: BlockPos?, state: BlockState?) : TileEntityConfigurableMachine(KontraptionBlocks.CANNON, pos, state) {
    var inputTank: IGasTank? = null
    private var inputHandler: IInputHandler<GasStack>? = null
    var cooldown: Int = 20
    var ship: Ship? = null

    init {
        inputHandler =
            inputTank?.let { InputHelper.getInputHandler(it, CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_INPUT) }
        configComponent =
            TileComponentConfig(
                this, TransmissionType.GAS,
            )
        configComponent.setupIOConfig(TransmissionType.GAS, inputTank, RelativeSide.BACK)

        ejectorComponent = TileComponentEjector(this, LongSupplier { 10 })
        ejectorComponent.setOutputData(
            configComponent,
            TransmissionType.GAS,
        ).setCanEject { canFunction(this) }
    }

    @NotNull
    override fun getInitialGasTanks(listener: IContentsListener?): IChemicalTankHolder<Gas, GasStack, IGasTank>? {
        val builder =
            ChemicalTankHelper.forSideGasWithConfig(
                Supplier<Direction> { this.direction },
                { this.config },
            )

        // Allow extracting out of the input gas tank if it isn't external OR the output tank is empty AND the input is radioactive
        builder.addTank(
            ChemicalTankBuilder.GAS.input(
                20_000,
                { gas: Gas -> gas === MekanismGases.OSMIUM.get() },
                null,
            ).also { inputTank = it },
        )
        return builder.build()
    }

    override fun onUpdateServer() {
        super.onUpdateServer()
        // TODO: something other than this this is so yucky
        if (ship == null) {
            ship = KontraptionVSUtils.getShipObjectManagingPos((level as ServerLevel), tilePos)
                ?: KontraptionVSUtils.getShipManagingPos((level as ServerLevel), tilePos)
        }
        if (cooldown <= 0) {
            if (canFunction(this)) {
                if (inputTank!!.extract(100L, Action.SIMULATE, AutomationType.MANUAL).amount >= 100L) {
                    inputTank!!.extract(100L, Action.EXECUTE, AutomationType.MANUAL)
                    cooldown = 80

                    val particleDir =
                        if (ship == null) {
                            blockState.getValue(BlockStateProperties.FACING).normal.toJOMLD()
                        } else {
                            ship!!.transform.shipToWorld.transformDirection(
                                blockState.getValue(BlockStateProperties.FACING).normal.toJOMLD(),
                            )
                        }

                    (
                        blockPos.toJOMLD().add(
                            blockState.getValue(BlockStateProperties.FACING).normal.toJOMLD().mul(3.5),
                        )
                    )?.let {
                        sendParticleData(
                            level as ServerLevel,
                            it.toMinecraft(),
                            particleDir,
                        )
                    }
                    level!!.playSound(null, blockPos, KontraptionSounds.CANNON_SHOT.get(), SoundSource.BLOCKS, 5F, 1F)
                    level?.let {
                        ShotHandler.shoot(
                            blockState.getValue(BlockStateProperties.FACING).normal.toJOMLD(),
                            it,
                            blockPos.toJOMLD()
                                .add(blockState.getValue(BlockStateProperties.FACING).normal.toJOMLD().mul(4.0)),
                            200.0,
                            { m -> hit(m) },
                        )
                    }
                }
            }
        } else {
            cooldown--
        }
    }

    private fun sendParticleData(
        level: Level,
        pos: Vec3,
        particleDir: Vector3d,
    ) {
        if (!isRemote && level is ServerLevel) {
            for (player in level.players()) {
                for (i in 1..40) {
                    // level.sendParticles(player, ParticleTypes.LARGE_SMOKE, true, pos.x+0.5, pos.y+0.5, pos.z+0.5, 1, 0.01, 0.01, 0.01, 0.0)
                    level.sendParticles(
                        player,
                        MuzzleFlashParticleData(
                            particleDir.x.toDouble(),
                            particleDir.y.toDouble(),
                            particleDir.z.toDouble(),
                            0.0,
                            Random.nextDouble(0.3, 1.0),
                        ),
                        true, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, 1, 0.01, 0.01, 0.01, 0.0,
                    )
                }
            }
        }
    }

    fun hit(clipResult: BlockHitResult) {
        // (level as ServerLevel).explode(PrimedTnt(EntityType.TNT, level), clipResult.blockPos.x.toDouble(), clipResult.blockPos.y.toDouble(), clipResult.blockPos.z.toDouble(), 5F, Level.ExplosionInteraction.TNT)
        if (level is ServerLevel) {
            Kontraption.blockDamageManager.damageBlock(
                clipResult.blockPos.immutable().offset(0, 0, 0),
                15,
                (level as ServerLevel).getBlockState(clipResult.blockPos),
                level as ServerLevel,
            )
            Kontraption.blockDamageManager.damageBlock(
                clipResult.blockPos.immutable().offset(1, 0, 0),
                5,
                (level as ServerLevel).getBlockState(clipResult.blockPos),
                level as ServerLevel,
            )
            Kontraption.blockDamageManager.damageBlock(
                clipResult.blockPos.immutable().offset(-1, 0, 0),
                5,
                (level as ServerLevel).getBlockState(clipResult.blockPos),
                level as ServerLevel,
            )
            Kontraption.blockDamageManager.damageBlock(
                clipResult.blockPos.immutable().offset(0, 1, 0),
                5,
                (level as ServerLevel).getBlockState(clipResult.blockPos),
                level as ServerLevel,
            )
            Kontraption.blockDamageManager.damageBlock(
                clipResult.blockPos.immutable().offset(0, -1, 0),
                5,
                (level as ServerLevel).getBlockState(clipResult.blockPos),
                level as ServerLevel,
            )
            Kontraption.blockDamageManager.damageBlock(
                clipResult.blockPos.immutable().offset(0, 0, 1),
                5,
                (level as ServerLevel).getBlockState(clipResult.blockPos),
                level as ServerLevel,
            )
            Kontraption.blockDamageManager.damageBlock(
                clipResult.blockPos.immutable().offset(0, 0, -1),
                5,
                (level as ServerLevel).getBlockState(clipResult.blockPos),
                level as ServerLevel,
            )
            for (player in (level as ServerLevel).players()) {
                (level as ServerLevel).sendParticles(player, ParticleTypes.TOTEM_OF_UNDYING, true, clipResult.blockPos.x.toDouble() + 0.5, clipResult.blockPos.y.toDouble() + 0.5, clipResult.blockPos.z.toDouble() + 0.5, 1, 0.001, 0.001, 0.001, 0.0)
                // level.sendParticles(player, ParticleTypes.TOTEM_OF_UNDYING, true, blockPos.x.toDouble(), blockPos.x.toDouble(), blockPos.x.toDouble())
            }
        }
    }
}
