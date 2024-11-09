package net.illuc.kontraption.renderers
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.illuc.kontraption.Kontraption
import net.illuc.kontraption.blockEntities.largeion.TileEntityIonCasing
import net.illuc.kontraption.blocks.BlockIonCasing
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec3
import net.minecraftforge.client.model.data.ModelData
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class LargeIonRenderer(
    context: BlockEntityRendererProvider.Context?,
) : BlockEntityRenderer<TileEntityIonCasing?> {
    private val resourcesz = ResourceLocation(Kontraption.MODID, "block/large_ion_ring_segment")
    private val valveresourcesz = ResourceLocation(Kontraption.MODID, "block/large_ion_ring_input")
    private val controllerresourcesz = ResourceLocation(Kontraption.MODID, "block/large_ion_ring_controller")
    private val cornerresourcesz = ResourceLocation(Kontraption.MODID, "block/large_ion_ring_corner")

    private val modelz: BakedModel = Minecraft.getInstance().modelManager.getModel(resourcesz)
    private val valvemodelz: BakedModel = Minecraft.getInstance().modelManager.getModel(valveresourcesz)
    private val controllermodelz: BakedModel = Minecraft.getInstance().modelManager.getModel(controllerresourcesz)
    private val cornermodelz: BakedModel = Minecraft.getInstance().modelManager.getModel(cornerresourcesz)

    private var fmodelz = Minecraft.getInstance().modelManager.missingModel
    var logger: Logger = LogManager.getLogger(Kontraption::class)

    private var xmv = -2.0f
    private var ymv = -1.0f
    private var zmv = 0.0f

    private var east = 0f
    private var west = 180f
    private var north = 90f
    private var south = 270f // eh ima move laterz

    init {
        if (modelz == Minecraft.getInstance().modelManager.missingModel) {
            logger.error("Failed to load: large_ion_ring_segment model")
            logger.error("RESOURCE PATHS" + resourcesz.path + " " + resourcesz.namespace)
        }
    }

    override fun render(
        blockEntity: TileEntityIonCasing?,
        partialTicks: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        combinedLight: Int,
        combinedOverlay: Int,
    ) {
        if (blockEntity == null) {
            return
        }
        val rotation = blockEntity.blockState.getValue(BlockIonCasing.ROT)
        var assembled = blockEntity.multiblock?.isFormed ?: false
        var sr = blockEntity.blockState.getValue(BlockIonCasing.SR)
        var state = blockEntity.blockState.getValue(BlockIonCasing.STATETYPE)

        when (state) {
            0 -> fmodelz = modelz
            1 -> fmodelz = valvemodelz
            2 -> fmodelz = controllermodelz
            3 -> fmodelz = cornermodelz
            else -> fmodelz = modelz
        }
        val rotationAngle =
            when (rotation) {
                Direction.NORTH -> north
                Direction.EAST -> east
                Direction.SOUTH -> south
                Direction.WEST -> west
                Direction.DOWN -> 0f
                Direction.UP -> 0f
                null -> 0f
            }
        poseStack.pushPose()
        if (assembled && sr == true) {
            poseStack.mulPose(Axis.YP.rotation(Math.toRadians(rotationAngle.toDouble()).toFloat()))
            var (ax, az) =
                when (rotation) {
                    Direction.NORTH -> 0.0f to 0.0f
                    Direction.EAST -> 1.0f to 0.0f
                    Direction.SOUTH -> 1.0f to -1.0f
                    Direction.WEST -> 0.0f to -1.0f
                    else -> 0f to 0f
                }

            if (state == 3) {
                az += 1.0f
            }

            poseStack.translate((xmv + ax), ymv, (zmv + az))
            Minecraft.getInstance().blockRenderer.modelRenderer.renderModel(
                poseStack.last(),
                buffer.getBuffer(RenderType.solid()),
                null,
                fmodelz,
                1.0f,
                1.0f,
                1.0f,
                combinedLight,
                combinedOverlay,
                ModelData.EMPTY,
                RenderType.solid(),
            )
        }
        poseStack.popPose()
    }

    override fun shouldRender(
        blockEntity: TileEntityIonCasing?,
        cameraPosition: Vec3,
    ): Boolean = true

    override fun shouldRenderOffScreen(blockEntity: TileEntityIonCasing?): Boolean = true
}
