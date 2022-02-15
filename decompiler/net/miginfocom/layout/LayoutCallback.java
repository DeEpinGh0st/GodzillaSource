package net.miginfocom.layout;

















































public abstract class LayoutCallback
{
  public UnitValue[] getPosition(ComponentWrapper comp) {
    return null;
  }









  
  public BoundSize[] getSize(ComponentWrapper comp) {
    return null;
  }
  
  public void correctBounds(ComponentWrapper comp) {}
}
