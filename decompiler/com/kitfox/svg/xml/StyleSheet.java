package com.kitfox.svg.xml;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;










public class StyleSheet
{
  HashMap<StyleSheetRule, String> ruleMap = new HashMap<StyleSheetRule, String>();


  
  public static StyleSheet parseSheet(String src) {
    Logger.getLogger("svgSalamandeLogger").log(Level.WARNING, "CSS parser not implemented yet");
    
    return null;
  }

  
  public void addStyleRule(StyleSheetRule rule, String value) {
    this.ruleMap.put(rule, value);
  }

  
  public boolean getStyle(StyleAttribute attrib, String tagName, String cssClass) {
    StyleSheetRule rule = new StyleSheetRule(attrib.getName(), tagName, cssClass);
    String value = this.ruleMap.get(rule);
    
    if (value != null) {
      
      attrib.setStringValue(value);
      return true;
    } 

    
    rule = new StyleSheetRule(attrib.getName(), null, cssClass);
    value = this.ruleMap.get(rule);
    
    if (value != null) {
      
      attrib.setStringValue(value);
      return true;
    } 

    
    rule = new StyleSheetRule(attrib.getName(), tagName, null);
    value = this.ruleMap.get(rule);
    
    if (value != null) {
      
      attrib.setStringValue(value);
      return true;
    } 
    
    return false;
  }
}
