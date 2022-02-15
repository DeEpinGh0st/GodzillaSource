package org.springframework.core.metrics.jfr;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.metrics.StartupStep;


























class FlightRecorderStartupStep
  implements StartupStep
{
  private final FlightRecorderStartupEvent event;
  private final FlightRecorderTags tags = new FlightRecorderTags();

  
  private final Consumer<FlightRecorderStartupStep> recordingCallback;


  
  public FlightRecorderStartupStep(long id, String name, long parentId, Consumer<FlightRecorderStartupStep> recordingCallback) {
    this.event = new FlightRecorderStartupEvent(id, name, parentId);
    this.event.begin();
    this.recordingCallback = recordingCallback;
  }


  
  public String getName() {
    return this.event.name;
  }

  
  public long getId() {
    return this.event.eventId;
  }

  
  public Long getParentId() {
    return Long.valueOf(this.event.parentId);
  }

  
  public StartupStep tag(String key, String value) {
    this.tags.add(key, value);
    return this;
  }

  
  public StartupStep tag(String key, Supplier<String> value) {
    this.tags.add(key, value.get());
    return this;
  }

  
  public StartupStep.Tags getTags() {
    return this.tags;
  }

  
  public void end() {
    this.event.end();
    if (this.event.shouldCommit()) {
      StringBuilder builder = new StringBuilder();
      this.tags.forEach(tag -> builder.append(tag.getKey()).append('=').append(tag.getValue()).append(','));

      
      this.event.setTags(builder.toString());
    } 
    this.event.commit();
    this.recordingCallback.accept(this);
  }
  
  protected FlightRecorderStartupEvent getEvent() {
    return this.event;
  }
  
  static class FlightRecorderTags
    implements StartupStep.Tags
  {
    private StartupStep.Tag[] tags = new StartupStep.Tag[0];
    
    public void add(String key, String value) {
      StartupStep.Tag[] newTags = new StartupStep.Tag[this.tags.length + 1];
      System.arraycopy(this.tags, 0, newTags, 0, this.tags.length);
      newTags[newTags.length - 1] = new FlightRecorderStartupStep.FlightRecorderTag(key, value);
      this.tags = newTags;
    }
    
    public void add(String key, Supplier<String> value) {
      add(key, value.get());
    }

    
    @NotNull
    public Iterator<StartupStep.Tag> iterator() {
      return new TagsIterator();
    }
    
    private class TagsIterator
      implements Iterator<StartupStep.Tag> {
      private int idx = 0;

      
      public boolean hasNext() {
        return (this.idx < FlightRecorderStartupStep.FlightRecorderTags.this.tags.length);
      }

      
      public StartupStep.Tag next() {
        return FlightRecorderStartupStep.FlightRecorderTags.this.tags[this.idx++];
      }

      
      public void remove() {
        throw new UnsupportedOperationException("tags are append only");
      }
      
      private TagsIterator() {}
    }
  }
  
  static class FlightRecorderTag
    implements StartupStep.Tag {
    private final String key;
    private final String value;
    
    public FlightRecorderTag(String key, String value) {
      this.key = key;
      this.value = value;
    }

    
    public String getKey() {
      return this.key;
    }

    
    public String getValue() {
      return this.value;
    }
  }
}
