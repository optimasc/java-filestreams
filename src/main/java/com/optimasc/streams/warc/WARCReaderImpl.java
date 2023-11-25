package com.optimasc.streams.warc;

import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.optimasc.io.SeekableDataInputStream;
import com.optimasc.streams.Attribute;
import com.optimasc.streams.DocumentInfo;
import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.internal.AbstractDocumentReader;
import com.optimasc.streams.internal.ChunkInfo;

public class WARCReaderImpl extends AbstractDocumentReader
{

  public byte[] byteBuffer = new byte[WARCUtilities.MAGIC_WARC.length()];
  
  public WARCReaderImpl()
  {
    super(64);
  }
  
  
  /** Reads a complete field name and decodes into a property key-value pair. If there
   *  is no more fields to read, this value returns null.
   *  
   * @param dataReader
   * @return
   * @throws DocumentStreamException
   */
  protected Attribute readNextField(SeekableDataInputStream dataReader) throws DocumentStreamException, IOException
  {
    StringBuffer buffer = new StringBuffer();
    Attribute prop;
    String s1;
    int separatorIndex = -1;
    int c= dataReader.readUnsignedByte();
    // Check if this is the end of the record structure.
    if (c == WARCUtilities.CR)
    {
      c = dataReader.readUnsignedByte();
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
      c = dataReader.readUnsignedByte();
    }
    c = dataReader.readUnsignedByte();
    if (c == -1) 
      errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_BLOCK_INVALID_HEADER));
    if (c != WARCUtilities.LF)
      errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_BLOCK_INVALID_HEADER));
    // check if there is really a field!
    if (separatorIndex == -1)
      errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_BLOCK_INVALID_HEADER));
    s1 = buffer.toString();
    prop = new Attribute(null,s1.substring(0,separatorIndex).trim(),s1.substring(separatorIndex+1,s1.length()).trim());
    return prop;
  }
  
  protected String readNextToken(SeekableDataInputStream dataReader) throws DocumentStreamException, IOException
  {
    int c= dataReader.readUnsignedByte();
    StringBuffer buffer = new StringBuffer();
    while (WARCUtilities.isWhiteSpace((char)c))
    {
      c = dataReader.readUnsignedByte();
      // End of block
      if (c == WARCUtilities.CR)
      {
        // Go back to CR character
        dataReader.seek(dataReader.getStreamPosition()-1);
        return null;
      }
      if (c == -1) 
        errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_BLOCK_INVALID_HEADER));
    }
    while (WARCUtilities.isWhiteSpace((char)c)==false)
    {
      c = dataReader.readUnsignedByte();
      // End of block
      if (c == WARCUtilities.CR)
      {
        // Go back to CR character
        dataReader.seek(dataReader.getStreamPosition()-1);
        return null;
      }
      if (c == -1) 
        errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_BLOCK_INVALID_HEADER));
      buffer.append((char)c);
    }
    return buffer.toString();
  }
  
  
  protected void skipToEndOfRecord(SeekableDataInputStream dataReader) throws DocumentStreamException, IOException
  {
    int c= dataReader.readUnsignedByte();
    while (c != WARCUtilities.CR)
    {
      c = dataReader.readUnsignedByte();
      if (c == -1) 
        errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_BLOCK_INVALID_HEADER));
    }
    c = dataReader.readUnsignedByte();
    if (c == -1) 
      errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_BLOCK_INVALID_HEADER));
    if (c != WARCUtilities.LF)
      errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_BLOCK_INVALID_HEADER));
  }

  
  protected void readChunkHeader(SeekableDataInputStream dataReader, ChunkInfo header)
      throws DocumentStreamException, IOException
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
    header.offset = reader.getStreamPosition();
    // There are two CRLF CRLF after the block data
    header.extraSize = 4;
*/ 
  }

  protected DocumentInfo readDocumentHeader(SeekableDataInputStream dataReader) throws IOException
  {
    int type;
    try
    {
    long position = dataReader.getStreamPosition();
    // Read the chunk identifier
    dataReader.readFully(byteBuffer, 0, WARCUtilities.MAGIC_WARC.length());
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
    dataReader.seek(position);
    
    DocumentInfo document = new DocumentInfo(chunkID,"application/warc",type,dataReader.length());
    return document;
    
  } catch (EOFException e)
    {
      return null;
    }
  }

}
