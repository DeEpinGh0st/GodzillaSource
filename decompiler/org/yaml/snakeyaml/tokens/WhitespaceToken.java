package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.Mark;

















public class WhitespaceToken
  extends Token
{
  public WhitespaceToken(Mark startMark, Mark endMark) {
    super(startMark, endMark);
  }

  
  public Token.ID getTokenId() {
    return Token.ID.Whitespace;
  }
}
