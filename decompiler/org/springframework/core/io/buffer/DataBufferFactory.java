package org.springframework.core.io.buffer;

import java.nio.ByteBuffer;
import java.util.List;

public interface DataBufferFactory {
  DataBuffer allocateBuffer();
  
  DataBuffer allocateBuffer(int paramInt);
  
  DataBuffer wrap(ByteBuffer paramByteBuffer);
  
  DataBuffer wrap(byte[] paramArrayOfbyte);
  
  DataBuffer join(List<? extends DataBuffer> paramList);
}
