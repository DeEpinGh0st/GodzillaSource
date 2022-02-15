package core.shell.cache;

import core.ApplicationContext;
import core.CoreClassLoader;
import core.imp.Payload;
import core.shell.ShellEntity;
import java.lang.reflect.Field;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import util.functions;
import util.http.ReqParameter;


public class CachePayload
{
  public static final String OPEN_CACHE_PAYLOAD = "_Cache";
  public static final String OPEN_USE_CACHE_PAYLOAD = "_Cache2";
  public static final String HANDLER_FIELD = "usePayloadCacheHandler" + System.currentTimeMillis();
  public static final String DirectoryName = "GodzillaCache";
  
  public static Payload openCachePayload(ShellEntity entity, Class<Payload> payloadType) {
    String className = payloadType.getName() + "_Cache";
    
    Class payloadClass = functions.loadClass((ClassLoader)ApplicationContext.PLUGIN_CLASSLOADER, className);
    if (payloadClass == null) {
      try {
        CtClass ctClass = classToCtClass(payloadType);
        CtMethod ctMethod = ctClass.getDeclaredMethod("evalFunc", new CtClass[] { classToCtClass(String.class), classToCtClass(String.class), classToCtClass(ReqParameter.class) });
        CtField ctField = new CtField(classToCtClass(PayloadCacheHandler.class), HANDLER_FIELD, ctClass);
        ctClass.addField(ctField);
        ctMethod.insertAfter(String.format("return this.%s.evalFunc($_,$1,$2,$3);", new Object[] { HANDLER_FIELD }));
        ctClass.setName(className);
        payloadClass = CoreClassLoader.defineClass3(ctClass.getName(), ctClass.toBytecode(), null);
        ctClass.detach();
      } catch (Exception e) {
        e.printStackTrace();
      } 
    }
    try {
      if (payloadClass == null) {
        return payloadType.newInstance();
      }
      Object payload = payloadClass.newInstance();
      Field handlerField = payloadClass.getDeclaredField(HANDLER_FIELD);
      handlerField.setAccessible(true);
      handlerField.set(payload, new UpdatePayloadCacheHandler(entity, (Payload)payload));
      return (Payload)payload;
    }
    catch (Throwable e) {
      e.printStackTrace();

      
      return null;
    } 
  }
  public static Payload openUseCachePayload(ShellEntity entity, Class<Payload> payloadType) {
    String className = payloadType.getName() + "_Cache2";
    
    Class payloadClass = functions.loadClass((ClassLoader)ApplicationContext.PLUGIN_CLASSLOADER, className);
    if (payloadClass == null) {
      try {
        CtClass ctClass = classToCtClass(payloadType);
        CtMethod ctMethod = ctClass.getDeclaredMethod("evalFunc", new CtClass[] { classToCtClass(String.class), classToCtClass(String.class), classToCtClass(ReqParameter.class) });
        CtField ctField = new CtField(classToCtClass(PayloadCacheHandler.class), HANDLER_FIELD, ctClass);
        ctClass.addField(ctField);
        ctMethod.insertBefore(String.format("return this.%s.evalFunc(null,$1,$2,$3);", new Object[] { HANDLER_FIELD }));
        ctClass.setName(className);
        payloadClass = CoreClassLoader.defineClass3(ctClass.getName(), ctClass.toBytecode(), null);
        ctClass.detach();
      } catch (Exception e) {
        e.printStackTrace();
      } 
    }
    try {
      if (payloadClass == null) {
        return payloadType.newInstance();
      }
      Object payload = payloadClass.newInstance();
      Field handlerField = payloadClass.getDeclaredField(HANDLER_FIELD);
      handlerField.setAccessible(true);
      handlerField.set(payload, new UsePayloadCacheHandler(entity, (Payload)payload));
      return (Payload)payload;
    }
    catch (Throwable e) {
      e.printStackTrace();

      
      return null;
    } 
  } private static CtClass classToCtClass(Class cs) {
    CtClass ctClass = null;
    try {
      ctClass = ClassPool.getDefault().get(cs.getName());
    } catch (Throwable e) {
      e.printStackTrace();
    } 
    return ctClass;
  }
}
