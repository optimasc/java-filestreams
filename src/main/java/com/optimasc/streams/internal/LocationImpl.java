package com.optimasc.streams.internal;

import com.optimasc.streams.Location;

public class LocationImpl implements Location
{
  protected long byteOffset;
  
  public LocationImpl(long position)
  {
    byteOffset = position;
  }

  // Not supported
  public int getLineNumber()
  {
    return -1;
  }

  // Not supported
  public int getColumnNumber()
  {
    return -1;
  }

  public long getOffset()
  {
    return byteOffset;
  }

  // Not supported
  public String getPublicId()
  {
    return null;
  }

  // Not supported
  public String getSystemId()
  {
    // TODO Auto-generated method stub
    return null;
  }

  
}
