package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatArcDarkContrastIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Arc Dark Contrast (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatArcDarkContrastIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Arc Dark Contrast (Material)", FlatArcDarkContrastIJTheme.class);
  }
  
  public FlatArcDarkContrastIJTheme() {
    super(Utils.loadTheme("Arc Dark Contrast.theme.json"));
  }

  
  public String getName() {
    return "Arc Dark Contrast (Material)";
  }
}
