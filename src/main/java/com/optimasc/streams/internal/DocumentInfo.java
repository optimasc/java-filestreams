package com.optimasc.streams.internal;

public class DocumentInfo
{
  protected String publicID;
  protected String shortTypeName;
  protected int    streamType;
  // The size of the document -- this field is requied
  protected long   size;
  
  public static final int TYPE_LITTLE_ENDIAN = 1;
  public static final int TYPE_UNKNOWN_ENDIAN = 2;
  public static final int TYPE_BIG_ENDIAN = 3;
  public static final int TYPE_CHARACTER = 4;
  
  
  
  public DocumentInfo(String publicID, String shortTypeName, int endian, long size)
  {
    super();
    this.publicID = publicID;
    this.shortTypeName = shortTypeName;
    this.streamType = endian;
    this.size = size;
  }

  /**
   * Returns the public ID or type of the document
   * @return the public ID, or null if not available
   */
  public String getPublicId()
  {
    return publicID;
  }
  
  /**
   * Returns the short generic type name as MIME signature of this
   * file type or null if not known.
   * 
   */
  public String getShortTypeName()
  {
    return shortTypeName;
  }
  
  public int getStreamType()
  {
    return streamType;
  }

}
