package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.Mark;















public final class StreamStartToken
  extends Token
{
  public StreamStartToken(Mark startMark, Mark endMark) {
    super(startMark, endMark);
  }

  
  public Token.ID getTokenId() {
    return Token.ID.StreamStart;
  }
}
