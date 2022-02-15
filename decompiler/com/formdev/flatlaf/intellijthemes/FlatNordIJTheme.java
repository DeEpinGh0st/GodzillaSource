package com.formdev.flatlaf.intellijthemes;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatNordIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Nord";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatNordIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Nord", FlatNordIJTheme.class);
  }
  
  public FlatNordIJTheme() {
    super(Utils.loadTheme("nord.theme.json"));
  }

  
  public String getName() {
    return "Nord";
  }
}
