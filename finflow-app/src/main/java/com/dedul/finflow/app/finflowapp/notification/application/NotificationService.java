package com.dedul.finflow.app.finflowapp.notification.application;

import com.dedul.finflow.app.finflowapp.notification.domain.NotificationType;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

  @Async("applicationTaskExecutor")
  public CompletableFuture<Void> sendExpenseApprovedNotification(
      UUID employeeId,
      UUID expenseId
  ) {
    return sendNotification(
        employeeId,
        expenseId,
        NotificationType.EXPENSE_APPROVED,
        "Expense approved",
        "Your expense claim has been approved."
    );
  }

  @Async("applicationTaskExecutor")
  public CompletableFuture<Void> sendExpenseRejectedNotification(
      UUID employeeId,
      UUID expenseId,
      String reason
  ) {
    return sendNotification(
        employeeId,
        expenseId,
        NotificationType.EXPENSE_REJECTED,
        "Expense rejected",
        "Your expense claim has been rejected. Reason: " + reason
    );
  }

  private CompletableFuture<Void> sendNotification(
      UUID employeeId,
      UUID expenseId,
      NotificationType type,
      String title,
      String body
  ) {
    try {
      simulateSlowNotificationProvider();

      log.info(
          "Notification sent: type={}, employeeId={}, expenseId={}, title={}, body={}",
          type,
          employeeId,
          expenseId,
          title,
          body
      );

      return CompletableFuture.completedFuture(null);
    } catch (Exception e) {
      log.error(
          "Failed to send notification: type={}, employeeId={}, expenseId={}",
          type,
          employeeId,
          expenseId,
          e
      );

      return CompletableFuture.failedFuture(e);
    }
  }

  private void simulateSlowNotificationProvider() {
    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Notification sending was interrupted", e);
    }
  }
}
