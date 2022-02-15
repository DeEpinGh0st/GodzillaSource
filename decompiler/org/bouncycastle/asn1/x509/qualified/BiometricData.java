package org.bouncycastle.asn1.x509.qualified;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class BiometricData extends ASN1Object {
  private TypeOfBiometricData typeOfBiometricData;
  
  private AlgorithmIdentifier hashAlgorithm;
  
  private ASN1OctetString biometricDataHash;
  
  private DERIA5String sourceDataUri;
  
  public static BiometricData getInstance(Object paramObject) {
    return (paramObject instanceof BiometricData) ? (BiometricData)paramObject : ((paramObject != null) ? new BiometricData(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private BiometricData(ASN1Sequence paramASN1Sequence) {
    Enumeration enumeration = paramASN1Sequence.getObjects();
    this.typeOfBiometricData = TypeOfBiometricData.getInstance(enumeration.nextElement());
    this.hashAlgorithm = AlgorithmIdentifier.getInstance(enumeration.nextElement());
    this.biometricDataHash = ASN1OctetString.getInstance(enumeration.nextElement());
    if (enumeration.hasMoreElements())
      this.sourceDataUri = DERIA5String.getInstance(enumeration.nextElement()); 
  }
  
  public BiometricData(TypeOfBiometricData paramTypeOfBiometricData, AlgorithmIdentifier paramAlgorithmIdentifier, ASN1OctetString paramASN1OctetString, DERIA5String paramDERIA5String) {
    this.typeOfBiometricData = paramTypeOfBiometricData;
    this.hashAlgorithm = paramAlgorithmIdentifier;
    this.biometricDataHash = paramASN1OctetString;
    this.sourceDataUri = paramDERIA5String;
  }
  
  public BiometricData(TypeOfBiometricData paramTypeOfBiometricData, AlgorithmIdentifier paramAlgorithmIdentifier, ASN1OctetString paramASN1OctetString) {
    this.typeOfBiometricData = paramTypeOfBiometricData;
    this.hashAlgorithm = paramAlgorithmIdentifier;
    this.biometricDataHash = paramASN1OctetString;
    this.sourceDataUri = null;
  }
  
  public TypeOfBiometricData getTypeOfBiometricData() {
    return this.typeOfBiometricData;
  }
  
  public AlgorithmIdentifier getHashAlgorithm() {
    return this.hashAlgorithm;
  }
  
  public ASN1OctetString getBiometricDataHash() {
    return this.biometricDataHash;
  }
  
  public DERIA5String getSourceDataUri() {
    return this.sourceDataUri;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.typeOfBiometricData);
    aSN1EncodableVector.add((ASN1Encodable)this.hashAlgorithm);
    aSN1EncodableVector.add((ASN1Encodable)this.biometricDataHash);
    if (this.sourceDataUri != null)
      aSN1EncodableVector.add((ASN1Encodable)this.sourceDataUri); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
