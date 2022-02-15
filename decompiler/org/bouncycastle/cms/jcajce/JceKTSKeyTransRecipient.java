package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.Provider;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KeyTransRecipient;
import org.bouncycastle.cms.KeyTransRecipientId;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.jcajce.JceKTSKeyUnwrapper;
import org.bouncycastle.util.encoders.Hex;

public abstract class JceKTSKeyTransRecipient implements KeyTransRecipient {
  private static final byte[] ANONYMOUS_SENDER = Hex.decode("0c14416e6f6e796d6f75732053656e64657220202020");
  
  private final byte[] partyVInfo;
  
  private PrivateKey recipientKey;
  
  protected EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
  
  protected EnvelopedDataHelper contentHelper = this.helper;
  
  protected Map extraMappings = new HashMap<Object, Object>();
  
  protected boolean validateKeySize = false;
  
  protected boolean unwrappedKeyMustBeEncodable;
  
  public JceKTSKeyTransRecipient(PrivateKey paramPrivateKey, byte[] paramArrayOfbyte) {
    this.recipientKey = paramPrivateKey;
    this.partyVInfo = paramArrayOfbyte;
  }
  
  public JceKTSKeyTransRecipient setProvider(Provider paramProvider) {
    this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(paramProvider));
    this.contentHelper = this.helper;
    return this;
  }
  
  public JceKTSKeyTransRecipient setProvider(String paramString) {
    this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(paramString));
    this.contentHelper = this.helper;
    return this;
  }
  
  public JceKTSKeyTransRecipient setAlgorithmMapping(ASN1ObjectIdentifier paramASN1ObjectIdentifier, String paramString) {
    this.extraMappings.put(paramASN1ObjectIdentifier, paramString);
    return this;
  }
  
  public JceKTSKeyTransRecipient setContentProvider(Provider paramProvider) {
    this.contentHelper = CMSUtils.createContentHelper(paramProvider);
    return this;
  }
  
  public JceKTSKeyTransRecipient setContentProvider(String paramString) {
    this.contentHelper = CMSUtils.createContentHelper(paramString);
    return this;
  }
  
  public JceKTSKeyTransRecipient setKeySizeValidation(boolean paramBoolean) {
    this.validateKeySize = paramBoolean;
    return this;
  }
  
  protected Key extractSecretKey(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2, byte[] paramArrayOfbyte) throws CMSException {
    JceKTSKeyUnwrapper jceKTSKeyUnwrapper = this.helper.createAsymmetricUnwrapper(paramAlgorithmIdentifier1, this.recipientKey, ANONYMOUS_SENDER, this.partyVInfo);
    try {
      Key key = this.helper.getJceKey(paramAlgorithmIdentifier2.getAlgorithm(), jceKTSKeyUnwrapper.generateUnwrappedKey(paramAlgorithmIdentifier2, paramArrayOfbyte));
      if (this.validateKeySize)
        this.helper.keySizeCheck(paramAlgorithmIdentifier2, key); 
      return key;
    } catch (OperatorException operatorException) {
      throw new CMSException("exception unwrapping key: " + operatorException.getMessage(), operatorException);
    } 
  }
  
  protected static byte[] getPartyVInfoFromRID(KeyTransRecipientId paramKeyTransRecipientId) throws IOException {
    return (paramKeyTransRecipientId.getSerialNumber() != null) ? (new IssuerAndSerialNumber(paramKeyTransRecipientId.getIssuer(), paramKeyTransRecipientId.getSerialNumber())).getEncoded("DER") : (new DEROctetString(paramKeyTransRecipientId.getSubjectKeyIdentifier())).getEncoded();
  }
}
