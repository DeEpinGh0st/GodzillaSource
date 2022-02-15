package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;




















public final class SequenceEndEvent
  extends CollectionEndEvent
{
  public SequenceEndEvent(Mark startMark, Mark endMark) {
    super(startMark, endMark);
  }

  
  public Event.ID getEventId() {
    return Event.ID.SequenceEnd;
  }
}
