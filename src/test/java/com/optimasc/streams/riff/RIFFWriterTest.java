package com.optimasc.streams.riff;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.vfs2.RandomAccessContent;

import com.optimasc.stream.TestUtilities;
import com.optimasc.streams.DefaultStreamFilter;
import com.optimasc.streams.DocumentInfo;
import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.DocumentStreamReader;
import com.optimasc.streams.FileSeekableOutputStream;
import com.optimasc.streams.jpeg.JPEGReader;
import com.optimasc.streams.jpeg.JPEGWriter;

import junit.framework.TestCase;

public class RIFFWriterTest extends TestCase
{

  public RIFFWriterTest(String name)
  {
    super(name);
  }

  protected void setUp() throws Exception
  {
    super.setUp();
  }

  protected void tearDown() throws Exception
  {
    super.tearDown();
  }
  
  public void testOne()
  {
    try
    {
      RandomAccessContent stream = new FileSeekableOutputStream("test.rif");
      RIFFWriter writer = new RIFFWriter(stream,false);
      writer.writeStartDocument("AVI ");
      writer.writeStartGroup("INFO",null);
      writer.writeEndGroup();
      writer.writeStartGroup("INF2",null);
        writer.writeStartElement("AUTH",null);
        writer.writeOctet(1);
        writer.writeOctet(2);
        writer.writeOctet(3);
        writer.writeEndElement();
      
        writer.writeStartElement("AUT2",null);
        writer.writeOctet(4);
        writer.writeOctet(5);
        writer.writeOctet(6);
        writer.writeOctet(7);
        writer.writeEndElement();
        
        
        writer.writeStartElement("DATE",null);
        writer.writeCharacters("2013-04-06");
        writer.writeEndElement();
        
      writer.writeEndGroup();
      writer.writeEndDocument();
      writer.flush();
    } catch (FileNotFoundException e)
    {
      fail();
    } catch (DocumentStreamException e)
    {
      fail();
    }
  }
  
  public void testTwo()
  {
      DocumentStreamReader reader;
      
    try {
        reader = new RIFFReader(getClass()
                  .getResourceAsStream("/res/sample1.avi"),new DefaultStreamFilter());
        RandomAccessContent stream = new FileSeekableOutputStream("sample1.avi");
        RIFFWriter writer = new RIFFWriter(stream,reader.getDocumentInfo().getStreamType()==DocumentInfo.TYPE_BIG_ENDIAN);
        TestUtilities.copy(reader, writer);
        stream.close();
        
        InputStream res1 = getClass().getResourceAsStream("/res/sample1.avi");
        InputStream res2 = new FileInputStream("sample1.avi");
        
        /* Valid if only both binaries are equal! */
        //assertTrue(TestUtilities.binaryDiff(res1,res2));
    } catch (FileNotFoundException e)
    {
      fail();
        
    } catch (DocumentStreamException e) {
        fail();
    } catch (IOException e) {
        fail();

    }

      
  }

}
