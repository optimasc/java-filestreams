package com.optimasc.streams;


/*
 * 
 * Copyright (c) 2004 Optima SC Inc. All rights reserved.
 * 
 * Redistribution and use in source and binary forms,
 * with or without modification in commercial and
 * non-commercial packages/software, are permitted
 * provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the
 * following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the
 * above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the
 * redistribution, if any, must include the following
 * acknowledgment:
 * 
 * "This product includes software developed by
 * Carl Eric Codere of Optima SC Inc."
 * 
 * Alternately, this acknowledgment may appear in the
 * software itself, if and wherever such third-party
 * acknowledgments normally appear.
 * 
 * 4. The names "Optima SC Inc." and "Carl Eric Codere" must
 * not be used to endorse or promote products derived from
 * this software without prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * OPTIMA SC INC. OR ITS CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Implements a @see(java.io.FileInputStream) which supports the mark and reset
 * methods which permits some kind of seeking in File Input streams. The class
 * uses @see(java.io.RandomAccessFile) to access the actual file.
 * 
 * @author carl eric codere
 */
public class FileMarkableInputStream extends InputStream
{

  /** The random access file */
  private RandomAccessFile file;

  /** Contains the last mark position */
  private long markPosition;

  /**
   * Creates a FileInputStream by opening a connection to an actual file, the
   * file named by the path name name in the file system. A new RandomAccessFile
   * object is created to represent this file connection. The file is opened in
   * read-only mode.
   * 
   * @param filename
   *          the system-dependent file name.
   * @throws IOException
   * @throws FileNotFoundException
   */
  public FileMarkableInputStream(String filename) throws IOException,
      FileNotFoundException
  {
    file = new RandomAccessFile(filename, "r");
    file.seek(0);
    // Invalid mark position
    markPosition = -1;
  }

  public int read(byte[] b) throws IOException
  {
    return file.read(b);
  }

  public int read(byte[] b, int off, int len) throws IOException
  {
    return file.read(b, off, len);
  }

  public int read() throws IOException
  {
    return file.read();
  }

  /**
   * Returns the number of bytes that can be read (or skipped over) from this
   * input stream without blocking by the next caller of a method for this input
   * stream.
   * 
   * If the length of the stream cannot fit into an integer datatype then, a
   * value of Integer.MAX_LENGTH is returned.
   * 
   * This permits to loop on this value, e.g as long as the value is MAX_LENGTH
   * you can read data and skip it.
   * 
   **/
  public int available() throws IOException
  {
    // Return -1 if the stream is longer than 2 Gbytes.
    if ((file.length() - file.getFilePointer()) > Integer.MAX_VALUE)
      return Integer.MAX_VALUE;
    return (int) (file.length() - file.getFilePointer());
  }

  public void close() throws IOException
  {
    file.close();
    file = null;
    super.close();
  }

  /**
   * Marks the current position in this input stream. A subsequent call to the
   * reset method repositions this stream at the last marked position so that
   * subsequent reads re-read the same bytes.
   * 
   * The readlimit argument is ignored for this implementation
   * 
   * @param readlimit
   *          Ignored in this implementation
   */
  public synchronized void mark(int readlimit)
  {
    try
    {
      markPosition = file.getFilePointer();
    } catch (IOException e)
    {
      // Do nothing
    }
  }

  public boolean markSupported()
  {
    return true;
  }

  public synchronized void reset() throws IOException
  {
    // The value will be -1 if mark has never ben called before.
    file.seek(markPosition);
  }

  public long skip(long n) throws IOException
  {
    file.seek(file.getFilePointer() + n);
    return n;
  }

  protected void finalize() throws Throwable
  {
    if (file != null)
      file.close();
//    super.finalize();
  }

  public long size() throws IOException
  {
    return file.length();
  }
}
