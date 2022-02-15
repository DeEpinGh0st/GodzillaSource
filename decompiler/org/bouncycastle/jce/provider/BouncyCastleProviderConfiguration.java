package org.bouncycastle.jce.provider;

import java.security.Permission;
import java.security.spec.ECParameterSpec;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.crypto.spec.DHParameterSpec;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jcajce.provider.config.ProviderConfigurationPermission;
import org.bouncycastle.jce.spec.ECParameterSpec;

class BouncyCastleProviderConfiguration implements ProviderConfiguration {
  private static Permission BC_EC_LOCAL_PERMISSION = (Permission)new ProviderConfigurationPermission("BC", "threadLocalEcImplicitlyCa");
  
  private static Permission BC_EC_PERMISSION = (Permission)new ProviderConfigurationPermission("BC", "ecImplicitlyCa");
  
  private static Permission BC_DH_LOCAL_PERMISSION = (Permission)new ProviderConfigurationPermission("BC", "threadLocalDhDefaultParams");
  
  private static Permission BC_DH_PERMISSION = (Permission)new ProviderConfigurationPermission("BC", "DhDefaultParams");
  
  private static Permission BC_EC_CURVE_PERMISSION = (Permission)new ProviderConfigurationPermission("BC", "acceptableEcCurves");
  
  private static Permission BC_ADDITIONAL_EC_CURVE_PERMISSION = (Permission)new ProviderConfigurationPermission("BC", "additionalEcParameters");
  
  private ThreadLocal ecThreadSpec = new ThreadLocal();
  
  private ThreadLocal dhThreadSpec = new ThreadLocal();
  
  private volatile ECParameterSpec ecImplicitCaParams;
  
  private volatile Object dhDefaultParams;
  
  private volatile Set acceptableNamedCurves = new HashSet();
  
  private volatile Map additionalECParameters = new HashMap<Object, Object>();
  
  void setParameter(String paramString, Object paramObject) {
    SecurityManager securityManager = System.getSecurityManager();
    if (paramString.equals("threadLocalEcImplicitlyCa")) {
      ECParameterSpec eCParameterSpec;
      if (securityManager != null)
        securityManager.checkPermission(BC_EC_LOCAL_PERMISSION); 
      if (paramObject instanceof ECParameterSpec || paramObject == null) {
        eCParameterSpec = (ECParameterSpec)paramObject;
      } else {
        eCParameterSpec = EC5Util.convertSpec((ECParameterSpec)paramObject, false);
      } 
      if (eCParameterSpec == null) {
        this.ecThreadSpec.remove();
      } else {
        this.ecThreadSpec.set(eCParameterSpec);
      } 
    } else if (paramString.equals("ecImplicitlyCa")) {
      if (securityManager != null)
        securityManager.checkPermission(BC_EC_PERMISSION); 
      if (paramObject instanceof ECParameterSpec || paramObject == null) {
        this.ecImplicitCaParams = (ECParameterSpec)paramObject;
      } else {
        this.ecImplicitCaParams = EC5Util.convertSpec((ECParameterSpec)paramObject, false);
      } 
    } else if (paramString.equals("threadLocalDhDefaultParams")) {
      Object object;
      if (securityManager != null)
        securityManager.checkPermission(BC_DH_LOCAL_PERMISSION); 
      if (paramObject instanceof DHParameterSpec || paramObject instanceof DHParameterSpec[] || paramObject == null) {
        object = paramObject;
      } else {
        throw new IllegalArgumentException("not a valid DHParameterSpec");
      } 
      if (object == null) {
        this.dhThreadSpec.remove();
      } else {
        this.dhThreadSpec.set(object);
      } 
    } else if (paramString.equals("DhDefaultParams")) {
      if (securityManager != null)
        securityManager.checkPermission(BC_DH_PERMISSION); 
      if (paramObject instanceof DHParameterSpec || paramObject instanceof DHParameterSpec[] || paramObject == null) {
        this.dhDefaultParams = paramObject;
      } else {
        throw new IllegalArgumentException("not a valid DHParameterSpec or DHParameterSpec[]");
      } 
    } else if (paramString.equals("acceptableEcCurves")) {
      if (securityManager != null)
        securityManager.checkPermission(BC_EC_CURVE_PERMISSION); 
      this.acceptableNamedCurves = (Set)paramObject;
    } else if (paramString.equals("additionalEcParameters")) {
      if (securityManager != null)
        securityManager.checkPermission(BC_ADDITIONAL_EC_CURVE_PERMISSION); 
      this.additionalECParameters = (Map)paramObject;
    } 
  }
  
  public ECParameterSpec getEcImplicitlyCa() {
    ECParameterSpec eCParameterSpec = this.ecThreadSpec.get();
    return (eCParameterSpec != null) ? eCParameterSpec : this.ecImplicitCaParams;
  }
  
  public DHParameterSpec getDHDefaultParameters(int paramInt) {
    Object object = this.dhThreadSpec.get();
    if (object == null)
      object = this.dhDefaultParams; 
    if (object instanceof DHParameterSpec) {
      DHParameterSpec dHParameterSpec = (DHParameterSpec)object;
      if (dHParameterSpec.getP().bitLength() == paramInt)
        return dHParameterSpec; 
    } else if (object instanceof DHParameterSpec[]) {
      DHParameterSpec[] arrayOfDHParameterSpec = (DHParameterSpec[])object;
      for (byte b = 0; b != arrayOfDHParameterSpec.length; b++) {
        if (arrayOfDHParameterSpec[b].getP().bitLength() == paramInt)
          return arrayOfDHParameterSpec[b]; 
      } 
    } 
    return null;
  }
  
  public Set getAcceptableNamedCurves() {
    return Collections.unmodifiableSet(this.acceptableNamedCurves);
  }
  
  public Map getAdditionalECParameters() {
    return Collections.unmodifiableMap(this.additionalECParameters);
  }
}
