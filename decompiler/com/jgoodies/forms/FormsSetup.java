package com.jgoodies.forms;

import com.jgoodies.forms.factories.ComponentFactory;
import com.jgoodies.forms.factories.DefaultComponentFactory;






































































public class FormsSetup
{
  private static final String DEBUG_TOOL_TIPS_ENABLED_KEY = "FormsSetup.debugToolTipsEnabled";
  private static ComponentFactory componentFactoryDefault;
  private static boolean labelForFeatureEnabledDefault = true;
  private static boolean opaqueDefault = false;
  private static boolean debugToolTipsEnabled = getDebugToolTipSystemProperty();


















  
  public static ComponentFactory getComponentFactoryDefault() {
    if (componentFactoryDefault == null) {
      componentFactoryDefault = (ComponentFactory)new DefaultComponentFactory();
    }
    return componentFactoryDefault;
  }








  
  public static void setComponentFactoryDefault(ComponentFactory factory) {
    componentFactoryDefault = factory;
  }









  
  public static boolean getLabelForFeatureEnabledDefault() {
    return labelForFeatureEnabledDefault;
  }











  
  public static void setLabelForFeatureEnabledDefault(boolean b) {
    labelForFeatureEnabledDefault = b;
  }





  
  public static boolean getOpaqueDefault() {
    return opaqueDefault;
  }









  
  public static void setOpaqueDefault(boolean b) {
    opaqueDefault = b;
  }






  
  public static boolean getDebugToolTipsEnabledDefault() {
    return debugToolTipsEnabled;
  }






  
  public static void setDebugToolTipsEnabled(boolean b) {
    debugToolTipsEnabled = b;
  }

  
  private static boolean getDebugToolTipSystemProperty() {
    try {
      String value = System.getProperty("FormsSetup.debugToolTipsEnabled");
      return "true".equalsIgnoreCase(value);
    } catch (SecurityException e) {
      return false;
    } 
  }
}
