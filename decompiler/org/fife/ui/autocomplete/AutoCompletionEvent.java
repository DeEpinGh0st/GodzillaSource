package org.fife.ui.autocomplete;

import java.util.EventObject;


























public class AutoCompletionEvent
  extends EventObject
{
  private Type type;
  
  public AutoCompletionEvent(AutoCompletion source, Type type) {
    super(source);
    this.type = type;
  }







  
  public AutoCompletion getAutoCompletion() {
    return (AutoCompletion)getSource();
  }






  
  public Type getEventType() {
    return this.type;
  }



  
  public enum Type
  {
    POPUP_SHOWN,
    POPUP_HIDDEN;
  }
}
