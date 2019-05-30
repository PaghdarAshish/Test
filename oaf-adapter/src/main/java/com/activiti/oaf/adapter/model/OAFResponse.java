package com.activiti.oaf.adapter.model;

import java.io.Serializable;

/**
 * Bean class for API response.
 * @author Pradip Patel
 */

public class OAFResponse implements Serializable
{
  private String success;
  private String message;
  private String errorCode;
  
  public String getSuccess()
  {
    return this.success;
  }
  
  public void setSuccess(String success)
  {
    this.success = success;
  }
  
  public String getMessage()
  {
    return this.message;
  }
  
  public void setMessage(String message)
  {
    this.message = message;
  }
  
  public String getErrorCode()
  {
    return this.errorCode;
  }
  
  public void setErrorCode(String errorCode)
  {
    this.errorCode = errorCode;
  }
}
