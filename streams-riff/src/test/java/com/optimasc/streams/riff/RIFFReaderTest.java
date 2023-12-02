package com.optimasc.streams.riff;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.optimasc.streams.ElementInfo;
import com.optimasc.streams.TestUtilities;
import com.optimasc.streams.DefaultStreamFilter;
import com.optimasc.streams.DocumentStreamConstants;
import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.DocumentStreamReader;
import com.optimasc.streams.StreamFilter;

import junit.framework.TestCase;

public class RIFFReaderTest extends TestCase implements StreamFilter
{

  public RIFFReaderTest(String name)
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
  
  
  public static final String sample1GroupNames[] =
  {
    // In ther order they are opened and closed
    "hdrl",
    "strl",
    "strl",
    "hdrl",
    "movi",
    "movi"
  };
  

  /** Main tree structure to parse **/
  public static final ElementInfo[] elements =
  {
    new ElementInfo("hdrl",-1,null),
      new ElementInfo("avih",-1,null),
      new ElementInfo("strl",-1,null),
        new ElementInfo("strh",-1,null,null),
        new ElementInfo("strf",-1,null,null),
        new ElementInfo("strn",-1,null,null),
    new ElementInfo("JUNK",0x6FC,null,null),
    new ElementInfo("movi",-1,null),
      new ElementInfo("00db",0x8BA8,null,null),
      new ElementInfo("00db",0x8BA8,null,null),
      new ElementInfo("00db",0x8BA8,null,null),
      new ElementInfo("00db",0x8BA8,null,null),
      new ElementInfo("00db",0x8BA8,null,null),
      new ElementInfo("00db",0x8BA8,null,null),
      new ElementInfo("00db",0x8BA8,null,null),
      new ElementInfo("00db",0x8BA8,null,null),
      new ElementInfo("00db",0x8BA8,null,null),
      new ElementInfo("00db",0x8BA8,null,null),
      new ElementInfo("00db",0x8BA8,null,null),
      new ElementInfo("00db",0x8BA8,null,null),
      new ElementInfo("00db",0x8BA8,null,null),
    new ElementInfo("idx1",0xD0,null,null)
  };
  
  
  
  public static final String sample1ChunkNames[] =
  {
    // No nesting - so open = close level
    "avih",
    "strh",
    "strf",
    "strn",
    "JUNK",
    "00db",
    "00db",
    "00db",
    "00db",
    "00db",
    "00db",
    "00db",
    "00db",
    "00db",
    "00db",
    "00db",
    "00db",
    "00db",
    "idx1"
  };
  
  
  
  
  public static final long sample1ChunkSizes[] =
  {
    0x38, // "avih",
    0x38, // "strh",
    0x28, // "strf",
    0x14, // "strn",
  };
  
  
  

  /** Test standard usage cases with a valid resource. 
   * 
   * @throws FileNotFoundException
   */
  public void testOne() throws FileNotFoundException
  {

    int groupNr = 0;
    int chunkNr = 0;
    byte[] buffer = new byte[1024*1024];
    try
    {
      InputStream is = getClass().getResourceAsStream("/res/sample1.avi");
      RIFFReader reader = new RIFFReader();
      reader.setInput(is, null);
      TestUtilities.parse(reader,elements);
    } catch (DocumentStreamException e)
    {
      fail();
    }
  }
  
  
  public static final String sample1SkipChunkNames[] =
  {
    // No nesting - so open = close level
    "avih",
    "strh",
    "strf",
    "strn",
    "JUNK",
    "idx1"
  };
  
  public static final long sample1SkipChunkSizes[] =
  {
    // No nesting - so open = close level
    0x38, // "avih",
    0x38, // "strh",
    0x28, // "strf",
    0x14, // "strn",
    0x6FC, // "JUNK",
    0xD0   //"idx1"
  };
  
  
  /** Test standard skip cases with a valid resource. 
   * 
   * @throws FileNotFoundException
   */
  
  public void testTwo() throws FileNotFoundException
  {

    int groupNr = 0;
    int chunkNr = 0;
    try
    {
      InputStream is = getClass().getResourceAsStream("/res/sample1.avi");
      RIFFReader reader = new RIFFReader();
      reader.setInput(is, null);
      reader.setFilter(this);
      
      while (reader.hasNext())
      {
        int id = reader.next();
        switch (id)
        {
        case DocumentStreamConstants.START_DOCUMENT:
          System.out.println("begin.");
          break;
        case DocumentStreamConstants.START_GROUP:
          System.out.println("<" + reader.getId().toString() + ">");
          assertEquals(sample1GroupNames[groupNr],reader.getId().toString());
          groupNr++;
          break;
        case DocumentStreamConstants.END_GROUP:
          assertEquals(sample1GroupNames[groupNr],reader.getId().toString());
          groupNr++;
          System.out.println("</" + reader.getId().toString() + ">");
          break;
        case DocumentStreamConstants.START_ELEMENT:
          assertEquals(sample1SkipChunkNames[chunkNr],reader.getId().toString());
          System.out.print("<" + reader.getId().toString() + ">");
          break;
        case DocumentStreamConstants.DATA:
          assertEquals(sample1SkipChunkSizes[chunkNr],reader.getDataSize());
          break;
        case DocumentStreamConstants.END_ELEMENT:
          assertEquals(sample1SkipChunkNames[chunkNr],reader.getId().toString());
          chunkNr++;
          System.out.println("</" + reader.getId().toString() + ">");
          break;
        case DocumentStreamConstants.END_DOCUMENT:
          System.out.println("end.");
          break;
        }
      }
    } catch (DocumentStreamException e)
    {
      fail();
    }
    catch (IOException e)
    {
      fail();
    }    

  }
  
  /** Test cases with malformed file. 
   * 
   * @throws FileNotFoundException
   */
  public void testThree() throws FileNotFoundException
  {

    int groupNr = 0;
    int chunkNr = 0;
    try
    {
      InputStream is = getClass()
          .getResourceAsStream("/res/dummy1.rif");
      RIFFReader reader = new RIFFReader();
      reader.setInput(is, null);
      reader.setFilter(this);
      
      while (reader.hasNext())
      {
        int id = reader.next();
        switch (id)
        {
        case DocumentStreamConstants.START_DOCUMENT:
          System.out.println("begin.");
          break;
        }
      }
    } catch (DocumentStreamException e)
    {
      // We should get an exception code here - since the format is invalid.
      return;
    }
    catch (IOException e)
    {
      // We should get an exception code here - since the format is invalid.
      return;
    }    
    fail();
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
