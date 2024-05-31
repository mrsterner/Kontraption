package net.illuc.kontraption.mixin;

import mekanism.common.tile.machine.TileEntityDigitalMiner;
import net.illuc.kontraption.util.VectorExtensionsKt;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.joml.Vector3ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.util.Objects;

@Mixin(TileEntityDigitalMiner.class)
public class MixinTileEntityDigitalMiner {



    /*@Redirect(
            method = "tryMineBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lmekanism/common/tile/machine/TileEntityDigitalMiner;getStartingPos()Lnet/minecraft/core/BlockPos;"
            ), remap = false
    )
    private BlockPos injected(TileEntityDigitalMiner instance) {
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
