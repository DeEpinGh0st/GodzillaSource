package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.Mark;















public final class FlowSequenceStartToken
  extends Token
{
  public FlowSequenceStartToken(Mark startMark, Mark endMark) {
    super(startMark, endMark);
  }

  
  public Token.ID getTokenId() {
    return Token.ID.FlowSequenceStart;
  }
}
