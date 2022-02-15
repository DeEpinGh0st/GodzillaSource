package org.bouncycastle.asn1.misc;

import org.bouncycastle.asn1.DERIA5String;

public class VerisignCzagExtension extends DERIA5String {
  public VerisignCzagExtension(DERIA5String paramDERIA5String) {
    super(paramDERIA5String.getString());
  }
  
  public String toString() {
    return "VerisignCzagExtension: " + getString();
  }
}
