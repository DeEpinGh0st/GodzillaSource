package org.bouncycastle.operator.jcajce;

import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.ProviderException;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.AsymmetricKeyUnwrapper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;

public class JceAsymmetricKeyUnwrapper extends AsymmetricKeyUnwrapper {
  private OperatorHelper helper = new OperatorHelper((JcaJceHelper)new DefaultJcaJceHelper());
  
  private Map extraMappings = new HashMap<Object, Object>();
  
  private PrivateKey privKey;
  
  private boolean unwrappedKeyMustBeEncodable;
  
  public JceAsymmetricKeyUnwrapper(AlgorithmIdentifier paramAlgorithmIdentifier, PrivateKey paramPrivateKey) {
    super(paramAlgorithmIdentifier);
    this.privKey = paramPrivateKey;
  }
  
  public JceAsymmetricKeyUnwrapper setProvider(Provider paramProvider) {
    this.helper = new OperatorHelper((JcaJceHelper)new ProviderJcaJceHelper(paramProvider));
    return this;
  }
  
  public JceAsymmetricKeyUnwrapper setProvider(String paramString) {
    this.helper = new OperatorHelper((JcaJceHelper)new NamedJcaJceHelper(paramString));
    return this;
  }
  
  public JceAsymmetricKeyUnwrapper setMustProduceEncodableUnwrappedKey(boolean paramBoolean) {
    this.unwrappedKeyMustBeEncodable = paramBoolean;
    return this;
  }
  
  public JceAsymmetricKeyUnwrapper setAlgorithmMapping(ASN1ObjectIdentifier paramASN1ObjectIdentifier, String paramString) {
    this.extraMappings.put(paramASN1ObjectIdentifier, paramString);
    return this;
  }
  
  public GenericKey generateUnwrappedKey(AlgorithmIdentifier paramAlgorithmIdentifier, byte[] paramArrayOfbyte) throws OperatorException {
    try {
      Key key = null;
      Cipher cipher = this.helper.createAsymmetricWrapper(getAlgorithmIdentifier().getAlgorithm(), this.extraMappings);
      AlgorithmParameters algorithmParameters = this.helper.createAlgorithmParameters(getAlgorithmIdentifier());
      try {
        if (algorithmParameters != null) {
          cipher.init(4, this.privKey, algorithmParameters);
        } else {
          cipher.init(4, this.privKey);
        } 
        key = cipher.unwrap(paramArrayOfbyte, this.helper.getKeyAlgorithmName(paramAlgorithmIdentifier.getAlgorithm()), 3);
        if (this.unwrappedKeyMustBeEncodable)
          try {
            byte[] arrayOfByte = key.getEncoded();
            if (arrayOfByte == null || arrayOfByte.length == 0)
              key = null; 
          } catch (Exception exception) {
            key = null;
          }  
      } catch (GeneralSecurityException generalSecurityException) {
      
      } catch (IllegalStateException illegalStateException) {
      
      } catch (UnsupportedOperationException unsupportedOperationException) {
      
      } catch (ProviderException providerException) {}
      if (key == null) {
        cipher.init(2, this.privKey);
        key = new SecretKeySpec(cipher.doFinal(paramArrayOfbyte), paramAlgorithmIdentifier.getAlgorithm().getId());
      } 
      return new JceGenericKey(paramAlgorithmIdentifier, key);
    } catch (InvalidKeyException invalidKeyException) {
      throw new OperatorException("key invalid: " + invalidKeyException.getMessage(), invalidKeyException);
    } catch (IllegalBlockSizeException illegalBlockSizeException) {
      throw new OperatorException("illegal blocksize: " + illegalBlockSizeException.getMessage(), illegalBlockSizeException);
    } catch (BadPaddingException badPaddingException) {
      throw new OperatorException("bad padding: " + badPaddingException.getMessage(), badPaddingException);
    } 
  }
}
