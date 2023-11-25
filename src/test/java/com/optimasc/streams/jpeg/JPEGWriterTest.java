package com.optimasc.streams.jpeg;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.optimasc.io.FileDataOutputStream;
import com.optimasc.stream.TestUtilities;
import com.optimasc.streams.DefaultStreamFilter;
import com.optimasc.streams.DocumentStreamException;

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
      OutputStream stream = new FileDataOutputStream("test.jpg");
      JPEGWriter writer = new JPEGWriter();
      writer.setOutput(stream, null);

      writer.writeStartDocument("");
      writer.writeStartElement(new Integer(JPEGUtilities.JPEG_ID_SOI), null);
      writer.writeEndElement();
      writer.writeStartElement(new Integer(JPEGUtilities.JPEG_ID_DATA), null);
      writer.writeByte(4);
      writer.writeByte(5);
      writer.writeByte(6);
      writer.writeByte(7);
      writer.writeByte(4);
      writer.writeByte(5);
      writer.writeByte(6);
      writer.writeByte(7);
      writer.writeEndElement();

      writer.writeStartElement(new Integer(JPEGUtilities.JPEG_ID_EOI), null);
      writer.writeEndElement();
      writer.writeEndDocument();
      writer.flush();
    } catch (FileNotFoundException e)
    {
      fail();
    } catch (IOException e)
    {
      fail(e.getMessage());
    }
  }

  public void testTwo()
  {
    JPEGReader reader;
    try
    {
      InputStream is = getClass().getResourceAsStream("/res/rembrant.jpg");
      reader = new JPEGReader();
      reader.setInput(is, null);
      OutputStream stream = new FileDataOutputStream("rembrant.jpg");
      JPEGWriter writer = new JPEGWriter();
      writer.setOutput(stream, null);
      TestUtilities.copy(reader, writer);
      stream.close();

      InputStream res1 = getClass().getResourceAsStream("/res/rembrant.jpg");
      InputStream res2 = new FileInputStream("rembrant.jpg");

      /* Valid if only both binaries are equal! */
      assertTrue(TestUtilities.binaryDiff(res1, res2));
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
