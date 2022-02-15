package org.bouncycastle.operator.jcajce;

import java.security.Key;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.cms.GenericHybridParameters;
import org.bouncycastle.asn1.cms.RsaKemParameters;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.util.DEROtherInfo;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.AsymmetricKeyUnwrapper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.util.Arrays;

public class JceKTSKeyUnwrapper extends AsymmetricKeyUnwrapper {
  private OperatorHelper helper = new OperatorHelper((JcaJceHelper)new DefaultJcaJceHelper());
  
  private Map extraMappings = new HashMap<Object, Object>();
  
  private PrivateKey privKey;
  
  private byte[] partyUInfo;
  
  private byte[] partyVInfo;
  
  public JceKTSKeyUnwrapper(AlgorithmIdentifier paramAlgorithmIdentifier, PrivateKey paramPrivateKey, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    super(paramAlgorithmIdentifier);
    this.privKey = paramPrivateKey;
    this.partyUInfo = Arrays.clone(paramArrayOfbyte1);
    this.partyVInfo = Arrays.clone(paramArrayOfbyte2);
  }
  
  public JceKTSKeyUnwrapper setProvider(Provider paramProvider) {
    this.helper = new OperatorHelper((JcaJceHelper)new ProviderJcaJceHelper(paramProvider));
    return this;
  }
  
  public JceKTSKeyUnwrapper setProvider(String paramString) {
    this.helper = new OperatorHelper((JcaJceHelper)new NamedJcaJceHelper(paramString));
    return this;
  }
  
  public GenericKey generateUnwrappedKey(AlgorithmIdentifier paramAlgorithmIdentifier, byte[] paramArrayOfbyte) throws OperatorException {
    Key key;
    GenericHybridParameters genericHybridParameters = GenericHybridParameters.getInstance(getAlgorithmIdentifier().getParameters());
    Cipher cipher = this.helper.createAsymmetricWrapper(getAlgorithmIdentifier().getAlgorithm(), this.extraMappings);
    String str = this.helper.getWrappingAlgorithmName(genericHybridParameters.getDem().getAlgorithm());
    RsaKemParameters rsaKemParameters = RsaKemParameters.getInstance(genericHybridParameters.getKem().getParameters());
    int i = rsaKemParameters.getKeyLength().intValue() * 8;
    try {
      DEROtherInfo dEROtherInfo = (new DEROtherInfo.Builder(genericHybridParameters.getDem(), this.partyUInfo, this.partyVInfo)).build();
      KTSParameterSpec kTSParameterSpec = (new KTSParameterSpec.Builder(str, i, dEROtherInfo.getEncoded())).withKdfAlgorithm(rsaKemParameters.getKeyDerivationFunction()).build();
      cipher.init(4, this.privKey, (AlgorithmParameterSpec)kTSParameterSpec);
      key = cipher.unwrap(paramArrayOfbyte, this.helper.getKeyAlgorithmName(paramAlgorithmIdentifier.getAlgorithm()), 3);
    } catch (Exception exception) {
      throw new OperatorException("Unable to unwrap contents key: " + exception.getMessage(), exception);
    } 
    return new JceGenericKey(paramAlgorithmIdentifier, key);
  }
}
