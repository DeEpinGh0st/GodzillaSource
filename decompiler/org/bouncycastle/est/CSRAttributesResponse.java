package org.bouncycastle.est;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.est.AttrOrOID;
import org.bouncycastle.asn1.est.CsrAttrs;
import org.bouncycastle.util.Encodable;

public class CSRAttributesResponse implements Encodable {
  private final CsrAttrs csrAttrs;
  
  private final HashMap<ASN1ObjectIdentifier, AttrOrOID> index;
  
  public CSRAttributesResponse(byte[] paramArrayOfbyte) throws ESTException {
    this(parseBytes(paramArrayOfbyte));
  }
  
  public CSRAttributesResponse(CsrAttrs paramCsrAttrs) throws ESTException {
    this.csrAttrs = paramCsrAttrs;
    this.index = new HashMap<ASN1ObjectIdentifier, AttrOrOID>(paramCsrAttrs.size());
    AttrOrOID[] arrayOfAttrOrOID = paramCsrAttrs.getAttrOrOIDs();
    for (byte b = 0; b != arrayOfAttrOrOID.length; b++) {
      AttrOrOID attrOrOID = arrayOfAttrOrOID[b];
      if (attrOrOID.isOid()) {
        this.index.put(attrOrOID.getOid(), attrOrOID);
      } else {
        this.index.put(attrOrOID.getAttribute().getAttrType(), attrOrOID);
      } 
    } 
  }
  
  private static CsrAttrs parseBytes(byte[] paramArrayOfbyte) throws ESTException {
    try {
      return CsrAttrs.getInstance(ASN1Primitive.fromByteArray(paramArrayOfbyte));
    } catch (Exception exception) {
      throw new ESTException("malformed data: " + exception.getMessage(), exception);
    } 
  }
  
  public boolean hasRequirement(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return this.index.containsKey(paramASN1ObjectIdentifier);
  }
  
  public boolean isAttribute(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return this.index.containsKey(paramASN1ObjectIdentifier) ? (!((AttrOrOID)this.index.get(paramASN1ObjectIdentifier)).isOid()) : false;
  }
  
  public boolean isEmpty() {
    return (this.csrAttrs.size() == 0);
  }
  
  public Collection<ASN1ObjectIdentifier> getRequirements() {
    return this.index.keySet();
  }
  
  public byte[] getEncoded() throws IOException {
    return this.csrAttrs.getEncoded();
  }
}
