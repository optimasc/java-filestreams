package com.optimasc.streams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import org.apache.commons.vfs2.RandomAccessContent;

public class FileSeekableOutputStream extends RandomAccessFile implements RandomAccessContent
{

  public FileSeekableOutputStream(File file, String mode) throws FileNotFoundException
  {
    super(file, mode);
  }
  
  public FileSeekableOutputStream(File file) throws FileNotFoundException
  {
    super(file, "rw");
  }
  
  
  public FileSeekableOutputStream(String name, String mode) throws FileNotFoundException
  {
    super(name, mode);
  }
  
  public FileSeekableOutputStream(String name) throws FileNotFoundException
  {
    super(name, "rw");
  }
  

  public InputStream getInputStream() throws IOException
  {
    return null;
  }


}
