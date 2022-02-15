package org.bouncycastle.pqc.asn1;

import java.math.BigInteger;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.pqc.crypto.gmss.GMSSLeaf;
import org.bouncycastle.pqc.crypto.gmss.GMSSParameters;
import org.bouncycastle.pqc.crypto.gmss.GMSSRootCalc;
import org.bouncycastle.pqc.crypto.gmss.GMSSRootSig;
import org.bouncycastle.pqc.crypto.gmss.Treehash;

public class GMSSPrivateKey extends ASN1Object {
  private ASN1Primitive primitive;
  
  private GMSSPrivateKey(ASN1Sequence paramASN1Sequence) {
    ASN1Sequence aSN1Sequence1 = (ASN1Sequence)paramASN1Sequence.getObjectAt(0);
    int[] arrayOfInt = new int[aSN1Sequence1.size()];
    for (byte b1 = 0; b1 < aSN1Sequence1.size(); b1++)
      arrayOfInt[b1] = checkBigIntegerInIntRange(aSN1Sequence1.getObjectAt(b1)); 
    ASN1Sequence aSN1Sequence2 = (ASN1Sequence)paramASN1Sequence.getObjectAt(1);
    byte[][] arrayOfByte1 = new byte[aSN1Sequence2.size()][];
    for (byte b2 = 0; b2 < arrayOfByte1.length; b2++)
      arrayOfByte1[b2] = ((DEROctetString)aSN1Sequence2.getObjectAt(b2)).getOctets(); 
    ASN1Sequence aSN1Sequence3 = (ASN1Sequence)paramASN1Sequence.getObjectAt(2);
    byte[][] arrayOfByte2 = new byte[aSN1Sequence3.size()][];
    for (byte b3 = 0; b3 < arrayOfByte2.length; b3++)
      arrayOfByte2[b3] = ((DEROctetString)aSN1Sequence3.getObjectAt(b3)).getOctets(); 
    ASN1Sequence aSN1Sequence4 = (ASN1Sequence)paramASN1Sequence.getObjectAt(3);
    byte[][][] arrayOfByte3 = new byte[aSN1Sequence4.size()][][];
    for (byte b4 = 0; b4 < arrayOfByte3.length; b4++) {
      ASN1Sequence aSN1Sequence = (ASN1Sequence)aSN1Sequence4.getObjectAt(b4);
      arrayOfByte3[b4] = new byte[aSN1Sequence.size()][];
      for (byte b = 0; b < (arrayOfByte3[b4]).length; b++)
        arrayOfByte3[b4][b] = ((DEROctetString)aSN1Sequence.getObjectAt(b)).getOctets(); 
    } 
    ASN1Sequence aSN1Sequence5 = (ASN1Sequence)paramASN1Sequence.getObjectAt(4);
    byte[][][] arrayOfByte4 = new byte[aSN1Sequence5.size()][][];
    for (byte b5 = 0; b5 < arrayOfByte4.length; b5++) {
      ASN1Sequence aSN1Sequence = (ASN1Sequence)aSN1Sequence5.getObjectAt(b5);
      arrayOfByte4[b5] = new byte[aSN1Sequence.size()][];
      for (byte b = 0; b < (arrayOfByte4[b5]).length; b++)
        arrayOfByte4[b5][b] = ((DEROctetString)aSN1Sequence.getObjectAt(b)).getOctets(); 
    } 
    ASN1Sequence aSN1Sequence6 = (ASN1Sequence)paramASN1Sequence.getObjectAt(5);
    Treehash[][] arrayOfTreehash = new Treehash[aSN1Sequence6.size()][];
  }
  
  public GMSSPrivateKey(int[] paramArrayOfint1, byte[][] paramArrayOfbyte1, byte[][] paramArrayOfbyte2, byte[][][] paramArrayOfbyte3, byte[][][] paramArrayOfbyte4, Treehash[][] paramArrayOfTreehash1, Treehash[][] paramArrayOfTreehash2, Vector[] paramArrayOfVector1, Vector[] paramArrayOfVector2, Vector[][] paramArrayOfVector3, Vector[][] paramArrayOfVector4, byte[][][] paramArrayOfbyte5, GMSSLeaf[] paramArrayOfGMSSLeaf1, GMSSLeaf[] paramArrayOfGMSSLeaf2, GMSSLeaf[] paramArrayOfGMSSLeaf3, int[] paramArrayOfint2, byte[][] paramArrayOfbyte6, GMSSRootCalc[] paramArrayOfGMSSRootCalc, byte[][] paramArrayOfbyte7, GMSSRootSig[] paramArrayOfGMSSRootSig, GMSSParameters paramGMSSParameters, AlgorithmIdentifier paramAlgorithmIdentifier) {
    AlgorithmIdentifier[] arrayOfAlgorithmIdentifier = { paramAlgorithmIdentifier };
    this.primitive = encode(paramArrayOfint1, paramArrayOfbyte1, paramArrayOfbyte2, paramArrayOfbyte3, paramArrayOfbyte4, paramArrayOfbyte5, paramArrayOfTreehash1, paramArrayOfTreehash2, paramArrayOfVector1, paramArrayOfVector2, paramArrayOfVector3, paramArrayOfVector4, paramArrayOfGMSSLeaf1, paramArrayOfGMSSLeaf2, paramArrayOfGMSSLeaf3, paramArrayOfint2, paramArrayOfbyte6, paramArrayOfGMSSRootCalc, paramArrayOfbyte7, paramArrayOfGMSSRootSig, paramGMSSParameters, arrayOfAlgorithmIdentifier);
  }
  
  private ASN1Primitive encode(int[] paramArrayOfint1, byte[][] paramArrayOfbyte1, byte[][] paramArrayOfbyte2, byte[][][] paramArrayOfbyte3, byte[][][] paramArrayOfbyte4, byte[][][] paramArrayOfbyte5, Treehash[][] paramArrayOfTreehash1, Treehash[][] paramArrayOfTreehash2, Vector[] paramArrayOfVector1, Vector[] paramArrayOfVector2, Vector[][] paramArrayOfVector3, Vector[][] paramArrayOfVector4, GMSSLeaf[] paramArrayOfGMSSLeaf1, GMSSLeaf[] paramArrayOfGMSSLeaf2, GMSSLeaf[] paramArrayOfGMSSLeaf3, int[] paramArrayOfint2, byte[][] paramArrayOfbyte6, GMSSRootCalc[] paramArrayOfGMSSRootCalc, byte[][] paramArrayOfbyte7, GMSSRootSig[] paramArrayOfGMSSRootSig, GMSSParameters paramGMSSParameters, AlgorithmIdentifier[] paramArrayOfAlgorithmIdentifier) {
    ASN1EncodableVector aSN1EncodableVector1 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
    for (byte b1 = 0; b1 < paramArrayOfint1.length; b1++)
      aSN1EncodableVector2.add((ASN1Encodable)new ASN1Integer(paramArrayOfint1[b1])); 
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector2));
    ASN1EncodableVector aSN1EncodableVector3 = new ASN1EncodableVector();
    for (byte b2 = 0; b2 < paramArrayOfbyte1.length; b2++)
      aSN1EncodableVector3.add((ASN1Encodable)new DEROctetString(paramArrayOfbyte1[b2])); 
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector3));
    ASN1EncodableVector aSN1EncodableVector4 = new ASN1EncodableVector();
    for (byte b3 = 0; b3 < paramArrayOfbyte2.length; b3++)
      aSN1EncodableVector4.add((ASN1Encodable)new DEROctetString(paramArrayOfbyte2[b3])); 
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector4));
    ASN1EncodableVector aSN1EncodableVector5 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector6 = new ASN1EncodableVector();
    for (byte b4 = 0; b4 < paramArrayOfbyte3.length; b4++) {
      for (byte b = 0; b < (paramArrayOfbyte3[b4]).length; b++)
        aSN1EncodableVector5.add((ASN1Encodable)new DEROctetString(paramArrayOfbyte3[b4][b])); 
      aSN1EncodableVector6.add((ASN1Encodable)new DERSequence(aSN1EncodableVector5));
      aSN1EncodableVector5 = new ASN1EncodableVector();
    } 
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector6));
    ASN1EncodableVector aSN1EncodableVector7 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector8 = new ASN1EncodableVector();
    for (byte b5 = 0; b5 < paramArrayOfbyte4.length; b5++) {
      for (byte b = 0; b < (paramArrayOfbyte4[b5]).length; b++)
        aSN1EncodableVector7.add((ASN1Encodable)new DEROctetString(paramArrayOfbyte4[b5][b])); 
      aSN1EncodableVector8.add((ASN1Encodable)new DERSequence(aSN1EncodableVector7));
      aSN1EncodableVector7 = new ASN1EncodableVector();
    } 
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector8));
    ASN1EncodableVector aSN1EncodableVector9 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector10 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector11 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector12 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector13 = new ASN1EncodableVector();
    byte b6;
    for (b6 = 0; b6 < paramArrayOfTreehash1.length; b6++) {
      for (byte b = 0; b < (paramArrayOfTreehash1[b6]).length; b++) {
        aSN1EncodableVector11.add((ASN1Encodable)new DERSequence((ASN1Encodable)paramArrayOfAlgorithmIdentifier[0]));
        int i = paramArrayOfTreehash1[b6][b].getStatInt()[1];
        aSN1EncodableVector12.add((ASN1Encodable)new DEROctetString(paramArrayOfTreehash1[b6][b].getStatByte()[0]));
        aSN1EncodableVector12.add((ASN1Encodable)new DEROctetString(paramArrayOfTreehash1[b6][b].getStatByte()[1]));
        aSN1EncodableVector12.add((ASN1Encodable)new DEROctetString(paramArrayOfTreehash1[b6][b].getStatByte()[2]));
        byte b22;
        for (b22 = 0; b22 < i; b22++)
          aSN1EncodableVector12.add((ASN1Encodable)new DEROctetString(paramArrayOfTreehash1[b6][b].getStatByte()[3 + b22])); 
        aSN1EncodableVector11.add((ASN1Encodable)new DERSequence(aSN1EncodableVector12));
        aSN1EncodableVector12 = new ASN1EncodableVector();
        aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(paramArrayOfTreehash1[b6][b].getStatInt()[0]));
        aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(i));
        aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(paramArrayOfTreehash1[b6][b].getStatInt()[2]));
        aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(paramArrayOfTreehash1[b6][b].getStatInt()[3]));
        aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(paramArrayOfTreehash1[b6][b].getStatInt()[4]));
        aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(paramArrayOfTreehash1[b6][b].getStatInt()[5]));
        for (b22 = 0; b22 < i; b22++)
          aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(paramArrayOfTreehash1[b6][b].getStatInt()[6 + b22])); 
        aSN1EncodableVector11.add((ASN1Encodable)new DERSequence(aSN1EncodableVector13));
        aSN1EncodableVector13 = new ASN1EncodableVector();
        aSN1EncodableVector10.add((ASN1Encodable)new DERSequence(aSN1EncodableVector11));
        aSN1EncodableVector11 = new ASN1EncodableVector();
      } 
      aSN1EncodableVector9.add((ASN1Encodable)new DERSequence(aSN1EncodableVector10));
      aSN1EncodableVector10 = new ASN1EncodableVector();
    } 
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector9));
    aSN1EncodableVector9 = new ASN1EncodableVector();
    aSN1EncodableVector10 = new ASN1EncodableVector();
    aSN1EncodableVector11 = new ASN1EncodableVector();
    aSN1EncodableVector12 = new ASN1EncodableVector();
    aSN1EncodableVector13 = new ASN1EncodableVector();
    for (b6 = 0; b6 < paramArrayOfTreehash2.length; b6++) {
      for (byte b = 0; b < (paramArrayOfTreehash2[b6]).length; b++) {
        aSN1EncodableVector11.add((ASN1Encodable)new DERSequence((ASN1Encodable)paramArrayOfAlgorithmIdentifier[0]));
        int i = paramArrayOfTreehash2[b6][b].getStatInt()[1];
        aSN1EncodableVector12.add((ASN1Encodable)new DEROctetString(paramArrayOfTreehash2[b6][b].getStatByte()[0]));
        aSN1EncodableVector12.add((ASN1Encodable)new DEROctetString(paramArrayOfTreehash2[b6][b].getStatByte()[1]));
        aSN1EncodableVector12.add((ASN1Encodable)new DEROctetString(paramArrayOfTreehash2[b6][b].getStatByte()[2]));
        byte b22;
        for (b22 = 0; b22 < i; b22++)
          aSN1EncodableVector12.add((ASN1Encodable)new DEROctetString(paramArrayOfTreehash2[b6][b].getStatByte()[3 + b22])); 
        aSN1EncodableVector11.add((ASN1Encodable)new DERSequence(aSN1EncodableVector12));
        aSN1EncodableVector12 = new ASN1EncodableVector();
        aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(paramArrayOfTreehash2[b6][b].getStatInt()[0]));
        aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(i));
        aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(paramArrayOfTreehash2[b6][b].getStatInt()[2]));
        aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(paramArrayOfTreehash2[b6][b].getStatInt()[3]));
        aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(paramArrayOfTreehash2[b6][b].getStatInt()[4]));
        aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(paramArrayOfTreehash2[b6][b].getStatInt()[5]));
        for (b22 = 0; b22 < i; b22++)
          aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(paramArrayOfTreehash2[b6][b].getStatInt()[6 + b22])); 
        aSN1EncodableVector11.add((ASN1Encodable)new DERSequence(aSN1EncodableVector13));
        aSN1EncodableVector13 = new ASN1EncodableVector();
        aSN1EncodableVector10.add((ASN1Encodable)new DERSequence(aSN1EncodableVector11));
        aSN1EncodableVector11 = new ASN1EncodableVector();
      } 
      aSN1EncodableVector9.add((ASN1Encodable)new DERSequence((ASN1Encodable)new DERSequence(aSN1EncodableVector10)));
      aSN1EncodableVector10 = new ASN1EncodableVector();
    } 
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector9));
    ASN1EncodableVector aSN1EncodableVector14 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector15 = new ASN1EncodableVector();
    for (byte b7 = 0; b7 < paramArrayOfbyte5.length; b7++) {
      for (byte b = 0; b < (paramArrayOfbyte5[b7]).length; b++)
        aSN1EncodableVector14.add((ASN1Encodable)new DEROctetString(paramArrayOfbyte5[b7][b])); 
      aSN1EncodableVector15.add((ASN1Encodable)new DERSequence(aSN1EncodableVector14));
      aSN1EncodableVector14 = new ASN1EncodableVector();
    } 
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector15));
    ASN1EncodableVector aSN1EncodableVector16 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector17 = new ASN1EncodableVector();
    for (byte b8 = 0; b8 < paramArrayOfVector1.length; b8++) {
      for (byte b = 0; b < paramArrayOfVector1[b8].size(); b++)
        aSN1EncodableVector16.add((ASN1Encodable)new DEROctetString(paramArrayOfVector1[b8].elementAt(b))); 
      aSN1EncodableVector17.add((ASN1Encodable)new DERSequence(aSN1EncodableVector16));
      aSN1EncodableVector16 = new ASN1EncodableVector();
    } 
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector17));
    ASN1EncodableVector aSN1EncodableVector18 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector19 = new ASN1EncodableVector();
    for (byte b9 = 0; b9 < paramArrayOfVector2.length; b9++) {
      for (byte b = 0; b < paramArrayOfVector2[b9].size(); b++)
        aSN1EncodableVector18.add((ASN1Encodable)new DEROctetString(paramArrayOfVector2[b9].elementAt(b))); 
      aSN1EncodableVector19.add((ASN1Encodable)new DERSequence(aSN1EncodableVector18));
      aSN1EncodableVector18 = new ASN1EncodableVector();
    } 
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector19));
    ASN1EncodableVector aSN1EncodableVector20 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector21 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector22 = new ASN1EncodableVector();
    for (byte b10 = 0; b10 < paramArrayOfVector3.length; b10++) {
      for (byte b = 0; b < (paramArrayOfVector3[b10]).length; b++) {
        for (byte b22 = 0; b22 < paramArrayOfVector3[b10][b].size(); b22++)
          aSN1EncodableVector20.add((ASN1Encodable)new DEROctetString(paramArrayOfVector3[b10][b].elementAt(b22))); 
        aSN1EncodableVector21.add((ASN1Encodable)new DERSequence(aSN1EncodableVector20));
        aSN1EncodableVector20 = new ASN1EncodableVector();
      } 
      aSN1EncodableVector22.add((ASN1Encodable)new DERSequence(aSN1EncodableVector21));
      aSN1EncodableVector21 = new ASN1EncodableVector();
    } 
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector22));
    ASN1EncodableVector aSN1EncodableVector23 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector24 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector25 = new ASN1EncodableVector();
    for (byte b11 = 0; b11 < paramArrayOfVector4.length; b11++) {
      for (byte b = 0; b < (paramArrayOfVector4[b11]).length; b++) {
        for (byte b22 = 0; b22 < paramArrayOfVector4[b11][b].size(); b22++)
          aSN1EncodableVector23.add((ASN1Encodable)new DEROctetString(paramArrayOfVector4[b11][b].elementAt(b22))); 
        aSN1EncodableVector24.add((ASN1Encodable)new DERSequence(aSN1EncodableVector23));
        aSN1EncodableVector23 = new ASN1EncodableVector();
      } 
      aSN1EncodableVector25.add((ASN1Encodable)new DERSequence(aSN1EncodableVector24));
      aSN1EncodableVector24 = new ASN1EncodableVector();
    } 
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector25));
    ASN1EncodableVector aSN1EncodableVector26 = new ASN1EncodableVector();
    aSN1EncodableVector11 = new ASN1EncodableVector();
    aSN1EncodableVector12 = new ASN1EncodableVector();
    aSN1EncodableVector13 = new ASN1EncodableVector();
    for (byte b12 = 0; b12 < paramArrayOfGMSSLeaf1.length; b12++) {
      aSN1EncodableVector11.add((ASN1Encodable)new DERSequence((ASN1Encodable)paramArrayOfAlgorithmIdentifier[0]));
      byte[][] arrayOfByte = paramArrayOfGMSSLeaf1[b12].getStatByte();
      aSN1EncodableVector12.add((ASN1Encodable)new DEROctetString(arrayOfByte[0]));
      aSN1EncodableVector12.add((ASN1Encodable)new DEROctetString(arrayOfByte[1]));
      aSN1EncodableVector12.add((ASN1Encodable)new DEROctetString(arrayOfByte[2]));
      aSN1EncodableVector12.add((ASN1Encodable)new DEROctetString(arrayOfByte[3]));
      aSN1EncodableVector11.add((ASN1Encodable)new DERSequence(aSN1EncodableVector12));
      aSN1EncodableVector12 = new ASN1EncodableVector();
      int[] arrayOfInt = paramArrayOfGMSSLeaf1[b12].getStatInt();
      aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(arrayOfInt[0]));
      aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(arrayOfInt[1]));
      aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(arrayOfInt[2]));
      aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(arrayOfInt[3]));
      aSN1EncodableVector11.add((ASN1Encodable)new DERSequence(aSN1EncodableVector13));
      aSN1EncodableVector13 = new ASN1EncodableVector();
      aSN1EncodableVector26.add((ASN1Encodable)new DERSequence(aSN1EncodableVector11));
      aSN1EncodableVector11 = new ASN1EncodableVector();
    } 
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector26));
    ASN1EncodableVector aSN1EncodableVector27 = new ASN1EncodableVector();
    aSN1EncodableVector11 = new ASN1EncodableVector();
    aSN1EncodableVector12 = new ASN1EncodableVector();
    aSN1EncodableVector13 = new ASN1EncodableVector();
    for (byte b13 = 0; b13 < paramArrayOfGMSSLeaf2.length; b13++) {
      aSN1EncodableVector11.add((ASN1Encodable)new DERSequence((ASN1Encodable)paramArrayOfAlgorithmIdentifier[0]));
      byte[][] arrayOfByte = paramArrayOfGMSSLeaf2[b13].getStatByte();
      aSN1EncodableVector12.add((ASN1Encodable)new DEROctetString(arrayOfByte[0]));
      aSN1EncodableVector12.add((ASN1Encodable)new DEROctetString(arrayOfByte[1]));
      aSN1EncodableVector12.add((ASN1Encodable)new DEROctetString(arrayOfByte[2]));
      aSN1EncodableVector12.add((ASN1Encodable)new DEROctetString(arrayOfByte[3]));
      aSN1EncodableVector11.add((ASN1Encodable)new DERSequence(aSN1EncodableVector12));
      aSN1EncodableVector12 = new ASN1EncodableVector();
      int[] arrayOfInt = paramArrayOfGMSSLeaf2[b13].getStatInt();
      aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(arrayOfInt[0]));
      aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(arrayOfInt[1]));
      aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(arrayOfInt[2]));
      aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(arrayOfInt[3]));
      aSN1EncodableVector11.add((ASN1Encodable)new DERSequence(aSN1EncodableVector13));
      aSN1EncodableVector13 = new ASN1EncodableVector();
      aSN1EncodableVector27.add((ASN1Encodable)new DERSequence(aSN1EncodableVector11));
      aSN1EncodableVector11 = new ASN1EncodableVector();
    } 
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector27));
    ASN1EncodableVector aSN1EncodableVector28 = new ASN1EncodableVector();
    aSN1EncodableVector11 = new ASN1EncodableVector();
    aSN1EncodableVector12 = new ASN1EncodableVector();
    aSN1EncodableVector13 = new ASN1EncodableVector();
    for (byte b14 = 0; b14 < paramArrayOfGMSSLeaf3.length; b14++) {
      aSN1EncodableVector11.add((ASN1Encodable)new DERSequence((ASN1Encodable)paramArrayOfAlgorithmIdentifier[0]));
      byte[][] arrayOfByte = paramArrayOfGMSSLeaf3[b14].getStatByte();
      aSN1EncodableVector12.add((ASN1Encodable)new DEROctetString(arrayOfByte[0]));
      aSN1EncodableVector12.add((ASN1Encodable)new DEROctetString(arrayOfByte[1]));
      aSN1EncodableVector12.add((ASN1Encodable)new DEROctetString(arrayOfByte[2]));
      aSN1EncodableVector12.add((ASN1Encodable)new DEROctetString(arrayOfByte[3]));
      aSN1EncodableVector11.add((ASN1Encodable)new DERSequence(aSN1EncodableVector12));
      aSN1EncodableVector12 = new ASN1EncodableVector();
      int[] arrayOfInt = paramArrayOfGMSSLeaf3[b14].getStatInt();
      aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(arrayOfInt[0]));
      aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(arrayOfInt[1]));
      aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(arrayOfInt[2]));
      aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(arrayOfInt[3]));
      aSN1EncodableVector11.add((ASN1Encodable)new DERSequence(aSN1EncodableVector13));
      aSN1EncodableVector13 = new ASN1EncodableVector();
      aSN1EncodableVector28.add((ASN1Encodable)new DERSequence(aSN1EncodableVector11));
      aSN1EncodableVector11 = new ASN1EncodableVector();
    } 
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector28));
    ASN1EncodableVector aSN1EncodableVector29 = new ASN1EncodableVector();
    for (byte b15 = 0; b15 < paramArrayOfint2.length; b15++)
      aSN1EncodableVector29.add((ASN1Encodable)new ASN1Integer(paramArrayOfint2[b15])); 
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector29));
    ASN1EncodableVector aSN1EncodableVector30 = new ASN1EncodableVector();
    for (byte b16 = 0; b16 < paramArrayOfbyte6.length; b16++)
      aSN1EncodableVector30.add((ASN1Encodable)new DEROctetString(paramArrayOfbyte6[b16])); 
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector30));
    ASN1EncodableVector aSN1EncodableVector31 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector32 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector33 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector34 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector35 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector36 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector37 = new ASN1EncodableVector();
    for (byte b17 = 0; b17 < paramArrayOfGMSSRootCalc.length; b17++) {
      aSN1EncodableVector32.add((ASN1Encodable)new DERSequence((ASN1Encodable)paramArrayOfAlgorithmIdentifier[0]));
      aSN1EncodableVector33 = new ASN1EncodableVector();
      int i = paramArrayOfGMSSRootCalc[b17].getStatInt()[0];
      int j = paramArrayOfGMSSRootCalc[b17].getStatInt()[7];
      aSN1EncodableVector34.add((ASN1Encodable)new DEROctetString(paramArrayOfGMSSRootCalc[b17].getStatByte()[0]));
      byte b;
      for (b = 0; b < i; b++)
        aSN1EncodableVector34.add((ASN1Encodable)new DEROctetString(paramArrayOfGMSSRootCalc[b17].getStatByte()[1 + b])); 
      for (b = 0; b < j; b++)
        aSN1EncodableVector34.add((ASN1Encodable)new DEROctetString(paramArrayOfGMSSRootCalc[b17].getStatByte()[1 + i + b])); 
      aSN1EncodableVector32.add((ASN1Encodable)new DERSequence(aSN1EncodableVector34));
      aSN1EncodableVector34 = new ASN1EncodableVector();
      aSN1EncodableVector35.add((ASN1Encodable)new ASN1Integer(i));
      aSN1EncodableVector35.add((ASN1Encodable)new ASN1Integer(paramArrayOfGMSSRootCalc[b17].getStatInt()[1]));
      aSN1EncodableVector35.add((ASN1Encodable)new ASN1Integer(paramArrayOfGMSSRootCalc[b17].getStatInt()[2]));
      aSN1EncodableVector35.add((ASN1Encodable)new ASN1Integer(paramArrayOfGMSSRootCalc[b17].getStatInt()[3]));
      aSN1EncodableVector35.add((ASN1Encodable)new ASN1Integer(paramArrayOfGMSSRootCalc[b17].getStatInt()[4]));
      aSN1EncodableVector35.add((ASN1Encodable)new ASN1Integer(paramArrayOfGMSSRootCalc[b17].getStatInt()[5]));
      aSN1EncodableVector35.add((ASN1Encodable)new ASN1Integer(paramArrayOfGMSSRootCalc[b17].getStatInt()[6]));
      aSN1EncodableVector35.add((ASN1Encodable)new ASN1Integer(j));
      for (b = 0; b < i; b++)
        aSN1EncodableVector35.add((ASN1Encodable)new ASN1Integer(paramArrayOfGMSSRootCalc[b17].getStatInt()[8 + b])); 
      for (b = 0; b < j; b++)
        aSN1EncodableVector35.add((ASN1Encodable)new ASN1Integer(paramArrayOfGMSSRootCalc[b17].getStatInt()[8 + i + b])); 
      aSN1EncodableVector32.add((ASN1Encodable)new DERSequence(aSN1EncodableVector35));
      aSN1EncodableVector35 = new ASN1EncodableVector();
      aSN1EncodableVector11 = new ASN1EncodableVector();
      aSN1EncodableVector12 = new ASN1EncodableVector();
      aSN1EncodableVector13 = new ASN1EncodableVector();
      if (paramArrayOfGMSSRootCalc[b17].getTreehash() != null)
        for (b = 0; b < (paramArrayOfGMSSRootCalc[b17].getTreehash()).length; b++) {
          aSN1EncodableVector11.add((ASN1Encodable)new DERSequence((ASN1Encodable)paramArrayOfAlgorithmIdentifier[0]));
          j = paramArrayOfGMSSRootCalc[b17].getTreehash()[b].getStatInt()[1];
          aSN1EncodableVector12.add((ASN1Encodable)new DEROctetString(paramArrayOfGMSSRootCalc[b17].getTreehash()[b].getStatByte()[0]));
          aSN1EncodableVector12.add((ASN1Encodable)new DEROctetString(paramArrayOfGMSSRootCalc[b17].getTreehash()[b].getStatByte()[1]));
          aSN1EncodableVector12.add((ASN1Encodable)new DEROctetString(paramArrayOfGMSSRootCalc[b17].getTreehash()[b].getStatByte()[2]));
          byte b22;
          for (b22 = 0; b22 < j; b22++)
            aSN1EncodableVector12.add((ASN1Encodable)new DEROctetString(paramArrayOfGMSSRootCalc[b17].getTreehash()[b].getStatByte()[3 + b22])); 
          aSN1EncodableVector11.add((ASN1Encodable)new DERSequence(aSN1EncodableVector12));
          aSN1EncodableVector12 = new ASN1EncodableVector();
          aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(paramArrayOfGMSSRootCalc[b17].getTreehash()[b].getStatInt()[0]));
          aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(j));
          aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(paramArrayOfGMSSRootCalc[b17].getTreehash()[b].getStatInt()[2]));
          aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(paramArrayOfGMSSRootCalc[b17].getTreehash()[b].getStatInt()[3]));
          aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(paramArrayOfGMSSRootCalc[b17].getTreehash()[b].getStatInt()[4]));
          aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(paramArrayOfGMSSRootCalc[b17].getTreehash()[b].getStatInt()[5]));
          for (b22 = 0; b22 < j; b22++)
            aSN1EncodableVector13.add((ASN1Encodable)new ASN1Integer(paramArrayOfGMSSRootCalc[b17].getTreehash()[b].getStatInt()[6 + b22])); 
          aSN1EncodableVector11.add((ASN1Encodable)new DERSequence(aSN1EncodableVector13));
          aSN1EncodableVector13 = new ASN1EncodableVector();
          aSN1EncodableVector36.add((ASN1Encodable)new DERSequence(aSN1EncodableVector11));
          aSN1EncodableVector11 = new ASN1EncodableVector();
        }  
      aSN1EncodableVector32.add((ASN1Encodable)new DERSequence(aSN1EncodableVector36));
      aSN1EncodableVector36 = new ASN1EncodableVector();
      aSN1EncodableVector20 = new ASN1EncodableVector();
      if (paramArrayOfGMSSRootCalc[b17].getRetain() != null)
        for (b = 0; b < (paramArrayOfGMSSRootCalc[b17].getRetain()).length; b++) {
          for (byte b22 = 0; b22 < paramArrayOfGMSSRootCalc[b17].getRetain()[b].size(); b22++)
            aSN1EncodableVector20.add((ASN1Encodable)new DEROctetString(paramArrayOfGMSSRootCalc[b17].getRetain()[b].elementAt(b22))); 
          aSN1EncodableVector37.add((ASN1Encodable)new DERSequence(aSN1EncodableVector20));
          aSN1EncodableVector20 = new ASN1EncodableVector();
        }  
      aSN1EncodableVector32.add((ASN1Encodable)new DERSequence(aSN1EncodableVector37));
      aSN1EncodableVector37 = new ASN1EncodableVector();
      aSN1EncodableVector31.add((ASN1Encodable)new DERSequence(aSN1EncodableVector32));
      aSN1EncodableVector32 = new ASN1EncodableVector();
    } 
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector31));
    ASN1EncodableVector aSN1EncodableVector38 = new ASN1EncodableVector();
    for (byte b18 = 0; b18 < paramArrayOfbyte7.length; b18++)
      aSN1EncodableVector38.add((ASN1Encodable)new DEROctetString(paramArrayOfbyte7[b18])); 
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector38));
    ASN1EncodableVector aSN1EncodableVector39 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector40 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector41 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector42 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector43 = new ASN1EncodableVector();
    for (byte b19 = 0; b19 < paramArrayOfGMSSRootSig.length; b19++) {
      aSN1EncodableVector40.add((ASN1Encodable)new DERSequence((ASN1Encodable)paramArrayOfAlgorithmIdentifier[0]));
      aSN1EncodableVector41 = new ASN1EncodableVector();
      aSN1EncodableVector42.add((ASN1Encodable)new DEROctetString(paramArrayOfGMSSRootSig[b19].getStatByte()[0]));
      aSN1EncodableVector42.add((ASN1Encodable)new DEROctetString(paramArrayOfGMSSRootSig[b19].getStatByte()[1]));
      aSN1EncodableVector42.add((ASN1Encodable)new DEROctetString(paramArrayOfGMSSRootSig[b19].getStatByte()[2]));
      aSN1EncodableVector42.add((ASN1Encodable)new DEROctetString(paramArrayOfGMSSRootSig[b19].getStatByte()[3]));
      aSN1EncodableVector42.add((ASN1Encodable)new DEROctetString(paramArrayOfGMSSRootSig[b19].getStatByte()[4]));
      aSN1EncodableVector40.add((ASN1Encodable)new DERSequence(aSN1EncodableVector42));
      aSN1EncodableVector42 = new ASN1EncodableVector();
      aSN1EncodableVector43.add((ASN1Encodable)new ASN1Integer(paramArrayOfGMSSRootSig[b19].getStatInt()[0]));
      aSN1EncodableVector43.add((ASN1Encodable)new ASN1Integer(paramArrayOfGMSSRootSig[b19].getStatInt()[1]));
      aSN1EncodableVector43.add((ASN1Encodable)new ASN1Integer(paramArrayOfGMSSRootSig[b19].getStatInt()[2]));
      aSN1EncodableVector43.add((ASN1Encodable)new ASN1Integer(paramArrayOfGMSSRootSig[b19].getStatInt()[3]));
      aSN1EncodableVector43.add((ASN1Encodable)new ASN1Integer(paramArrayOfGMSSRootSig[b19].getStatInt()[4]));
      aSN1EncodableVector43.add((ASN1Encodable)new ASN1Integer(paramArrayOfGMSSRootSig[b19].getStatInt()[5]));
      aSN1EncodableVector43.add((ASN1Encodable)new ASN1Integer(paramArrayOfGMSSRootSig[b19].getStatInt()[6]));
      aSN1EncodableVector43.add((ASN1Encodable)new ASN1Integer(paramArrayOfGMSSRootSig[b19].getStatInt()[7]));
      aSN1EncodableVector43.add((ASN1Encodable)new ASN1Integer(paramArrayOfGMSSRootSig[b19].getStatInt()[8]));
      aSN1EncodableVector40.add((ASN1Encodable)new DERSequence(aSN1EncodableVector43));
      aSN1EncodableVector43 = new ASN1EncodableVector();
      aSN1EncodableVector39.add((ASN1Encodable)new DERSequence(aSN1EncodableVector40));
      aSN1EncodableVector40 = new ASN1EncodableVector();
    } 
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector39));
    ASN1EncodableVector aSN1EncodableVector44 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector45 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector46 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector47 = new ASN1EncodableVector();
    for (byte b20 = 0; b20 < (paramGMSSParameters.getHeightOfTrees()).length; b20++) {
      aSN1EncodableVector45.add((ASN1Encodable)new ASN1Integer(paramGMSSParameters.getHeightOfTrees()[b20]));
      aSN1EncodableVector46.add((ASN1Encodable)new ASN1Integer(paramGMSSParameters.getWinternitzParameter()[b20]));
      aSN1EncodableVector47.add((ASN1Encodable)new ASN1Integer(paramGMSSParameters.getK()[b20]));
    } 
    aSN1EncodableVector44.add((ASN1Encodable)new ASN1Integer(paramGMSSParameters.getNumOfLayers()));
    aSN1EncodableVector44.add((ASN1Encodable)new DERSequence(aSN1EncodableVector45));
    aSN1EncodableVector44.add((ASN1Encodable)new DERSequence(aSN1EncodableVector46));
    aSN1EncodableVector44.add((ASN1Encodable)new DERSequence(aSN1EncodableVector47));
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector44));
    ASN1EncodableVector aSN1EncodableVector48 = new ASN1EncodableVector();
    for (byte b21 = 0; b21 < paramArrayOfAlgorithmIdentifier.length; b21++)
      aSN1EncodableVector48.add((ASN1Encodable)paramArrayOfAlgorithmIdentifier[b21]); 
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector48));
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector1);
  }
  
  private static int checkBigIntegerInIntRange(ASN1Encodable paramASN1Encodable) {
    BigInteger bigInteger = ((ASN1Integer)paramASN1Encodable).getValue();
    if (bigInteger.compareTo(BigInteger.valueOf(2147483647L)) > 0 || bigInteger.compareTo(BigInteger.valueOf(-2147483648L)) < 0)
      throw new IllegalArgumentException("BigInteger not in Range: " + bigInteger.toString()); 
    return bigInteger.intValue();
  }
  
  public ASN1Primitive toASN1Primitive() {
    return this.primitive;
  }
}
