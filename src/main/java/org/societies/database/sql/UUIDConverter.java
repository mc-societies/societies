package org.societies.database.sql;

import org.jooq.Converter;

import java.util.UUID;

/**
 * Represents a UUIDConverter
 */
public class UUIDConverter implements Converter<byte[], UUID> {
    public static final int BYTE_LENGTH = 8;
    public static final int UUID_LENGTH = 16;

    @Override
    public UUID from(byte[] uuid) {
        if (uuid == null) {
            return null;
        }

        if (uuid.length != UUID_LENGTH) {
            throw new IllegalArgumentException("byte[] must have 16 bytes!");
        }

        long msb = 0;
        long lsb = 0;

        for (int i = 0; i < 8; i++) {
            msb = (msb << 8) | (uuid[i] & 0xff);
        }
        for (int i = 8; i < 16; i++) {
            lsb = (lsb << 8) | (uuid[i] & 0xff);
        }

        return new UUID(msb, lsb);
    }

    @Override
    public byte[] to(UUID uuid) {
        if (uuid == null) {
            return null;
        }

        long most = uuid.getMostSignificantBits();
        long least = uuid.getLeastSignificantBits();

        byte[] bytes = new byte[UUID_LENGTH];

        for (int i = 0; i < BYTE_LENGTH; i++) {
            bytes[i] = (byte) (most >>> ((7 - i) * BYTE_LENGTH));
            bytes[BYTE_LENGTH + i] = (byte) (least >>> ((7 - i) * BYTE_LENGTH));
        }
        return bytes;
    }

    @Override
    public Class<byte[]> fromType() {
        return byte[].class;
    }

    @Override
    public Class<UUID> toType() {
        return UUID.class;
    }
}
