package org.springframework.cglib.proxy;

import org.springframework.asm.Type;












class CallbackInfo
{
  private Class cls;
  private CallbackGenerator generator;
  private Type type;
  
  public static Type[] determineTypes(Class[] callbackTypes) {
    return determineTypes(callbackTypes, true);
  }
  
  public static Type[] determineTypes(Class[] callbackTypes, boolean checkAll) {
    Type[] types = new Type[callbackTypes.length];
    for (int i = 0; i < types.length; i++) {
      types[i] = determineType(callbackTypes[i], checkAll);
    }
    return types;
  }
  
  public static Type[] determineTypes(Callback[] callbacks) {
    return determineTypes(callbacks, true);
  }
  
  public static Type[] determineTypes(Callback[] callbacks, boolean checkAll) {
    Type[] types = new Type[callbacks.length];
    for (int i = 0; i < types.length; i++) {
      types[i] = determineType(callbacks[i], checkAll);
    }
    return types;
  }
  
  public static CallbackGenerator[] getGenerators(Type[] callbackTypes) {
    CallbackGenerator[] generators = new CallbackGenerator[callbackTypes.length];
    for (int i = 0; i < generators.length; i++) {
      generators[i] = getGenerator(callbackTypes[i]);
    }
    return generators;
  }






  
  private static final CallbackInfo[] CALLBACKS = new CallbackInfo[] { new CallbackInfo(NoOp.class, NoOpGenerator.INSTANCE), new CallbackInfo(MethodInterceptor.class, MethodInterceptorGenerator.INSTANCE), new CallbackInfo(InvocationHandler.class, InvocationHandlerGenerator.INSTANCE), new CallbackInfo(LazyLoader.class, LazyLoaderGenerator.INSTANCE), new CallbackInfo(Dispatcher.class, DispatcherGenerator.INSTANCE), new CallbackInfo(FixedValue.class, FixedValueGenerator.INSTANCE), new CallbackInfo(ProxyRefDispatcher.class, DispatcherGenerator.PROXY_REF_INSTANCE) };








  
  private CallbackInfo(Class cls, CallbackGenerator generator) {
    this.cls = cls;
    this.generator = generator;
    this.type = Type.getType(cls);
  }
  
  private static Type determineType(Callback callback, boolean checkAll) {
    if (callback == null) {
      throw new IllegalStateException("Callback is null");
    }
    return determineType(callback.getClass(), checkAll);
  }
  
  private static Type determineType(Class<?> callbackType, boolean checkAll) {
    Class cur = null;
    Type type = null;
    for (int i = 0; i < CALLBACKS.length; i++) {
      CallbackInfo info = CALLBACKS[i];
      if (info.cls.isAssignableFrom(callbackType)) {
        if (cur != null) {
          throw new IllegalStateException("Callback implements both " + cur + " and " + info.cls);
        }
        cur = info.cls;
        type = info.type;
        if (!checkAll) {
          break;
        }
      } 
    } 
    if (cur == null) {
      throw new IllegalStateException("Unknown callback type " + callbackType);
    }
    return type;
  }
  
  private static CallbackGenerator getGenerator(Type callbackType) {
    for (int i = 0; i < CALLBACKS.length; i++) {
      CallbackInfo info = CALLBACKS[i];
      if (info.type.equals(callbackType)) {
        return info.generator;
      }
    } 
    throw new IllegalStateException("Unknown callback type " + callbackType);
  }
}
