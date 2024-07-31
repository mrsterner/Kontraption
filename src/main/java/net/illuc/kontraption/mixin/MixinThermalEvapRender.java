package net.illuc.kontraption.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import mekanism.client.render.tileentity.MultiblockTileEntityRenderer;
import mekanism.client.render.tileentity.RenderThermalEvaporationPlant;
import mekanism.common.content.evaporation.EvaporationMultiblockData;
import mekanism.common.tile.multiblock.TileEntityThermalEvaporationController;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;

import javax.inject.Inject;

@Mixin(RenderThermalEvaporationPlant.class)
public class MixinThermalEvapRender extends MultiblockTileEntityRenderer<EvaporationMultiblockData, TileEntityThermalEvaporationController> {

    protected MixinThermalEvapRender(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void render(TileEntityThermalEvaporationController tileEntityThermalEvaporationController, EvaporationMultiblockData evaporationMultiblockData, float partialTick, PoseStack matrix, MultiBufferSource renderer, int light, int overlayLight, ProfilerFiller profiler) {
        return;
    }

    @Override
    protected String getProfilerSection() {
        return "thermalEvaporationController";
    }
}


