package net.illuc.kontraption.mixin;

import mekanism.common.content.miner.ThreadMinerSearch;
import mekanism.common.tile.machine.TileEntityDigitalMiner;
import net.illuc.kontraption.util.VectorExtensionsKt;
import net.minecraft.core.BlockPos;
import org.joml.Vector3ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Mixin(ThreadMinerSearch.class)
public class MixinThreadMinerSearch {



    /*@Redirect(
            method = "run",
            at = @At(
                    value = "INVOKE",
                    target = "Lmekanism/common/tile/machine/TileEntityDigitalMiner;getBlockPos()Lnet/minecraft/core/BlockPos;"
            ), remap = false
    )
    private BlockPos getBlockPosInject(TileEntityDigitalMiner instance) {
        System.out.println("mixinging");
        final Ship ship = VSGameUtilsKt.getShipManagingPos(instance.getLevel(), instance.getBlockPos());
        if (ship == null){
            return instance.getBlockPos();
        }else{
            System.out.println(VectorExtensionsKt.toBlockPos( (VSGameUtilsKt.toWorldCoordinates(ship, instance.getBlockPos()))));
            return VectorExtensionsKt.toBlockPos( (VSGameUtilsKt.toWorldCoordinates(ship, instance.getBlockPos())));
        }
    }

    @Redirect(
            method = "run",
            at = @At(
                    value = "INVOKE",
                    target = "Lmekanism/common/tile/machine/TileEntityDigitalMiner;getStartingPos()Lnet/minecraft/core/BlockPos;"
            ), remap = false
    )
    private BlockPos getStartingPosInject(TileEntityDigitalMiner instance) {
        System.out.println("mixinging");
        final Ship ship = VSGameUtilsKt.getShipManagingPos(instance.getLevel(), instance.getBlockPos());
        if (ship == null){
            return instance.getStartingPos();
        }else{

            BlockPos blockPos = VectorExtensionsKt.toBlockPos( (VSGameUtilsKt.toWorldCoordinates(ship, instance.getStartingPos())));
            System.out.println(new BlockPos(blockPos.getX(), instance.getMinY(), blockPos.getZ()));
            return new BlockPos(blockPos.getX(), instance.getMinY(), blockPos.getZ());
        }
    }*/
}
