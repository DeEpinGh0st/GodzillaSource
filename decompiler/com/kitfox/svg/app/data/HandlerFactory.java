package com.kitfox.svg.app.data;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;






































public class HandlerFactory
  implements URLStreamHandlerFactory
{
  static Handler handler = new Handler();

  
  public URLStreamHandler createURLStreamHandler(String protocol) {
    if ("data".equals(protocol))
    {
      return handler;
    }
    return null;
  }
}
