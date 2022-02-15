package com.formdev.flatlaf.json;





























































abstract class JsonHandler<A, O>
{
  JsonParser parser;
  
  protected Location getLocation() {
    return this.parser.getLocation();
  }







  
  public void startNull() {}







  
  public void endNull() {}







  
  public void startBoolean() {}







  
  public void endBoolean(boolean value) {}







  
  public void startString() {}







  
  public void endString(String string) {}







  
  public void startNumber() {}






  
  public void endNumber(String string) {}






  
  public A startArray() {
    return null;
  }











  
  public void endArray(A array) {}











  
  public void startArrayValue(A array) {}











  
  public void endArrayValue(A array) {}











  
  public O startObject() {
    return null;
  }
  
  public void endObject(O object) {}
  
  public void startObjectName(O object) {}
  
  public void endObjectName(O object, String name) {}
  
  public void startObjectValue(O object, String name) {}
  
  public void endObjectValue(O object, String name) {}
}
