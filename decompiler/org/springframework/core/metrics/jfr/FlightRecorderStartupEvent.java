package org.springframework.core.metrics.jfr;

import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Event;
import jdk.jfr.Label;




























@Category({"Spring Application"})
@Label("Startup Step")
@Description("Spring Application Startup")
class FlightRecorderStartupEvent
  extends Event
{
  public final long eventId;
  public final long parentId;
  @Label("Name")
  public final String name;
  @Label("Tags")
  String tags = "";

  
  public FlightRecorderStartupEvent(long eventId, String name, long parentId) {
    this.name = name;
    this.eventId = eventId;
    this.parentId = parentId;
  }
  
  public void setTags(String tags) {
    this.tags = tags;
  }
}
