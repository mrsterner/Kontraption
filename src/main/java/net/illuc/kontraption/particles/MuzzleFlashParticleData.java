package net.illuc.kontraption.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.illuc.kontraption.KontraptionParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nonnull;
import java.util.Locale;

public record MuzzleFlashParticleData(Double posX, Double posY, Double posZ, Double scale, Double speed) implements ParticleOptions {

    public static final Deserializer<MuzzleFlashParticleData> DESERIALIZER = new Deserializer<MuzzleFlashParticleData>() {
        @Nonnull
        @Override
        public MuzzleFlashParticleData fromCommand(@Nonnull ParticleType<MuzzleFlashParticleData> type, @Nonnull StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            Double x = reader.readDouble();
            reader.expect(' ');
            Double y = reader.readDouble();
            reader.expect(' ');
            Double z = reader.readDouble();
            reader.expect(' ');
            Double sc = reader.readDouble();
            reader.expect(' ');
            Double sp = reader.readDouble();
            return new MuzzleFlashParticleData(x, y, z, sc, sp);

        }

        @Nonnull
        @Override
        public MuzzleFlashParticleData fromNetwork(@Nonnull ParticleType<MuzzleFlashParticleData> type, FriendlyByteBuf buf) {
            return new MuzzleFlashParticleData(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble());
        }
    };

    public static final Codec<MuzzleFlashParticleData> CODEC = RecordCodecBuilder.create(val -> val.group(
            Codec.DOUBLE.fieldOf("x").forGetter(data -> data.posX),
            Codec.DOUBLE.fieldOf("y").forGetter(data -> data.posX),
            Codec.DOUBLE.fieldOf("z").forGetter(data -> data.posX),
            Codec.DOUBLE.fieldOf("scale").forGetter(data -> data.posX),
            Codec.DOUBLE.fieldOf("speed").forGetter(data -> data.posX)
    ).apply(val, MuzzleFlashParticleData::new));





    @Nonnull
    @Override
    public ParticleType<?> getType() {
        return KontraptionParticleTypes.INSTANCE.getMUZZLE_FLASH().get();
    }

    @Override
    public void writeToNetwork(@Nonnull FriendlyByteBuf buffer) {
        buffer.writeDouble(posX);
        buffer.writeDouble(posY);
        buffer.writeDouble(posZ);
        buffer.writeDouble(scale);
        buffer.writeDouble(speed);
    }




    @Nonnull
    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f", getType(), posX, posY, posZ, scale, speed);
    }



}