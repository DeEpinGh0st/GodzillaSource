package org.apache.log4j.net;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import org.apache.log4j.helpers.LogLog;

















public class ZeroConfSupport
{
  private static Object jmDNS = initializeJMDNS();
  
  Object serviceInfo;
  
  private static Class jmDNSClass;
  private static Class serviceInfoClass;
  
  public ZeroConfSupport(String zone, int port, String name, Map properties) {
    boolean isVersion3 = false;
    
    try {
      jmDNSClass.getMethod("create", null);
      isVersion3 = true;
    } catch (NoSuchMethodException e) {}


    
    if (isVersion3) {
      LogLog.debug("using JmDNS version 3 to construct serviceInfo instance");
      this.serviceInfo = buildServiceInfoVersion3(zone, port, name, properties);
    } else {
      LogLog.debug("using JmDNS version 1.0 to construct serviceInfo instance");
      this.serviceInfo = buildServiceInfoVersion1(zone, port, name, properties);
    } 
  }
  
  public ZeroConfSupport(String zone, int port, String name) {
    this(zone, port, name, new HashMap());
  }

  
  private static Object createJmDNSVersion1() {
    try {
      return jmDNSClass.newInstance();
    } catch (InstantiationException e) {
      LogLog.warn("Unable to instantiate JMDNS", e);
    } catch (IllegalAccessException e) {
      LogLog.warn("Unable to instantiate JMDNS", e);
    } 
    return null;
  }

  
  private static Object createJmDNSVersion3() {
    try {
      Method jmDNSCreateMethod = jmDNSClass.getMethod("create", null);
      return jmDNSCreateMethod.invoke(null, null);
    } catch (IllegalAccessException e) {
      LogLog.warn("Unable to instantiate jmdns class", e);
    } catch (NoSuchMethodException e) {
      LogLog.warn("Unable to access constructor", e);
    } catch (InvocationTargetException e) {
      LogLog.warn("Unable to call constructor", e);
    } 
    return null;
  }

  
  private Object buildServiceInfoVersion1(String zone, int port, String name, Map properties) {
    Hashtable hashtableProperties = new Hashtable(properties);
    try {
      Class[] args = new Class[6];
      args[0] = String.class;
      args[1] = String.class;
      args[2] = int.class;
      args[3] = int.class;
      args[4] = int.class;
      args[5] = Hashtable.class;
      Constructor constructor = serviceInfoClass.getConstructor(args);
      Object[] values = new Object[6];
      values[0] = zone;
      values[1] = name;
      values[2] = new Integer(port);
      values[3] = new Integer(0);
      values[4] = new Integer(0);
      values[5] = hashtableProperties;
      Object result = constructor.newInstance(values);
      LogLog.debug("created serviceinfo: " + result);
      return result;
    } catch (IllegalAccessException e) {
      LogLog.warn("Unable to construct ServiceInfo instance", e);
    } catch (NoSuchMethodException e) {
      LogLog.warn("Unable to get ServiceInfo constructor", e);
    } catch (InstantiationException e) {
      LogLog.warn("Unable to construct ServiceInfo instance", e);
    } catch (InvocationTargetException e) {
      LogLog.warn("Unable to construct ServiceInfo instance", e);
    } 
    return null;
  }
  
  private Object buildServiceInfoVersion3(String zone, int port, String name, Map properties) {
    try {
      Class[] args = new Class[6];
      args[0] = String.class;
      args[1] = String.class;
      args[2] = int.class;
      args[3] = int.class;
      args[4] = int.class;
      args[5] = Map.class;
      Method serviceInfoCreateMethod = serviceInfoClass.getMethod("create", args);
      Object[] values = new Object[6];
      values[0] = zone;
      values[1] = name;
      values[2] = new Integer(port);
      values[3] = new Integer(0);
      values[4] = new Integer(0);
      values[5] = properties;
      Object result = serviceInfoCreateMethod.invoke(null, values);
      LogLog.debug("created serviceinfo: " + result);
      return result;
    } catch (IllegalAccessException e) {
      LogLog.warn("Unable to invoke create method", e);
    } catch (NoSuchMethodException e) {
      LogLog.warn("Unable to find create method", e);
    } catch (InvocationTargetException e) {
      LogLog.warn("Unable to invoke create method", e);
    } 
    return null;
  }
  
  public void advertise() {
    try {
      Method method = jmDNSClass.getMethod("registerService", new Class[] { serviceInfoClass });
      method.invoke(jmDNS, new Object[] { this.serviceInfo });
      LogLog.debug("registered serviceInfo: " + this.serviceInfo);
    } catch (IllegalAccessException e) {
      LogLog.warn("Unable to invoke registerService method", e);
    } catch (NoSuchMethodException e) {
      LogLog.warn("No registerService method", e);
    } catch (InvocationTargetException e) {
      LogLog.warn("Unable to invoke registerService method", e);
    } 
  }
  
  public void unadvertise() {
    try {
      Method method = jmDNSClass.getMethod("unregisterService", new Class[] { serviceInfoClass });
      method.invoke(jmDNS, new Object[] { this.serviceInfo });
      LogLog.debug("unregistered serviceInfo: " + this.serviceInfo);
    } catch (IllegalAccessException e) {
      LogLog.warn("Unable to invoke unregisterService method", e);
    } catch (NoSuchMethodException e) {
      LogLog.warn("No unregisterService method", e);
    } catch (InvocationTargetException e) {
      LogLog.warn("Unable to invoke unregisterService method", e);
    } 
  }
  
  private static Object initializeJMDNS() {
    try {
      jmDNSClass = Class.forName("javax.jmdns.JmDNS");
      serviceInfoClass = Class.forName("javax.jmdns.ServiceInfo");
    } catch (ClassNotFoundException e) {
      LogLog.warn("JmDNS or serviceInfo class not found", e);
    } 

    
    boolean isVersion3 = false;
    
    try {
      jmDNSClass.getMethod("create", null);
      isVersion3 = true;
    } catch (NoSuchMethodException e) {}


    
    if (isVersion3) {
      return createJmDNSVersion3();
    }
    return createJmDNSVersion1();
  }

  
  public static Object getJMDNSInstance() {
    return jmDNS;
  }
}
