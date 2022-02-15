package com.formdev.flatlaf.intellijthemes;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatGruvboxDarkMediumIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Gruvbox Dark Medium";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatGruvboxDarkMediumIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Gruvbox Dark Medium", FlatGruvboxDarkMediumIJTheme.class);
  }
  
  public FlatGruvboxDarkMediumIJTheme() {
    super(Utils.loadTheme("gruvbox_dark_medium.theme.json"));
  }

  
  public String getName() {
    return "Gruvbox Dark Medium";
  }
}
