package com.intellij.uiDesigner.lw;

import java.awt.LayoutManager;
import org.jdom.Element;



















public final class LwScrollPane
  extends LwContainer
{
  public LwScrollPane(String className) {
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
