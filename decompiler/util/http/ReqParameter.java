package util.http;

import core.ui.component.dialog.ShellSuperRequest;
import java.util.Iterator;
import util.functions;

















public class ReqParameter
  extends Parameter
{
  public String format() {
    String randomRP = ShellSuperRequest.randomReqParameter();
    if (randomRP != null && randomRP.length() > 1) {
      add(functions.getRandomString(5), randomRP);
    }

    
    Iterator<String> keys = this.hashMap.keySet().iterator();
    StringBuffer buffer = new StringBuffer();


    
    while (keys.hasNext()) {
      String key = keys.next();
      buffer.append(key);
      buffer.append("=");
      Object valueObject = this.hashMap.get(key);
      if (valueObject.getClass().isAssignableFrom(byte[].class)) {
        buffer.append(functions.base64EncodeToString((byte[])valueObject));
      } else {
        buffer.append(functions.base64EncodeToString(((String)valueObject).getBytes()));
      } 
      buffer.append("&");
    } 
    String temString = buffer.delete(buffer.length() - 1, buffer.length()).toString();
    return temString;
  }
  
  public byte[] formatEx() {
    return serialize();
  }
}
