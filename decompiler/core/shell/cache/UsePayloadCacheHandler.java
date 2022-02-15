package core.shell.cache;

import core.imp.Payload;
import core.shell.ShellEntity;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import util.Log;
import util.functions;
import util.http.ReqParameter;

public class UsePayloadCacheHandler
  extends PayloadCacheHandler
{
  public UsePayloadCacheHandler(ShellEntity entity, Payload payload) {
    super(entity, payload);
  }

  
  public byte[] evalFunc(byte[] realResult, String className, String funcName, ReqParameter parameter) {
    if (className == null && funcName != null) {
      try {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        String methodName = stack[3].getMethodName();
        if (Arrays.binarySearch((Object[])blackMethod, methodName) < 0) {
          if ("downloadFile".equals(methodName)) {
            synchronized (this.rc4) {
              byte[] arrayOfByte; File file = new File(this.currentDirectory + functions.byteArrayToHex(functions.md5(parameter.getParameterByteArray("fileName"))));
              
              try (FileInputStream fileInputStream = new FileInputStream(file)) {
                arrayOfByte = functions.gzipD(this.rc4.decryptMessage(functions.readInputStream(fileInputStream), this.shellId));
              } catch (Throwable e) {
                return "The cache file does not exist".getBytes();
              } 
              return (arrayOfByte == null) ? new byte[0] : arrayOfByte;
            } 
          }
          this.payload.fillParameter(className, funcName, parameter);
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          byteArrayOutputStream.write(funcName.getBytes());
          byteArrayOutputStream.write(parameter.formatEx());
          byte[] ret = this.cacheDb.getSetingValue(functions.byteArrayToHex(functions.md5(byteArrayOutputStream.toByteArray())));
          return (ret == null) ? "The operation has no cache".getBytes() : functions.gzipD(ret);
        }
      
      } catch (Exception e) {
        Log.error(e);
      } 
    }
    
    return "Payload does not cache the plugin return".getBytes();
  }
}
