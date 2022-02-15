package core;

import core.shell.ShellEntity;
import java.net.InetSocketAddress;
import java.net.Proxy;


public class ProxyT
{
  private static final String[] PTOXY_TYPES = new String[] { "NO_PROXY", "HTTP", "SOCKS", "GLOBAL_PROXY" };


  
  public static Proxy getProxy(ShellEntity context) {
    try {
      String type = context.getProxyType();
      InetSocketAddress inetSocketAddress = new InetSocketAddress(context.getProxyHost(), context.getProxyPort());
      if ("SOCKS".equalsIgnoreCase(type))
        return new Proxy(Proxy.Type.SOCKS, inetSocketAddress); 
      if ("HTTP".equalsIgnoreCase(type))
        return new Proxy(Proxy.Type.HTTP, inetSocketAddress); 
      if ("GLOBAL_PROXY".equalsIgnoreCase(type)) {
        inetSocketAddress = new InetSocketAddress(Db.tryGetSetingValue("globalProxyHost", "127.0.0.1"), Integer.parseInt(Db.tryGetSetingValue("globalProxyPort", "8888")));
        type = Db.tryGetSetingValue("globalProxyType", "NO_PROXY");
        if ("SOCKS".equalsIgnoreCase(type))
          return new Proxy(Proxy.Type.SOCKS, inetSocketAddress); 
        if ("HTTP".equalsIgnoreCase(type)) {
          return new Proxy(Proxy.Type.HTTP, inetSocketAddress);
        }
      } else {
        return Proxy.NO_PROXY;
      } 
    } catch (Exception e) {
      return Proxy.NO_PROXY;
    } 
    return Proxy.NO_PROXY;
  }
  public static String[] getAllProxyType() {
    return PTOXY_TYPES;
  }
}
