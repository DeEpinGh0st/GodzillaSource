package org.bouncycastle.crypto.tls;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.agreement.srp.SRP6VerifierGenerator;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.SRP6GroupParameters;
import org.bouncycastle.util.Strings;

public class SimulatedTlsSRPIdentityManager implements TlsSRPIdentityManager {
  private static final byte[] PREFIX_PASSWORD = Strings.toByteArray("password");
  
  private static final byte[] PREFIX_SALT = Strings.toByteArray("salt");
  
  protected SRP6GroupParameters group;
  
  protected SRP6VerifierGenerator verifierGenerator;
  
  protected Mac mac;
  
  public static SimulatedTlsSRPIdentityManager getRFC5054Default(SRP6GroupParameters paramSRP6GroupParameters, byte[] paramArrayOfbyte) {
    SRP6VerifierGenerator sRP6VerifierGenerator = new SRP6VerifierGenerator();
    sRP6VerifierGenerator.init(paramSRP6GroupParameters, TlsUtils.createHash((short)2));
    HMac hMac = new HMac(TlsUtils.createHash((short)2));
    hMac.init((CipherParameters)new KeyParameter(paramArrayOfbyte));
    return new SimulatedTlsSRPIdentityManager(paramSRP6GroupParameters, sRP6VerifierGenerator, (Mac)hMac);
  }
  
  public SimulatedTlsSRPIdentityManager(SRP6GroupParameters paramSRP6GroupParameters, SRP6VerifierGenerator paramSRP6VerifierGenerator, Mac paramMac) {
    this.group = paramSRP6GroupParameters;
    this.verifierGenerator = paramSRP6VerifierGenerator;
    this.mac = paramMac;
  }
  
  public TlsSRPLoginParameters getLoginParameters(byte[] paramArrayOfbyte) {
    this.mac.update(PREFIX_SALT, 0, PREFIX_SALT.length);
    this.mac.update(paramArrayOfbyte, 0, paramArrayOfbyte.length);
    byte[] arrayOfByte1 = new byte[this.mac.getMacSize()];
    this.mac.doFinal(arrayOfByte1, 0);
    this.mac.update(PREFIX_PASSWORD, 0, PREFIX_PASSWORD.length);
    this.mac.update(paramArrayOfbyte, 0, paramArrayOfbyte.length);
    byte[] arrayOfByte2 = new byte[this.mac.getMacSize()];
    this.mac.doFinal(arrayOfByte2, 0);
    BigInteger bigInteger = this.verifierGenerator.generateVerifier(arrayOfByte1, paramArrayOfbyte, arrayOfByte2);
    return new TlsSRPLoginParameters(this.group, bigInteger, arrayOfByte1);
  }
}
