package com.optimasc.streams;

/** Represents high-level information on a stream. 
 * 
 * @author Carl Eric Codère
 *
 */
public class DocumentInfo
{
  protected String publicID;
  protected String mimeType;
  protected int    streamType;
  // The size of the document -- this field is required
  protected long   size;
  
  /** Stream type is a little-endian binary oriented stream. */
  public static final int TYPE_LITTLE_ENDIAN = 1;
  /** Stream type is an unknown endian binary oriented stream. */
  public static final int TYPE_UNKNOWN_ENDIAN = 2;
  /** Stream type is a big-endian binary oriented stream. */
  public static final int TYPE_BIG_ENDIAN = 3;
  /** Stream type is a character type stream. */
  public static final int TYPE_CHARACTER = 4;
  
  
  /** Creates a document information.
   * 
   * @param publicID This is usually the category/instance type associated with this document.
   * @param MIMEType The MIME type identifier
   * @param endian The stream type, one of the TYPE constants defined above.
   * @param size The size of the stream in bytes.
   */
  public DocumentInfo(String publicID, String MIMEType, int endian, long size)
  {
    super();
    this.publicID = publicID;
    this.mimeType = MIMEType;
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
    return mimeType;
  }
  
  
  /** Returns the format of the stream. */
  public int getStreamType()
  {
    return streamType;
  }
  
  /** Returns the size of the stream or chunk. */
  public long getSize()
  {
    return size;
  }
  

}
