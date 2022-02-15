package com.formdev.flatlaf.intellijthemes;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatSpacegrayIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Spacegray";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatSpacegrayIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Spacegray", FlatSpacegrayIJTheme.class);
  }
  
  public FlatSpacegrayIJTheme() {
    super(Utils.loadTheme("Spacegray.theme.json"));
  }

  
  public String getName() {
    return "Spacegray";
  }
}
