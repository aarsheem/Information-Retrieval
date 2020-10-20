package utility;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public interface Compression {
    public boolean isCompress();
    public ByteBuffer encode(Integer[] arr);
    public Integer[] decode(RandomAccessFile file, Long offset, Integer count);
}
