package net.illuc.kontraption.client.gui

import mekanism.client.SpecialColors
import mekanism.client.gui.GuiConfigurableTile
import mekanism.client.gui.GuiMekanismTile
import mekanism.client.gui.element.GuiSideHolder
import mekanism.client.gui.element.bar.GuiChemicalBar
import mekanism.client.gui.element.bar.GuiMergedChemicalBar
import mekanism.client.gui.element.gauge.GaugeType
import mekanism.client.gui.element.gauge.GuiGasGauge
import mekanism.common.inventory.container.tile.MekanismTileContainer
import net.illuc.kontraption.blockEntities.TileEntityCannon
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.jetbrains.annotations.NotNull


class GuiGun(container: MekanismTileContainer<TileEntityCannon>?, inv: Inventory?, title: Component?) :
    GuiConfigurableTile<TileEntityCannon, MekanismTileContainer<TileEntityCannon>?>(container, inv, title) {
    init {
        dynamicSlots = true
        addGuiElements()
    }

    override fun addGuiElements() {
        //Add the side holder before the slots, as it holds a couple of the slots
        //addRenderableWidget(GuiSideHolder.armorHolder(this))
        //addRenderableWidget(GuiSideHolder.create(this, imageWidth, 36, 98, false, true, SpecialColors.TAB_ARMOR_SLOTS));
        super.addGuiElements()
        addRenderableWidget(GuiChemicalBar(this, GuiChemicalBar.getProvider(tile.inputTank, tile.getGasTanks(null)), 20, 16, 140, 10, true))



    }

    override fun drawForegroundText(@NotNull guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        renderTitleText(guiGraphics)
        drawString(guiGraphics, playerInventoryTitle, inventoryLabelX, inventoryLabelY, titleTextColor())
        super.drawForegroundText(guiGraphics, mouseX, mouseY)
    }
}
