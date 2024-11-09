package net.illuc.kontraption.multiblocks.largeionring.parts

import it.zerono.mods.zerocore.base.multiblock.part.GenericDeviceBlock
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartBlock
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartTypeProperties
import net.illuc.kontraption.KontraptionTileEntityTypes
import net.illuc.kontraption.multiblocks.common.PowerTapBlock
import net.illuc.kontraption.multiblocks.largeionring.IIonRingPartType
import net.illuc.kontraption.multiblocks.largeionring.LargeIonRingMultiBlock
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.common.util.NonNullSupplier

enum class IonRingPartTypes(
    private val tileTypeSupplier: NonNullSupplier<NonNullSupplier<BlockEntityType<*>>>,
    private val blockFactory: (MultiblockPartBlock.MultiblockPartProperties<IIonRingPartType>) -> MultiblockPartBlock<LargeIonRingMultiBlock, IIonRingPartType>,
    private val translationKey: String = "",
) : IIonRingPartType {
    Casing(
        NonNullSupplier { NonNullSupplier(KontraptionTileEntityTypes.LARGE_ION_THRUSTER_CASING::get) },
        { props -> MultiblockPartBlock(props) },
    ),
    Coil(
        NonNullSupplier { NonNullSupplier(KontraptionTileEntityTypes.LARGE_ION_THRUSTER_COIL::get) },
        { props -> MultiblockPartBlock(props) },
    ),

    Controller(
        NonNullSupplier { NonNullSupplier(net.illuc.kontraption.KontraptionTileEntityTypes.LARGE_ION_THRUSTER_CONTROLLER::get) },
        { props -> GenericDeviceBlock(props) },
        "block.largemultiblock.largeionringcontroller",
    ),

    PowerPortRF(
        NonNullSupplier { NonNullSupplier(net.illuc.kontraption.KontraptionTileEntityTypes.LARGE_ION_THRUSTER_VALVE::get) },
        { props -> PowerTapBlock(props) },
        "block.largemultiblock.largeionringpowerportfe",
    ),
    ;

    override fun getPartTypeProperties(): MultiblockPartTypeProperties<LargeIonRingMultiBlock, IIonRingPartType> = properties

    override fun getSerializedName(): String = name

    private val properties =
        MultiblockPartTypeProperties(
            tileTypeSupplier,
            blockFactory,
            translationKey,
            { it },
        )
}
