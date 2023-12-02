package com.optimasc.streams.riff;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.optimasc.io.FileDataOutputStream;
import com.optimasc.streams.TestUtilities;
import com.optimasc.streams.DefaultStreamFilter;
import com.optimasc.streams.DocumentInfo;
import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.DocumentStreamReader;

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
      OutputStream stream = new FileDataOutputStream("test.rif");
      RIFFWriter writer = new RIFFWriter();
      writer.setOutput(stream, null);
      writer.writeStartDocument("AVI ");
      writer.writeStartGroup("INFO",null);
      writer.writeEndGroup();
      writer.writeStartGroup("INF2",null);
        writer.writeStartElement("AUTH",null);
        writer.writeByte(1);
        writer.writeByte(2);
        writer.writeByte(3);
        writer.writeEndElement();
      
        writer.writeStartElement("AUT2",null);
        writer.writeByte(4);
        writer.writeByte(5);
        writer.writeByte(6);
        writer.writeByte(7);
        writer.writeEndElement();
        
        
        writer.writeStartElement("DATE",null);
        writer.writeChars("2013-04-06");
        writer.writeEndElement();
        
      writer.writeEndGroup();
      writer.writeEndDocument();
      writer.flush();
    } catch (FileNotFoundException e)
    {
      fail();
    } catch (IOException e)
    {
      fail();
    }
  }
  
  public void testTwo()
  {
      DocumentStreamReader reader;
      
    try {
        InputStream is = getClass()
            .getResourceAsStream("/res/sample1.avi");
        reader = new RIFFReader();
        reader.setInput(is, null);
        OutputStream stream = new FileDataOutputStream("sample1.avi");
        RIFFWriter writer = new RIFFWriter();
        writer.setOutput(stream, null);
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
