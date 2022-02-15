package com.formdev.flatlaf.intellijthemes;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatGruvboxDarkHardIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Gruvbox Dark Hard";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatGruvboxDarkHardIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Gruvbox Dark Hard", FlatGruvboxDarkHardIJTheme.class);
  }
  
  public FlatGruvboxDarkHardIJTheme() {
    super(Utils.loadTheme("gruvbox_dark_hard.theme.json"));
  }

  
  public String getName() {
    return "Gruvbox Dark Hard";
  }
}
