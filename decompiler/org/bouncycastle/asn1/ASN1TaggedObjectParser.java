package org.bouncycastle.asn1;

import java.io.IOException;

public interface ASN1TaggedObjectParser extends ASN1Encodable, InMemoryRepresentable {
  int getTagNo();
  
  ASN1Encodable getObjectParser(int paramInt, boolean paramBoolean) throws IOException;
}
