package org.bouncycastle.jcajce.provider.symmetric;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cms.GCMParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.ClassUtil;
import org.bouncycastle.util.Integers;

class GcmSpecUtil {
  static final Class gcmSpecClass = ClassUtil.loadClass(GcmSpecUtil.class, "javax.crypto.spec.GCMParameterSpec");
  
  static boolean gcmSpecExists() {
    return (gcmSpecClass != null);
  }
  
  static boolean isGcmSpec(AlgorithmParameterSpec paramAlgorithmParameterSpec) {
    return (gcmSpecClass != null && gcmSpecClass.isInstance(paramAlgorithmParameterSpec));
  }
  
  static boolean isGcmSpec(Class paramClass) {
    return (gcmSpecClass == paramClass);
  }
  
  static AlgorithmParameterSpec extractGcmSpec(ASN1Primitive paramASN1Primitive) throws InvalidParameterSpecException {
    try {
      GCMParameters gCMParameters = GCMParameters.getInstance(paramASN1Primitive);
      Constructor<AlgorithmParameterSpec> constructor = gcmSpecClass.getConstructor(new Class[] { int.class, byte[].class });
      return constructor.newInstance(new Object[] { Integers.valueOf(gCMParameters.getIcvLen() * 8), gCMParameters.getNonce() });
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new InvalidParameterSpecException("No constructor found!");
    } catch (Exception exception) {
      throw new InvalidParameterSpecException("Construction failed: " + exception.getMessage());
    } 
  }
  
  static GCMParameters extractGcmParameters(AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidParameterSpecException {
    try {
      Method method1 = gcmSpecClass.getDeclaredMethod("getTLen", new Class[0]);
      Method method2 = gcmSpecClass.getDeclaredMethod("getIV", new Class[0]);
      return new GCMParameters((byte[])method2.invoke(paramAlgorithmParameterSpec, new Object[0]), ((Integer)method1.invoke(paramAlgorithmParameterSpec, new Object[0])).intValue() / 8);
    } catch (Exception exception) {
      throw new InvalidParameterSpecException("Cannot process GCMParameterSpec");
    } 
  }
}
