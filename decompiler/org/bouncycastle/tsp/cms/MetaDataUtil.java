package org.bouncycastle.tsp.cms;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.cms.Attributes;
import org.bouncycastle.asn1.cms.MetaData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.DigestCalculator;

class MetaDataUtil {
  private final MetaData metaData;
  
  MetaDataUtil(MetaData paramMetaData) {
    this.metaData = paramMetaData;
  }
  
  void initialiseMessageImprintDigestCalculator(DigestCalculator paramDigestCalculator) throws CMSException {
    if (this.metaData != null && this.metaData.isHashProtected())
      try {
        paramDigestCalculator.getOutputStream().write(this.metaData.getEncoded("DER"));
      } catch (IOException iOException) {
        throw new CMSException("unable to initialise calculator from metaData: " + iOException.getMessage(), iOException);
      }  
  }
  
  String getFileName() {
    return (this.metaData != null) ? convertString((ASN1String)this.metaData.getFileName()) : null;
  }
  
  String getMediaType() {
    return (this.metaData != null) ? convertString((ASN1String)this.metaData.getMediaType()) : null;
  }
  
  Attributes getOtherMetaData() {
    return (this.metaData != null) ? this.metaData.getOtherMetaData() : null;
  }
  
  private String convertString(ASN1String paramASN1String) {
    return (paramASN1String != null) ? paramASN1String.toString() : null;
  }
}
