package net.illuc.kontraption.util.guiutils

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.ImageButton
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

@OnlyIn(Dist.CLIENT)
class ScaledImageButton(
    private val xm: Int,
    private val ym: Int,
    private val widthm: Int,
    private val heightm: Int,
    xTexStart: Int,
    yTexStart: Int,
    yDiffTex: Int,
    resourceLocation: ResourceLocation,
    textureWidth: Int,
    textureHeight: Int,
    onPress: OnPress,
    private val scale: Float,
    private val messagez: Component,
) : ImageButton(xm, ym, (widthm * scale * 0.69f).toInt(), (heightm * scale).toInt(), xTexStart, yTexStart, yDiffTex, resourceLocation, textureWidth, textureHeight, onPress, messagez) {
    var minecraft: Minecraft = Minecraft.getInstance()

    override fun render(
        pGuiGraphics: GuiGraphics,
        pMouseX: Int,
        pMouseY: Int,
        pPartialTick: Float,
    ) {
        pGuiGraphics.pose().pushPose()
        pGuiGraphics.pose().scale(scale, scale, 1.0f)
        pGuiGraphics.pose().translate(((xm / scale) - xm), ((ym / scale) - ym), 1.0f)
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
        pGuiGraphics.pose().popPose()
        pGuiGraphics.pose().pushPose()
        pGuiGraphics.pose().scale(1.5f, 1.5f, 1.0f)
        pGuiGraphics.pose().translate(xm / 1.5f - xm - 23, ym / 1.5f - ym - 40, 1.0f)
        val i = fgColor
        this.renderString(pGuiGraphics, minecraft.font, i or (Mth.ceil(this.alpha * 255.0f) shl 24))
        pGuiGraphics.pose().popPose()
    }

    override fun renderWidget(
        pGuiGraphics: GuiGraphics,
        pMouseX: Int,
        pMouseY: Int,
        pPartialTick: Float,
    ) {
        this.renderTexture(pGuiGraphics, this.resourceLocation, this.x, this.y, this.xTexStart, this.yTexStart, this.yDiffTex, this.widthm, this.heightm, this.textureWidth, this.textureHeight)
    }

    override fun renderString(
        pGuiGraphics: GuiGraphics,
        pFont: Font,
        pColor: Int,
    ) {
        this.renderScrollingString(pGuiGraphics, pFont, 20, pColor)
    }

    override fun renderScrollingString(
        pGuiGraphics: GuiGraphics,
        pFont: Font,
        pWidth: Int,
        pColor: Int,
    ) {
        val i = this.x + pWidth
        val j = this.x + this.heightm - pWidth
        renderScrollingString(pGuiGraphics, pFont, this.message, i, this.y, j, this.y + this.heightm, pColor)
    }
}
