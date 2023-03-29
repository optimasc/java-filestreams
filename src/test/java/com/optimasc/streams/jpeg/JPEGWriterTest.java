package com.optimasc.streams.jpeg;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.vfs2.RandomAccessContent;

import com.optimasc.stream.TestUtilities;
import com.optimasc.streams.DefaultStreamFilter;
import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.FileSeekableOutputStream;

import junit.framework.TestCase;

public class JPEGWriterTest extends TestCase
{

  public JPEGWriterTest(String name)
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
      RandomAccessContent stream = new FileSeekableOutputStream("test.jpg");
      JPEGWriter writer = new JPEGWriter(stream);
      
      writer.writeStartDocument("");
        writer.writeStartElement(new Integer(JPEGUtilities.JPEG_ID_SOI),null);
        writer.writeEndElement();
        writer.writeStartElement(new Integer(JPEGUtilities.JPEG_ID_DATA),null);
        writer.writeOctet(4);
        writer.writeOctet(5);
        writer.writeOctet(6);
        writer.writeOctet(7);
        writer.writeOctet(4);
        writer.writeOctet(5);
        writer.writeOctet(6);
        writer.writeOctet(7);
        writer.writeEndElement();
        
        
        writer.writeStartElement(new Integer(JPEGUtilities.JPEG_ID_EOI),null);
        writer.writeEndElement();
      writer.writeEndDocument();
      writer.flush();
    } catch (FileNotFoundException e)
    {
      fail();
    } catch (DocumentStreamException e)
    {
      fail(e.getMessage());
    }
  }
  
  public void testTwo()
  {
      JPEGReader reader;
	try {
		reader = new JPEGReader(getClass()
		          .getResourceAsStream("/res/rembrant.jpg"),new DefaultStreamFilter());
	    RandomAccessContent stream = new FileSeekableOutputStream("rembrant.jpg","rw");
	    JPEGWriter writer = new JPEGWriter(stream);
	    TestUtilities.copy(reader, writer);
	    stream.close();
	    
	    InputStream res1 = getClass().getResourceAsStream("/res/rembrant.jpg");
        InputStream res2 = new FileInputStream("rembrant.jpg");
	    
        /* Valid if only both binaries are equal! */
	    assertTrue(TestUtilities.binaryDiff(res1,res2));
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
