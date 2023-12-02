package com.optimasc.streams;


import com.optimasc.streams.Attribute;

public class ElementInfo
{
  public String id;
  public boolean isChunk;
  public byte[] data;
  public long size;
  public Attribute[] attributes;

  /** Create a chunk data element.
   * 
   * @param name
   * @param size
   * @param data
   * @param childs
   */
  public ElementInfo(String name, long size, Attribute[] attributes, byte[] data)
  {
    this.id = name;
    this.size = size;
    this.data = data;
    this.attributes = attributes;
    isChunk = false;
  }
  
  /** Create a group data element.
   */
  public ElementInfo(String name, long size, Attribute[] attributes)
  {
    this.id = name;
    this.size = size;
    this.attributes = attributes;
    isChunk = false;
  }
  
}
