package com.optimasc.streams;


public class DefaultStreamFilter implements StreamFilter
{

  public boolean accept(DocumentStreamReader reader)
  {
    return true;
  }

}
