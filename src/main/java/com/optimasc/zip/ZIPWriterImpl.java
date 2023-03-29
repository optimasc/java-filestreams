package com.optimasc.zip;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import org.apache.commons.vfs2.RandomAccessContent;

import com.optimasc.date.DOSDate;
import com.optimasc.streams.internal.ChunkInfo;
import com.optimasc.streams.internal.AbstractDocumentWriter;
import com.optimasc.streams.internal.ResourceChunkInfo;

import com.optimasc.streams.DocumentStreamException;
import com.optimasc.utils.CanonicalChars;

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
  
  public ZIPWriterImpl(RandomAccessContent outputStream) throws DocumentStreamException
  {
    super(outputStream, false,1);
    validator = new ZIPUtilities();
    // Contains all elements currently written to help writing central directory information. 
    dirEntries = new Vector();
  }

  /** Writes the local header information */
  protected void writeLocalHeader(ResourceChunkInfo chunkData) throws DocumentStreamException
  {
    String filename = chunkData.id.toString();
    // Convert to a version which contains only ASCII characters. 
    String convertedFilename = CanonicalChars.convertCanonical(filename);
    boolean localCompress = false;
    byte[] bFilename;
    
    chunkData.offset = dataWriter.getPosition();
    /** If no attributes are specified, we manage everything internally. */
    if (chunkData.getAttributes().size()==0)
    {
      localCompress = true;
    }
    try
    {
      bFilename = convertedFilename.getBytes("ISO-8859-1");
    } catch (UnsupportedEncodingException e)
    {
      throw new DocumentStreamException(DocumentStreamException.ERR_IO);
    }
    /* local file header signature */
    dataWriter.write32Little(ZIPUtilities.MAGIC_LOCAL_HEADER);
    /* version needed to extract       2 bytes */
    dataWriter.write16Little(ZIP_MINIMUM_VERSION);
    /* general purpose bit flag        2 bytes */
    dataWriter.write16Little(0);
    /* compression method              2 bytes */
    /* If no compression method is specified, then the value is stored. */
    if (localCompress)
      dataWriter.write16Little(ZIPUtilities.COMPRESSION_NONE);
    /* last mod file time              2 bytes */
    /* last mod file date              2 bytes */
    if (localCompress)
    {
      Calendar cal = Calendar.getInstance();  
      cal.setTime(new Date(System.currentTimeMillis()));
      dataWriter.write16Little(DOSDate.CalendarToDOSTime(cal));
      dataWriter.write16Little(DOSDate.CalendarToDOSDate(cal));
    } else
    {
      Calendar cal = Calendar.getInstance();  
      cal.setTime(chunkData.getLastModified());
      dataWriter.write16Little(DOSDate.CalendarToDOSTime(cal));
      dataWriter.write16Little(DOSDate.CalendarToDOSDate(cal));
    }
    //!!!!!!!!!!!!!!!!!!!!!!!!!!1
    /* crc-32                          4 bytes */
    dataWriter.write32Little(crc32Value);
    /* compressed size                 4 bytes */
    dataWriter.write32Little(chunkData.size);
    /* uncompressed size               4 bytes */
    /* If no compression, the data is stored. */
    if (localCompress)
      dataWriter.write32Little(chunkData.size);
    else
      dataWriter.write32Little(chunkData.getRealSize());
    /* file name length                2 bytes */
    dataWriter.write16Little(bFilename.length);
    
    /* extra field length              2 bytes */
    dataWriter.write16Little(0);
    
    /* file name (variable size) */
    dataWriter.write(bFilename, 0, bFilename.length);
    /* extra field (variable size) */
    
    
    crc.engineReset();
  }

  public void writeEndDocument() throws DocumentStreamException
  {
    byte[] bFilename;
    long centralDirPos;
    long centralDirEnd;
    String filename;
    String convertedFilename;
    boolean localCompress;
    
    // Do some validation first.
    super.writeEndDocument();
    
    centralDirPos = dataWriter.getPosition();
    
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
      try
      {
        bFilename = convertedFilename.getBytes("ISO-8859-1");
      } catch (UnsupportedEncodingException e)
      {
        throw new DocumentStreamException(DocumentStreamException.ERR_IO);
      }
      
      /* central file header signature   4 bytes  (0x02014b50) */
      dataWriter.write32Little(ZIPUtilities.MAGIC_CENTRAL_DIRECTORY_HEADER);
      /* version made by                 2 bytes  */
      dataWriter.write16Little(ZIP_MADE_BY);
      /* version needed to extract       2 bytes  */
      dataWriter.write16Little(ZIP_MINIMUM_VERSION);
      /* general purpose bit flag        2 bytes  */
      dataWriter.write16Little(0);
      /* compression method              2 bytes  */
      if (localCompress)
        dataWriter.write16Little(ZIPUtilities.COMPRESSION_NONE);
      
      /* last mod file time              2 bytes */
      /* last mod file date              2 bytes */
      if (localCompress)
      {
        Calendar cal = Calendar.getInstance();  
        cal.setTime(new Date(System.currentTimeMillis()));
        dataWriter.write16Little(DOSDate.CalendarToDOSTime(cal));
        dataWriter.write16Little(DOSDate.CalendarToDOSDate(cal));
      } else
      {
        Calendar cal = Calendar.getInstance();  
        cal.setTime(chunkInfo.getLastModified());
        dataWriter.write16Little(DOSDate.CalendarToDOSTime(cal));
        dataWriter.write16Little(DOSDate.CalendarToDOSDate(cal));
      }
      /* crc-32                          4 bytes  */
      dataWriter.write32Little(crc32Value);
      /* compressed size                 4 bytes  */
      dataWriter.write32Little(chunkInfo.size);
      /* uncompressed size               4 bytes  */
      /* If no compression, the data is stored. */
      if (chunkInfo.getCompression()==null)
        dataWriter.write32Little(chunkInfo.size);
      else
        dataWriter.write32Little(chunkInfo.getRealSize());
      /* file name length                2 bytes  */
      dataWriter.write16Little(bFilename.length);
      /* extra field length              2 bytes  */
      dataWriter.write16Little(0);
      /* file comment length             2 bytes  */
      dataWriter.write16Little(0);
      /* disk number start               2 bytes  */
      dataWriter.write16Little(0);
      /* internal file attributes        2 bytes  */
      dataWriter.write16Little(0);
      /* external file attributes        4 bytes  */
      dataWriter.write32Little(0);
      /* relative offset of local header 4 bytes  */
      dataWriter.write32Little(chunkInfo.offset);
      
      /* file name (variable size) */
      dataWriter.write(bFilename, 0, bFilename.length);
      /* extra field (variable size) */
      /* file comment (variable size) */
    }
    centralDirEnd = dataWriter.getPosition(); 
    
    
    /** --------------- write of central directory record --------------- */
    
    /* end of central dir signature    4 bytes  (0x06054b50) */
    dataWriter.write32Little(ZIPUtilities.MAGIC_CENTRAL_DIRECTORY_END);
    /* number of this disk             2 bytes               */
    dataWriter.write16Little(0);
    /* number of the disk with the start of the central directory     2 bytes */
    dataWriter.write16Little(0);
    /* total number of entries in the central directory on this disk  2 bytes */                 
    dataWriter.write16Little(dirEntries.size());
    /* total number of entries in the central directory               2 bytes */
    dataWriter.write16Little(dirEntries.size());
    /* size of the central directory   4 bytes */
    dataWriter.write32Little(centralDirEnd-centralDirPos);
    /* offset of start of central directory with respect to the starting disk number        4 bytes */
    dataWriter.write32Little(centralDirPos);
    /* .ZIP file comment length        2 bytes  */
    dataWriter.write16Little(0);
    /* .ZIP file comment       (variable size)  */
  }

  protected void writeChunkHeader(ChunkInfo chunkData)
      throws DocumentStreamException
  {
    headerPos = dataWriter.getPosition();
    crc.engineReset();
    crc32Value = 0;
    writeLocalHeader((ResourceChunkInfo) chunkData);
  }

  protected void writeFixupChunkHeader(ChunkInfo chunkData)
      throws DocumentStreamException
  {
    long currentPos = dataWriter.getPosition();
    dataWriter.setPosition(headerPos);
    
    /** Actually create a directory entry by copying the data to use. */
    ResourceChunkInfo resChunk = new ResourceChunkInfo();
    resChunk.copy(chunkData);
    dirEntries.addElement(resChunk);
    crc32Value = crc.engineDigest();
    writeLocalHeader(resChunk);
    dataWriter.setPosition(currentPos);
  }

  protected void writeChunkFooter(ChunkInfo chunkData)
      throws DocumentStreamException
  {
  }

  public void warning(DocumentStreamException exception)
      throws DocumentStreamException
  {
    // TODO Auto-generated method stub
    
  }

  public void error(DocumentStreamException exception)
      throws DocumentStreamException
  {
    // TODO Auto-generated method stub
    
  }

  public void fatalError(DocumentStreamException exception)
      throws DocumentStreamException
  {
    // TODO Auto-generated method stub
    
  }

  /** Chunks for this type of file is an actual ResourceChunkInfo */
  protected ChunkInfo newChunkInfo()
  {
    return new ResourceChunkInfo();
  }

  public void writeStartDocument(String publicID)
      throws DocumentStreamException
  {
    // TODO Auto-generated method stub
    
  }

  public void writeOctetString(byte[] buffer, int off, int len)
      throws DocumentStreamException
  {
    crc.engineUpdate(buffer, off, len);     
    super.writeOctetString(buffer, off, len);
  }

  
}
