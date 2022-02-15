package org.bouncycastle.asn1.isismtt.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.DirectoryString;

public class ProfessionInfo extends ASN1Object {
  public static final ASN1ObjectIdentifier Rechtsanwltin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".1");
  
  public static final ASN1ObjectIdentifier Rechtsanwalt = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".2");
  
  public static final ASN1ObjectIdentifier Rechtsbeistand = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".3");
  
  public static final ASN1ObjectIdentifier Steuerberaterin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".4");
  
  public static final ASN1ObjectIdentifier Steuerberater = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".5");
  
  public static final ASN1ObjectIdentifier Steuerbevollmchtigte = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".6");
  
  public static final ASN1ObjectIdentifier Steuerbevollmchtigter = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".7");
  
  public static final ASN1ObjectIdentifier Notarin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".8");
  
  public static final ASN1ObjectIdentifier Notar = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".9");
  
  public static final ASN1ObjectIdentifier Notarvertreterin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".10");
  
  public static final ASN1ObjectIdentifier Notarvertreter = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".11");
  
  public static final ASN1ObjectIdentifier Notariatsverwalterin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".12");
  
  public static final ASN1ObjectIdentifier Notariatsverwalter = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".13");
  
  public static final ASN1ObjectIdentifier Wirtschaftsprferin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".14");
  
  public static final ASN1ObjectIdentifier Wirtschaftsprfer = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".15");
  
  public static final ASN1ObjectIdentifier VereidigteBuchprferin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".16");
  
  public static final ASN1ObjectIdentifier VereidigterBuchprfer = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".17");
  
  public static final ASN1ObjectIdentifier Patentanwltin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".18");
  
  public static final ASN1ObjectIdentifier Patentanwalt = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".19");
  
  private NamingAuthority namingAuthority;
  
  private ASN1Sequence professionItems;
  
  private ASN1Sequence professionOIDs;
  
  private String registrationNumber;
  
  private ASN1OctetString addProfessionInfo;
  
  public static ProfessionInfo getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof ProfessionInfo)
      return (ProfessionInfo)paramObject; 
    if (paramObject instanceof ASN1Sequence)
      return new ProfessionInfo((ASN1Sequence)paramObject); 
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  private ProfessionInfo(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() > 5)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    Enumeration<ASN1Encodable> enumeration = paramASN1Sequence.getObjects();
    ASN1Encodable aSN1Encodable = enumeration.nextElement();
    if (aSN1Encodable instanceof ASN1TaggedObject) {
      if (((ASN1TaggedObject)aSN1Encodable).getTagNo() != 0)
        throw new IllegalArgumentException("Bad tag number: " + ((ASN1TaggedObject)aSN1Encodable).getTagNo()); 
      this.namingAuthority = NamingAuthority.getInstance((ASN1TaggedObject)aSN1Encodable, true);
      aSN1Encodable = enumeration.nextElement();
    } 
    this.professionItems = ASN1Sequence.getInstance(aSN1Encodable);
    if (enumeration.hasMoreElements()) {
      aSN1Encodable = enumeration.nextElement();
      if (aSN1Encodable instanceof ASN1Sequence) {
        this.professionOIDs = ASN1Sequence.getInstance(aSN1Encodable);
      } else if (aSN1Encodable instanceof DERPrintableString) {
        this.registrationNumber = DERPrintableString.getInstance(aSN1Encodable).getString();
      } else if (aSN1Encodable instanceof ASN1OctetString) {
        this.addProfessionInfo = ASN1OctetString.getInstance(aSN1Encodable);
      } else {
        throw new IllegalArgumentException("Bad object encountered: " + aSN1Encodable.getClass());
      } 
    } 
    if (enumeration.hasMoreElements()) {
      aSN1Encodable = enumeration.nextElement();
      if (aSN1Encodable instanceof DERPrintableString) {
        this.registrationNumber = DERPrintableString.getInstance(aSN1Encodable).getString();
      } else if (aSN1Encodable instanceof org.bouncycastle.asn1.DEROctetString) {
        this.addProfessionInfo = (ASN1OctetString)aSN1Encodable;
      } else {
        throw new IllegalArgumentException("Bad object encountered: " + aSN1Encodable.getClass());
      } 
    } 
    if (enumeration.hasMoreElements()) {
      aSN1Encodable = enumeration.nextElement();
      if (aSN1Encodable instanceof org.bouncycastle.asn1.DEROctetString) {
        this.addProfessionInfo = (ASN1OctetString)aSN1Encodable;
      } else {
        throw new IllegalArgumentException("Bad object encountered: " + aSN1Encodable.getClass());
      } 
    } 
  }
  
  public ProfessionInfo(NamingAuthority paramNamingAuthority, DirectoryString[] paramArrayOfDirectoryString, ASN1ObjectIdentifier[] paramArrayOfASN1ObjectIdentifier, String paramString, ASN1OctetString paramASN1OctetString) {
    this.namingAuthority = paramNamingAuthority;
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    byte b;
    for (b = 0; b != paramArrayOfDirectoryString.length; b++)
      aSN1EncodableVector.add((ASN1Encodable)paramArrayOfDirectoryString[b]); 
    this.professionItems = (ASN1Sequence)new DERSequence(aSN1EncodableVector);
    if (paramArrayOfASN1ObjectIdentifier != null) {
      aSN1EncodableVector = new ASN1EncodableVector();
      for (b = 0; b != paramArrayOfASN1ObjectIdentifier.length; b++)
        aSN1EncodableVector.add((ASN1Encodable)paramArrayOfASN1ObjectIdentifier[b]); 
      this.professionOIDs = (ASN1Sequence)new DERSequence(aSN1EncodableVector);
    } 
    this.registrationNumber = paramString;
    this.addProfessionInfo = paramASN1OctetString;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.namingAuthority != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.namingAuthority)); 
    aSN1EncodableVector.add((ASN1Encodable)this.professionItems);
    if (this.professionOIDs != null)
      aSN1EncodableVector.add((ASN1Encodable)this.professionOIDs); 
    if (this.registrationNumber != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERPrintableString(this.registrationNumber, true)); 
    if (this.addProfessionInfo != null)
      aSN1EncodableVector.add((ASN1Encodable)this.addProfessionInfo); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  public ASN1OctetString getAddProfessionInfo() {
    return this.addProfessionInfo;
  }
  
  public NamingAuthority getNamingAuthority() {
    return this.namingAuthority;
  }
  
  public DirectoryString[] getProfessionItems() {
    DirectoryString[] arrayOfDirectoryString = new DirectoryString[this.professionItems.size()];
    byte b = 0;
    Enumeration enumeration = this.professionItems.getObjects();
    while (enumeration.hasMoreElements())
      arrayOfDirectoryString[b++] = DirectoryString.getInstance(enumeration.nextElement()); 
    return arrayOfDirectoryString;
  }
  
  public ASN1ObjectIdentifier[] getProfessionOIDs() {
    if (this.professionOIDs == null)
      return new ASN1ObjectIdentifier[0]; 
    ASN1ObjectIdentifier[] arrayOfASN1ObjectIdentifier = new ASN1ObjectIdentifier[this.professionOIDs.size()];
    byte b = 0;
    Enumeration enumeration = this.professionOIDs.getObjects();
    while (enumeration.hasMoreElements())
      arrayOfASN1ObjectIdentifier[b++] = ASN1ObjectIdentifier.getInstance(enumeration.nextElement()); 
    return arrayOfASN1ObjectIdentifier;
  }
  
  public String getRegistrationNumber() {
    return this.registrationNumber;
  }
}
