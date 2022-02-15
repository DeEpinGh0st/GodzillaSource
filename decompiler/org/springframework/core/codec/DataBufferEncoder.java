package org.springframework.core.codec;

import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
























public class DataBufferEncoder
  extends AbstractEncoder<DataBuffer>
{
  public DataBufferEncoder() {
    super(new MimeType[] { MimeTypeUtils.ALL });
  }


  
  public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
    Class<?> clazz = elementType.toClass();
    return (super.canEncode(elementType, mimeType) && DataBuffer.class.isAssignableFrom(clazz));
  }




  
  public Flux<DataBuffer> encode(Publisher<? extends DataBuffer> inputStream, DataBufferFactory bufferFactory, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
    Flux<DataBuffer> flux = Flux.from(inputStream);
    if (this.logger.isDebugEnabled() && !Hints.isLoggingSuppressed(hints)) {
      flux = flux.doOnNext(buffer -> logValue(buffer, hints));
    }
    return flux;
  }



  
  public DataBuffer encodeValue(DataBuffer buffer, DataBufferFactory bufferFactory, ResolvableType valueType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
    if (this.logger.isDebugEnabled() && !Hints.isLoggingSuppressed(hints)) {
      logValue(buffer, hints);
    }
    return buffer;
  }
  
  private void logValue(DataBuffer buffer, @Nullable Map<String, Object> hints) {
    String logPrefix = Hints.getLogPrefix(hints);
    this.logger.debug(logPrefix + "Writing " + buffer.readableByteCount() + " bytes");
  }
}
