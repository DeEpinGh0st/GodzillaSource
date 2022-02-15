package org.bouncycastle.jcajce.provider.symmetric.util;

import java.lang.reflect.Constructor;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactorySpi;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class BaseSecretKeyFactory extends SecretKeyFactorySpi implements PBE {
  protected String algName;
  
  protected ASN1ObjectIdentifier algOid;
  
  protected BaseSecretKeyFactory(String paramString, ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    this.algName = paramString;
    this.algOid = paramASN1ObjectIdentifier;
  }
  
  protected SecretKey engineGenerateSecret(KeySpec paramKeySpec) throws InvalidKeySpecException {
    if (paramKeySpec instanceof SecretKeySpec)
      return new SecretKeySpec(((SecretKeySpec)paramKeySpec).getEncoded(), this.algName); 
    throw new InvalidKeySpecException("Invalid KeySpec");
  }
  
  protected KeySpec engineGetKeySpec(SecretKey paramSecretKey, Class<?> paramClass) throws InvalidKeySpecException {
    if (paramClass == null)
      throw new InvalidKeySpecException("keySpec parameter is null"); 
    if (paramSecretKey == null)
      throw new InvalidKeySpecException("key parameter is null"); 
    if (SecretKeySpec.class.isAssignableFrom(paramClass))
      return new SecretKeySpec(paramSecretKey.getEncoded(), this.algName); 
    try {
      Class[] arrayOfClass = { byte[].class };
      Constructor<?> constructor = paramClass.getConstructor(arrayOfClass);
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramSecretKey.getEncoded();
      return (KeySpec)constructor.newInstance(arrayOfObject);
    } catch (Exception exception) {
      throw new InvalidKeySpecException(exception.toString());
    } 
  }
  
  protected SecretKey engineTranslateKey(SecretKey paramSecretKey) throws InvalidKeyException {
    if (paramSecretKey == null)
      throw new InvalidKeyException("key parameter is null"); 
    if (!paramSecretKey.getAlgorithm().equalsIgnoreCase(this.algName))
      throw new InvalidKeyException("Key not of type " + this.algName + "."); 
    return new SecretKeySpec(paramSecretKey.getEncoded(), this.algName);
  }
}
