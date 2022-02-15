package org.bouncycastle.tsp.cms;

import java.net.URI;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.cms.Attributes;
import org.bouncycastle.asn1.cms.MetaData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.DigestCalculator;

public class CMSTimeStampedGenerator {
  protected MetaData metaData;
  
  protected URI dataUri;
  
  public void setDataUri(URI paramURI) {
    this.dataUri = paramURI;
  }
  
  public void setMetaData(boolean paramBoolean, String paramString1, String paramString2) {
    setMetaData(paramBoolean, paramString1, paramString2, (Attributes)null);
  }
  
  public void setMetaData(boolean paramBoolean, String paramString1, String paramString2, Attributes paramAttributes) {
    DERUTF8String dERUTF8String = null;
    if (paramString1 != null)
      dERUTF8String = new DERUTF8String(paramString1); 
    DERIA5String dERIA5String = null;
    if (paramString2 != null)
      dERIA5String = new DERIA5String(paramString2); 
    setMetaData(paramBoolean, dERUTF8String, dERIA5String, paramAttributes);
  }
  
  private void setMetaData(boolean paramBoolean, DERUTF8String paramDERUTF8String, DERIA5String paramDERIA5String, Attributes paramAttributes) {
    this.metaData = new MetaData(ASN1Boolean.getInstance(paramBoolean), paramDERUTF8String, paramDERIA5String, paramAttributes);
  }
  
  public void initialiseMessageImprintDigestCalculator(DigestCalculator paramDigestCalculator) throws CMSException {
    MetaDataUtil metaDataUtil = new MetaDataUtil(this.metaData);
    metaDataUtil.initialiseMessageImprintDigestCalculator(paramDigestCalculator);
  }
}
