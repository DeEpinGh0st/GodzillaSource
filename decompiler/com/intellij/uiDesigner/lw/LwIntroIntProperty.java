package com.intellij.uiDesigner.lw;

import org.jdom.Element;














public final class LwIntroIntProperty
  extends LwIntrospectedProperty
{
  public LwIntroIntProperty(String name) {
    super(name, Integer.class.getName());
  }
  
  public Object read(Element element) throws Exception {
    return new Integer(LwXmlReader.getRequiredInt(element, "value"));
  }
}
