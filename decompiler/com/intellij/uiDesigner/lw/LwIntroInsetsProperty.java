package com.intellij.uiDesigner.lw;

import org.jdom.Element;

















public final class LwIntroInsetsProperty
  extends LwIntrospectedProperty
{
  public LwIntroInsetsProperty(String name) {
    super(name, "java.awt.Insets");
  }
  
  public Object read(Element element) throws Exception {
    return LwXmlReader.readInsets(element);
  }
}
