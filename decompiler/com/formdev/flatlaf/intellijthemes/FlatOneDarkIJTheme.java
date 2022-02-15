package com.formdev.flatlaf.intellijthemes;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatOneDarkIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "One Dark";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatOneDarkIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("One Dark", FlatOneDarkIJTheme.class);
  }
  
  public FlatOneDarkIJTheme() {
    super(Utils.loadTheme("one_dark.theme.json"));
  }

  
  public String getName() {
    return "One Dark";
  }
}
