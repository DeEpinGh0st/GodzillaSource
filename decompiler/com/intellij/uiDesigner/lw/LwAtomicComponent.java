package com.intellij.uiDesigner.lw;

import org.jdom.Element;


















public class LwAtomicComponent
  extends LwComponent
{
  public LwAtomicComponent(String className) {
    super(className);
  }
  
  public void read(Element element, PropertiesProvider provider) throws Exception {
    readBase(element);
    readConstraints(element);
    readProperties(element, provider);
  }
}
