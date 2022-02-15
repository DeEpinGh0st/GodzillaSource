package shells.plugins.java;

import core.annotation.PluginAnnotation;
import java.io.InputStream;
import shells.plugins.generic.RealCmd;
import util.Log;
import util.functions;

























@PluginAnnotation(payloadName = "JavaDynamicPayload", Name = "RealCmd", DisplayName = "虚拟终端")
public class JRealCmd
  extends RealCmd
{
  private static final String CLASS_NAME = "plugin.RealCmd";
  
  public byte[] readPlugin() {
    byte[] data = null;
    try {
      InputStream inputStream = getClass().getResourceAsStream("assets/RealCmd.classs");
      data = functions.readInputStream(inputStream);
      inputStream.close();
    } catch (Exception e) {
      Log.error(e);
    } 
    return data;
  }

  
  public String getClassName() {
    return "plugin.RealCmd";
  }
}
