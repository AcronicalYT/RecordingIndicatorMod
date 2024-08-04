package dev.acronical.recordingindicator.client;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record RecordingIndicatorPayload(byte[] payloadByteArray) implements CustomPayload {
    public static final Id<RecordingIndicatorPayload> ID = new Id<>(RecordingIndicatorClient.DATA_PACKET_ID);

    public static final PacketCodec<RegistryByteBuf, RecordingIndicatorPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BYTE_ARRAY, RecordingIndicatorPayload::payloadByteArray,
            RecordingIndicatorPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
