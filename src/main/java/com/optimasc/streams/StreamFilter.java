package com.optimasc.streams;


public interface StreamFilter
{
  boolean accept(DocumentStreamReader reader); 
}
