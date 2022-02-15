package com.formdev.flatlaf;

























public class FlatDarculaLaf
  extends FlatDarkLaf
{
  public static final String NAME = "FlatLaf Darcula";
  
  public static boolean install() {
    return install(new FlatDarculaLaf());
  }
  
  public static void installLafInfo() {
    installLafInfo("FlatLaf Darcula", (Class)FlatDarculaLaf.class);
  }

  
  public String getName() {
    return "FlatLaf Darcula";
  }

  
  public String getDescription() {
    return "FlatLaf Darcula Look and Feel";
  }
}
