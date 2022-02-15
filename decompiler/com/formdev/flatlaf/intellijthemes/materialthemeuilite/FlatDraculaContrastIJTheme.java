package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatDraculaContrastIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Dracula Contrast (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatDraculaContrastIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Dracula Contrast (Material)", FlatDraculaContrastIJTheme.class);
  }
  
  public FlatDraculaContrastIJTheme() {
    super(Utils.loadTheme("Dracula Contrast.theme.json"));
  }

  
  public String getName() {
    return "Dracula Contrast (Material)";
  }
}
