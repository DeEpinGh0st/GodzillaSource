package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatGitHubIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "GitHub (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatGitHubIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("GitHub (Material)", FlatGitHubIJTheme.class);
  }
  
  public FlatGitHubIJTheme() {
    super(Utils.loadTheme("GitHub.theme.json"));
  }

  
  public String getName() {
    return "GitHub (Material)";
  }
}
