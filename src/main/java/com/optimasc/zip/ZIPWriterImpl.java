package com.optimasc.zip;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import java.util.zip.CRC32;

import com.optimasc.date.DOSDate;
import com.optimasc.streams.internal.ChunkInfo;
import com.optimasc.streams.internal.AbstractDocumentWriter;
import com.optimasc.streams.internal.ResourceChunkInfo;
import com.optimasc.streams.DocumentStreamException;
import com.optimasc.nio.charset.CanonicalChars;

/** Implements a simple 32-bit ZIP file writer. The actual
 *  data by this implementation is not compressed internally, 
 *  so everything must be done manually before hand.
 *  
 *  If no attributes are specified when creating each element,
 *  the following defaults will exist:
 *    * The compression is set to "stored" - no compression.
 *    * The CRC-32 is calculated on the data input.
 *    * The last modification file timestamp will be set to the current
 *       date and time.
 *  For other configurations, the following attributes are required
 *  when creating the following elements:
 *    * The ATTRIBUTE_NAME_DATE_MODIFIED attribute should be set to the
 *      required timestamp of the element.
 *    * The ATTRIBUTE_NAME_COMPRESSION_TYPE attribute should be set to
 *      the format of the data that will be written. Only deflate,
 *      shrink, store and implode methods are recognized.   
 *    * The ATTRIBUTE_NAME_SIZE attribute should be set to the uncompressed
 *      data size.
 *    * The ATTRIBUTE_NAME_HASH_VALUE should be set to the ZIP CRC-32 of the
 *      uncompressed data.
 *    * The following attributes are optional and will be used using
 *      extra fields:
 *       ** ATTRIBUTE_NAME_COMMENT shall be stored in the comment file if present.
 *       ** ATTRIBUTE_NAME_DATE_ACCESSED shall be stored in the NTFS Extra field   
 *       ** ATTRIBUTE_NAME_DATE_CREATED shall be stored in the NTFS Extra field                  
 *  
 *   In all cases, Info-ZIP extra fields for comments and filenames shall be
 *   added as required if non-ASCII are present.
 *   
 * @author Carl Eric Codere
 *
 */
public class ZIPWriterImpl extends AbstractDocumentWriter
{
  /** Version made by field: PKZIP 2.0 */
  static final int ZIP_MINIMUM_VERSION = 0x0014;
  /** Version made by field: Made by MS-DOS */
  static final int ZIP_MADE_BY         = 0x0014;
  
  long headerPos;
  /** Contains all directory entries written in local header, used to write out the
   *  central directory structures.
   */
  Vector dirEntries;
  ZIPCRC32 crc = new ZIPCRC32();
  long crc32Value = 0;
  
  public ZIPWriterImpl()
  {
    super(false,1);
    validator = new ZIPUtilities();
    // Contains all elements currently written to help writing central directory information. 
    dirEntries = new Vector();
  }

  /** Writes the local header information */
  protected void writeLocalHeader(ResourceChunkInfo chunkData) throws IOException
  {
    String filename = chunkData.id.toString();
    // Convert to a version which contains only ASCII characters. 
    String convertedFilename = CanonicalChars.convertCanonical(filename);
    boolean localCompress = false;
    byte[] bFilename;
    
    chunkData.offset = dataWriter.getStreamPosition();
    /** If no attributes are specified, we manage everything internally. */
    if (chunkData.getAttributes().size()==0)
    {
      localCompress = true;
    }
    bFilename = convertedFilename.getBytes("ISO-8859-1");
    /* local file header signature */
    dataWriter.writeInt(ZIPUtilities.MAGIC_LOCAL_HEADER);
    /* version needed to extract       2 bytes */
    dataWriter.writeShort(ZIP_MINIMUM_VERSION);
    /* general purpose bit flag        2 bytes */
    dataWriter.writeShort(0);
    /* compression method              2 bytes */
    /* If no compression method is specified, then the value is stored. */
    if (localCompress)
      dataWriter.writeShort(ZIPUtilities.COMPRESSION_NONE);
    /* last mod file time              2 bytes */
    /* last mod file date              2 bytes */
    if (localCompress)
    {
      Calendar cal = Calendar.getInstance();  
      cal.setTime(new Date(System.currentTimeMillis()));
      dataWriter.writeShort(DOSDate.CalendarToDOSTime(cal));
      dataWriter.writeShort(DOSDate.CalendarToDOSDate(cal));
    } else
    {
      Calendar cal = Calendar.getInstance();  
      cal.setTime(chunkData.getLastModifiedDate());
      dataWriter.writeShort(DOSDate.CalendarToDOSTime(cal));
      dataWriter.writeShort(DOSDate.CalendarToDOSDate(cal));
    }
    //!!!!!!!!!!!!!!!!!!!!!!!!!!1
    /* crc-32                          4 bytes */
    dataWriter.writeInt((int)crc32Value);
    /* compressed size                 4 bytes */
    dataWriter.writeInt((int)chunkData.size);
    /* uncompressed size               4 bytes */
    /* If no compression, the data is stored. */
    if (localCompress)
      dataWriter.writeInt((int)chunkData.size);
    else
      dataWriter.writeInt((int)chunkData.getSize());
    /* file name length                2 bytes */
    dataWriter.writeShort(bFilename.length);
    
    /* extra field length              2 bytes */
    dataWriter.writeShort(0);
    
    /* file name (variable size) */
    dataWriter.write(bFilename, 0, bFilename.length);
    /* extra field (variable size) */
    
    
    crc.engineReset();
  }

  public void writeEndDocument() throws IOException
  {
    byte[] bFilename;
    long centralDirPos;
    long centralDirEnd;
    String filename;
    String convertedFilename;
    boolean localCompress;
    
    // Do some validation first.
    super.writeEndDocument();
    
    centralDirPos = dataWriter.getStreamPosition();
    
    /** Write each entry in the central directory */
    for (int i = 0; i < dirEntries.size(); i++)
    {
      localCompress = false; 
      
      ResourceChunkInfo chunkInfo = (ResourceChunkInfo)dirEntries.elementAt(i);
      /** If no attributes are specified, we manage everything internally. */
      if (chunkInfo.getAttributes().size()==0)
      {
        localCompress = true;
      }
      
      filename = chunkInfo.id.toString();
      // Convert to a version which contains only ASCII characters. 
      convertedFilename = CanonicalChars.convertCanonical(filename);
      bFilename = convertedFilename.getBytes("ISO-8859-1");
      
      /* central file header signature   4 bytes  (0x02014b50) */
      dataWriter.writeInt(ZIPUtilities.MAGIC_CENTRAL_DIRECTORY_HEADER);
      /* version made by                 2 bytes  */
      dataWriter.writeShort(ZIP_MADE_BY);
      /* version needed to extract       2 bytes  */
      dataWriter.writeShort(ZIP_MINIMUM_VERSION);
      /* general purpose bit flag        2 bytes  */
      dataWriter.writeShort(0);
      /* compression method              2 bytes  */
      if (localCompress)
        dataWriter.writeShort(ZIPUtilities.COMPRESSION_NONE);
      
      /* last mod file time              2 bytes */
      /* last mod file date              2 bytes */
      if (localCompress)
      {
        Calendar cal = Calendar.getInstance();  
        cal.setTime(new Date(System.currentTimeMillis()));
        dataWriter.writeShort(DOSDate.CalendarToDOSTime(cal));
        dataWriter.writeShort(DOSDate.CalendarToDOSDate(cal));
      } else
      {
        Calendar cal = Calendar.getInstance();  
        cal.setTime(chunkInfo.getLastModifiedDate());
        dataWriter.writeShort(DOSDate.CalendarToDOSTime(cal));
        dataWriter.writeShort(DOSDate.CalendarToDOSDate(cal));
      }
      /* crc-32                          4 bytes  */
      dataWriter.writeInt((int)crc32Value);
      /* compressed size                 4 bytes  */
      dataWriter.writeInt((int)chunkInfo.size);
      /* uncompressed size               4 bytes  */
      /* If no compression, the data is stored. */
      if (chunkInfo.getCompression()==null)
        dataWriter.writeInt((int)chunkInfo.size);
      else
        dataWriter.writeInt((int)chunkInfo.getSize());
      /* file name length                2 bytes  */
      dataWriter.writeShort(bFilename.length);
      /* extra field length              2 bytes  */
      dataWriter.writeShort(0);
      /* file comment length             2 bytes  */
      dataWriter.writeShort(0);
      /* disk number start               2 bytes  */
      dataWriter.writeShort(0);
      /* internal file attributes        2 bytes  */
      dataWriter.writeShort(0);
      /* external file attributes        4 bytes  */
      dataWriter.writeInt(0);
      /* relative offset of local header 4 bytes  */
      dataWriter.writeInt((int)chunkInfo.offset);
      
      /* file name (variable size) */
      dataWriter.write(bFilename, 0, bFilename.length);
      /* extra field (variable size) */
      /* file comment (variable size) */
    }
    centralDirEnd = dataWriter.getStreamPosition(); 
    
    
    /** --------------- write of central directory record --------------- */
    
    /* end of central dir signature    4 bytes  (0x06054b50) */
    dataWriter.writeInt(ZIPUtilities.MAGIC_CENTRAL_DIRECTORY_END);
    /* number of this disk             2 bytes               */
    dataWriter.writeShort(0);
    /* number of the disk with the start of the central directory     2 bytes */
    dataWriter.writeShort(0);
    /* total number of entries in the central directory on this disk  2 bytes */                 
    dataWriter.writeShort(dirEntries.size());
    /* total number of entries in the central directory               2 bytes */
    dataWriter.writeShort(dirEntries.size());
    /* size of the central directory   4 bytes */
    dataWriter.writeInt((int)(centralDirEnd-centralDirPos));
    /* offset of start of central directory with respect to the starting disk number        4 bytes */
    dataWriter.writeInt((int)centralDirPos);
    /* .ZIP file comment length        2 bytes  */
    dataWriter.writeShort(0);
    /* .ZIP file comment       (variable size)  */
  }

  protected void writeChunkHeader(ChunkInfo chunkData)
      throws IOException
  {
    headerPos = dataWriter.getStreamPosition();
    crc.engineReset();
    crc32Value = 0;
    writeLocalHeader((ResourceChunkInfo) chunkData);
  }

  protected void writeFixupChunkHeader(ChunkInfo chunkData)
      throws IOException
  {
    long currentPos = dataWriter.getStreamPosition();
    dataWriter.seek(headerPos);
    
    /** Actually create a directory entry by copying the data to use. */
    ResourceChunkInfo resChunk = new ResourceChunkInfo();
    resChunk.copy(chunkData);
    dirEntries.addElement(resChunk);
    crc32Value = crc.engineDigest();
    writeLocalHeader(resChunk);
    dataWriter.seek(currentPos);
  }

  protected void writeChunkFooter(ChunkInfo chunkData)
      throws IOException
  {
  }


  /** Chunks for this type of file is an actual ResourceChunkInfo */
  protected ChunkInfo newChunkInfo()
  {
    return new ResourceChunkInfo();
  }

  public void writeStartDocument(String publicID)
      throws IOException
  {
    // TODO Auto-generated method stub
    
  }
  /** This calculates the CRC-32 value of the data also. */
  public void write(byte[] buffer, int off, int len) throws IOException
  {
    crc.engineUpdate(buffer, 0, len);     
    super.write(buffer, off, len);
  }

  /** This calculates the CRC-32 value of the data also. */
  public void write(int b) throws IOException
  {
    w[0] = (byte) b;
    crc.engineUpdate(w, 0, 1);     
    super.write(b);
  }

  /** This calculates the CRC-32 value of the data also. */
  public void write(byte[] buffer) throws IOException
  {
    crc.engineUpdate(buffer, 0, buffer.length);     
    super.write(buffer);
  }
  

  
}
