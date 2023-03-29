package com.optimasc.streams.internal;

import java.io.IOException;
import java.io.InputStream;

import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.FileMarkableInputStream;
import com.optimasc.streams.ErrorHandler;

/**
 * Class that implements reading from a stream with correct error management and
 * that supports both little and big streamType data reading. Because of the limitation
 * of the InputStream class, the maximum size supported of the resources is
 * 2 GBytes, unless the inputStream implementation is of type FileMarkableInputStream,
 * in which case, it is possible to retrieve the length of the stream on 64-bits.
 * 
 * @author Carl Eric Codere
 * 
 */
public class DataReader
{
  protected InputStream inputStream;

  protected long size;
  /** Current stream position in bytes, used for error reporting. */
  protected long position;
  /** Internal buffer for reading data from the stream */
  protected byte[] w = new byte[8];
  protected boolean littleEndian = false;
  
  protected ErrorHandler errorHandler;

  public DataReader(InputStream inputStream, ErrorHandler handler) throws DocumentStreamException
  {
    super();
    this.inputStream = inputStream;
    this.errorHandler = handler;
    try
    {
      inputStream.mark(inputStream.available());
      /* Try to retrieve the 64-bit data length of the resource. */
      if (inputStream instanceof FileMarkableInputStream)
      {
        FileMarkableInputStream mis = (FileMarkableInputStream)inputStream; 
        size = mis.size(); 
      } else
      {
        size = inputStream.available();
      }
    } catch (IOException e)
    {
      errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_IO,
          new LocationImpl(position)));
    }
    position = 0;
  }
  
  /** Reads the data without error checking. 
   * 
   * @return -1 if end of stream has been reached.
   */
  public int read8Raw() throws DocumentStreamException
  {
    int currentLength = 0;
    int readValue;
    try
    {
    readValue = inputStream.read();
    if (readValue == -1)
      return readValue;
    currentLength += 1;
    position += 1;
    return readValue;
    } catch (IOException e)
    {
      errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_IO,
          new LocationImpl(position)));
    }
    return -1;
  }

  public void read(byte[] b, int off, int len) throws DocumentStreamException
  {
    int currentLength = 0;
    int readValue;
    try
    {
      while (len > 0)
      {
        readValue = inputStream.read(b, off, len);
        if (readValue == -1)
          errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_END_OF_STREAM,
              new LocationImpl(position)));
        currentLength += readValue;
        position += readValue;
        len = len - currentLength;
      }
    } catch (IOException e)
    {
      errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_IO,
          new LocationImpl(position)));
    }
  }

  public long read32Little() throws DocumentStreamException
  {
    read(w, 0, 4);
    return (w[3]) << 24 | (w[2] & 0xff) << 16 | (w[1] & 0xff) << 8
        | (w[0] & 0xff);
  }

  public long read32Big() throws DocumentStreamException
  {
    read(w, 0, 4);
    return (((w[0] & 0xFF) << 24) | ((w[1] & 0xFF) << 16) | ((w[2] & 0xFF) << 8) | (w[3] & 0xFF));

  }
  
  
  public long read64Big() throws DocumentStreamException
  {
    read(w, 0, 8);
    return (((long)w[0] << 56) |
            ((long)(w[1] & 255) << 48) |
            ((long)(w[2] & 255) << 40) |
            ((long)(w[3] & 255) << 32) |
            ((long)(w[4] & 255) << 24) |
            ((w[5] & 255) << 16) |
            ((w[6] & 255) <<  8) |
            ((w[7] & 255) <<  0));

  }
  
  public long read64Little() throws DocumentStreamException
  {
    read(w, 0, 8);
    return (long) (w[7]) << 56
    | (long) (w[6] & 0xff) << 48
    | (long) (w[5] & 0xff) << 40
    | (long) (w[4] & 0xff) << 32
    | (long) (w[3] & 0xff) << 24
    | (long) (w[2] & 0xff) << 16
    | (long) (w[1] & 0xff) << 8
    | (long) (w[0] & 0xff);
  }
  
  

  public short read16Little() throws DocumentStreamException
  {
    read(w, 0, 2);
    return (short) ((w[1] & 0xff) << 8 | (w[0] & 0xff));

  }

  public short read16Big() throws DocumentStreamException
  {
    read(w, 0, 2);
    return (short) ((w[0] & 0xff) << 8 | (w[1] & 0xff));

  }
  
  public int read8() throws DocumentStreamException
  {
    read(w, 0, 1);
    return  (w[0] & 0xFF);

  }
  

  public void skip(long n) throws DocumentStreamException
  {
    long count;
    while (n > 0)
    {
      try
      {
        count = inputStream.skip(n);
        if (count <= 0)
        {
          errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_IO,
              new LocationImpl(position)));
        }
        position += count;
        n = n - count;
      } catch (IOException e)
      {
        errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_IO,
            new LocationImpl(position)));
      }
    }
  }

  /** Return the stream position */
  public long getPosition()
  {
    return position;
  }

  /**
   * Return the stream position
   * 
   * @throws DocumentStreamException
   */
  public void setPosition(long off) throws DocumentStreamException
  {
    try
    {
      inputStream.reset();
      skip(off);
      this.position = off;
    } catch (IOException e)
    {
      errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_IO,
          new LocationImpl(position)));
    }

  }
  
  public long getSize()
  {
    return size;
  }

}
