package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.Mark;















public final class StreamEndToken
  extends Token
{
  public StreamEndToken(Mark startMark, Mark endMark) {
    super(startMark, endMark);
  }

  
  public Token.ID getTokenId() {
    return Token.ID.StreamEnd;
  }
}
