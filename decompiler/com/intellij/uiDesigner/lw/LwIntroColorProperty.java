package com.intellij.uiDesigner.lw;

import org.jdom.Element;

















public class LwIntroColorProperty
  extends LwIntrospectedProperty
{
  public LwIntroColorProperty(String name) {
    super(name, "java.awt.Color");
  }
  
  public Object read(Element element) throws Exception {
    return LwXmlReader.getColorDescriptor(element);
  }
}
