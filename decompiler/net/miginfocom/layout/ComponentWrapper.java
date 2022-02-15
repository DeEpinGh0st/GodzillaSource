package net.miginfocom.layout;

public interface ComponentWrapper {
  public static final int TYPE_UNSET = -1;
  
  public static final int TYPE_UNKNOWN = 0;
  
  public static final int TYPE_CONTAINER = 1;
  
  public static final int TYPE_LABEL = 2;
  
  public static final int TYPE_TEXT_FIELD = 3;
  
  public static final int TYPE_TEXT_AREA = 4;
  
  public static final int TYPE_BUTTON = 5;
  
  public static final int TYPE_LIST = 6;
  
  public static final int TYPE_TABLE = 7;
  
  public static final int TYPE_SCROLL_PANE = 8;
  
  public static final int TYPE_IMAGE = 9;
  
  public static final int TYPE_PANEL = 10;
  
  public static final int TYPE_COMBO_BOX = 11;
  
  public static final int TYPE_SLIDER = 12;
  
  public static final int TYPE_SPINNER = 13;
  
  public static final int TYPE_PROGRESS_BAR = 14;
  
  public static final int TYPE_TREE = 15;
  
  public static final int TYPE_CHECK_BOX = 16;
  
  public static final int TYPE_SCROLL_BAR = 17;
  
  public static final int TYPE_SEPARATOR = 18;
  
  public static final int TYPE_TABBED_PANE = 19;
  
  Object getComponent();
  
  int getX();
  
  int getY();
  
  int getWidth();
  
  int getHeight();
  
  int getScreenLocationX();
  
  int getScreenLocationY();
  
  int getMinimumWidth(int paramInt);
  
  int getMinimumHeight(int paramInt);
  
  int getPreferredWidth(int paramInt);
  
  int getPreferredHeight(int paramInt);
  
  int getMaximumWidth(int paramInt);
  
  int getMaximumHeight(int paramInt);
  
  void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  boolean isVisible();
  
  int getBaseline(int paramInt1, int paramInt2);
  
  boolean hasBaseline();
  
  ContainerWrapper getParent();
  
  float getPixelUnitFactor(boolean paramBoolean);
  
  int getHorizontalScreenDPI();
  
  int getVerticalScreenDPI();
  
  int getScreenWidth();
  
  int getScreenHeight();
  
  String getLinkId();
  
  int getLayoutHashCode();
  
  int[] getVisualPadding();
  
  void paintDebugOutline(boolean paramBoolean);
  
  int getComponentType(boolean paramBoolean);
  
  int getContentBias();
}
