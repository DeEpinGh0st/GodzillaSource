package net.miginfocom.layout;

public interface ContainerWrapper extends ComponentWrapper {
  ComponentWrapper[] getComponents();
  
  int getComponentCount();
  
  Object getLayout();
  
  boolean isLeftToRight();
  
  void paintDebugCell(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
}
