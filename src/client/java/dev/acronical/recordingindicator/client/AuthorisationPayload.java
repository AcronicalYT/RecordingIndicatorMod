package dev.acronical.recordingindicator.client;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record AuthorisationPayload(byte[] authByteArray) implements CustomPayload {
    public static final Id<AuthorisationPayload> ID = new Id<>(RecordingIndicatorClient.AUTH_PACKET_ID);

    public static final PacketCodec<RegistryByteBuf, AuthorisationPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BYTE_ARRAY, AuthorisationPayload::authByteArray,
            AuthorisationPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
