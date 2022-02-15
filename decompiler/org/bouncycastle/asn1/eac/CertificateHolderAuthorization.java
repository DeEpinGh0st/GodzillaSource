package org.bouncycastle.asn1.eac;

import java.io.IOException;
import java.util.Hashtable;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERApplicationSpecific;
import org.bouncycastle.util.Integers;

public class CertificateHolderAuthorization extends ASN1Object {
  ASN1ObjectIdentifier oid;
  
  DERApplicationSpecific accessRights;
  
  public static final ASN1ObjectIdentifier id_role_EAC = EACObjectIdentifiers.bsi_de.branch("3.1.2.1");
  
  public static final int CVCA = 192;
  
  public static final int DV_DOMESTIC = 128;
  
  public static final int DV_FOREIGN = 64;
  
  public static final int IS = 0;
  
  public static final int RADG4 = 2;
  
  public static final int RADG3 = 1;
  
  static Hashtable RightsDecodeMap = new Hashtable<Object, Object>();
  
  static BidirectionalMap AuthorizationRole = new BidirectionalMap();
  
  static Hashtable ReverseMap = new Hashtable<Object, Object>();
  
  public static String getRoleDescription(int paramInt) {
    return (String)AuthorizationRole.get(Integers.valueOf(paramInt));
  }
  
  public static int getFlag(String paramString) {
    Integer integer = (Integer)AuthorizationRole.getReverse(paramString);
    if (integer == null)
      throw new IllegalArgumentException("Unknown value " + paramString); 
    return integer.intValue();
  }
  
  private void setPrivateData(ASN1InputStream paramASN1InputStream) throws IOException {
    ASN1Primitive aSN1Primitive = paramASN1InputStream.readObject();
    if (aSN1Primitive instanceof ASN1ObjectIdentifier) {
      this.oid = (ASN1ObjectIdentifier)aSN1Primitive;
    } else {
      throw new IllegalArgumentException("no Oid in CerticateHolderAuthorization");
    } 
    aSN1Primitive = paramASN1InputStream.readObject();
    if (aSN1Primitive instanceof DERApplicationSpecific) {
      this.accessRights = (DERApplicationSpecific)aSN1Primitive;
    } else {
      throw new IllegalArgumentException("No access rights in CerticateHolderAuthorization");
    } 
  }
  
  public CertificateHolderAuthorization(ASN1ObjectIdentifier paramASN1ObjectIdentifier, int paramInt) throws IOException {
    setOid(paramASN1ObjectIdentifier);
    setAccessRights((byte)paramInt);
  }
  
  public CertificateHolderAuthorization(DERApplicationSpecific paramDERApplicationSpecific) throws IOException {
    if (paramDERApplicationSpecific.getApplicationTag() == 76)
      setPrivateData(new ASN1InputStream(paramDERApplicationSpecific.getContents())); 
  }
  
  public int getAccessRights() {
    return this.accessRights.getContents()[0] & 0xFF;
  }
  
  private void setAccessRights(byte paramByte) {
    byte[] arrayOfByte = new byte[1];
    arrayOfByte[0] = paramByte;
    this.accessRights = new DERApplicationSpecific(19, arrayOfByte);
  }
  
  public ASN1ObjectIdentifier getOid() {
    return this.oid;
  }
  
  private void setOid(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    this.oid = paramASN1ObjectIdentifier;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.oid);
    aSN1EncodableVector.add((ASN1Encodable)this.accessRights);
    return (ASN1Primitive)new DERApplicationSpecific(76, aSN1EncodableVector);
  }
  
  static {
    RightsDecodeMap.put(Integers.valueOf(2), "RADG4");
    RightsDecodeMap.put(Integers.valueOf(1), "RADG3");
    AuthorizationRole.put(Integers.valueOf(192), "CVCA");
    AuthorizationRole.put(Integers.valueOf(128), "DV_DOMESTIC");
    AuthorizationRole.put(Integers.valueOf(64), "DV_FOREIGN");
    AuthorizationRole.put(Integers.valueOf(0), "IS");
  }
}
