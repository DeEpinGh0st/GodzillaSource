package org.bouncycastle.asn1;

class DERFactory {
  static final ASN1Sequence EMPTY_SEQUENCE = new DERSequence();
  
  static final ASN1Set EMPTY_SET = new DERSet();
  
  static ASN1Sequence createSequence(ASN1EncodableVector paramASN1EncodableVector) {
    return (paramASN1EncodableVector.size() < 1) ? EMPTY_SEQUENCE : new DLSequence(paramASN1EncodableVector);
  }
  
  static ASN1Set createSet(ASN1EncodableVector paramASN1EncodableVector) {
    return (paramASN1EncodableVector.size() < 1) ? EMPTY_SET : new DLSet(paramASN1EncodableVector);
  }
}
