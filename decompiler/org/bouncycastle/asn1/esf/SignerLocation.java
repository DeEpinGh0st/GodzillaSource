package org.bouncycastle.asn1.esf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.x500.DirectoryString;

public class SignerLocation extends ASN1Object {
  private DirectoryString countryName;
  
  private DirectoryString localityName;
  
  private ASN1Sequence postalAddress;
  
  private SignerLocation(ASN1Sequence paramASN1Sequence) {
    Enumeration<ASN1TaggedObject> enumeration = paramASN1Sequence.getObjects();
    while (enumeration.hasMoreElements()) {
      ASN1TaggedObject aSN1TaggedObject = enumeration.nextElement();
      switch (aSN1TaggedObject.getTagNo()) {
        case 0:
          this.countryName = DirectoryString.getInstance(aSN1TaggedObject, true);
          continue;
        case 1:
          this.localityName = DirectoryString.getInstance(aSN1TaggedObject, true);
          continue;
        case 2:
          if (aSN1TaggedObject.isExplicit()) {
            this.postalAddress = ASN1Sequence.getInstance(aSN1TaggedObject, true);
          } else {
            this.postalAddress = ASN1Sequence.getInstance(aSN1TaggedObject, false);
          } 
          if (this.postalAddress != null && this.postalAddress.size() > 6)
            throw new IllegalArgumentException("postal address must contain less than 6 strings"); 
          continue;
      } 
      throw new IllegalArgumentException("illegal tag");
    } 
  }
  
  private SignerLocation(DirectoryString paramDirectoryString1, DirectoryString paramDirectoryString2, ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence != null && paramASN1Sequence.size() > 6)
      throw new IllegalArgumentException("postal address must contain less than 6 strings"); 
    this.countryName = paramDirectoryString1;
    this.localityName = paramDirectoryString2;
    this.postalAddress = paramASN1Sequence;
  }
  
  public SignerLocation(DirectoryString paramDirectoryString1, DirectoryString paramDirectoryString2, DirectoryString[] paramArrayOfDirectoryString) {
    this(paramDirectoryString1, paramDirectoryString2, (ASN1Sequence)new DERSequence((ASN1Encodable[])paramArrayOfDirectoryString));
  }
  
  public SignerLocation(DERUTF8String paramDERUTF8String1, DERUTF8String paramDERUTF8String2, ASN1Sequence paramASN1Sequence) {
    this(DirectoryString.getInstance(paramDERUTF8String1), DirectoryString.getInstance(paramDERUTF8String2), paramASN1Sequence);
  }
  
  public static SignerLocation getInstance(Object paramObject) {
    return (paramObject == null || paramObject instanceof SignerLocation) ? (SignerLocation)paramObject : new SignerLocation(ASN1Sequence.getInstance(paramObject));
  }
  
  public DirectoryString getCountry() {
    return this.countryName;
  }
  
  public DirectoryString getLocality() {
    return this.localityName;
  }
  
  public DirectoryString[] getPostal() {
    if (this.postalAddress == null)
      return null; 
    DirectoryString[] arrayOfDirectoryString = new DirectoryString[this.postalAddress.size()];
    for (byte b = 0; b != arrayOfDirectoryString.length; b++)
      arrayOfDirectoryString[b] = DirectoryString.getInstance(this.postalAddress.getObjectAt(b)); 
    return arrayOfDirectoryString;
  }
  
  public DERUTF8String getCountryName() {
    return (this.countryName == null) ? null : new DERUTF8String(getCountry().getString());
  }
  
  public DERUTF8String getLocalityName() {
    return (this.localityName == null) ? null : new DERUTF8String(getLocality().getString());
  }
  
  public ASN1Sequence getPostalAddress() {
    return this.postalAddress;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.countryName != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.countryName)); 
    if (this.localityName != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 1, (ASN1Encodable)this.localityName)); 
    if (this.postalAddress != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 2, (ASN1Encodable)this.postalAddress)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
