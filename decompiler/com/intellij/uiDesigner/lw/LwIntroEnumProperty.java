package com.intellij.uiDesigner.lw;

import java.lang.reflect.Method;
import org.jdom.Element;




















public class LwIntroEnumProperty
  extends LwIntrospectedProperty
{
  private final Class myEnumClass;
  
  public LwIntroEnumProperty(String name, Class enumClass) {
    super(name, enumClass.getName());
    this.myEnumClass = enumClass;
  }
  
  public Object read(Element element) throws Exception {
    String value = element.getAttributeValue("value");
    Method method = this.myEnumClass.getMethod("valueOf", new Class[] { String.class });
    return method.invoke(null, new Object[] { value });
  }
  
  public String getCodeGenPropertyClassName() {
    return "java.lang.Enum";
  }
}
