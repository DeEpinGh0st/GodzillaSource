package org.yaml.snakeyaml.emitter;

import java.io.IOException;
import org.yaml.snakeyaml.events.Event;

public interface Emitable {
  void emit(Event paramEvent) throws IOException;
}
