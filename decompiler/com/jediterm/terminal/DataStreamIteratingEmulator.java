package com.jediterm.terminal;

import com.jediterm.terminal.emulator.Emulator;
import java.io.IOException;




public abstract class DataStreamIteratingEmulator
  implements Emulator
{
  protected final TerminalDataStream myDataStream;
  protected final Terminal myTerminal;
  private boolean myEof = false;
  
  public DataStreamIteratingEmulator(TerminalDataStream dataStream, Terminal terminal) {
    this.myDataStream = dataStream;
    this.myTerminal = terminal;
  }

  
  public boolean hasNext() {
    return !this.myEof;
  }

  
  public void resetEof() {
    this.myEof = false;
  }

  
  public void next() throws IOException {
    try {
      char b = this.myDataStream.getChar();
      
      processChar(b, this.myTerminal);
    }
    catch (EOF e) {
      this.myEof = true;
    } 
  }
  
  protected abstract void processChar(char paramChar, Terminal paramTerminal) throws IOException;
}
