package org.bouncycastle.asn1.x509.sigi;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.DirectoryString;

public class NameOrPseudonym extends ASN1Object implements ASN1Choice {
  private DirectoryString pseudonym;
  
  private DirectoryString surname;
  
  private ASN1Sequence givenName;
  
  public static NameOrPseudonym getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof NameOrPseudonym)
      return (NameOrPseudonym)paramObject; 
    if (paramObject instanceof org.bouncycastle.asn1.ASN1String)
      return new NameOrPseudonym(DirectoryString.getInstance(paramObject)); 
    if (paramObject instanceof ASN1Sequence)
      return new NameOrPseudonym((ASN1Sequence)paramObject); 
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  public NameOrPseudonym(DirectoryString paramDirectoryString) {
    this.pseudonym = paramDirectoryString;
  }
  
  private NameOrPseudonym(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 2)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    if (!(paramASN1Sequence.getObjectAt(0) instanceof org.bouncycastle.asn1.ASN1String))
      throw new IllegalArgumentException("Bad object encountered: " + paramASN1Sequence.getObjectAt(0).getClass()); 
    this.surname = DirectoryString.getInstance(paramASN1Sequence.getObjectAt(0));
    this.givenName = ASN1Sequence.getInstance(paramASN1Sequence.getObjectAt(1));
  }
  
  public NameOrPseudonym(String paramString) {
    this(new DirectoryString(paramString));
  }
  
  public NameOrPseudonym(DirectoryString paramDirectoryString, ASN1Sequence paramASN1Sequence) {
    this.surname = paramDirectoryString;
    this.givenName = paramASN1Sequence;
  }
  
  public DirectoryString getPseudonym() {
    return this.pseudonym;
  }
  
  public DirectoryString getSurname() {
    return this.surname;
  }
  
  public DirectoryString[] getGivenName() {
    DirectoryString[] arrayOfDirectoryString = new DirectoryString[this.givenName.size()];
    byte b = 0;
    Enumeration enumeration = this.givenName.getObjects();
    while (enumeration.hasMoreElements())
      arrayOfDirectoryString[b++] = DirectoryString.getInstance(enumeration.nextElement()); 
    return arrayOfDirectoryString;
  }
  
  public ASN1Primitive toASN1Primitive() {
    if (this.pseudonym != null)
      return this.pseudonym.toASN1Primitive(); 
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.surname);
    aSN1EncodableVector.add((ASN1Encodable)this.givenName);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
