package com.optimasc.streams.warc;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import com.optimasc.streams.DocumentInfo;
import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.StreamFilter;
import com.optimasc.streams.internal.ChunkInfo;
import com.optimasc.streams.internal.DataReader;
import com.optimasc.streams.internal.AbstractDocumentReader;
import com.optimasc.streams.riff.RIFFUtilities;
import com.optimasc.utils.Property;
//import com.optimasc.utils.PropertyArray;

public class WARCReaderImpl extends AbstractDocumentReader
{

  public byte[] byteBuffer = new byte[WARCUtilities.MAGIC_WARC.length()];
  
  public WARCReaderImpl(InputStream inputStream, StreamFilter filter) throws DocumentStreamException
  {
    super(64, inputStream, filter);
  }
  
  
  /** Reads a complete field name and decodes into a property key-value pair. If there
   *  is no more fields to read, this value returns null.
   *  
   * @param dataReader
   * @return
   * @throws DocumentStreamException
   */
  protected Property readNextField(DataReader dataReader) throws DocumentStreamException
  {
    StringBuffer buffer = new StringBuffer();
    Property prop;
    String s1;
    int separatorIndex = -1;
    int c= dataReader.read8Raw();
    // Check if this is the end of the record structure.
    if (c == WARCUtilities.CR)
    {
      c = dataReader.read8Raw();
      if (c != WARCUtilities.LF)
        errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_BLOCK_INVALID_HEADER));
      return null;
    }
    while (c != WARCUtilities.CR)
    {
      if (c == -1) 
        errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_BLOCK_INVALID_HEADER));
      // Only the first colon is the separator index
      if ((c == ':') && (separatorIndex == -1))
        separatorIndex = buffer.length();
      buffer.append((char)c);
      c = dataReader.read8Raw();
    }
    c = dataReader.read8Raw();
    if (c == -1) 
      errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_BLOCK_INVALID_HEADER));
    if (c != WARCUtilities.LF)
      errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_BLOCK_INVALID_HEADER));
    // check if there is really a field!
    if (separatorIndex == -1)
      errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_BLOCK_INVALID_HEADER));
    s1 = buffer.toString();
    prop = new Property(s1.substring(0,separatorIndex).trim(),s1.substring(separatorIndex+1,s1.length()).trim());
    return prop;
  }
  
  protected String readNextToken(DataReader dataReader) throws DocumentStreamException
  {
    int c= dataReader.read8Raw();
    StringBuffer buffer = new StringBuffer();
    while (WARCUtilities.isWhiteSpace((char)c))
    {
      c = dataReader.read8Raw();
      // End of block
      if (c == WARCUtilities.CR)
      {
        // Go back to CR character
        dataReader.setPosition(dataReader.getPosition()-1);
        return null;
      }
      if (c == -1) 
        errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_BLOCK_INVALID_HEADER));
    }
    while (WARCUtilities.isWhiteSpace((char)c)==false)
    {
      c = dataReader.read8Raw();
      // End of block
      if (c == WARCUtilities.CR)
      {
        // Go back to CR character
        dataReader.setPosition(dataReader.getPosition()-1);
        return null;
      }
      if (c == -1) 
        errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_BLOCK_INVALID_HEADER));
      buffer.append((char)c);
    }
    return buffer.toString();
  }
  
  
  protected void skipToEndOfRecord(DataReader dataReader) throws DocumentStreamException
  {
    int c= dataReader.read8Raw();
    while (c != WARCUtilities.CR)
    {
      c = dataReader.read8Raw();
      if (c == -1) 
        errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_BLOCK_INVALID_HEADER));
    }
    c = dataReader.read8Raw();
    if (c == -1) 
      errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_BLOCK_INVALID_HEADER));
    if (c != WARCUtilities.LF)
      errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_BLOCK_INVALID_HEADER));
  }

  
  protected void readChunkHeader(DataReader dataReader, ChunkInfo header)
      throws DocumentStreamException
  {
    header.reset();
/*	  
    PropertyArray propList = new PropertyArray(false);
    String sig = null;
    Property p = null;
    header.reset();
    
    // Read the signature information
    dataReader.read(byteBuffer, 0, WARCUtilities.MAGIC_WARC.length());
    try
    {
      sig = new String(byteBuffer, "UTF-8");
    } catch (UnsupportedEncodingException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // We only support version 1.0
    if (sig.equals(WARCUtilities.MAGIC_WARC)==false)
    {
      errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_BLOCK_INVALID_ID,sig));
    }
    
    // Read all header information
    do 
    {
      p = readNextField(dataReader);
      if (p != null)
      {
        propList.putProperty(p);
        header.attributes.add(new Attribute(null,p.getKey(),p.getValue()));
      }
    } while (p != null);
    
    // Validate the necessary elements
    if (propList.getProperty(WARCUtilities.FIELD_LENGTH_UPPER)==null)
    {
      errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_BLOCK_INVALID_HEADER,WARCUtilities.FIELD_LENGTH_UPPER));
    } else
    {
      header.size = Long.parseLong(propList.getProperty(WARCUtilities.FIELD_LENGTH_UPPER).getValue());
    }
    if (propList.getProperty(WARCUtilities.FIELD_RECORD_TYPE_UPPER)==null)
    {
      errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_BLOCK_INVALID_HEADER,WARCUtilities.FIELD_RECORD_TYPE_UPPER));
    } else
    {
    }
    header.id = propList.getProperty(WARCUtilities.FIELD_RECORD_TYPE_UPPER).getValue();
    // Validate the mandatory elements but which are less important to us.
    if (propList.getProperty(WARCUtilities.FIELD_RECORD_ID_UPPER)==null)
    {
      errorHandler.warning(new DocumentStreamException(DocumentStreamException.ERR_BLOCK_INVALID_HEADER,WARCUtilities.FIELD_RECORD_ID_UPPER));
    }
    if (propList.getProperty(WARCUtilities.FIELD_RECORD_DATE_UPPER)==null)
    {
      errorHandler.warning(new DocumentStreamException(DocumentStreamException.ERR_BLOCK_INVALID_HEADER,WARCUtilities.FIELD_RECORD_DATE_UPPER));
    }
    header.type = ChunkInfo.TYPE_CHUNK;
    header.offset = reader.getPosition();
    // There are two CRLF CRLF after the block data
    header.extraSize = 4;
*/ 
  }

  protected DocumentInfo readDocumentHeader(DataReader dataReader)
  {
    int type;
    try
    {
    long position = dataReader.getPosition();
    // Read the chunk identifier
    dataReader.read(byteBuffer, 0, WARCUtilities.MAGIC_WARC.length());
    String chunkID = null;
    try
    {
      chunkID = new String(byteBuffer, "UTF-8");
    } catch (UnsupportedEncodingException e1)
    {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    if (chunkID.equals(WARCUtilities.MAGIC_WARC))
    {
      type = DocumentInfo.TYPE_CHARACTER;
    } else 
      return null;
    dataReader.setPosition(position);
    
    DocumentInfo document = new DocumentInfo(chunkID,"application/warc",type,dataReader.getSize());
    return document;
    
  } catch (DocumentStreamException e)
  {
    return null;
  }
    


  }

}
