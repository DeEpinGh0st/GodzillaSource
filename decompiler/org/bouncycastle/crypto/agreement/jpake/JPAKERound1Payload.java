package org.bouncycastle.crypto.agreement.jpake;

import java.math.BigInteger;
import org.bouncycastle.util.Arrays;

public class JPAKERound1Payload {
  private final String participantId;
  
  private final BigInteger gx1;
  
  private final BigInteger gx2;
  
  private final BigInteger[] knowledgeProofForX1;
  
  private final BigInteger[] knowledgeProofForX2;
  
  public JPAKERound1Payload(String paramString, BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger[] paramArrayOfBigInteger1, BigInteger[] paramArrayOfBigInteger2) {
    JPAKEUtil.validateNotNull(paramString, "participantId");
    JPAKEUtil.validateNotNull(paramBigInteger1, "gx1");
    JPAKEUtil.validateNotNull(paramBigInteger2, "gx2");
    JPAKEUtil.validateNotNull(paramArrayOfBigInteger1, "knowledgeProofForX1");
    JPAKEUtil.validateNotNull(paramArrayOfBigInteger2, "knowledgeProofForX2");
    this.participantId = paramString;
    this.gx1 = paramBigInteger1;
    this.gx2 = paramBigInteger2;
    this.knowledgeProofForX1 = Arrays.copyOf(paramArrayOfBigInteger1, paramArrayOfBigInteger1.length);
    this.knowledgeProofForX2 = Arrays.copyOf(paramArrayOfBigInteger2, paramArrayOfBigInteger2.length);
  }
  
  public String getParticipantId() {
    return this.participantId;
  }
  
  public BigInteger getGx1() {
    return this.gx1;
  }
  
  public BigInteger getGx2() {
    return this.gx2;
  }
  
  public BigInteger[] getKnowledgeProofForX1() {
    return Arrays.copyOf(this.knowledgeProofForX1, this.knowledgeProofForX1.length);
  }
  
  public BigInteger[] getKnowledgeProofForX2() {
    return Arrays.copyOf(this.knowledgeProofForX2, this.knowledgeProofForX2.length);
  }
}
