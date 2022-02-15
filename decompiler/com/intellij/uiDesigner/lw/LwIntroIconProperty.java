package com.intellij.uiDesigner.lw;

import org.jdom.Element;


















public class LwIntroIconProperty
  extends LwIntrospectedProperty
{
  public LwIntroIconProperty(String name) {
    super(name, "javax.swing.Icon");
  }
  
  public Object read(Element element) throws Exception {
    String value = LwXmlReader.getRequiredString(element, "value");
    return new IconDescriptor(value);
  }
}
