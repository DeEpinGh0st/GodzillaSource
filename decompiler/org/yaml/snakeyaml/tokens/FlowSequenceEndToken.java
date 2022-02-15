package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.Mark;















public final class FlowSequenceEndToken
  extends Token
{
  public FlowSequenceEndToken(Mark startMark, Mark endMark) {
    super(startMark, endMark);
  }

  
  public Token.ID getTokenId() {
    return Token.ID.FlowSequenceEnd;
  }
}
