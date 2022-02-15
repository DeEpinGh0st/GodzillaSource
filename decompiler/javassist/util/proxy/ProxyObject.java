package javassist.util.proxy;

public interface ProxyObject extends Proxy {
  void setHandler(MethodHandler paramMethodHandler);
  
  MethodHandler getHandler();
}
