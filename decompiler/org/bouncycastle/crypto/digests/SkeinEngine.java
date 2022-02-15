package org.bouncycastle.crypto.digests;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.engines.ThreefishEngine;
import org.bouncycastle.crypto.params.SkeinParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Memoable;

public class SkeinEngine implements Memoable {
  public static final int SKEIN_256 = 256;
  
  public static final int SKEIN_512 = 512;
  
  public static final int SKEIN_1024 = 1024;
  
  private static final int PARAM_TYPE_KEY = 0;
  
  private static final int PARAM_TYPE_CONFIG = 4;
  
  private static final int PARAM_TYPE_MESSAGE = 48;
  
  private static final int PARAM_TYPE_OUTPUT = 63;
  
  private static final Hashtable INITIAL_STATES = new Hashtable<Object, Object>();
  
  final ThreefishEngine threefish;
  
  private final int outputSizeBytes;
  
  long[] chain;
  
  private long[] initialState;
  
  private byte[] key;
  
  private Parameter[] preMessageParameters;
  
  private Parameter[] postMessageParameters;
  
  private final UBI ubi;
  
  private final byte[] singleByte = new byte[1];
  
  private static void initialState(int paramInt1, int paramInt2, long[] paramArrayOflong) {
    INITIAL_STATES.put(variantIdentifier(paramInt1 / 8, paramInt2 / 8), paramArrayOflong);
  }
  
  private static Integer variantIdentifier(int paramInt1, int paramInt2) {
    return new Integer(paramInt2 << 16 | paramInt1);
  }
  
  public SkeinEngine(int paramInt1, int paramInt2) {
    if (paramInt2 % 8 != 0)
      throw new IllegalArgumentException("Output size must be a multiple of 8 bits. :" + paramInt2); 
    this.outputSizeBytes = paramInt2 / 8;
    this.threefish = new ThreefishEngine(paramInt1);
    this.ubi = new UBI(this.threefish.getBlockSize());
  }
  
  public SkeinEngine(SkeinEngine paramSkeinEngine) {
    this(paramSkeinEngine.getBlockSize() * 8, paramSkeinEngine.getOutputSize() * 8);
    copyIn(paramSkeinEngine);
  }
  
  private void copyIn(SkeinEngine paramSkeinEngine) {
    this.ubi.reset(paramSkeinEngine.ubi);
    this.chain = Arrays.clone(paramSkeinEngine.chain, this.chain);
    this.initialState = Arrays.clone(paramSkeinEngine.initialState, this.initialState);
    this.key = Arrays.clone(paramSkeinEngine.key, this.key);
    this.preMessageParameters = clone(paramSkeinEngine.preMessageParameters, this.preMessageParameters);
    this.postMessageParameters = clone(paramSkeinEngine.postMessageParameters, this.postMessageParameters);
  }
  
  private static Parameter[] clone(Parameter[] paramArrayOfParameter1, Parameter[] paramArrayOfParameter2) {
    if (paramArrayOfParameter1 == null)
      return null; 
    if (paramArrayOfParameter2 == null || paramArrayOfParameter2.length != paramArrayOfParameter1.length)
      paramArrayOfParameter2 = new Parameter[paramArrayOfParameter1.length]; 
    System.arraycopy(paramArrayOfParameter1, 0, paramArrayOfParameter2, 0, paramArrayOfParameter2.length);
    return paramArrayOfParameter2;
  }
  
  public Memoable copy() {
    return new SkeinEngine(this);
  }
  
  public void reset(Memoable paramMemoable) {
    SkeinEngine skeinEngine = (SkeinEngine)paramMemoable;
    if (getBlockSize() != skeinEngine.getBlockSize() || this.outputSizeBytes != skeinEngine.outputSizeBytes)
      throw new IllegalArgumentException("Incompatible parameters in provided SkeinEngine."); 
    copyIn(skeinEngine);
  }
  
  public int getOutputSize() {
    return this.outputSizeBytes;
  }
  
  public int getBlockSize() {
    return this.threefish.getBlockSize();
  }
  
  public void init(SkeinParameters paramSkeinParameters) {
    this.chain = null;
    this.key = null;
    this.preMessageParameters = null;
    this.postMessageParameters = null;
    if (paramSkeinParameters != null) {
      byte[] arrayOfByte = paramSkeinParameters.getKey();
      if (arrayOfByte.length < 16)
        throw new IllegalArgumentException("Skein key must be at least 128 bits."); 
      initParams(paramSkeinParameters.getParameters());
    } 
    createInitialState();
    ubiInit(48);
  }
  
  private void initParams(Hashtable paramHashtable) {
    Enumeration<Integer> enumeration = paramHashtable.keys();
    Vector<Parameter> vector1 = new Vector();
    Vector<Parameter> vector2 = new Vector();
    while (enumeration.hasMoreElements()) {
      Integer integer = enumeration.nextElement();
      byte[] arrayOfByte = (byte[])paramHashtable.get(integer);
      if (integer.intValue() == 0) {
        this.key = arrayOfByte;
        continue;
      } 
      if (integer.intValue() < 48) {
        vector1.addElement(new Parameter(integer.intValue(), arrayOfByte));
        continue;
      } 
      vector2.addElement(new Parameter(integer.intValue(), arrayOfByte));
    } 
    this.preMessageParameters = new Parameter[vector1.size()];
    vector1.copyInto((Object[])this.preMessageParameters);
    sort(this.preMessageParameters);
    this.postMessageParameters = new Parameter[vector2.size()];
    vector2.copyInto((Object[])this.postMessageParameters);
    sort(this.postMessageParameters);
  }
  
  private static void sort(Parameter[] paramArrayOfParameter) {
    if (paramArrayOfParameter == null)
      return; 
    for (byte b = 1; b < paramArrayOfParameter.length; b++) {
      Parameter parameter = paramArrayOfParameter[b];
      int i;
      for (i = b; i > 0 && parameter.getType() < paramArrayOfParameter[i - 1].getType(); i--)
        paramArrayOfParameter[i] = paramArrayOfParameter[i - 1]; 
      paramArrayOfParameter[i] = parameter;
    } 
  }
  
  private void createInitialState() {
    long[] arrayOfLong = (long[])INITIAL_STATES.get(variantIdentifier(getBlockSize(), getOutputSize()));
    if (this.key == null && arrayOfLong != null) {
      this.chain = Arrays.clone(arrayOfLong);
    } else {
      this.chain = new long[getBlockSize() / 8];
      if (this.key != null)
        ubiComplete(0, this.key); 
      ubiComplete(4, (new Configuration((this.outputSizeBytes * 8))).getBytes());
    } 
    if (this.preMessageParameters != null)
      for (byte b = 0; b < this.preMessageParameters.length; b++) {
        Parameter parameter = this.preMessageParameters[b];
        ubiComplete(parameter.getType(), parameter.getValue());
      }  
    this.initialState = Arrays.clone(this.chain);
  }
  
  public void reset() {
    System.arraycopy(this.initialState, 0, this.chain, 0, this.chain.length);
    ubiInit(48);
  }
  
  private void ubiComplete(int paramInt, byte[] paramArrayOfbyte) {
    ubiInit(paramInt);
    this.ubi.update(paramArrayOfbyte, 0, paramArrayOfbyte.length, this.chain);
    ubiFinal();
  }
  
  private void ubiInit(int paramInt) {
    this.ubi.reset(paramInt);
  }
  
  private void ubiFinal() {
    this.ubi.doFinal(this.chain);
  }
  
  private void checkInitialised() {
    if (this.ubi == null)
      throw new IllegalArgumentException("Skein engine is not initialised."); 
  }
  
  public void update(byte paramByte) {
    this.singleByte[0] = paramByte;
    update(this.singleByte, 0, 1);
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    checkInitialised();
    this.ubi.update(paramArrayOfbyte, paramInt1, paramInt2, this.chain);
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) {
    checkInitialised();
    if (paramArrayOfbyte.length < paramInt + this.outputSizeBytes)
      throw new OutputLengthException("Output buffer is too short to hold output"); 
    ubiFinal();
    if (this.postMessageParameters != null)
      for (byte b1 = 0; b1 < this.postMessageParameters.length; b1++) {
        Parameter parameter = this.postMessageParameters[b1];
        ubiComplete(parameter.getType(), parameter.getValue());
      }  
    int i = getBlockSize();
    int j = (this.outputSizeBytes + i - 1) / i;
    for (byte b = 0; b < j; b++) {
      int k = Math.min(i, this.outputSizeBytes - b * i);
      output(b, paramArrayOfbyte, paramInt + b * i, k);
    } 
    reset();
    return this.outputSizeBytes;
  }
  
  private void output(long paramLong, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    byte[] arrayOfByte = new byte[8];
    ThreefishEngine.wordToBytes(paramLong, arrayOfByte, 0);
    long[] arrayOfLong = new long[this.chain.length];
    ubiInit(63);
    this.ubi.update(arrayOfByte, 0, arrayOfByte.length, arrayOfLong);
    this.ubi.doFinal(arrayOfLong);
    int i = (paramInt2 + 8 - 1) / 8;
    for (byte b = 0; b < i; b++) {
      int j = Math.min(8, paramInt2 - b * 8);
      if (j == 8) {
        ThreefishEngine.wordToBytes(arrayOfLong[b], paramArrayOfbyte, paramInt1 + b * 8);
      } else {
        ThreefishEngine.wordToBytes(arrayOfLong[b], arrayOfByte, 0);
        System.arraycopy(arrayOfByte, 0, paramArrayOfbyte, paramInt1 + b * 8, j);
      } 
    } 
  }
  
  static {
    initialState(256, 128, new long[] { -2228972824489528736L, -8629553674646093540L, 1155188648486244218L, -3677226592081559102L });
    initialState(256, 160, new long[] { 1450197650740764312L, 3081844928540042640L, -3136097061834271170L, 3301952811952417661L });
    initialState(256, 224, new long[] { -4176654842910610933L, -8688192972455077604L, -7364642305011795836L, 4056579644589979102L });
    initialState(256, 256, new long[] { -243853671043386295L, 3443677322885453875L, -5531612722399640561L, 7662005193972177513L });
    initialState(512, 128, new long[] { -6288014694233956526L, 2204638249859346602L, 3502419045458743507L, -4829063503441264548L, 983504137758028059L, 1880512238245786339L, -6715892782214108542L, 7602827311880509485L });
    initialState(512, 160, new long[] { 2934123928682216849L, -4399710721982728305L, 1684584802963255058L, 5744138295201861711L, 2444857010922934358L, -2807833639722848072L, -5121587834665610502L, 118355523173251694L });
    initialState(512, 224, new long[] { -3688341020067007964L, -3772225436291745297L, -8300862168937575580L, 4146387520469897396L, 1106145742801415120L, 7455425944880474941L, -7351063101234211863L, -7048981346965512457L });
    initialState(512, 384, new long[] { -6631894876634615969L, -5692838220127733084L, -7099962856338682626L, -2911352911530754598L, 2000907093792408677L, 9140007292425499655L, 6093301768906360022L, 2769176472213098488L });
    initialState(512, 512, new long[] { 5261240102383538638L, 978932832955457283L, -8083517948103779378L, -7339365279355032399L, 6752626034097301424L, -1531723821829733388L, -7417126464950782685L, -5901786942805128141L });
  }
  
  private static class Configuration {
    private byte[] bytes = new byte[32];
    
    public Configuration(long param1Long) {
      this.bytes[0] = 83;
      this.bytes[1] = 72;
      this.bytes[2] = 65;
      this.bytes[3] = 51;
      this.bytes[4] = 1;
      this.bytes[5] = 0;
      ThreefishEngine.wordToBytes(param1Long, this.bytes, 8);
    }
    
    public byte[] getBytes() {
      return this.bytes;
    }
  }
  
  public static class Parameter {
    private int type;
    
    private byte[] value;
    
    public Parameter(int param1Int, byte[] param1ArrayOfbyte) {
      this.type = param1Int;
      this.value = param1ArrayOfbyte;
    }
    
    public int getType() {
      return this.type;
    }
    
    public byte[] getValue() {
      return this.value;
    }
  }
  
  private class UBI {
    private final SkeinEngine.UbiTweak tweak = new SkeinEngine.UbiTweak();
    
    private byte[] currentBlock;
    
    private int currentOffset;
    
    private long[] message;
    
    public UBI(int param1Int) {
      this.currentBlock = new byte[param1Int];
      this.message = new long[this.currentBlock.length / 8];
    }
    
    public void reset(UBI param1UBI) {
      this.currentBlock = Arrays.clone(param1UBI.currentBlock, this.currentBlock);
      this.currentOffset = param1UBI.currentOffset;
      this.message = Arrays.clone(param1UBI.message, this.message);
      this.tweak.reset(param1UBI.tweak);
    }
    
    public void reset(int param1Int) {
      this.tweak.reset();
      this.tweak.setType(param1Int);
      this.currentOffset = 0;
    }
    
    public void update(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2, long[] param1ArrayOflong) {
      int i = 0;
      while (param1Int2 > i) {
        if (this.currentOffset == this.currentBlock.length) {
          processBlock(param1ArrayOflong);
          this.tweak.setFirst(false);
          this.currentOffset = 0;
        } 
        int j = Math.min(param1Int2 - i, this.currentBlock.length - this.currentOffset);
        System.arraycopy(param1ArrayOfbyte, param1Int1 + i, this.currentBlock, this.currentOffset, j);
        i += j;
        this.currentOffset += j;
        this.tweak.advancePosition(j);
      } 
    }
    
    private void processBlock(long[] param1ArrayOflong) {
      SkeinEngine.this.threefish.init(true, SkeinEngine.this.chain, this.tweak.getWords());
      byte b;
      for (b = 0; b < this.message.length; b++)
        this.message[b] = ThreefishEngine.bytesToWord(this.currentBlock, b * 8); 
      SkeinEngine.this.threefish.processBlock(this.message, param1ArrayOflong);
      for (b = 0; b < param1ArrayOflong.length; b++)
        param1ArrayOflong[b] = param1ArrayOflong[b] ^ this.message[b]; 
    }
    
    public void doFinal(long[] param1ArrayOflong) {
      for (int i = this.currentOffset; i < this.currentBlock.length; i++)
        this.currentBlock[i] = 0; 
      this.tweak.setFinal(true);
      processBlock(param1ArrayOflong);
    }
  }
  
  private static class UbiTweak {
    private static final long LOW_RANGE = 9223372034707292160L;
    
    private static final long T1_FINAL = -9223372036854775808L;
    
    private static final long T1_FIRST = 4611686018427387904L;
    
    private long[] tweak = new long[2];
    
    private boolean extendedPosition;
    
    public UbiTweak() {
      reset();
    }
    
    public void reset(UbiTweak param1UbiTweak) {
      this.tweak = Arrays.clone(param1UbiTweak.tweak, this.tweak);
      this.extendedPosition = param1UbiTweak.extendedPosition;
    }
    
    public void reset() {
      this.tweak[0] = 0L;
      this.tweak[1] = 0L;
      this.extendedPosition = false;
      setFirst(true);
    }
    
    public void setType(int param1Int) {
      this.tweak[1] = this.tweak[1] & 0xFFFFFFC000000000L | (param1Int & 0x3FL) << 56L;
    }
    
    public int getType() {
      return (int)(this.tweak[1] >>> 56L & 0x3FL);
    }
    
    public void setFirst(boolean param1Boolean) {
      if (param1Boolean) {
        this.tweak[1] = this.tweak[1] | 0x4000000000000000L;
      } else {
        this.tweak[1] = this.tweak[1] & 0xBFFFFFFFFFFFFFFFL;
      } 
    }
    
    public boolean isFirst() {
      return ((this.tweak[1] & 0x4000000000000000L) != 0L);
    }
    
    public void setFinal(boolean param1Boolean) {
      if (param1Boolean) {
        this.tweak[1] = this.tweak[1] | Long.MIN_VALUE;
      } else {
        this.tweak[1] = this.tweak[1] & Long.MAX_VALUE;
      } 
    }
    
    public boolean isFinal() {
      return ((this.tweak[1] & Long.MIN_VALUE) != 0L);
    }
    
    public void advancePosition(int param1Int) {
      if (this.extendedPosition) {
        long[] arrayOfLong = new long[3];
        arrayOfLong[0] = this.tweak[0] & 0xFFFFFFFFL;
        arrayOfLong[1] = this.tweak[0] >>> 32L & 0xFFFFFFFFL;
        arrayOfLong[2] = this.tweak[1] & 0xFFFFFFFFL;
        long l = param1Int;
        for (byte b = 0; b < arrayOfLong.length; b++) {
          l += arrayOfLong[b];
          arrayOfLong[b] = l;
          l >>>= 32L;
        } 
        this.tweak[0] = (arrayOfLong[1] & 0xFFFFFFFFL) << 32L | arrayOfLong[0] & 0xFFFFFFFFL;
        this.tweak[1] = this.tweak[1] & 0xFFFFFFFF00000000L | arrayOfLong[2] & 0xFFFFFFFFL;
      } else {
        long l = this.tweak[0];
        l += param1Int;
        this.tweak[0] = l;
        if (l > 9223372034707292160L)
          this.extendedPosition = true; 
      } 
    }
    
    public long[] getWords() {
      return this.tweak;
    }
    
    public String toString() {
      return getType() + " first: " + isFirst() + ", final: " + isFinal();
    }
  }
}
