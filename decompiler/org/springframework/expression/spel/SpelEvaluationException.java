package org.springframework.expression.spel;

import org.springframework.expression.EvaluationException;




























public class SpelEvaluationException
  extends EvaluationException
{
  private final SpelMessage message;
  private final Object[] inserts;
  
  public SpelEvaluationException(SpelMessage message, Object... inserts) {
    super(message.formatMessage(inserts));
    this.message = message;
    this.inserts = inserts;
  }
  
  public SpelEvaluationException(int position, SpelMessage message, Object... inserts) {
    super(position, message.formatMessage(inserts));
    this.message = message;
    this.inserts = inserts;
  }
  
  public SpelEvaluationException(int position, Throwable cause, SpelMessage message, Object... inserts) {
    super(position, message.formatMessage(inserts), cause);
    this.message = message;
    this.inserts = inserts;
  }
  
  public SpelEvaluationException(Throwable cause, SpelMessage message, Object... inserts) {
    super(message.formatMessage(inserts), cause);
    this.message = message;
    this.inserts = inserts;
  }




  
  public void setPosition(int position) {
    this.position = position;
  }



  
  public SpelMessage getMessageCode() {
    return this.message;
  }



  
  public Object[] getInserts() {
    return this.inserts;
  }
}
