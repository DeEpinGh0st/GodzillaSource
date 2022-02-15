package org.bouncycastle.cert.ocsp;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.ocsp.OCSPResponse;
import org.bouncycastle.asn1.ocsp.OCSPResponseStatus;
import org.bouncycastle.asn1.ocsp.ResponseBytes;

public class OCSPRespBuilder {
  public static final int SUCCESSFUL = 0;
  
  public static final int MALFORMED_REQUEST = 1;
  
  public static final int INTERNAL_ERROR = 2;
  
  public static final int TRY_LATER = 3;
  
  public static final int SIG_REQUIRED = 5;
  
  public static final int UNAUTHORIZED = 6;
  
  public OCSPResp build(int paramInt, Object paramObject) throws OCSPException {
    if (paramObject == null)
      return new OCSPResp(new OCSPResponse(new OCSPResponseStatus(paramInt), null)); 
    if (paramObject instanceof BasicOCSPResp) {
      DEROctetString dEROctetString;
      BasicOCSPResp basicOCSPResp = (BasicOCSPResp)paramObject;
      try {
        dEROctetString = new DEROctetString(basicOCSPResp.getEncoded());
      } catch (IOException iOException) {
        throw new OCSPException("can't encode object.", iOException);
      } 
      ResponseBytes responseBytes = new ResponseBytes(OCSPObjectIdentifiers.id_pkix_ocsp_basic, (ASN1OctetString)dEROctetString);
      return new OCSPResp(new OCSPResponse(new OCSPResponseStatus(paramInt), responseBytes));
    } 
    throw new OCSPException("unknown response object");
  }
}
