package net.illuc.kontraption.item

import com.wildfire.main.GenderPlayer
import com.wildfire.main.WildfireGender
import mekanism.common.Mekanism
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.UseAnim
import net.minecraft.world.level.Level

class ItemEstrogen(properties: Properties) : Item(properties) {
    override fun use(level: Level, player: Player, interactionHand: InteractionHand): InteractionResultHolder<ItemStack> {
        player.startUsingItem(interactionHand)

        return InteractionResultHolder.success(player.getItemInHand(interactionHand))
    }

    override fun getUseDuration(stack: ItemStack): Int {
        return 32
    }

    override fun getUseAnimation(stack: ItemStack): UseAnim {
        return UseAnim.EAT
    }

    override fun finishUsingItem(stack: ItemStack, level: Level, livingEntity: LivingEntity): ItemStack {
        if(Mekanism.hooks.WildfireGenderModLoaded){
            WildfireGender.getPlayerById(livingEntity.uuid)?.updateGender(GenderPlayer.Gender.FEMALE)
        }
        return super.finishUsingItem(stack, level, livingEntity)
    }
}