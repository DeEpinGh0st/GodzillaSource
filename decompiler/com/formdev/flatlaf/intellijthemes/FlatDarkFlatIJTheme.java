package com.formdev.flatlaf.intellijthemes;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatDarkFlatIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Dark Flat";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatDarkFlatIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Dark Flat", FlatDarkFlatIJTheme.class);
  }
  
  public FlatDarkFlatIJTheme() {
    super(Utils.loadTheme("DarkFlatTheme.theme.json"));
  }

  
  public String getName() {
    return "Dark Flat";
  }
}
