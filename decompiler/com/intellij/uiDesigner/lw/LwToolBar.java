package com.intellij.uiDesigner.lw;

import java.awt.LayoutManager;
import org.jdom.Element;



















public class LwToolBar
  extends LwContainer
{
  public LwToolBar(String className) {
    super(className);
  }
  
  protected LayoutManager createInitialLayout() {
    return null;
  }
  
  public void read(Element element, PropertiesProvider provider) throws Exception {
    readNoLayout(element, provider);
  }
  
  protected void readConstraintsForChild(Element element, LwComponent component) {}
}
