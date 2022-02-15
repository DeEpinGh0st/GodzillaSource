package com.jediterm.terminal.debug;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.List;
import org.apache.log4j.Logger;

public class ControlSequenceVisualizer {
  private static final Logger LOG = Logger.getLogger(ControlSequenceVisualizer.class);



  
  private File myTempFile = null; public ControlSequenceVisualizer() {
    try {
      this.myTempFile = File.createTempFile("jeditermData", ".txt");
      this.myTempFile.deleteOnExit();
    }
    catch (IOException e) {
      throw new IllegalStateException(e);
    } 
  }
  
  public String getVisualizedString(List<char[]> chunks) {
    try {
      writeChunksToFile(chunks);
      
      return readOutput("teseq " + this.myTempFile.getAbsolutePath());
    }
    catch (IOException e) {
      return "Control sequence visualizer teseq is not installed.\nSee http://www.gnu.org/software/teseq/\nNow printing characters as is:\n\n" + 
        
        joinChunks(chunks);
    } 
  }
  
  private static String joinChunks(List<char[]> chunks) {
    StringBuilder sb = new StringBuilder();
    
    for (char[] ch : chunks) {
      sb.append(ch);
    }
    
    return sb.toString();
  }
  
  private void writeChunksToFile(List<char[]> chunks) throws IOException {
    OutputStreamWriter stream = new OutputStreamWriter(new FileOutputStream(this.myTempFile, false));
    try {
      for (char[] data : chunks) {
        stream.write(data, 0, data.length);
      }
    } finally {
      
      stream.close();
    } 
  }

  
  public String readOutput(String command) throws IOException {
    Process process = Runtime.getRuntime().exec(command);
    
    Reader inStreamReader = new InputStreamReader(process.getInputStream());
    BufferedReader in = new BufferedReader(inStreamReader);
    
    StringBuilder sb = new StringBuilder();
    int i = 0;
    String lastNum = null; String line;
    while ((line = in.readLine()) != null) {
      if (!line.startsWith("&") && !line.startsWith("\"")) {
        lastNum = String.format("%3d ", new Object[] { Integer.valueOf(i++) });
        sb.append(lastNum);
      }
      else if (lastNum != null) {
        sb.append(CharBuffer.allocate(lastNum.length()).toString().replace(false, ' '));
      } 
      
      sb.append(line);
      sb.append("\n");
    } 
    in.close();
    return sb.toString();
  }
}
