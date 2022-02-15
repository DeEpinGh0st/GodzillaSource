package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.Extension;

class Utils {
  static BodyPartID[] toBodyPartIDArray(ASN1Sequence paramASN1Sequence) {
    BodyPartID[] arrayOfBodyPartID = new BodyPartID[paramASN1Sequence.size()];
    for (byte b = 0; b != paramASN1Sequence.size(); b++)
      arrayOfBodyPartID[b] = BodyPartID.getInstance(paramASN1Sequence.getObjectAt(b)); 
    return arrayOfBodyPartID;
  }
  
  static BodyPartID[] clone(BodyPartID[] paramArrayOfBodyPartID) {
    BodyPartID[] arrayOfBodyPartID = new BodyPartID[paramArrayOfBodyPartID.length];
    System.arraycopy(paramArrayOfBodyPartID, 0, arrayOfBodyPartID, 0, paramArrayOfBodyPartID.length);
    return arrayOfBodyPartID;
  }
  
  static Extension[] clone(Extension[] paramArrayOfExtension) {
    Extension[] arrayOfExtension = new Extension[paramArrayOfExtension.length];
    System.arraycopy(paramArrayOfExtension, 0, arrayOfExtension, 0, paramArrayOfExtension.length);
    return arrayOfExtension;
  }
}
