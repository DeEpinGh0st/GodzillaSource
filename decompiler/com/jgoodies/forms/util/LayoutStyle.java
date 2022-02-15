package com.jgoodies.forms.util;

import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.forms.layout.ConstantSize;
import com.jgoodies.forms.layout.Size;




















































public abstract class LayoutStyle
{
  private static LayoutStyle current = initialLayoutStyle();










  
  private static LayoutStyle initialLayoutStyle() {
    return SystemUtils.IS_OS_MAC ? MacLayoutStyle.INSTANCE : WindowsLayoutStyle.INSTANCE;
  }










  
  public static LayoutStyle getCurrent() {
    return current;
  }






  
  public static void setCurrent(LayoutStyle newLayoutStyle) {
    current = newLayoutStyle;
  }
  
  public abstract Size getDefaultButtonWidth();
  
  public abstract Size getDefaultButtonHeight();
  
  public abstract ConstantSize getDialogMarginX();
  
  public abstract ConstantSize getDialogMarginY();
  
  public abstract ConstantSize getTabbedDialogMarginX();
  
  public abstract ConstantSize getTabbedDialogMarginY();
  
  public abstract ConstantSize getLabelComponentPadX();
  
  public abstract ConstantSize getLabelComponentPadY();
  
  public abstract ConstantSize getRelatedComponentsPadX();
  
  public abstract ConstantSize getRelatedComponentsPadY();
  
  public abstract ConstantSize getUnrelatedComponentsPadX();
  
  public abstract ConstantSize getUnrelatedComponentsPadY();
  
  public abstract ConstantSize getNarrowLinePad();
  
  public abstract ConstantSize getLinePad();
  
  public abstract ConstantSize getParagraphPad();
  
  public abstract ConstantSize getButtonBarPad();
}
