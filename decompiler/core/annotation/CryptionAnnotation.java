package core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CryptionAnnotation {
  String payloadName();
  
  String Name();
}
