package org.yaml.snakeyaml.comments;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import org.yaml.snakeyaml.events.CommentEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.parser.Parser;



























public class CommentEventsCollector
{
  private List<CommentLine> commentLineList;
  private Queue<Event> eventSource;
  private CommentType[] expectedCommentTypes;
  
  public CommentEventsCollector(final Parser parser, CommentType... expectedCommentTypes) {
    this.eventSource = new AbstractQueue<Event>()
      {
        public boolean offer(Event e)
        {
          throw new UnsupportedOperationException();
        }

        
        public Event poll() {
          return parser.getEvent();
        }

        
        public Event peek() {
          return parser.peekEvent();
        }

        
        public Iterator<Event> iterator() {
          throw new UnsupportedOperationException();
        }

        
        public int size() {
          throw new UnsupportedOperationException();
        }
      };
    
    this.expectedCommentTypes = expectedCommentTypes;
    this.commentLineList = new ArrayList<>();
  }









  
  public CommentEventsCollector(Queue<Event> eventSource, CommentType... expectedCommentTypes) {
    this.eventSource = eventSource;
    this.expectedCommentTypes = expectedCommentTypes;
    this.commentLineList = new ArrayList<>();
  }







  
  private boolean isEventExpected(Event event) {
    if (event == null || !event.is(Event.ID.Comment)) {
      return false;
    }
    CommentEvent commentEvent = (CommentEvent)event;
    for (CommentType type : this.expectedCommentTypes) {
      if (commentEvent.getCommentType() == type) {
        return true;
      }
    } 
    return false;
  }






  
  public CommentEventsCollector collectEvents() {
    collectEvents(null);
    return this;
  }









  
  public Event collectEvents(Event event) {
    if (event != null) {
      if (isEventExpected(event)) {
        this.commentLineList.add(new CommentLine((CommentEvent)event));
      } else {
        return event;
      } 
    }
    while (isEventExpected(this.eventSource.peek())) {
      this.commentLineList.add(new CommentLine((CommentEvent)this.eventSource.poll()));
    }
    return null;
  }









  
  public Event collectEventsAndPoll(Event event) {
    Event nextEvent = collectEvents(event);
    return (nextEvent != null) ? nextEvent : this.eventSource.poll();
  }





  
  public List<CommentLine> consume() {
    try {
      return this.commentLineList;
    } finally {
      this.commentLineList = new ArrayList<>();
    } 
  }




  
  public boolean isEmpty() {
    return this.commentLineList.isEmpty();
  }
}
