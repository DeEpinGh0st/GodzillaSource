package org.bouncycastle.cms;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.KEKRecipientInfo;
import org.bouncycastle.asn1.cms.KeyAgreeRecipientInfo;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.cms.PasswordRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestCalculator;

class CMSEnvelopedHelper {
  static RecipientInformationStore buildRecipientInformationStore(ASN1Set paramASN1Set, AlgorithmIdentifier paramAlgorithmIdentifier, CMSSecureReadable paramCMSSecureReadable) {
    return buildRecipientInformationStore(paramASN1Set, paramAlgorithmIdentifier, paramCMSSecureReadable, null);
  }
  
  static RecipientInformationStore buildRecipientInformationStore(ASN1Set paramASN1Set, AlgorithmIdentifier paramAlgorithmIdentifier, CMSSecureReadable paramCMSSecureReadable, AuthAttributesProvider paramAuthAttributesProvider) {
    ArrayList<RecipientInformation> arrayList = new ArrayList();
    for (byte b = 0; b != paramASN1Set.size(); b++) {
      RecipientInfo recipientInfo = RecipientInfo.getInstance(paramASN1Set.getObjectAt(b));
      readRecipientInfo(arrayList, recipientInfo, paramAlgorithmIdentifier, paramCMSSecureReadable, paramAuthAttributesProvider);
    } 
    return new RecipientInformationStore(arrayList);
  }
  
  private static void readRecipientInfo(List<KeyTransRecipientInformation> paramList, RecipientInfo paramRecipientInfo, AlgorithmIdentifier paramAlgorithmIdentifier, CMSSecureReadable paramCMSSecureReadable, AuthAttributesProvider paramAuthAttributesProvider) {
    ASN1Encodable aSN1Encodable = paramRecipientInfo.getInfo();
    if (aSN1Encodable instanceof KeyTransRecipientInfo) {
      paramList.add(new KeyTransRecipientInformation((KeyTransRecipientInfo)aSN1Encodable, paramAlgorithmIdentifier, paramCMSSecureReadable, paramAuthAttributesProvider));
    } else if (aSN1Encodable instanceof KEKRecipientInfo) {
      paramList.add(new KEKRecipientInformation((KEKRecipientInfo)aSN1Encodable, paramAlgorithmIdentifier, paramCMSSecureReadable, paramAuthAttributesProvider));
    } else if (aSN1Encodable instanceof KeyAgreeRecipientInfo) {
      KeyAgreeRecipientInformation.readRecipientInfo(paramList, (KeyAgreeRecipientInfo)aSN1Encodable, paramAlgorithmIdentifier, paramCMSSecureReadable, paramAuthAttributesProvider);
    } else if (aSN1Encodable instanceof PasswordRecipientInfo) {
      paramList.add(new PasswordRecipientInformation((PasswordRecipientInfo)aSN1Encodable, paramAlgorithmIdentifier, paramCMSSecureReadable, paramAuthAttributesProvider));
    } 
  }
  
  static class CMSAuthenticatedSecureReadable implements CMSSecureReadable {
    private AlgorithmIdentifier algorithm;
    
    private CMSReadable readable;
    
    CMSAuthenticatedSecureReadable(AlgorithmIdentifier param1AlgorithmIdentifier, CMSReadable param1CMSReadable) {
      this.algorithm = param1AlgorithmIdentifier;
      this.readable = param1CMSReadable;
    }
    
    public InputStream getInputStream() throws IOException, CMSException {
      return this.readable.getInputStream();
    }
  }
  
  static class CMSDigestAuthenticatedSecureReadable implements CMSSecureReadable {
    private DigestCalculator digestCalculator;
    
    private CMSReadable readable;
    
    public CMSDigestAuthenticatedSecureReadable(DigestCalculator param1DigestCalculator, CMSReadable param1CMSReadable) {
      this.digestCalculator = param1DigestCalculator;
      this.readable = param1CMSReadable;
    }
    
    public InputStream getInputStream() throws IOException, CMSException {
      return new FilterInputStream(this.readable.getInputStream()) {
          public int read() throws IOException {
            int i = this.in.read();
            if (i >= 0)
              CMSEnvelopedHelper.CMSDigestAuthenticatedSecureReadable.this.digestCalculator.getOutputStream().write(i); 
            return i;
          }
          
          public int read(byte[] param2ArrayOfbyte, int param2Int1, int param2Int2) throws IOException {
            int i = this.in.read(param2ArrayOfbyte, param2Int1, param2Int2);
            if (i >= 0)
              CMSEnvelopedHelper.CMSDigestAuthenticatedSecureReadable.this.digestCalculator.getOutputStream().write(param2ArrayOfbyte, param2Int1, i); 
            return i;
          }
        };
    }
    
    public byte[] getDigest() {
      return this.digestCalculator.getDigest();
    }
  }
  
  static class CMSEnvelopedSecureReadable implements CMSSecureReadable {
    private AlgorithmIdentifier algorithm;
    
    private CMSReadable readable;
    
    CMSEnvelopedSecureReadable(AlgorithmIdentifier param1AlgorithmIdentifier, CMSReadable param1CMSReadable) {
      this.algorithm = param1AlgorithmIdentifier;
      this.readable = param1CMSReadable;
    }
    
    public InputStream getInputStream() throws IOException, CMSException {
      return this.readable.getInputStream();
    }
  }
}
