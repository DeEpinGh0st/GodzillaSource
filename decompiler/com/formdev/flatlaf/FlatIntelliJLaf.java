package com.formdev.flatlaf;

























public class FlatIntelliJLaf
  extends FlatLightLaf
{
  public static final String NAME = "FlatLaf IntelliJ";
  
  public static boolean install() {
    return install(new FlatIntelliJLaf());
  }
  
  public static void installLafInfo() {
    installLafInfo("FlatLaf IntelliJ", (Class)FlatIntelliJLaf.class);
  }

  
  public String getName() {
    return "FlatLaf IntelliJ";
  }

  
  public String getDescription() {
    return "FlatLaf IntelliJ Look and Feel";
  }
}
