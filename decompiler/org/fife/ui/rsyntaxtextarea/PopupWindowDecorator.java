package org.fife.ui.rsyntaxtextarea;

import javax.swing.JWindow;








































public abstract class PopupWindowDecorator
{
  private static PopupWindowDecorator decorator;
  
  public abstract void decorate(JWindow paramJWindow);
  
  public static PopupWindowDecorator get() {
    return decorator;
  }









  
  public static void set(PopupWindowDecorator decorator) {
    PopupWindowDecorator.decorator = decorator;
  }
}
