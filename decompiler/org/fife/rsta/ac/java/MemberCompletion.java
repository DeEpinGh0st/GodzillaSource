package org.fife.rsta.ac.java;

interface MemberCompletion extends JavaSourceCompletion {
  String getEnclosingClassName(boolean paramBoolean);
  
  String getSignature();
  
  String getType();
  
  boolean isDeprecated();
  
  public static interface Data extends IconFactory.IconData {
    String getEnclosingClassName(boolean param1Boolean);
    
    String getSignature();
    
    String getSummary();
    
    String getType();
    
    boolean isConstructor();
  }
}
