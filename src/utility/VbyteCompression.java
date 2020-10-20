package utility;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VbyteCompression implements Compression {
    public boolean isCompress(){
        return true;
    }

    public ByteBuffer encode(Integer[] arr) {
        byte[] temp = new byte[arr.length * 4];
        int id = 0;
        for (int i : arr) {
            while (i >= 128) {
                temp[id++] = (byte) (i & 0x7F);
                i >>>= 7;
            }
            temp[id++] = (byte) (i | 0x80);
        }
        return ByteBuffer.wrap(Arrays.copyOf(temp, id));
    }

    public Integer[] decode(RandomAccessFile file, Long offset, Integer count) {
        byte[] byteArray = new byte[count];
        try {
            file.seek(offset);
            file.read(byteArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Integer> output = new ArrayList<>();
        for (int i = 0; i < byteArray.length; i++) {
            int position = 0;
            int result = ((int) byteArray[i] & 0x7F);
            while ((byteArray[i] & 0x80) == 0) {
                i += 1;
                position += 1;
                int unsignedByte = ((int) byteArray[i] & 0x7F);
                result |= (unsignedByte << (7 * position));
            }
            output.add(result);
        }
        return output.toArray(new Integer[output.size()]);
    }
}
