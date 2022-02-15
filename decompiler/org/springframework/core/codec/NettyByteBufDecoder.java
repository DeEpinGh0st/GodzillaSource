package org.springframework.core.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.Map;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBuffer;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
























public class NettyByteBufDecoder
  extends AbstractDataBufferDecoder<ByteBuf>
{
  public NettyByteBufDecoder() {
    super(new MimeType[] { MimeTypeUtils.ALL });
  }


  
  public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
    return (ByteBuf.class.isAssignableFrom(elementType.toClass()) && super
      .canDecode(elementType, mimeType));
  }



  
  public ByteBuf decode(DataBuffer dataBuffer, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
    if (this.logger.isDebugEnabled()) {
      this.logger.debug(Hints.getLogPrefix(hints) + "Read " + dataBuffer.readableByteCount() + " bytes");
    }
    if (dataBuffer instanceof NettyDataBuffer) {
      return ((NettyDataBuffer)dataBuffer).getNativeBuffer();
    }
    
    byte[] bytes = new byte[dataBuffer.readableByteCount()];
    dataBuffer.read(bytes);
    ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
    DataBufferUtils.release(dataBuffer);
    return byteBuf;
  }
}
