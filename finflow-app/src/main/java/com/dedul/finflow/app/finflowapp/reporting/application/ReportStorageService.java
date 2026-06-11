package com.dedul.finflow.app.finflowapp.reporting.application;

public interface ReportStorageService {

  void upload(String key, String contentType, byte[] content);

  byte[] download(String key);
}
