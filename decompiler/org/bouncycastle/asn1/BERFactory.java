package org.bouncycastle.asn1;

class BERFactory {
  static final BERSequence EMPTY_SEQUENCE = new BERSequence();
  
  static final BERSet EMPTY_SET = new BERSet();
  
  static BERSequence createSequence(ASN1EncodableVector paramASN1EncodableVector) {
    return (paramASN1EncodableVector.size() < 1) ? EMPTY_SEQUENCE : new BERSequence(paramASN1EncodableVector);
  }
  
  static BERSet createSet(ASN1EncodableVector paramASN1EncodableVector) {
    return (paramASN1EncodableVector.size() < 1) ? EMPTY_SET : new BERSet(paramASN1EncodableVector);
  }
}
