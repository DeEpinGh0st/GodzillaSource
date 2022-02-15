package com.formdev.flatlaf.intellijthemes;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatGrayIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Gray";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatGrayIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Gray", FlatGrayIJTheme.class);
  }
  
  public FlatGrayIJTheme() {
    super(Utils.loadTheme("Gray.theme.json"));
  }

  
  public String getName() {
    return "Gray";
  }
}
