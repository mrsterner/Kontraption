package net.illuc.kontraption.client

import net.illuc.kontraption.particles.MuzzleFlashParticleData
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.*
import net.minecraft.world.phys.Vec3
import javax.annotation.Nonnull
import kotlin.math.absoluteValue
import kotlin.random.Random

class MuzzleFlashParticle protected constructor(world: ClientLevel?, posX: Double, posY: Double, posZ: Double, velX: Double, velY: Double, velZ: Double, scale: Double, speed: Double, spriteSet: SpriteSet?) : TextureSheetParticle(world, posX, posY, posZ, velX, velY, velZ) {
    private var spriteset: SpriteSet? = null
    init {
        lifetime = (40*speed).toInt()
        friction = (1.0*(1-(speed))).toFloat()
        setSpriteFromAge(spriteSet)
        spriteset = spriteSet
        //yo = 1.0
        val velVec = Vec3(velX, velY, velZ)
        var randX = 1 - velVec.normalize().x.absoluteValue
        var randY = 1 - velVec.normalize().y.absoluteValue
        var randZ = 1 - velVec.normalize().z.absoluteValue
        xd = velX + randX*(Random.nextFloat() * 0.8f - 0.4f)
        yd = velY+ randY*(Random.nextFloat() * 0.8f - 0.4f)
        zd = velZ + randZ*(Random.nextFloat() * 0.8f - 0.4f)
        scale((2f+scale).toFloat())
    }

    public override fun getLightColor(partialTick: Float): Int {
        return 190 + (20f * (1.0f - Minecraft.getInstance().options.gamma().get())).toInt()
    }

    override fun tick() {
        super.tick()
        fadeOut()
        this.setSpriteFromAge(spriteset)
        if (this.onGround){
            yd = 0.0
            //xd = Rand
        }
    }

    private fun fadeOut() {
        alpha = -(0.5f / lifetime.toFloat()) * age + 1
    }

    override fun getRenderType(): ParticleRenderType {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT
    }

    class Factory(private val spriteSet: SpriteSet?) : ParticleProvider<MuzzleFlashParticleData> {
        override fun createParticle(muzzleFlashParticleData: MuzzleFlashParticleData, world: ClientLevel, x: Double, y: Double, z: Double, p_107426_: Double, p_107427_: Double, p_107428_: Double): Particle? {
            return MuzzleFlashParticle(world, x, y, z, muzzleFlashParticleData.posX, muzzleFlashParticleData.posY, muzzleFlashParticleData.posZ, muzzleFlashParticleData.scale, muzzleFlashParticleData.speed, spriteSet)
        }
    }
}