package org.springframework.core.codec;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;






































































public interface Decoder<T>
{
  boolean canDecode(ResolvableType paramResolvableType, @Nullable MimeType paramMimeType);
  
  Flux<T> decode(Publisher<DataBuffer> paramPublisher, ResolvableType paramResolvableType, @Nullable MimeType paramMimeType, @Nullable Map<String, Object> paramMap);
  
  Mono<T> decodeToMono(Publisher<DataBuffer> paramPublisher, ResolvableType paramResolvableType, @Nullable MimeType paramMimeType, @Nullable Map<String, Object> paramMap);
  
  @Nullable
  default T decode(DataBuffer buffer, ResolvableType targetType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) throws DecodingException {
    Throwable failure;
    CompletableFuture<T> future = decodeToMono((Publisher<DataBuffer>)Mono.just(buffer), targetType, mimeType, hints).toFuture();
    Assert.state(future.isDone(), "DataBuffer decoding should have completed.");

    
    try {
      return future.get();
    }
    catch (ExecutionException ex) {
      failure = ex.getCause();
    }
    catch (InterruptedException ex) {
      failure = ex;
    } 
    throw (failure instanceof CodecException) ? (CodecException)failure : new DecodingException("Failed to decode: " + failure
        .getMessage(), failure);
  }









  
  List<MimeType> getDecodableMimeTypes();









  
  default List<MimeType> getDecodableMimeTypes(ResolvableType targetType) {
    return canDecode(targetType, null) ? getDecodableMimeTypes() : Collections.<MimeType>emptyList();
  }
}
