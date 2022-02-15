package com.formdev.flatlaf.intellijthemes;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatDraculaIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Dracula";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatDraculaIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Dracula", FlatDraculaIJTheme.class);
  }
  
  public FlatDraculaIJTheme() {
    super(Utils.loadTheme("Dracula.theme.json"));
  }

  
  public String getName() {
    return "Dracula";
  }
}
