package utility;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class EmptyCompression implements Compression {
    public boolean isCompress(){
        return false;
    }

    public ByteBuffer encode(Integer[] arr){
        ByteBuffer buffer = ByteBuffer.allocate(arr.length * 4);
        for(int i = 0; i < arr.length; i++) buffer.putInt(arr[i]);
        return buffer;
    }

    public Integer[] decode(RandomAccessFile file, Long offset, Integer count){
        byte[] byteArray = new byte[count];
        try {
            file.seek(offset);
            file.read(byteArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Integer intArray[] = new Integer[count/4];
        int off = 0;
        for(int i = 0; i < count/4; i++) {
            intArray[i] = (byteArray[3 + off] & 0xFF) | ((byteArray[2 + off] & 0xFF) << 8) |
                    ((byteArray[1 + off] & 0xFF) << 16) | ((byteArray[0 + off] & 0xFF) << 24);
            off += 4;
        }
        return intArray;
    }
}
