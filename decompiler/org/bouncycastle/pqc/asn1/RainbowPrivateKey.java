package org.bouncycastle.pqc.asn1;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.pqc.crypto.rainbow.Layer;
import org.bouncycastle.pqc.crypto.rainbow.util.RainbowUtil;

public class RainbowPrivateKey extends ASN1Object {
  private ASN1Integer version;
  
  private ASN1ObjectIdentifier oid;
  
  private byte[][] invA1;
  
  private byte[] b1;
  
  private byte[][] invA2;
  
  private byte[] b2;
  
  private byte[] vi;
  
  private Layer[] layers;
  
  private RainbowPrivateKey(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.getObjectAt(0) instanceof ASN1Integer) {
      this.version = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(0));
    } else {
      this.oid = ASN1ObjectIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
    } 
    ASN1Sequence aSN1Sequence1 = (ASN1Sequence)paramASN1Sequence.getObjectAt(1);
    this.invA1 = new byte[aSN1Sequence1.size()][];
    for (byte b1 = 0; b1 < aSN1Sequence1.size(); b1++)
      this.invA1[b1] = ((ASN1OctetString)aSN1Sequence1.getObjectAt(b1)).getOctets(); 
    ASN1Sequence aSN1Sequence2 = (ASN1Sequence)paramASN1Sequence.getObjectAt(2);
    this.b1 = ((ASN1OctetString)aSN1Sequence2.getObjectAt(0)).getOctets();
    ASN1Sequence aSN1Sequence3 = (ASN1Sequence)paramASN1Sequence.getObjectAt(3);
    this.invA2 = new byte[aSN1Sequence3.size()][];
    for (byte b2 = 0; b2 < aSN1Sequence3.size(); b2++)
      this.invA2[b2] = ((ASN1OctetString)aSN1Sequence3.getObjectAt(b2)).getOctets(); 
    ASN1Sequence aSN1Sequence4 = (ASN1Sequence)paramASN1Sequence.getObjectAt(4);
    this.b2 = ((ASN1OctetString)aSN1Sequence4.getObjectAt(0)).getOctets();
    ASN1Sequence aSN1Sequence5 = (ASN1Sequence)paramASN1Sequence.getObjectAt(5);
    this.vi = ((ASN1OctetString)aSN1Sequence5.getObjectAt(0)).getOctets();
    ASN1Sequence aSN1Sequence6 = (ASN1Sequence)paramASN1Sequence.getObjectAt(6);
    byte[][][][] arrayOfByte1 = new byte[aSN1Sequence6.size()][][][];
    byte[][][][] arrayOfByte2 = new byte[aSN1Sequence6.size()][][][];
    byte[][][] arrayOfByte = new byte[aSN1Sequence6.size()][][];
    byte[][] arrayOfByte3 = new byte[aSN1Sequence6.size()][];
    int i;
    for (i = 0; i < aSN1Sequence6.size(); i++) {
      ASN1Sequence aSN1Sequence7 = (ASN1Sequence)aSN1Sequence6.getObjectAt(i);
      ASN1Sequence aSN1Sequence8 = (ASN1Sequence)aSN1Sequence7.getObjectAt(0);
      arrayOfByte1[i] = new byte[aSN1Sequence8.size()][][];
      for (byte b4 = 0; b4 < aSN1Sequence8.size(); b4++) {
        ASN1Sequence aSN1Sequence = (ASN1Sequence)aSN1Sequence8.getObjectAt(b4);
        arrayOfByte1[i][b4] = new byte[aSN1Sequence.size()][];
        for (byte b = 0; b < aSN1Sequence.size(); b++)
          arrayOfByte1[i][b4][b] = ((ASN1OctetString)aSN1Sequence.getObjectAt(b)).getOctets(); 
      } 
      ASN1Sequence aSN1Sequence9 = (ASN1Sequence)aSN1Sequence7.getObjectAt(1);
      arrayOfByte2[i] = new byte[aSN1Sequence9.size()][][];
      for (byte b5 = 0; b5 < aSN1Sequence9.size(); b5++) {
        ASN1Sequence aSN1Sequence = (ASN1Sequence)aSN1Sequence9.getObjectAt(b5);
        arrayOfByte2[i][b5] = new byte[aSN1Sequence.size()][];
        for (byte b = 0; b < aSN1Sequence.size(); b++)
          arrayOfByte2[i][b5][b] = ((ASN1OctetString)aSN1Sequence.getObjectAt(b)).getOctets(); 
      } 
      ASN1Sequence aSN1Sequence10 = (ASN1Sequence)aSN1Sequence7.getObjectAt(2);
      arrayOfByte[i] = new byte[aSN1Sequence10.size()][];
      for (byte b6 = 0; b6 < aSN1Sequence10.size(); b6++)
        arrayOfByte[i][b6] = ((ASN1OctetString)aSN1Sequence10.getObjectAt(b6)).getOctets(); 
      arrayOfByte3[i] = ((ASN1OctetString)aSN1Sequence7.getObjectAt(3)).getOctets();
    } 
    i = this.vi.length - 1;
    this.layers = new Layer[i];
    for (byte b3 = 0; b3 < i; b3++) {
      Layer layer = new Layer(this.vi[b3], this.vi[b3 + 1], RainbowUtil.convertArray(arrayOfByte1[b3]), RainbowUtil.convertArray(arrayOfByte2[b3]), RainbowUtil.convertArray(arrayOfByte[b3]), RainbowUtil.convertArray(arrayOfByte3[b3]));
      this.layers[b3] = layer;
    } 
  }
  
  public RainbowPrivateKey(short[][] paramArrayOfshort1, short[] paramArrayOfshort2, short[][] paramArrayOfshort3, short[] paramArrayOfshort4, int[] paramArrayOfint, Layer[] paramArrayOfLayer) {
    this.version = new ASN1Integer(1L);
    this.invA1 = RainbowUtil.convertArray(paramArrayOfshort1);
    this.b1 = RainbowUtil.convertArray(paramArrayOfshort2);
    this.invA2 = RainbowUtil.convertArray(paramArrayOfshort3);
    this.b2 = RainbowUtil.convertArray(paramArrayOfshort4);
    this.vi = RainbowUtil.convertIntArray(paramArrayOfint);
    this.layers = paramArrayOfLayer;
  }
  
  public static RainbowPrivateKey getInstance(Object paramObject) {
    return (paramObject instanceof RainbowPrivateKey) ? (RainbowPrivateKey)paramObject : ((paramObject != null) ? new RainbowPrivateKey(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1Integer getVersion() {
    return this.version;
  }
  
  public short[][] getInvA1() {
    return RainbowUtil.convertArray(this.invA1);
  }
  
  public short[] getB1() {
    return RainbowUtil.convertArray(this.b1);
  }
  
  public short[] getB2() {
    return RainbowUtil.convertArray(this.b2);
  }
  
  public short[][] getInvA2() {
    return RainbowUtil.convertArray(this.invA2);
  }
  
  public Layer[] getLayers() {
    return this.layers;
  }
  
  public int[] getVi() {
    return RainbowUtil.convertArraytoInt(this.vi);
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector1 = new ASN1EncodableVector();
    if (this.version != null) {
      aSN1EncodableVector1.add((ASN1Encodable)this.version);
    } else {
      aSN1EncodableVector1.add((ASN1Encodable)this.oid);
    } 
    ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
    for (byte b1 = 0; b1 < this.invA1.length; b1++)
      aSN1EncodableVector2.add((ASN1Encodable)new DEROctetString(this.invA1[b1])); 
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector2));
    ASN1EncodableVector aSN1EncodableVector3 = new ASN1EncodableVector();
    aSN1EncodableVector3.add((ASN1Encodable)new DEROctetString(this.b1));
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector3));
    ASN1EncodableVector aSN1EncodableVector4 = new ASN1EncodableVector();
    for (byte b2 = 0; b2 < this.invA2.length; b2++)
      aSN1EncodableVector4.add((ASN1Encodable)new DEROctetString(this.invA2[b2])); 
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector4));
    ASN1EncodableVector aSN1EncodableVector5 = new ASN1EncodableVector();
    aSN1EncodableVector5.add((ASN1Encodable)new DEROctetString(this.b2));
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector5));
    ASN1EncodableVector aSN1EncodableVector6 = new ASN1EncodableVector();
    aSN1EncodableVector6.add((ASN1Encodable)new DEROctetString(this.vi));
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector6));
    ASN1EncodableVector aSN1EncodableVector7 = new ASN1EncodableVector();
    for (byte b3 = 0; b3 < this.layers.length; b3++) {
      ASN1EncodableVector aSN1EncodableVector8 = new ASN1EncodableVector();
      byte[][][] arrayOfByte1 = RainbowUtil.convertArray(this.layers[b3].getCoeffAlpha());
      ASN1EncodableVector aSN1EncodableVector9 = new ASN1EncodableVector();
      for (byte b4 = 0; b4 < arrayOfByte1.length; b4++) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        for (byte b = 0; b < (arrayOfByte1[b4]).length; b++)
          aSN1EncodableVector.add((ASN1Encodable)new DEROctetString(arrayOfByte1[b4][b])); 
        aSN1EncodableVector9.add((ASN1Encodable)new DERSequence(aSN1EncodableVector));
      } 
      aSN1EncodableVector8.add((ASN1Encodable)new DERSequence(aSN1EncodableVector9));
      byte[][][] arrayOfByte2 = RainbowUtil.convertArray(this.layers[b3].getCoeffBeta());
      ASN1EncodableVector aSN1EncodableVector10 = new ASN1EncodableVector();
      for (byte b5 = 0; b5 < arrayOfByte2.length; b5++) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        for (byte b = 0; b < (arrayOfByte2[b5]).length; b++)
          aSN1EncodableVector.add((ASN1Encodable)new DEROctetString(arrayOfByte2[b5][b])); 
        aSN1EncodableVector10.add((ASN1Encodable)new DERSequence(aSN1EncodableVector));
      } 
      aSN1EncodableVector8.add((ASN1Encodable)new DERSequence(aSN1EncodableVector10));
      byte[][] arrayOfByte = RainbowUtil.convertArray(this.layers[b3].getCoeffGamma());
      ASN1EncodableVector aSN1EncodableVector11 = new ASN1EncodableVector();
      for (byte b6 = 0; b6 < arrayOfByte.length; b6++)
        aSN1EncodableVector11.add((ASN1Encodable)new DEROctetString(arrayOfByte[b6])); 
      aSN1EncodableVector8.add((ASN1Encodable)new DERSequence(aSN1EncodableVector11));
      aSN1EncodableVector8.add((ASN1Encodable)new DEROctetString(RainbowUtil.convertArray(this.layers[b3].getCoeffEta())));
      aSN1EncodableVector7.add((ASN1Encodable)new DERSequence(aSN1EncodableVector8));
    } 
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector7));
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector1);
  }
}
