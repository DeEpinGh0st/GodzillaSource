package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;


















public abstract class NodeEvent
  extends Event
{
  private final String anchor;
  
  public NodeEvent(String anchor, Mark startMark, Mark endMark) {
    super(startMark, endMark);
    this.anchor = anchor;
  }









  
  public String getAnchor() {
    return this.anchor;
  }

  
  protected String getArguments() {
    return "anchor=" + this.anchor;
  }
}
