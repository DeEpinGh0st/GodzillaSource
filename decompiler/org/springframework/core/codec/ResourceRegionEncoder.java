package org.springframework.core.codec;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.OptionalLong;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
































public class ResourceRegionEncoder
  extends AbstractEncoder<ResourceRegion>
{
  public static final int DEFAULT_BUFFER_SIZE = 4096;
  public static final String BOUNDARY_STRING_HINT = ResourceRegionEncoder.class.getName() + ".boundaryString";
  
  private final int bufferSize;

  
  public ResourceRegionEncoder() {
    this(4096);
  }
  
  public ResourceRegionEncoder(int bufferSize) {
    super(new MimeType[] { MimeTypeUtils.APPLICATION_OCTET_STREAM, MimeTypeUtils.ALL });
    Assert.isTrue((bufferSize > 0), "'bufferSize' must be larger than 0");
    this.bufferSize = bufferSize;
  }

  
  public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
    return (super.canEncode(elementType, mimeType) && ResourceRegion.class
      .isAssignableFrom(elementType.toClass()));
  }




  
  public Flux<DataBuffer> encode(Publisher<? extends ResourceRegion> input, DataBufferFactory bufferFactory, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
    Assert.notNull(input, "'inputStream' must not be null");
    Assert.notNull(bufferFactory, "'bufferFactory' must not be null");
    Assert.notNull(elementType, "'elementType' must not be null");
    
    if (input instanceof Mono) {
      return Mono.from(input)
        .flatMapMany(region -> !region.getResource().isReadable() ? Flux.error((Throwable)new EncodingException("Resource " + region.getResource() + " is not readable")) : writeResourceRegion(region, bufferFactory, hints));
    }






    
    String boundaryString = Hints.<String>getRequiredHint(hints, BOUNDARY_STRING_HINT);
    byte[] startBoundary = toAsciiBytes("\r\n--" + boundaryString + "\r\n");
    byte[] contentType = (mimeType != null) ? toAsciiBytes("Content-Type: " + mimeType + "\r\n") : new byte[0];
    
    return Flux.from(input)
      .concatMap(region -> {
          if (!region.getResource().isReadable()) {
            return (Publisher)Flux.error((Throwable)new EncodingException("Resource " + region.getResource() + " is not readable"));
          }


          
          Flux<DataBuffer> prefix = Flux.just((Object[])new DataBuffer[] { bufferFactory.wrap(startBoundary), bufferFactory.wrap(contentType), bufferFactory.wrap(getContentRangeHeader(region)) });


          
          return (Publisher)prefix.concatWith((Publisher)writeResourceRegion(region, bufferFactory, hints));
        }).concatWithValues((Object[])new DataBuffer[] { getRegionSuffix(bufferFactory, boundaryString) });
  }




  
  private Flux<DataBuffer> writeResourceRegion(ResourceRegion region, DataBufferFactory bufferFactory, @Nullable Map<String, Object> hints) {
    Resource resource = region.getResource();
    long position = region.getPosition();
    long count = region.getCount();
    
    if (this.logger.isDebugEnabled() && !Hints.isLoggingSuppressed(hints)) {
      this.logger.debug(Hints.getLogPrefix(hints) + "Writing region " + position + "-" + (position + count) + " of [" + resource + "]");
    }

    
    Flux<DataBuffer> in = DataBufferUtils.read(resource, position, bufferFactory, this.bufferSize);
    if (this.logger.isDebugEnabled()) {
      in = in.doOnNext(buffer -> Hints.touchDataBuffer(buffer, hints, this.logger));
    }
    return DataBufferUtils.takeUntilByteCount((Publisher)in, count);
  }
  
  private DataBuffer getRegionSuffix(DataBufferFactory bufferFactory, String boundaryString) {
    byte[] endBoundary = toAsciiBytes("\r\n--" + boundaryString + "--");
    return bufferFactory.wrap(endBoundary);
  }
  
  private byte[] toAsciiBytes(String in) {
    return in.getBytes(StandardCharsets.US_ASCII);
  }
  
  private byte[] getContentRangeHeader(ResourceRegion region) {
    long start = region.getPosition();
    long end = start + region.getCount() - 1L;
    OptionalLong contentLength = contentLength(region.getResource());
    if (contentLength.isPresent()) {
      long length = contentLength.getAsLong();
      return toAsciiBytes("Content-Range: bytes " + start + '-' + end + '/' + length + "\r\n\r\n");
    } 
    
    return toAsciiBytes("Content-Range: bytes " + start + '-' + end + "\r\n\r\n");
  }








  
  private OptionalLong contentLength(Resource resource) {
    if (InputStreamResource.class != resource.getClass()) {
      try {
        return OptionalLong.of(resource.contentLength());
      }
      catch (IOException iOException) {}
    }
    
    return OptionalLong.empty();
  }
}
