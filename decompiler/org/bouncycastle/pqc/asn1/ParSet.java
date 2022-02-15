package org.bouncycastle.pqc.asn1;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class ParSet extends ASN1Object {
  private static final BigInteger ZERO = BigInteger.valueOf(0L);
  
  private int t;
  
  private int[] h;
  
  private int[] w;
  
  private int[] k;
  
  private static int checkBigIntegerInIntRangeAndPositive(BigInteger paramBigInteger) {
    if (paramBigInteger.compareTo(BigInteger.valueOf(2147483647L)) > 0 || paramBigInteger.compareTo(ZERO) <= 0)
      throw new IllegalArgumentException("BigInteger not in Range: " + paramBigInteger.toString()); 
    return paramBigInteger.intValue();
  }
  
  private ParSet(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 4)
      throw new IllegalArgumentException("sie of seqOfParams = " + paramASN1Sequence.size()); 
    BigInteger bigInteger = ((ASN1Integer)paramASN1Sequence.getObjectAt(0)).getValue();
    this.t = checkBigIntegerInIntRangeAndPositive(bigInteger);
    ASN1Sequence aSN1Sequence1 = (ASN1Sequence)paramASN1Sequence.getObjectAt(1);
    ASN1Sequence aSN1Sequence2 = (ASN1Sequence)paramASN1Sequence.getObjectAt(2);
    ASN1Sequence aSN1Sequence3 = (ASN1Sequence)paramASN1Sequence.getObjectAt(3);
    if (aSN1Sequence1.size() != this.t || aSN1Sequence2.size() != this.t || aSN1Sequence3.size() != this.t)
      throw new IllegalArgumentException("invalid size of sequences"); 
    this.h = new int[aSN1Sequence1.size()];
    this.w = new int[aSN1Sequence2.size()];
    this.k = new int[aSN1Sequence3.size()];
    for (byte b = 0; b < this.t; b++) {
      this.h[b] = checkBigIntegerInIntRangeAndPositive(((ASN1Integer)aSN1Sequence1.getObjectAt(b)).getValue());
      this.w[b] = checkBigIntegerInIntRangeAndPositive(((ASN1Integer)aSN1Sequence2.getObjectAt(b)).getValue());
      this.k[b] = checkBigIntegerInIntRangeAndPositive(((ASN1Integer)aSN1Sequence3.getObjectAt(b)).getValue());
    } 
  }
  
  public ParSet(int paramInt, int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3) {
    this.t = paramInt;
    this.h = paramArrayOfint1;
    this.w = paramArrayOfint2;
    this.k = paramArrayOfint3;
  }
  
  public static ParSet getInstance(Object paramObject) {
    return (paramObject instanceof ParSet) ? (ParSet)paramObject : ((paramObject != null) ? new ParSet(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public int getT() {
    return this.t;
  }
  
  public int[] getH() {
    return Arrays.clone(this.h);
  }
  
  public int[] getW() {
    return Arrays.clone(this.w);
  }
  
  public int[] getK() {
    return Arrays.clone(this.k);
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector1 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector3 = new ASN1EncodableVector();
    for (byte b = 0; b < this.h.length; b++) {
      aSN1EncodableVector1.add((ASN1Encodable)new ASN1Integer(this.h[b]));
      aSN1EncodableVector2.add((ASN1Encodable)new ASN1Integer(this.w[b]));
      aSN1EncodableVector3.add((ASN1Encodable)new ASN1Integer(this.k[b]));
    } 
    ASN1EncodableVector aSN1EncodableVector4 = new ASN1EncodableVector();
    aSN1EncodableVector4.add((ASN1Encodable)new ASN1Integer(this.t));
    aSN1EncodableVector4.add((ASN1Encodable)new DERSequence(aSN1EncodableVector1));
    aSN1EncodableVector4.add((ASN1Encodable)new DERSequence(aSN1EncodableVector2));
    aSN1EncodableVector4.add((ASN1Encodable)new DERSequence(aSN1EncodableVector3));
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector4);
  }
}
