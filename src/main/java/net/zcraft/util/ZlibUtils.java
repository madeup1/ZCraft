package net.zcraft.util;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ZlibUtils
{
    public static byte[] compress(byte[] data) {
        try {
            Deflater deflater = new Deflater(); // default: zlib with headers
            deflater.setInput(data);
            deflater.finish();

            ByteArrayOutputStream out = new ByteArrayOutputStream(data.length);
            byte[] buffer = new byte[512];
            while (!deflater.finished()) {
                int count = deflater.deflate(buffer);
                out.write(buffer, 0, count);
            }
            deflater.end();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] decompress(byte[] input) {
        try {
            Inflater inflater = new Inflater(); // default: expects zlib headers
            inflater.setInput(input);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            while (!inflater.finished() && !inflater.needsInput()) {
                int count = inflater.inflate(buffer);
                if (count == 0) break; // safety check
                out.write(buffer, 0, count);
            }
            inflater.end();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
