package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatGitHubContrastIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "GitHub Contrast (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatGitHubContrastIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("GitHub Contrast (Material)", FlatGitHubContrastIJTheme.class);
  }
  
  public FlatGitHubContrastIJTheme() {
    super(Utils.loadTheme("GitHub Contrast.theme.json"));
  }

  
  public String getName() {
    return "GitHub Contrast (Material)";
  }
}
