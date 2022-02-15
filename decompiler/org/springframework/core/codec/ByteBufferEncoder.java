package org.springframework.core.codec;

import java.nio.ByteBuffer;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
























public class ByteBufferEncoder
  extends AbstractEncoder<ByteBuffer>
{
  public ByteBufferEncoder() {
    super(new MimeType[] { MimeTypeUtils.ALL });
  }


  
  public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
    Class<?> clazz = elementType.toClass();
    return (super.canEncode(elementType, mimeType) && ByteBuffer.class.isAssignableFrom(clazz));
  }




  
  public Flux<DataBuffer> encode(Publisher<? extends ByteBuffer> inputStream, DataBufferFactory bufferFactory, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
    return Flux.from(inputStream).map(byteBuffer -> encodeValue(byteBuffer, bufferFactory, elementType, mimeType, hints));
  }




  
  public DataBuffer encodeValue(ByteBuffer byteBuffer, DataBufferFactory bufferFactory, ResolvableType valueType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
    DataBuffer dataBuffer = bufferFactory.wrap(byteBuffer);
    if (this.logger.isDebugEnabled() && !Hints.isLoggingSuppressed(hints)) {
      String logPrefix = Hints.getLogPrefix(hints);
      this.logger.debug(logPrefix + "Writing " + dataBuffer.readableByteCount() + " bytes");
    } 
    return dataBuffer;
  }
}
