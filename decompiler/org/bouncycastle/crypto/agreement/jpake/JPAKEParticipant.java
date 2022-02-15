package org.bouncycastle.crypto.agreement.jpake;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.util.Arrays;

public class JPAKEParticipant {
  public static final int STATE_INITIALIZED = 0;
  
  public static final int STATE_ROUND_1_CREATED = 10;
  
  public static final int STATE_ROUND_1_VALIDATED = 20;
  
  public static final int STATE_ROUND_2_CREATED = 30;
  
  public static final int STATE_ROUND_2_VALIDATED = 40;
  
  public static final int STATE_KEY_CALCULATED = 50;
  
  public static final int STATE_ROUND_3_CREATED = 60;
  
  public static final int STATE_ROUND_3_VALIDATED = 70;
  
  private final String participantId;
  
  private char[] password;
  
  private final Digest digest;
  
  private final SecureRandom random;
  
  private final BigInteger p;
  
  private final BigInteger q;
  
  private final BigInteger g;
  
  private String partnerParticipantId;
  
  private BigInteger x1;
  
  private BigInteger x2;
  
  private BigInteger gx1;
  
  private BigInteger gx2;
  
  private BigInteger gx3;
  
  private BigInteger gx4;
  
  private BigInteger b;
  
  private int state;
  
  public JPAKEParticipant(String paramString, char[] paramArrayOfchar) {
    this(paramString, paramArrayOfchar, JPAKEPrimeOrderGroups.NIST_3072);
  }
  
  public JPAKEParticipant(String paramString, char[] paramArrayOfchar, JPAKEPrimeOrderGroup paramJPAKEPrimeOrderGroup) {
    this(paramString, paramArrayOfchar, paramJPAKEPrimeOrderGroup, (Digest)new SHA256Digest(), new SecureRandom());
  }
  
  public JPAKEParticipant(String paramString, char[] paramArrayOfchar, JPAKEPrimeOrderGroup paramJPAKEPrimeOrderGroup, Digest paramDigest, SecureRandom paramSecureRandom) {
    JPAKEUtil.validateNotNull(paramString, "participantId");
    JPAKEUtil.validateNotNull(paramArrayOfchar, "password");
    JPAKEUtil.validateNotNull(paramJPAKEPrimeOrderGroup, "p");
    JPAKEUtil.validateNotNull(paramDigest, "digest");
    JPAKEUtil.validateNotNull(paramSecureRandom, "random");
    if (paramArrayOfchar.length == 0)
      throw new IllegalArgumentException("Password must not be empty."); 
    this.participantId = paramString;
    this.password = Arrays.copyOf(paramArrayOfchar, paramArrayOfchar.length);
    this.p = paramJPAKEPrimeOrderGroup.getP();
    this.q = paramJPAKEPrimeOrderGroup.getQ();
    this.g = paramJPAKEPrimeOrderGroup.getG();
    this.digest = paramDigest;
    this.random = paramSecureRandom;
    this.state = 0;
  }
  
  public int getState() {
    return this.state;
  }
  
  public JPAKERound1Payload createRound1PayloadToSend() {
    if (this.state >= 10)
      throw new IllegalStateException("Round1 payload already created for " + this.participantId); 
    this.x1 = JPAKEUtil.generateX1(this.q, this.random);
    this.x2 = JPAKEUtil.generateX2(this.q, this.random);
    this.gx1 = JPAKEUtil.calculateGx(this.p, this.g, this.x1);
    this.gx2 = JPAKEUtil.calculateGx(this.p, this.g, this.x2);
    BigInteger[] arrayOfBigInteger1 = JPAKEUtil.calculateZeroKnowledgeProof(this.p, this.q, this.g, this.gx1, this.x1, this.participantId, this.digest, this.random);
    BigInteger[] arrayOfBigInteger2 = JPAKEUtil.calculateZeroKnowledgeProof(this.p, this.q, this.g, this.gx2, this.x2, this.participantId, this.digest, this.random);
    this.state = 10;
    return new JPAKERound1Payload(this.participantId, this.gx1, this.gx2, arrayOfBigInteger1, arrayOfBigInteger2);
  }
  
  public void validateRound1PayloadReceived(JPAKERound1Payload paramJPAKERound1Payload) throws CryptoException {
    if (this.state >= 20)
      throw new IllegalStateException("Validation already attempted for round1 payload for" + this.participantId); 
    this.partnerParticipantId = paramJPAKERound1Payload.getParticipantId();
    this.gx3 = paramJPAKERound1Payload.getGx1();
    this.gx4 = paramJPAKERound1Payload.getGx2();
    BigInteger[] arrayOfBigInteger1 = paramJPAKERound1Payload.getKnowledgeProofForX1();
    BigInteger[] arrayOfBigInteger2 = paramJPAKERound1Payload.getKnowledgeProofForX2();
    JPAKEUtil.validateParticipantIdsDiffer(this.participantId, paramJPAKERound1Payload.getParticipantId());
    JPAKEUtil.validateGx4(this.gx4);
    JPAKEUtil.validateZeroKnowledgeProof(this.p, this.q, this.g, this.gx3, arrayOfBigInteger1, paramJPAKERound1Payload.getParticipantId(), this.digest);
    JPAKEUtil.validateZeroKnowledgeProof(this.p, this.q, this.g, this.gx4, arrayOfBigInteger2, paramJPAKERound1Payload.getParticipantId(), this.digest);
    this.state = 20;
  }
  
  public JPAKERound2Payload createRound2PayloadToSend() {
    if (this.state >= 30)
      throw new IllegalStateException("Round2 payload already created for " + this.participantId); 
    if (this.state < 20)
      throw new IllegalStateException("Round1 payload must be validated prior to creating Round2 payload for " + this.participantId); 
    BigInteger bigInteger1 = JPAKEUtil.calculateGA(this.p, this.gx1, this.gx3, this.gx4);
    BigInteger bigInteger2 = JPAKEUtil.calculateS(this.password);
    BigInteger bigInteger3 = JPAKEUtil.calculateX2s(this.q, this.x2, bigInteger2);
    BigInteger bigInteger4 = JPAKEUtil.calculateA(this.p, this.q, bigInteger1, bigInteger3);
    BigInteger[] arrayOfBigInteger = JPAKEUtil.calculateZeroKnowledgeProof(this.p, this.q, bigInteger1, bigInteger4, bigInteger3, this.participantId, this.digest, this.random);
    this.state = 30;
    return new JPAKERound2Payload(this.participantId, bigInteger4, arrayOfBigInteger);
  }
  
  public void validateRound2PayloadReceived(JPAKERound2Payload paramJPAKERound2Payload) throws CryptoException {
    if (this.state >= 40)
      throw new IllegalStateException("Validation already attempted for round2 payload for" + this.participantId); 
    if (this.state < 20)
      throw new IllegalStateException("Round1 payload must be validated prior to validating Round2 payload for " + this.participantId); 
    BigInteger bigInteger = JPAKEUtil.calculateGA(this.p, this.gx3, this.gx1, this.gx2);
    this.b = paramJPAKERound2Payload.getA();
    BigInteger[] arrayOfBigInteger = paramJPAKERound2Payload.getKnowledgeProofForX2s();
    JPAKEUtil.validateParticipantIdsDiffer(this.participantId, paramJPAKERound2Payload.getParticipantId());
    JPAKEUtil.validateParticipantIdsEqual(this.partnerParticipantId, paramJPAKERound2Payload.getParticipantId());
    JPAKEUtil.validateGa(bigInteger);
    JPAKEUtil.validateZeroKnowledgeProof(this.p, this.q, bigInteger, this.b, arrayOfBigInteger, paramJPAKERound2Payload.getParticipantId(), this.digest);
    this.state = 40;
  }
  
  public BigInteger calculateKeyingMaterial() {
    if (this.state >= 50)
      throw new IllegalStateException("Key already calculated for " + this.participantId); 
    if (this.state < 40)
      throw new IllegalStateException("Round2 payload must be validated prior to creating key for " + this.participantId); 
    BigInteger bigInteger1 = JPAKEUtil.calculateS(this.password);
    Arrays.fill(this.password, false);
    this.password = null;
    BigInteger bigInteger2 = JPAKEUtil.calculateKeyingMaterial(this.p, this.q, this.gx4, this.x2, bigInteger1, this.b);
    this.x1 = null;
    this.x2 = null;
    this.b = null;
    this.state = 50;
    return bigInteger2;
  }
  
  public JPAKERound3Payload createRound3PayloadToSend(BigInteger paramBigInteger) {
    if (this.state >= 60)
      throw new IllegalStateException("Round3 payload already created for " + this.participantId); 
    if (this.state < 50)
      throw new IllegalStateException("Keying material must be calculated prior to creating Round3 payload for " + this.participantId); 
    BigInteger bigInteger = JPAKEUtil.calculateMacTag(this.participantId, this.partnerParticipantId, this.gx1, this.gx2, this.gx3, this.gx4, paramBigInteger, this.digest);
    this.state = 60;
    return new JPAKERound3Payload(this.participantId, bigInteger);
  }
  
  public void validateRound3PayloadReceived(JPAKERound3Payload paramJPAKERound3Payload, BigInteger paramBigInteger) throws CryptoException {
    if (this.state >= 70)
      throw new IllegalStateException("Validation already attempted for round3 payload for" + this.participantId); 
    if (this.state < 50)
      throw new IllegalStateException("Keying material must be calculated validated prior to validating Round3 payload for " + this.participantId); 
    JPAKEUtil.validateParticipantIdsDiffer(this.participantId, paramJPAKERound3Payload.getParticipantId());
    JPAKEUtil.validateParticipantIdsEqual(this.partnerParticipantId, paramJPAKERound3Payload.getParticipantId());
    JPAKEUtil.validateMacTag(this.participantId, this.partnerParticipantId, this.gx1, this.gx2, this.gx3, this.gx4, paramBigInteger, this.digest, paramJPAKERound3Payload.getMacTag());
    this.gx1 = null;
    this.gx2 = null;
    this.gx3 = null;
    this.gx4 = null;
    this.state = 70;
  }
}
