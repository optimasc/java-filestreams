package com.optimasc.streams.internal;

import java.io.IOException;
import org.apache.commons.vfs2.RandomAccessContent;

import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.ErrorHandler;

public class DataWriter
{
  
  protected RandomAccessContent outputStream;

  protected long size;
  /** Current stream position in bytes, used for error reporting. */
  protected long position;
  /** Internal buffer for reading data from the stream */
  protected byte[] w = new byte[8];
  
  protected ErrorHandler errorHandler;
  
  public DataWriter(RandomAccessContent outputStream, ErrorHandler handler) throws DocumentStreamException
  {
    super();
    this.outputStream = outputStream;
    this.errorHandler = handler;
    position = 0;
    size = 0;
  }

  public void write(byte[] b, int off, int len) throws DocumentStreamException
  {
    try
    {
      if (len > 0)
      {
        outputStream.write(b, off, len);
        position += len;
      }
    } catch (IOException e)
    {
      errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_IO,
          new LocationImpl(position)));
    }
    if (position > size)
    {
      size = position;
    }
  }
  
  public void write32Little(long value) throws DocumentStreamException
  {
    w[3] = (byte)(0xff & (value >> 24));
    w[2] = (byte)(0xff & (value >> 16));
    w[1] = (byte)(0xff & (value >> 8));
    w[0] = (byte)(0xff & value);
    write(w, 0, 4);
  }

  public void write32Big(long value) throws DocumentStreamException
  {
    w[0]   = (byte)(0xff & (value >> 24));
    w[1] = (byte)(0xff & (value >> 16));
    w[2] = (byte)(0xff & (value >> 8));
    w[3] = (byte)(0xff & value);
    write(w, 0, 4);
  }

  public void write16Little(int value) throws DocumentStreamException
  {
    w[0] = (byte)(value & 0xFF);
    w[1] = (byte)((value  >>> 8) & 0xFF);
    write(w, 0, 2);
  }

  public void write16Big(int value) throws DocumentStreamException
  {
    w[1] = (byte)(value & 0xFF);
    w[0] = (byte)((value  >>> 8) & 0xFF);
    write(w, 0, 2);
  }
  
  public void write8(int value) throws DocumentStreamException
  {
    w[0] = (byte) (value & 0xFF);
    write(w, 0, 1);
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
        outputStream.seek(off);
      } catch (IOException e)
      {
        errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_IO,
            new LocationImpl(position)));
      }
      this.position = off;
  }
  
  public long getSize()
  {
    return position;
  }
  
  public void flush() throws DocumentStreamException
  {
/*    try
    {
//      outputStream.flush();
    } catch (IOException e)
    {
      errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_IO,
          new LocationImpl(position)));
    }*/
  }
  
  public void write64Big(long value) throws DocumentStreamException 
  {
    w[0]   = (byte)(0xff & (value >> 56));
    w[1] = (byte)(0xff & (value >> 48));
    w[2] = (byte)(0xff & (value >> 40));
    w[3] = (byte)(0xff & (value >> 32));
    w[4] = (byte)(0xff & (value >> 24));
    w[5] = (byte)(0xff & (value >> 16));
    w[6] = (byte)(0xff & (value >> 8));
    w[7] = (byte)(0xff & value);
    write(w,0,8);
  }
  
  public void write64Little(long value)  throws DocumentStreamException 
  {
    w[7]   = (byte)(0xff & (value >> 56));
    w[6] = (byte)(0xff & (value >> 48));
    w[5] = (byte)(0xff & (value >> 40));
    w[4] = (byte)(0xff & (value >> 32));
    w[3] = (byte)(0xff & (value >> 24));
    w[2] = (byte)(0xff & (value >> 16));
    w[1] = (byte)(0xff & (value >> 8));
    w[0] = (byte)(0xff & value);
    write(w,0,8);
  }
  

  
}
