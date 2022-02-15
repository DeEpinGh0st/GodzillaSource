package org.bouncycastle.operator.jcajce;

import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Provider;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAESOAEPparams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.AsymmetricKeyWrapper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;

public class JceAsymmetricKeyWrapper extends AsymmetricKeyWrapper {
  private OperatorHelper helper = new OperatorHelper((JcaJceHelper)new DefaultJcaJceHelper());
  
  private Map extraMappings = new HashMap<Object, Object>();
  
  private PublicKey publicKey;
  
  private SecureRandom random;
  
  private static final Map digests = new HashMap<Object, Object>();
  
  public JceAsymmetricKeyWrapper(PublicKey paramPublicKey) {
    super(SubjectPublicKeyInfo.getInstance(paramPublicKey.getEncoded()).getAlgorithm());
    this.publicKey = paramPublicKey;
  }
  
  public JceAsymmetricKeyWrapper(X509Certificate paramX509Certificate) {
    this(paramX509Certificate.getPublicKey());
  }
  
  public JceAsymmetricKeyWrapper(AlgorithmIdentifier paramAlgorithmIdentifier, PublicKey paramPublicKey) {
    super(paramAlgorithmIdentifier);
    this.publicKey = paramPublicKey;
  }
  
  public JceAsymmetricKeyWrapper(AlgorithmParameterSpec paramAlgorithmParameterSpec, PublicKey paramPublicKey) {
    super(extractFromSpec(paramAlgorithmParameterSpec));
    this.publicKey = paramPublicKey;
  }
  
  public JceAsymmetricKeyWrapper setProvider(Provider paramProvider) {
    this.helper = new OperatorHelper((JcaJceHelper)new ProviderJcaJceHelper(paramProvider));
    return this;
  }
  
  public JceAsymmetricKeyWrapper setProvider(String paramString) {
    this.helper = new OperatorHelper((JcaJceHelper)new NamedJcaJceHelper(paramString));
    return this;
  }
  
  public JceAsymmetricKeyWrapper setSecureRandom(SecureRandom paramSecureRandom) {
    this.random = paramSecureRandom;
    return this;
  }
  
  public JceAsymmetricKeyWrapper setAlgorithmMapping(ASN1ObjectIdentifier paramASN1ObjectIdentifier, String paramString) {
    this.extraMappings.put(paramASN1ObjectIdentifier, paramString);
    return this;
  }
  
  public byte[] generateWrappedKey(GenericKey paramGenericKey) throws OperatorException {
    Cipher cipher = this.helper.createAsymmetricWrapper(getAlgorithmIdentifier().getAlgorithm(), this.extraMappings);
    AlgorithmParameters algorithmParameters = this.helper.createAlgorithmParameters(getAlgorithmIdentifier());
    byte[] arrayOfByte = null;
    try {
      if (algorithmParameters != null) {
        cipher.init(3, this.publicKey, algorithmParameters, this.random);
      } else {
        cipher.init(3, this.publicKey, this.random);
      } 
      arrayOfByte = cipher.wrap(OperatorUtils.getJceKey(paramGenericKey));
    } catch (InvalidKeyException invalidKeyException) {
    
    } catch (GeneralSecurityException generalSecurityException) {
    
    } catch (IllegalStateException illegalStateException) {
    
    } catch (UnsupportedOperationException unsupportedOperationException) {
    
    } catch (ProviderException providerException) {}
    if (arrayOfByte == null)
      try {
        cipher.init(1, this.publicKey, this.random);
        arrayOfByte = cipher.doFinal(OperatorUtils.getJceKey(paramGenericKey).getEncoded());
      } catch (InvalidKeyException invalidKeyException) {
        throw new OperatorException("unable to encrypt contents key", invalidKeyException);
      } catch (GeneralSecurityException generalSecurityException) {
        throw new OperatorException("unable to encrypt contents key", generalSecurityException);
      }  
    return arrayOfByte;
  }
  
  private static AlgorithmIdentifier extractFromSpec(AlgorithmParameterSpec paramAlgorithmParameterSpec) {
    if (paramAlgorithmParameterSpec instanceof OAEPParameterSpec) {
      OAEPParameterSpec oAEPParameterSpec = (OAEPParameterSpec)paramAlgorithmParameterSpec;
      if (oAEPParameterSpec.getMGFAlgorithm().equals(OAEPParameterSpec.DEFAULT.getMGFAlgorithm())) {
        if (oAEPParameterSpec.getPSource() instanceof PSource.PSpecified)
          return new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSAES_OAEP, (ASN1Encodable)new RSAESOAEPparams(getDigest(oAEPParameterSpec.getDigestAlgorithm()), new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, (ASN1Encodable)getDigest(((MGF1ParameterSpec)oAEPParameterSpec.getMGFParameters()).getDigestAlgorithm())), new AlgorithmIdentifier(PKCSObjectIdentifiers.id_pSpecified, (ASN1Encodable)new DEROctetString(((PSource.PSpecified)oAEPParameterSpec.getPSource()).getValue())))); 
        throw new IllegalArgumentException("unknown PSource: " + oAEPParameterSpec.getPSource().getAlgorithm());
      } 
      throw new IllegalArgumentException("unknown MGF: " + oAEPParameterSpec.getMGFAlgorithm());
    } 
    throw new IllegalArgumentException("unknown spec: " + paramAlgorithmParameterSpec.getClass().getName());
  }
  
  private static AlgorithmIdentifier getDigest(String paramString) {
    AlgorithmIdentifier algorithmIdentifier = (AlgorithmIdentifier)digests.get(paramString);
    if (algorithmIdentifier != null)
      return algorithmIdentifier; 
    throw new IllegalArgumentException("unknown digest name: " + paramString);
  }
  
  static {
    digests.put("SHA-1", new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, (ASN1Encodable)DERNull.INSTANCE));
    digests.put("SHA-1", new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, (ASN1Encodable)DERNull.INSTANCE));
    digests.put("SHA224", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha224, (ASN1Encodable)DERNull.INSTANCE));
    digests.put("SHA-224", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha224, (ASN1Encodable)DERNull.INSTANCE));
    digests.put("SHA256", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, (ASN1Encodable)DERNull.INSTANCE));
    digests.put("SHA-256", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, (ASN1Encodable)DERNull.INSTANCE));
    digests.put("SHA384", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384, (ASN1Encodable)DERNull.INSTANCE));
    digests.put("SHA-384", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384, (ASN1Encodable)DERNull.INSTANCE));
    digests.put("SHA512", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512, (ASN1Encodable)DERNull.INSTANCE));
    digests.put("SHA-512", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512, (ASN1Encodable)DERNull.INSTANCE));
    digests.put("SHA512/224", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_224, (ASN1Encodable)DERNull.INSTANCE));
    digests.put("SHA-512/224", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_224, (ASN1Encodable)DERNull.INSTANCE));
    digests.put("SHA-512(224)", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_224, (ASN1Encodable)DERNull.INSTANCE));
    digests.put("SHA512/256", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_256, (ASN1Encodable)DERNull.INSTANCE));
    digests.put("SHA-512/256", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_256, (ASN1Encodable)DERNull.INSTANCE));
    digests.put("SHA-512(256)", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_256, (ASN1Encodable)DERNull.INSTANCE));
  }
}
