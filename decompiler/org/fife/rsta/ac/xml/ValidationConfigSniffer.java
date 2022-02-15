package org.fife.rsta.ac.xml;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.Token;




public class ValidationConfigSniffer
{
  public ValidationConfig sniff(RSyntaxDocument doc) {
    ValidationConfig config = null;

    
    for (Token token : doc) {
      switch (token.getType()) {
        case 30:
        case 26:
          break;
      } 




    
    } 
    return config;
  }
}
