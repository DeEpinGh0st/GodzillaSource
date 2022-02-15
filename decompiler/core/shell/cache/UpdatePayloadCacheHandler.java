package core.shell.cache;

import core.imp.Payload;
import core.shell.ShellEntity;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import util.functions;
import util.http.ReqParameter;



public class UpdatePayloadCacheHandler
  extends PayloadCacheHandler
{
  public UpdatePayloadCacheHandler(ShellEntity entity, Payload payload) {
    super(entity, payload);
  }


  
  public byte[] evalFunc(byte[] realResult, String className, String funcName, ReqParameter parameter) {
    if (className == null && funcName != null && realResult != null && realResult.length > 0) {
      try {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        String methodName = stack[3].getMethodName();
        if (Arrays.binarySearch((Object[])blackMethod, methodName) < 0) {
          if ("downloadFile".equals(methodName)) {
            synchronized (this.rc4) {
              File file = new File(this.currentDirectory + functions.byteArrayToHex(functions.md5(parameter.getParameterByteArray("fileName"))));
              try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                fileOutputStream.write(this.rc4.encryptMessage(functions.gzipE(realResult), this.shellId));
              } 
            } 
          } else {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.write(funcName.getBytes());
            byteArrayOutputStream.write(parameter.formatEx());
            this.cacheDb.updateSetingKV(functions.byteArrayToHex(functions.md5(byteArrayOutputStream.toByteArray())), functions.gzipE(realResult));
          } 
        }
      } catch (Exception e) {
        e.printStackTrace();
      } 
    }
    
    return realResult;
  }
}
