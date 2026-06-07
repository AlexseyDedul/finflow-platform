package com.dedul.finflow.app.finflowapp.expense.application.event;

import com.dedul.finflow.app.finflowapp.shared.events.EventEnvelope;
import com.dedul.finflow.app.finflowapp.shared.events.sqs.SqsEventPublisher;
import com.dedul.finflow.app.finflowapp.shared.events.sqs.SqsQueueUrlResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpenseEventPublisher {

  private final SqsEventPublisher publisher;
  private final SqsQueueUrlResolver queueUrlResolver;

  @Value("${app.aws.sqs.expense-submitted-queue-name}")
  private String expenseSubmittedQueueName;

  public void publishExpenseSubmitted(ExpenseSubmittedEvent payload) {
    String queueUrl = queueUrlResolver.resolve(expenseSubmittedQueueName);

    publisher.publish(queueUrl, EventEnvelope.of("ExpenseSubmittedEvent", payload));
  }
}
