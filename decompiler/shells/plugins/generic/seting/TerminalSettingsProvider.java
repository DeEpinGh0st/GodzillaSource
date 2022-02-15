package shells.plugins.generic.seting;

import com.jediterm.terminal.TerminalColor;
import com.jediterm.terminal.TextStyle;
import com.jediterm.terminal.emulator.ColorPalette;
import com.jediterm.terminal.emulator.ColorPaletteImpl;
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider;
import core.Db;
import core.ui.MainActivity;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.yaml.snakeyaml.Yaml;
import util.Log;
import util.UiFunction;

public class TerminalSettingsProvider
  extends DefaultSettingsProvider
{
  public static final String FONT_NAME_KEY = "Terminal-FontName";
  public static final String FONT_SIZE_KEY = "Terminal-FontSize";
  public static final String FONT_TYPE_KEY = "Terminal-FontType";
  public static final String TERMINAL_STYLE_KEY = "Terminal-FontStyle";
  private static final ArrayList<String> TERMINAL_STYLES = new ArrayList<>();
  
  private static final String STYLE_ZIP = "assets/alacritty.zip";
  
  private TextStyle defaultStyle;
  
  private TextStyle selectionStyle;
  
  static {
    try {
      ZipInputStream zipInputStream = new ZipInputStream(TerminalSettingsProvider.class.getResourceAsStream("assets/alacritty.zip"));
      
      ZipEntry zipEntry = null;
      
      while ((zipEntry = zipInputStream.getNextEntry()) != null) {
        String zipEntryName = zipEntry.getName();
        if (zipEntryName.endsWith(".yml")) {
          TERMINAL_STYLES.add(zipEntry.getName().replace(".yml", ""));
        }
        zipInputStream.closeEntry();
      } 
      zipInputStream.close();
    } catch (Exception e) {
      Log.error(e);
    } 
  }

  
  public TerminalSettingsProvider() {
    this(getTerminalStyle());
  }
  
  public TerminalSettingsProvider(String styleName) {
    formatStyle(styleName);
  }
  
  public void formatStyle(String styleName) {
    try (ZipInputStream zipInputStream = new ZipInputStream(TerminalSettingsProvider.class.getResourceAsStream("assets/alacritty.zip"))) {
      
      ZipEntry zipEntry = null;
      
      while ((zipEntry = zipInputStream.getNextEntry()) != null) {
        String zipEntryName = zipEntry.getName();
        if (zipEntryName.endsWith(".yml")) {
          zipEntryName = zipEntry.getName().replace(".yml", "");
          if (styleName.equals(zipEntryName)) {
            try {
              Yaml yaml = new Yaml();
              
              HashMap map = (HashMap)yaml.loadAs(zipInputStream, HashMap.class);
              
              HashMap colors = (HashMap)map.get("colors");
              
              HashMap cursorColor = (HashMap)colors.get("cursor");
              HashMap selectionColor = (HashMap)colors.get("selection");
              HashMap primaryColor = (HashMap)colors.get("primary");
              this
                .defaultStyle = new TextStyle(TerminalColor.awt(Color.decode(primaryColor.get("foreground").toString())), TerminalColor.awt(Color.decode(primaryColor.get("background").toString())));
              this
                .selectionStyle = new TextStyle(TerminalColor.awt(Color.decode(selectionColor.get("text").toString())), TerminalColor.awt(Color.decode(selectionColor.get("background").toString())));
            } catch (Exception e) {
              Log.error(e);
            } 
          }
        } 
        zipInputStream.closeEntry();
      }
    
    } catch (Exception e) {
      Log.error(e);
    } 
  }

  
  public TextStyle getDefaultStyle() {
    return (this.defaultStyle != null) ? this.defaultStyle : super.getDefaultStyle();
  }

  
  public TextStyle getSelectionColor() {
    return this.selectionStyle;
  }

  
  public TextStyle getFoundPatternColor() {
    return getSelectionColor();
  }

  
  public ColorPalette getTerminalColorPalette() {
    return ColorPaletteImpl.XTERM_PALETTE;
  }

  
  public Font getTerminalFont() {
    try {
      String fontName = null;
      if ((fontName = getFontName()) != null) {
        Font font = new Font(fontName, UiFunction.getFontType(getFontType()), (int)getTerminalFontSize());
        if (font != null) {
          return font;
        }
      } else {
        Font font = MainActivity.getMainActivityFrame().getGraphics().getFont();
        if (font != null) {
          return font;
        }
      } 
    } catch (Exception e) {
      Log.error(e);
    } 
    return super.getTerminalFont();
  }

  
  public float getTerminalFontSize() {
    return getFontSize();
  }
  
  public static String getFontName() {
    return Db.getSetingValue("Terminal-FontName");
  }
  
  public static int getFontSize() {
    return Db.getSetingIntValue("Terminal-FontSize", 14);
  }
  public static String getFontType() {
    return Db.getSetingValue("Terminal-FontType", "PLAIN");
  }
  public static String getTerminalStyle() {
    return Db.getSetingValue("Terminal-FontStyle", "hack");
  }
  
  public static String[] getTerminalStyles() {
    return TERMINAL_STYLES.<String>toArray(new String[0]);
  }
}
