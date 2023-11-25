package com.optimasc.streams.jpeg;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.optimasc.stream.TestUtilities;
import com.optimasc.streams.DefaultStreamFilter;
import com.optimasc.streams.DocumentStreamConstants;
import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.jpeg.JPEGReader;

import junit.framework.TestCase;

public class JPEGReaderTest extends TestCase
{

  public JPEGReaderTest(String name)
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

  public void testOne() throws FileNotFoundException
  {
    try
    {
      InputStream is = getClass().getResourceAsStream("/res/rembrant.jpg");
      JPEGReader reader = new JPEGReader();
      reader.setInput(is, null);
      TestUtilities.parse(reader, null);
    } catch (DocumentStreamException e)
    {
      fail();
    }
  }
  
  public void testTwo() throws FileNotFoundException
  {
    boolean exceptionThrown = false;
    try
    {
      /* This JPEG File contains a JPGn marker in which the format is unknown, so
       * it should fail.
       */
      InputStream is = getClass()
          .getResourceAsStream("/res/corrupt.jpg");
      JPEGReader reader = new JPEGReader();
      reader.setInput(is, null);
      TestUtilities.parse(reader, null);
    } catch (DocumentStreamException e)
    {
      exceptionThrown =true;
    }
    if (exceptionThrown ==false)
    {
      fail();
    }
  }
  

  public void testThree() throws FileNotFoundException
  {
    boolean exceptionThrown = false;
    try
    {
      InputStream is = getClass()
          .getResourceAsStream("/res/wizard.jpg");
      JPEGReader reader = new JPEGReader();
      reader.setInput(is, null);
      TestUtilities.parse(reader, null);
    } catch (DocumentStreamException e)
    {
      exceptionThrown = true;
    }
    if (exceptionThrown ==false)
    {
      fail();
    }
  }
  
}
