package org.springframework.util;

import java.util.UUID;

@FunctionalInterface
public interface IdGenerator {
  UUID generateId();
}
