package core.c2profile;






























public class C2Generate
{
  public String encryptMethodName = "";
  public String decryptMethodName = "";
  public String customGenerateContent = "";
  
  public boolean aggregation = false;
  
  public boolean enabledCustomGenerateContent = false;
  public byte[] afterGenerate = "".getBytes();
  public boolean enabledCustomEncryptMethodName = false;
  public boolean enabledCustomDecryptMethodName = false;
  public boolean enabledStaticPayload;
}
