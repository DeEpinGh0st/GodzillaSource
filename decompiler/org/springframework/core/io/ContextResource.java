package org.springframework.core.io;

public interface ContextResource extends Resource {
  String getPathWithinContext();
}
