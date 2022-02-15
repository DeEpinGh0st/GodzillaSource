package com.formdev.flatlaf.intellijthemes;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatGruvboxDarkSoftIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Gruvbox Dark Soft";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatGruvboxDarkSoftIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Gruvbox Dark Soft", FlatGruvboxDarkSoftIJTheme.class);
  }
  
  public FlatGruvboxDarkSoftIJTheme() {
    super(Utils.loadTheme("gruvbox_dark_soft.theme.json"));
  }

  
  public String getName() {
    return "Gruvbox Dark Soft";
  }
}
