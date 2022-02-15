package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;




















public final class MappingEndEvent
  extends CollectionEndEvent
{
  public MappingEndEvent(Mark startMark, Mark endMark) {
    super(startMark, endMark);
  }

  
  public Event.ID getEventId() {
    return Event.ID.MappingEnd;
  }
}
