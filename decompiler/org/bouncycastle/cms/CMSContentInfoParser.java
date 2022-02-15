package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1StreamParser;
import org.bouncycastle.asn1.cms.ContentInfoParser;

public class CMSContentInfoParser {
  protected ContentInfoParser _contentInfo;
  
  protected InputStream _data;
  
  protected CMSContentInfoParser(InputStream paramInputStream) throws CMSException {
    this._data = paramInputStream;
    try {
      ASN1StreamParser aSN1StreamParser = new ASN1StreamParser(paramInputStream);
      ASN1SequenceParser aSN1SequenceParser = (ASN1SequenceParser)aSN1StreamParser.readObject();
      if (aSN1SequenceParser == null)
        throw new CMSException("No content found."); 
      this._contentInfo = new ContentInfoParser(aSN1SequenceParser);
    } catch (IOException iOException) {
      throw new CMSException("IOException reading content.", iOException);
    } catch (ClassCastException classCastException) {
      throw new CMSException("Unexpected object reading content.", classCastException);
    } 
  }
  
  public void close() throws IOException {
    this._data.close();
  }
}
