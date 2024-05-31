package net.illuc.kontraption.particle

import com.mojang.serialization.Codec
import mekanism.common.particle.LaserParticleData
import net.illuc.kontraption.particles.MuzzleFlashParticleData
import net.illuc.kontraption.particles.ThrusterParticleData
import net.minecraft.core.particles.ParticleType
import javax.annotation.Nonnull


class MuzzleFlashParticleType : ParticleType<MuzzleFlashParticleData>(false, MuzzleFlashParticleData.DESERIALIZER) {
    @Nonnull
    override fun codec(): Codec<MuzzleFlashParticleData> {
        return MuzzleFlashParticleData.CODEC
    }
}