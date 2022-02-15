package org.springframework.cglib.core;

public interface GeneratorStrategy {
  byte[] generate(ClassGenerator paramClassGenerator) throws Exception;
  
  boolean equals(Object paramObject);
}
