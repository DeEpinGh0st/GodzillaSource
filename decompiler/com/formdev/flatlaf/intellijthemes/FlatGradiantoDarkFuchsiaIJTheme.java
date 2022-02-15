package com.formdev.flatlaf.intellijthemes;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatGradiantoDarkFuchsiaIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Gradianto Dark Fuchsia";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatGradiantoDarkFuchsiaIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Gradianto Dark Fuchsia", FlatGradiantoDarkFuchsiaIJTheme.class);
  }
  
  public FlatGradiantoDarkFuchsiaIJTheme() {
    super(Utils.loadTheme("Gradianto_dark_fuchsia.theme.json"));
  }

  
  public String getName() {
    return "Gradianto Dark Fuchsia";
  }
}
