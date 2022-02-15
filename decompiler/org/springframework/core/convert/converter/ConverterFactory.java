package org.springframework.core.convert.converter;

public interface ConverterFactory<S, R> {
  <T extends R> Converter<S, T> getConverter(Class<T> paramClass);
}
