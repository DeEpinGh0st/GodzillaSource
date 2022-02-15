package com.jgoodies.forms.util;

import com.jgoodies.forms.layout.ConstantSize;
import com.jgoodies.forms.layout.Size;
import com.jgoodies.forms.layout.Sizes;





































final class WindowsLayoutStyle
  extends LayoutStyle
{
  static final WindowsLayoutStyle INSTANCE = new WindowsLayoutStyle();







  
  private static final Size BUTTON_WIDTH = (Size)Sizes.dluX(50);
  private static final Size BUTTON_HEIGHT = (Size)Sizes.dluY(14);



  
  private static final ConstantSize DIALOG_MARGIN_X = Sizes.DLUX7;
  private static final ConstantSize DIALOG_MARGIN_Y = Sizes.DLUY7;
  
  private static final ConstantSize TABBED_DIALOG_MARGIN_X = Sizes.DLUX4;
  private static final ConstantSize TABBED_DIALOG_MARGIN_Y = Sizes.DLUY4;
  
  private static final ConstantSize LABEL_COMPONENT_PADX = Sizes.DLUX3;
  private static final ConstantSize RELATED_COMPONENTS_PADX = Sizes.DLUX4;
  private static final ConstantSize UNRELATED_COMPONENTS_PADX = Sizes.DLUX7;
  
  private static final ConstantSize LABEL_COMPONENT_PADY = Sizes.DLUY2;
  private static final ConstantSize RELATED_COMPONENTS_PADY = Sizes.DLUY4;
  private static final ConstantSize UNRELATED_COMPONENTS_PADY = Sizes.DLUY7;
  private static final ConstantSize NARROW_LINE_PAD = Sizes.DLUY2;
  private static final ConstantSize LINE_PAD = Sizes.DLUY3;
  private static final ConstantSize PARAGRAPH_PAD = Sizes.DLUY9;
  private static final ConstantSize BUTTON_BAR_PAD = Sizes.DLUY5;




  
  public Size getDefaultButtonWidth() {
    return BUTTON_WIDTH;
  }


  
  public Size getDefaultButtonHeight() {
    return BUTTON_HEIGHT;
  }


  
  public ConstantSize getDialogMarginX() {
    return DIALOG_MARGIN_X;
  }


  
  public ConstantSize getDialogMarginY() {
    return DIALOG_MARGIN_Y;
  }


  
  public ConstantSize getTabbedDialogMarginX() {
    return TABBED_DIALOG_MARGIN_X;
  }


  
  public ConstantSize getTabbedDialogMarginY() {
    return TABBED_DIALOG_MARGIN_Y;
  }


  
  public ConstantSize getLabelComponentPadX() {
    return LABEL_COMPONENT_PADX;
  }


  
  public ConstantSize getLabelComponentPadY() {
    return LABEL_COMPONENT_PADY;
  }


  
  public ConstantSize getRelatedComponentsPadX() {
    return RELATED_COMPONENTS_PADX;
  }


  
  public ConstantSize getRelatedComponentsPadY() {
    return RELATED_COMPONENTS_PADY;
  }


  
  public ConstantSize getUnrelatedComponentsPadX() {
    return UNRELATED_COMPONENTS_PADX;
  }


  
  public ConstantSize getUnrelatedComponentsPadY() {
    return UNRELATED_COMPONENTS_PADY;
  }


  
  public ConstantSize getNarrowLinePad() {
    return NARROW_LINE_PAD;
  }


  
  public ConstantSize getLinePad() {
    return LINE_PAD;
  }


  
  public ConstantSize getParagraphPad() {
    return PARAGRAPH_PAD;
  }


  
  public ConstantSize getButtonBarPad() {
    return BUTTON_BAR_PAD;
  }
}
