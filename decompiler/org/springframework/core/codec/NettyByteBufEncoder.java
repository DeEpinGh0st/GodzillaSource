package org.springframework.core.codec;

import io.netty.buffer.ByteBuf;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
























public class NettyByteBufEncoder
  extends AbstractEncoder<ByteBuf>
{
  public NettyByteBufEncoder() {
    super(new MimeType[] { MimeTypeUtils.ALL });
  }


  
  public boolean canEncode(ResolvableType type, @Nullable MimeType mimeType) {
    Class<?> clazz = type.toClass();
    return (super.canEncode(type, mimeType) && ByteBuf.class.isAssignableFrom(clazz));
  }




  
  public Flux<DataBuffer> encode(Publisher<? extends ByteBuf> inputStream, DataBufferFactory bufferFactory, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
    return Flux.from(inputStream).map(byteBuffer -> encodeValue(byteBuffer, bufferFactory, elementType, mimeType, hints));
  }




  
  public DataBuffer encodeValue(ByteBuf byteBuf, DataBufferFactory bufferFactory, ResolvableType valueType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
    if (this.logger.isDebugEnabled() && !Hints.isLoggingSuppressed(hints)) {
      String logPrefix = Hints.getLogPrefix(hints);
      this.logger.debug(logPrefix + "Writing " + byteBuf.readableBytes() + " bytes");
    } 
    if (bufferFactory instanceof NettyDataBufferFactory) {
      return (DataBuffer)((NettyDataBufferFactory)bufferFactory).wrap(byteBuf);
    }
    byte[] bytes = new byte[byteBuf.readableBytes()];
    byteBuf.readBytes(bytes);
    byteBuf.release();
    return bufferFactory.wrap(bytes);
  }
}
