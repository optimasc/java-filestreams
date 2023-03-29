package com.optimasc.streams.zip;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.vfs2.RandomAccessContent;

import com.optimasc.stream.TestUtilities;
import com.optimasc.streams.DefaultStreamFilter;
import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.FileSeekableOutputStream;
import com.optimasc.zip.ZIPReaderImpl;
import com.optimasc.zip.ZIPWriterImpl;

import junit.framework.TestCase;

public class ZIPWriterTest extends TestCase
{

  protected void setUp() throws Exception
  {
    super.setUp();
  }

  protected void tearDown() throws Exception
  {
    super.tearDown();
  }

  public void test01()
  {
    try {
        int v;
        InputStream reader = getClass().getResourceAsStream("/res/sample.vcf");
        RandomAccessContent stream = new FileSeekableOutputStream("sample.zip");
        ZIPWriterImpl writer = new ZIPWriterImpl(stream);
        writer.writeStartDocument("");
        writer.writeStartElement("sample.vcf", null);
        while ((v = reader.read()) != -1)
        {
          writer.writeOctet(v);
        }
        writer.writeEndElement();
        writer.writeEndDocument();
        stream.close();
        reader.close();
    } catch (FileNotFoundException e)
    {
      fail();
        
    } catch (DocumentStreamException e) 
    {
        fail();
    } catch (IOException e) 
    {
      fail();
    }
  }
}
