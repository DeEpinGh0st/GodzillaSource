package org.springframework.core.codec;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

























public class ResourceDecoder
  extends AbstractDataBufferDecoder<Resource>
{
  public static String FILENAME_HINT = ResourceDecoder.class.getName() + ".filename";

  
  public ResourceDecoder() {
    super(new MimeType[] { MimeTypeUtils.ALL });
  }


  
  public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
    return (Resource.class.isAssignableFrom(elementType.toClass()) && super
      .canDecode(elementType, mimeType));
  }



  
  public Flux<Resource> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
    return Flux.from((Publisher)decodeToMono(inputStream, elementType, mimeType, hints));
  }



  
  public Resource decode(DataBuffer dataBuffer, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
    final byte[] bytes = new byte[dataBuffer.readableByteCount()];
    dataBuffer.read(bytes);
    DataBufferUtils.release(dataBuffer);
    
    if (this.logger.isDebugEnabled()) {
      this.logger.debug(Hints.getLogPrefix(hints) + "Read " + bytes.length + " bytes");
    }
    
    Class<?> clazz = elementType.toClass();
    final String filename = (hints != null) ? (String)hints.get(FILENAME_HINT) : null;
    if (clazz == InputStreamResource.class) {
      return (Resource)new InputStreamResource(new ByteArrayInputStream(bytes))
        {
          public String getFilename() {
            return filename;
          }
          
          public long contentLength() {
            return bytes.length;
          }
        };
    }
    if (Resource.class.isAssignableFrom(clazz)) {
      return (Resource)new ByteArrayResource(bytes)
        {
          public String getFilename() {
            return filename;
          }
        };
    }
    
    throw new IllegalStateException("Unsupported resource class: " + clazz);
  }
}
