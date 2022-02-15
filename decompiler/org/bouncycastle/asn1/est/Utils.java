package org.bouncycastle.asn1.est;

class Utils {
  static AttrOrOID[] clone(AttrOrOID[] paramArrayOfAttrOrOID) {
    AttrOrOID[] arrayOfAttrOrOID = new AttrOrOID[paramArrayOfAttrOrOID.length];
    System.arraycopy(paramArrayOfAttrOrOID, 0, arrayOfAttrOrOID, 0, paramArrayOfAttrOrOID.length);
    return arrayOfAttrOrOID;
  }
}
