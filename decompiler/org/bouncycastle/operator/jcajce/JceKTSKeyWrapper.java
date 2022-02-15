package org.bouncycastle.operator.jcajce;

import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.cms.GenericHybridParameters;
import org.bouncycastle.asn1.cms.RsaKemParameters;
import org.bouncycastle.asn1.iso.ISOIECObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.util.DEROtherInfo;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.AsymmetricKeyWrapper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.util.Arrays;

public class JceKTSKeyWrapper extends AsymmetricKeyWrapper {
  private final String symmetricWrappingAlg;
  
  private final int keySizeInBits;
  
  private final byte[] partyUInfo;
  
  private final byte[] partyVInfo;
  
  private OperatorHelper helper = new OperatorHelper((JcaJceHelper)new DefaultJcaJceHelper());
  
  private PublicKey publicKey;
  
  private SecureRandom random;
  
  public JceKTSKeyWrapper(PublicKey paramPublicKey, String paramString, int paramInt, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    super(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_rsa_KEM, (ASN1Encodable)new GenericHybridParameters(new AlgorithmIdentifier(ISOIECObjectIdentifiers.id_kem_rsa, (ASN1Encodable)new RsaKemParameters(new AlgorithmIdentifier(X9ObjectIdentifiers.id_kdf_kdf3, (ASN1Encodable)new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256)), (paramInt + 7) / 8)), JceSymmetricKeyWrapper.determineKeyEncAlg(paramString, paramInt))));
    this.publicKey = paramPublicKey;
    this.symmetricWrappingAlg = paramString;
    this.keySizeInBits = paramInt;
    this.partyUInfo = Arrays.clone(paramArrayOfbyte1);
    this.partyVInfo = Arrays.clone(paramArrayOfbyte2);
  }
  
  public JceKTSKeyWrapper(X509Certificate paramX509Certificate, String paramString, int paramInt, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this(paramX509Certificate.getPublicKey(), paramString, paramInt, paramArrayOfbyte1, paramArrayOfbyte2);
  }
  
  public JceKTSKeyWrapper setProvider(Provider paramProvider) {
    this.helper = new OperatorHelper((JcaJceHelper)new ProviderJcaJceHelper(paramProvider));
    return this;
  }
  
  public JceKTSKeyWrapper setProvider(String paramString) {
    this.helper = new OperatorHelper((JcaJceHelper)new NamedJcaJceHelper(paramString));
    return this;
  }
  
  public JceKTSKeyWrapper setSecureRandom(SecureRandom paramSecureRandom) {
    this.random = paramSecureRandom;
    return this;
  }
  
  public byte[] generateWrappedKey(GenericKey paramGenericKey) throws OperatorException {
    Cipher cipher = this.helper.createAsymmetricWrapper(getAlgorithmIdentifier().getAlgorithm(), new HashMap<Object, Object>());
    try {
      DEROtherInfo dEROtherInfo = (new DEROtherInfo.Builder(JceSymmetricKeyWrapper.determineKeyEncAlg(this.symmetricWrappingAlg, this.keySizeInBits), this.partyUInfo, this.partyVInfo)).build();
      KTSParameterSpec kTSParameterSpec = (new KTSParameterSpec.Builder(this.symmetricWrappingAlg, this.keySizeInBits, dEROtherInfo.getEncoded())).build();
      cipher.init(3, this.publicKey, (AlgorithmParameterSpec)kTSParameterSpec, this.random);
      return cipher.wrap(OperatorUtils.getJceKey(paramGenericKey));
    } catch (Exception exception) {
      throw new OperatorException("Unable to wrap contents key: " + exception.getMessage(), exception);
    } 
  }
}
