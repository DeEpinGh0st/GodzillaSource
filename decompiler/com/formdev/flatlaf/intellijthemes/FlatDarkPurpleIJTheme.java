package com.formdev.flatlaf.intellijthemes;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatDarkPurpleIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Dark purple";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatDarkPurpleIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Dark purple", FlatDarkPurpleIJTheme.class);
  }
  
  public FlatDarkPurpleIJTheme() {
    super(Utils.loadTheme("DarkPurple.theme.json"));
  }

  
  public String getName() {
    return "Dark purple";
  }
}
