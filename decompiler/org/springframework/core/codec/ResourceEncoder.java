package org.springframework.core.codec;

import java.util.Map;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;






























public class ResourceEncoder
  extends AbstractSingleValueEncoder<Resource>
{
  public static final int DEFAULT_BUFFER_SIZE = 4096;
  private final int bufferSize;
  
  public ResourceEncoder() {
    this(4096);
  }
  
  public ResourceEncoder(int bufferSize) {
    super(new MimeType[] { MimeTypeUtils.APPLICATION_OCTET_STREAM, MimeTypeUtils.ALL });
    Assert.isTrue((bufferSize > 0), "'bufferSize' must be larger than 0");
    this.bufferSize = bufferSize;
  }


  
  public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
    Class<?> clazz = elementType.toClass();
    return (super.canEncode(elementType, mimeType) && Resource.class.isAssignableFrom(clazz));
  }



  
  protected Flux<DataBuffer> encode(Resource resource, DataBufferFactory bufferFactory, ResolvableType type, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
    if (this.logger.isDebugEnabled() && !Hints.isLoggingSuppressed(hints)) {
      String logPrefix = Hints.getLogPrefix(hints);
      this.logger.debug(logPrefix + "Writing [" + resource + "]");
    } 
    return DataBufferUtils.read(resource, bufferFactory, this.bufferSize);
  }
}
