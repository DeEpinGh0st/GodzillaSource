package org.fife.rsta.ac.js.ecma.api.ecma3;

import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSDateFunctions;































































































public abstract class JSDate
  implements JSDateFunctions
{
  public JSDate prototype;
  protected JSFunction constructor;
  
  public JSDate() {}
  
  public JSDate(JSNumber milliseconds) {}
  
  public JSDate(JSString datestring) {}
  
  public JSDate(JSNumber year, JSNumber month, JSNumber day, JSNumber hours, JSNumber minutes, JSNumber seconds, JSNumber ms) {}
  
  public static JSNumber UTC(JSNumber year, JSNumber month, JSNumber day, JSNumber hour, JSNumber min, JSNumber sec, JSNumber ms) {
    return null;
  }










  
  public static JSNumber parse(JSString string) {
    return null;
  }
}
