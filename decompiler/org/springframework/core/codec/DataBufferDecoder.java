package org.springframework.core.codec;

import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;


































public class DataBufferDecoder
  extends AbstractDataBufferDecoder<DataBuffer>
{
  public DataBufferDecoder() {
    super(new MimeType[] { MimeTypeUtils.ALL });
  }


  
  public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
    return (DataBuffer.class.isAssignableFrom(elementType.toClass()) && super
      .canDecode(elementType, mimeType));
  }



  
  public Flux<DataBuffer> decode(Publisher<DataBuffer> input, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
    return Flux.from(input);
  }



  
  public DataBuffer decode(DataBuffer buffer, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
    if (this.logger.isDebugEnabled()) {
      this.logger.debug(Hints.getLogPrefix(hints) + "Read " + buffer.readableByteCount() + " bytes");
    }
    return buffer;
  }
}
