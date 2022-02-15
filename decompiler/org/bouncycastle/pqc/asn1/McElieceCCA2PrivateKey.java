package org.bouncycastle.pqc.asn1;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.pqc.math.linearalgebra.GF2mField;
import org.bouncycastle.pqc.math.linearalgebra.Permutation;
import org.bouncycastle.pqc.math.linearalgebra.PolynomialGF2mSmallM;

public class McElieceCCA2PrivateKey extends ASN1Object {
  private int n;
  
  private int k;
  
  private byte[] encField;
  
  private byte[] encGp;
  
  private byte[] encP;
  
  private AlgorithmIdentifier digest;
  
  public McElieceCCA2PrivateKey(int paramInt1, int paramInt2, GF2mField paramGF2mField, PolynomialGF2mSmallM paramPolynomialGF2mSmallM, Permutation paramPermutation, AlgorithmIdentifier paramAlgorithmIdentifier) {
    this.n = paramInt1;
    this.k = paramInt2;
    this.encField = paramGF2mField.getEncoded();
    this.encGp = paramPolynomialGF2mSmallM.getEncoded();
    this.encP = paramPermutation.getEncoded();
    this.digest = paramAlgorithmIdentifier;
  }
  
  private McElieceCCA2PrivateKey(ASN1Sequence paramASN1Sequence) {
    BigInteger bigInteger1 = ((ASN1Integer)paramASN1Sequence.getObjectAt(0)).getValue();
    this.n = bigInteger1.intValue();
    BigInteger bigInteger2 = ((ASN1Integer)paramASN1Sequence.getObjectAt(1)).getValue();
    this.k = bigInteger2.intValue();
    this.encField = ((ASN1OctetString)paramASN1Sequence.getObjectAt(2)).getOctets();
    this.encGp = ((ASN1OctetString)paramASN1Sequence.getObjectAt(3)).getOctets();
    this.encP = ((ASN1OctetString)paramASN1Sequence.getObjectAt(4)).getOctets();
    this.digest = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(5));
  }
  
  public int getN() {
    return this.n;
  }
  
  public int getK() {
    return this.k;
  }
  
  public GF2mField getField() {
    return new GF2mField(this.encField);
  }
  
  public PolynomialGF2mSmallM getGoppaPoly() {
    return new PolynomialGF2mSmallM(getField(), this.encGp);
  }
  
  public Permutation getP() {
    return new Permutation(this.encP);
  }
  
  public AlgorithmIdentifier getDigest() {
    return this.digest;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(this.n));
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(this.k));
    aSN1EncodableVector.add((ASN1Encodable)new DEROctetString(this.encField));
    aSN1EncodableVector.add((ASN1Encodable)new DEROctetString(this.encGp));
    aSN1EncodableVector.add((ASN1Encodable)new DEROctetString(this.encP));
    aSN1EncodableVector.add((ASN1Encodable)this.digest);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  public static McElieceCCA2PrivateKey getInstance(Object paramObject) {
    return (paramObject instanceof McElieceCCA2PrivateKey) ? (McElieceCCA2PrivateKey)paramObject : ((paramObject != null) ? new McElieceCCA2PrivateKey(ASN1Sequence.getInstance(paramObject)) : null);
  }
}
