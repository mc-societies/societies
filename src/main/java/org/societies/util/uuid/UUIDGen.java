package org.societies.util.uuid;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.societies.util.MersenneTwisterFast;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Represents a UUIDGen
 */
public class UUIDGen {
    private static final long START_EPOCH;

    static {
        TimeZone timeZone = TimeZone.getTimeZone("GMT-0");
        Calendar cal = Calendar.getInstance(timeZone);
        cal.set(Calendar.YEAR, 1582);
        cal.set(Calendar.MONTH, Calendar.OCTOBER);
        cal.set(Calendar.DAY_OF_MONTH, 15);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        START_EPOCH = cal.getTimeInMillis();
    }

    private static final long clock = new MersenneTwisterFast().nextLong();

    public static final int BYTE_LENGTH = 8;
    public static final int UUID_LENGTH = 16;
    public static final int TIME_BASED_VERSION = 1;
    public static final long VERSION_1 = 0x0000000000001000L;

    private static final UUIDGen instance = new UUIDGen();

    private long lastNanos;
    private long clockNode;

    public UUIDGen() {
        clockNode = getClockSeqAndNode0();
    }

    public UUID generateUUID1() {
        return new UUID(createMSBSafe(), getClockSeqAndNode());
    }

    public long getClockSeqAndNode() {
        return clockNode;
    }

    public long createMSBSafe() {
        long time = createTimeSafe();
        time |= VERSION_1;
        return time;
    }

    public long createMSBUnsafe() {
        long time = createTimeUnsafe(getTime());
        time |= VERSION_1;
        return time;
    }

    private synchronized long createTimeSafe() {
        long nanosSince = getTime();
        if (nanosSince > lastNanos) {
            lastNanos = nanosSince;
        } else {
            nanosSince = ++lastNanos;
        }

        return createTime(nanosSince);
    }

    private long createTimeUnsafe(long time) {
        long nanosSince = getCorrectTime(time);
        return createTime(nanosSince);
    }

    private long getTime() {
        return getCorrectTime(System.currentTimeMillis());
    }

    private long getCorrectTime(long time) {
        return (time - START_EPOCH) * 10000;
    }

    private long createTime(long nanosSince) {
        long msb = 0L;
        msb |= (0x00000000ffffffffL & nanosSince) << 32;
        msb |= (0x0000ffff00000000L & nanosSince) >>> 16;
        msb |= (0xffff000000000000L & nanosSince) >>> 48;
        return msb;
    }

    private long getClockSeqAndNode0() {
        long lsb = 0;
        lsb |= (0x000000000000000fL & clock) << 56;
        lsb |= 0x8000000000000000L;
        lsb |= (0x00000000000000ffL & clock) << 48;
        lsb |= makeNode();
        return lsb;
    }

    private long makeNode() {
        byte[] mac = getMACAddress();

        long node = 0;
        for (int i = 0; i < Math.min(6, mac.length); i++) {
            node |= (0x00000000000000ff & (long) mac[i]) << (5 - i) * BYTE_LENGTH;
        }

        return node;
    }

    private static byte[] getMACAddress() {
        try {
            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();

            while (networks.hasMoreElements()) {
                NetworkInterface network = networks.nextElement();
                byte[] mac = network.getHardwareAddress();

                if (mac != null) {
                    return mac;
                }
            }
        } catch (SocketException ignored) {
        }

        return fakeMacAdress();
    }

    private static byte[] fakeMacAdress() {
        byte[] hash = new byte[6];
        new Random().nextBytes(hash);
        return hash;
    }

    private byte[] createTimeUUIDBytes(long msb) {
        long lsb = getClockSeqAndNode0();
        byte[] uuidBytes = new byte[UUID_LENGTH];

        for (int i = 0; i < BYTE_LENGTH; i++) {
            uuidBytes[i] = (byte) (msb >>> BYTE_LENGTH * (7 - i));
        }

        for (int i = 0; i < BYTE_LENGTH; i++) {
            uuidBytes[i] = (byte) (lsb << BYTE_LENGTH * (7 - i));
        }

        return uuidBytes;
    }

    public static UUID generateType1UUID() {
        return instance.generateUUID1();
    }

    public static UUID getUUID(ByteBuffer buffer) {
        return new UUID(buffer.getLong(buffer.position()), buffer.getLong(buffer.position() + BYTE_LENGTH));
    }

    public static UUID read(DataInput in) throws IOException {
        return new UUID(in.readLong(), in.readLong());
    }

    public static void write(UUID uuid, DataOutput out) throws IOException {
        out.writeLong(uuid.getMostSignificantBits());
        out.writeLong(uuid.getLeastSignificantBits());
    }

    @Contract("null -> null")
    public static byte[] toByteArray(@Nullable UUID uuid) {
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

    public static UUID toUUID(byte[] uuid) {
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

    public static long getAdjustedTimestamp(UUID uuid) {
        if (uuid.version() != TIME_BASED_VERSION) {
            throw new IllegalArgumentException("incompatible with uuid version: " + uuid.version());
        }
        return (uuid.timestamp() / 10000) + START_EPOCH;
    }
}
