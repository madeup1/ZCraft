package net.zcraft.network.buffers;

import net.zcraft.position.BlockPos;
import net.zcraft.position.Vec3;
import net.zcraft.util.Flags;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public interface Types
{
    INetworkType<Integer> INT = new INetworkType<Integer>()
    {
        @Override
        public Integer read(ReadBuffer buffer)
        {
            byte[] data = buffer.UNSAFE_read(4);

            if (data == null)
                return 0;

            int value = 0;
            for (int i = 0; i < data.length; i++) {
                value |= (data[i] & 0xFF) << ((data.length - 1 - i) * 8);
            }

            if (Flags.ENDIAN_CONVERSION)
                value = Integer.reverseBytes(value);

            return value;
        }

        @Override
        public void write(WriteBuffer buffer, Integer value)
        {
            if (Flags.ENDIAN_CONVERSION)
            {
                value = Integer.reverseBytes(value);
            }

            byte[] data = new byte[4];
            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) (value >> ((data.length - 1 - i) * 8));
            }

            buffer.UNSAFE_write(data);
        }
    };

    INetworkType<Byte> BYTE = new INetworkType<Byte>()
    {
        @Override
        public Byte read(ReadBuffer buffer)
        {
            return buffer.UNSAFE_read(1)[0];
        }

        @Override
        public void write(WriteBuffer buffer, Byte value)
        {
            buffer.UNSAFE_write(new byte[]{value});
        }
    };

    INetworkType<Short> SHORT = new INetworkType<Short>()
    {
        @Override
        public Short read(ReadBuffer buffer)
        {
            byte[] data = buffer.UNSAFE_read(2);

            if (data == null)
                return 0;

            short value = 0;
            for (int i = 0; i < data.length; i++) {
                value |= (short) ((data[i] & 0xFF) << ((data.length - 1 - i) * 8));
            }

            if (Flags.ENDIAN_CONVERSION)
                value = Short.reverseBytes(value);

            return value;
        }

        @Override
        public void write(WriteBuffer buffer, Short value)
        {
            if (Flags.ENDIAN_CONVERSION)
            {
                value = Short.reverseBytes(value);
            }

            byte[] data = new byte[2];
            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) (value >> ((data.length - 1 - i) * 8));
            }

            buffer.UNSAFE_write(data);
        }
    };

    INetworkType<Long> LONG = new INetworkType<Long>()
    {
        @Override
        public Long read(ReadBuffer buffer)
        {
            byte[] data = buffer.UNSAFE_read(8);

            if (data == null)
                return 0L;

            long value = 0;
            for (int i = 0; i < data.length; i++) {
                value |= ((long) (data[i] & 0xFF) << ((data.length - 1 - i) * 8));
            }

            if (Flags.ENDIAN_CONVERSION)
                value = Long.reverseBytes(value);

            return value;
        }

        @Override
        public void write(WriteBuffer buffer, Long value)
        {
            if (Flags.ENDIAN_CONVERSION)
            {
                value = Long.reverseBytes(value);
            }

            byte[] data = new byte[8];
            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) (value >> ((data.length - 1 - i) * 8));
            }

            buffer.UNSAFE_write(data);
        }
    };

    INetworkType<Float> FLOAT = new INetworkType<Float>()
    {
        @Override
        public Float read(ReadBuffer buffer)
        {
            return Float.intBitsToFloat(buffer.read(INT));
        }

        @Override
        public void write(WriteBuffer buffer, Float value)
        {
            buffer.write(INT, Float.floatToIntBits(value));
        }
    };

    INetworkType<Double> DOUBLE = new INetworkType<Double>()
    {
        @Override
        public Double read(ReadBuffer buffer)
        {
            return Double.longBitsToDouble(buffer.read(LONG));
        }

        @Override
        public void write(WriteBuffer buffer, Double value)
        {
            buffer.write(LONG, Double.doubleToLongBits(value));
        }
    };

    INetworkType<Integer> VARINT = new INetworkType<Integer>()
    {
        @Override
        public Integer read(ReadBuffer buffer)
        {
            int value = 0;
            int pos = 0;

            while (true)
            {
                byte b = buffer.read(BYTE);
                value |= (b & 127) << pos++ * 7;

                if (pos > 5)
                    throw new RuntimeException("Varint bigash");

                if ((b & 128) != 128)
                    break;
            }

            return value;
        }

        @Override
        public void write(WriteBuffer buffer, Integer value)
        {
            int val = value;
            while ((val & -128) != 0)
            {
                buffer.write(BYTE, (byte) (val & 127 | 128));
                val >>= 7;
            }

            buffer.write(BYTE, (byte) val);
        }
    };

    INetworkType<Long> VARLONG = new INetworkType<Long>()
    {
        @Override
        public Long read(ReadBuffer buffer)
        {
            long value = 0;
            int pos = 0;

            while (true)
            {
                byte b = buffer.read(BYTE);
                value |= (b & 127) << pos++ * 7;

                if (pos > 10)
                    throw new RuntimeException("Varint bigash");

                if ((b & 128) != 128)
                    break;
            }

            return value;
        }

        @Override
        public void write(WriteBuffer buffer, Long value)
        {
            long val = value;
            while ((val & -128) != 0)
            {
                buffer.write(BYTE, (byte) (val & 127 | 128));
                val >>= 7;
            }

            buffer.write(BYTE, (byte) val);
        }
    };

    INetworkType<Boolean> BOOLEAN = new INetworkType<Boolean>()
    {
        @Override
        public Boolean read(ReadBuffer buffer)
        {
            return buffer.read(BYTE) == 0x01;
        }

        @Override
        public void write(WriteBuffer buffer, Boolean value)
        {
            buffer.write(BYTE, (byte) (value ? 0x01 : 0x00));
        }
    };

    INetworkType<UUID> UUID = new INetworkType<UUID>()
    {
        @Override
        public UUID read(ReadBuffer buffer)
        {
            long l1 = buffer.read(LONG);
            long l2 = buffer.read(LONG);

            return new UUID(l1, l2);
        }

        @Override
        public void write(WriteBuffer buffer, UUID value)
        {
            buffer.write(LONG, value.getLeastSignificantBits());
            buffer.write(LONG, value.getMostSignificantBits());
        }
    };

    INetworkType<BlockPos> BLOCKPOS = new INetworkType<BlockPos>()
    {
        @Override
        public BlockPos read(ReadBuffer buffer)
        {
            long val = buffer.read(LONG);

            int x = (int) (val >> 38);
            int y = (int) ((val >> 26) & 0xFFF);
            int z = (int) (val << 38 >> 38); // Sign extend from 26-bit

            return new BlockPos(x, y, z);
        }

        @Override
        public void write(WriteBuffer buffer, BlockPos pos)
        {
            buffer.write(LONG, ((long) (pos.x() & 0x3FFFFFF) << 38) | ((long) (pos.y() & 0xFFF) << 26) | (pos.z() & 0x3FFFFFF));
        }
    };

    INetworkType<Vec3> VEC3 = new INetworkType<Vec3>()
    {
        @Override
        public Vec3 read(ReadBuffer buffer)
        {
            return new Vec3(buffer.read(DOUBLE), buffer.read(DOUBLE), buffer.read(DOUBLE));
        }

        @Override
        public void write(WriteBuffer buffer, Vec3 value)
        {
            buffer.write(DOUBLE, value.x());
            buffer.write(DOUBLE, value.y());
            buffer.write(DOUBLE, value.z());
        }
    };

    INetworkType<String> STRING = new INetworkType<String>()
    {
        @Override
        public String read(ReadBuffer buffer)
        {
            int len = buffer.read(VARINT);

            return new String(buffer.UNSAFE_read(len));
        }

        @Override
        public void write(WriteBuffer buffer, String value)
        {
            buffer.write(VARINT, value.length());
            buffer.UNSAFE_write(value.getBytes(StandardCharsets.UTF_8));
        }
    };
}
