package com.optimasc.streams.tiff;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.optimasc.stream.TestUtilities;
import com.optimasc.streams.DefaultStreamFilter;
import com.optimasc.streams.DocumentStreamConstants;
import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.DocumentStreamReader;
import com.optimasc.streams.StreamFilter;

import junit.framework.TestCase;

public class TIFFReaderTest extends TestCase implements StreamFilter
{

  public TIFFReaderTest(String name)
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
  
  
  

  /** Test standard usage cases with a valid resource. 
   * 
   * @throws FileNotFoundException
   */
  public void testOne() throws FileNotFoundException
  {

    try
    {

      TIFFReader reader = new TIFFReader(getClass()
          .getResourceAsStream("/res/rembrant1.tif"), new DefaultStreamFilter());
      TestUtilities.parse(reader, null);
    } catch (DocumentStreamException e)
    {
      fail();
    }
  }
  
  
  

  public boolean accept(DocumentStreamReader reader)
  {
    if (reader.getEventType()==DocumentStreamConstants.START_GROUP)
    {
      // Skip only the movi group
      if (reader.getId().toString().equals("movi"))
      {
        return false;
      }
    }
    return true;
  }
  

}
