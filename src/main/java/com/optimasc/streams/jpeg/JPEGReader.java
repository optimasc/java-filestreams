package com.optimasc.streams.jpeg;

import java.io.InputStream;

import com.optimasc.streams.DocumentInfo;
import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.StreamFilter;
import com.optimasc.streams.internal.ChunkInfo;
import com.optimasc.streams.internal.DataReader;
import com.optimasc.streams.internal.AbstractDocumentReader;

/**
 * Implements a JPEG reader.
 * 
 * @author Carl Eric Codere
 * 
 */
public class JPEGReader extends AbstractDocumentReader
{

  public byte[] byteBuffer = new byte[4];
  protected boolean bigEndian;
  // Contains the image data.
  protected ChunkInfo dataHeader;
  /* Indicates that Start of image marker has been found */
  boolean SOSFound;
  /* Indicates that the end of the document has been reached */
  boolean endOfDocument;
  
  

  public JPEGReader(InputStream inputStream, StreamFilter filter)
      throws DocumentStreamException
  {
    super(64, inputStream, filter);
    bigEndian = true;
    dataHeader = newChunkInfo();
  }

  protected void readChunkHeader(DataReader dataReader, ChunkInfo header)
      throws DocumentStreamException
  {

    int w;
    int id;
    boolean marker = false;

    // Check if this is the image data chunk 
    if (SOSFound == true)
    {
      header.copy(dataHeader);
      SOSFound = false;
      return;
    }
    
    // The file can be padded anywhere with FF, as specified with the standard 
    marker = false;
    do
    {
      id = dataReader.read8();
      //  At least one marker has been found 
      if (id == JPEGUtilities.JPEG_MARKER)
        marker = true;
    } while (id == JPEGUtilities.JPEG_MARKER);
    // According to the standard, the 2nd byte must not be equal to 0xFF or 0x00 
    // if there was no marker byte before this byte, there is surely an error    
    if ((id == 0) || (marker == false))
    {
      errorHandler.error(new DocumentStreamException("Invalid block in file"));
    }

    header.reset();
    header.type = ChunkInfo.TYPE_CHUNK;
    header.id = new Integer(id);
    // THESE DO NOT HAVE ANY LENGTH BYTES! 
    switch (id)
    {
      case JPEGUtilities.JPEG_ID_TEM:
      case JPEGUtilities.JPEG_ID_RST0:
      case JPEGUtilities.JPEG_ID_rst1:
      case JPEGUtilities.JPEG_ID_rst2:
      case JPEGUtilities.JPEG_ID_rst3:
      case JPEGUtilities.JPEG_ID_rst4:
      case JPEGUtilities.JPEG_ID_rst5:
      case JPEGUtilities.JPEG_ID_rst6:
      case JPEGUtilities.JPEG_ID_rst7:
      case JPEGUtilities.JPEG_ID_SOI:
      case JPEGUtilities.JPEG_ID_EOI:
        header.size = 0;
        return;
    }
    
    
    // Read in the size 
    w = dataReader.read16Big() & 0xffff;

    // Decrement by 2, because the length contains these length bytes 
    if (w > 0)
    {
      header.size = w - 2;
    }
    // If this is the start of scan , then skip all data until EOI
    //  marker
    //
    if (id == JPEGUtilities.JPEG_ID_SOS)
    {
      SOSFound = true;
      dataHeader.reset();
      dataHeader.size = 0;
      long oldpos = dataReader.getPosition();
      dataReader.setPosition(oldpos + header.size);
      // If the SOS has been found, create an actual DATA block 
      while (true)
      {
        int b1 = dataReader.read8();
        dataHeader.size++;
        if (b1 == JPEGUtilities.JPEG_MARKER)
        {
          b1 = dataReader.read8();
          dataHeader.size++;
          ;
          if (b1 == (JPEGUtilities.JPEG_MAGIC_EOI_SIGNATURE & 0xff))
          {
            // The EOI bytes are not part of this chunk data, so we must subtract them
            dataHeader.size = dataHeader.size - 2;
            dataHeader.type = ChunkInfo.TYPE_CHUNK;
            dataHeader.id = new Integer(JPEGUtilities.JPEG_ID_DATA);
            dataReader.setPosition(oldpos);
            break;
          }
        }
      }
    }
  }

  protected DocumentInfo readDocumentHeader(DataReader dataReader)
  {
    DocumentInfo document = new DocumentInfo("", JPEGUtilities.MIME_TYPE, 0,
        dataReader.getSize());
    // Read the chunk identifier
    try
    {
      dataReader.read(byteBuffer, 0, 2);
      int w = (byteBuffer[0] << 8) | (byteBuffer[1]);
      if (w == JPEGUtilities.JPEG_MAGIC_SOI_SIGNATURE)
      {
        // check the file tailer
        dataReader.setPosition(dataReader.getSize() - 2);
        dataReader.read(byteBuffer, 0, 2);
        w = (byteBuffer[0] << 8) | (byteBuffer[1]);
        if (w == JPEGUtilities.JPEG_MAGIC_EOI_SIGNATURE)
        {
          dataReader.setPosition(0);
          return document;
        }
      } else
      /* There is possibly some extra data after the file,
        so checking EOI might not work as expected. So also
        check the data
      */
      {
        dataReader.setPosition(2);
        w = dataReader.read8();
        if (w == JPEGUtilities.JPEG_MARKER)
        {
          w = dataReader.read8();
          /* If the second segment is one of these types,
              it is probably a JPEG file, even though
              it contains extra data at the end of the file. */
          switch (w)
          {
            case JPEGUtilities.JPEG_ID_APP0:
            case JPEGUtilities.JPEG_ID_APP1:
            case JPEGUtilities.JPEG_ID_APP2:
            case JPEGUtilities.JPEG_ID_APP3:
            case JPEGUtilities.JPEG_ID_APP4:
            case JPEGUtilities.JPEG_ID_APP5:
            case JPEGUtilities.JPEG_ID_APP6:
            case JPEGUtilities.JPEG_ID_APP7:
            case JPEGUtilities.JPEG_ID_APP8:
            case JPEGUtilities.JPEG_ID_APP9:
            case JPEGUtilities.JPEG_ID_APP10:
            case JPEGUtilities.JPEG_ID_APP11:
            case JPEGUtilities.JPEG_ID_APP12:
            case JPEGUtilities.JPEG_ID_APP13:
            case JPEGUtilities.JPEG_ID_APP14:
            case JPEGUtilities.JPEG_ID_APP15:
              dataReader.setPosition(0);
              return document;
          }
        }
      }
    } catch (DocumentStreamException e)
    {
      return null;
    }
    return null;
  }

  /** Overriden to indicate that when EOI marker is found, which
   *  is translated internally to JPEG_ID_DATA, the document
   *  is ended.
   * 
   */
  protected boolean isDocumentEnd(ChunkInfo current)
      throws DocumentStreamException
  {
    /* Check if the current chunk is Scan image data, then 
     * for sure, nothing is left after this.
     */
    Integer obj = (Integer) current.id;
    if (obj.intValue() == JPEGUtilities.JPEG_ID_EOI)
    {
      endOfDocument = true;
      return true;
    }
    return super.isDocumentEnd(current);
  }

  protected void verifyEndOfDocument() throws DocumentStreamException
  {
    /* If we know that the end of the document has been reached,
     * but there is still some data to read, we know there is 
     * extra data at the end.
     */
    if (endOfDocument)
    {
      /** The 2 bytes represent the EOI marker */
      if ((reader.getPosition()+2) < (reader.getSize()))
      {
        errorHandler.warning(new DocumentStreamException(
            DocumentStreamException.ERR_EXTRA_DATA));
        
      } else
      {
        super.verifyEndOfDocument();
      }
    }
    
  }
  
  
  

}
