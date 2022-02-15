package com.formdev.flatlaf.intellijthemes;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatMonocaiIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Monocai";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatMonocaiIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Monocai", FlatMonocaiIJTheme.class);
  }
  
  public FlatMonocaiIJTheme() {
    super(Utils.loadTheme("Monocai.theme.json"));
  }

  
  public String getName() {
    return "Monocai";
  }
}
