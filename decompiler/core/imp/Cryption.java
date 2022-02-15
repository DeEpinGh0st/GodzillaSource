package core.imp;

import core.shell.ShellEntity;

public interface Cryption {
  void init(ShellEntity paramShellEntity);
  
  byte[] encode(byte[] paramArrayOfbyte);
  
  byte[] decode(byte[] paramArrayOfbyte);
  
  boolean isSendRLData();
  
  byte[] generate(String paramString1, String paramString2);
  
  boolean check();
}
