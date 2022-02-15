package org.bouncycastle.crypto.prng;

public class ThreadedSeedGenerator {
  public byte[] generateSeed(int paramInt, boolean paramBoolean) {
    SeedGenerator seedGenerator = new SeedGenerator();
    return seedGenerator.generateSeed(paramInt, paramBoolean);
  }
  
  private class SeedGenerator implements Runnable {
    private volatile int counter = 0;
    
    private volatile boolean stop = false;
    
    private SeedGenerator() {}
    
    public void run() {
      while (!this.stop)
        this.counter++; 
    }
    
    public byte[] generateSeed(int param1Int, boolean param1Boolean) {
      int j;
      Thread thread = new Thread(this);
      byte[] arrayOfByte = new byte[param1Int];
      this.counter = 0;
      this.stop = false;
      int i = 0;
      thread.start();
      if (param1Boolean) {
        j = param1Int;
      } else {
        j = param1Int * 8;
      } 
      for (byte b = 0; b < j; b++) {
        while (this.counter == i) {
          try {
            Thread.sleep(1L);
          } catch (InterruptedException interruptedException) {}
        } 
        i = this.counter;
        if (param1Boolean) {
          arrayOfByte[b] = (byte)(i & 0xFF);
        } else {
          int k = b / 8;
          arrayOfByte[k] = (byte)(arrayOfByte[k] << 1 | i & 0x1);
        } 
      } 
      this.stop = true;
      return arrayOfByte;
    }
  }
}
