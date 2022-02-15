package org.springframework.core.codec;

import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;




































public abstract class AbstractDataBufferDecoder<T>
  extends AbstractDecoder<T>
{
  private int maxInMemorySize = 262144;

  
  protected AbstractDataBufferDecoder(MimeType... supportedMimeTypes) {
    super(supportedMimeTypes);
  }













  
  public void setMaxInMemorySize(int byteCount) {
    this.maxInMemorySize = byteCount;
  }




  
  public int getMaxInMemorySize() {
    return this.maxInMemorySize;
  }




  
  public Flux<T> decode(Publisher<DataBuffer> input, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
    return Flux.from(input).map(buffer -> decodeDataBuffer(buffer, elementType, mimeType, hints));
  }



  
  public Mono<T> decodeToMono(Publisher<DataBuffer> input, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
    return DataBufferUtils.join(input, this.maxInMemorySize)
      .map(buffer -> decodeDataBuffer(buffer, elementType, mimeType, hints));
  }







  
  @Deprecated
  @Nullable
  protected T decodeDataBuffer(DataBuffer buffer, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
    return decode(buffer, elementType, mimeType, hints);
  }
}
