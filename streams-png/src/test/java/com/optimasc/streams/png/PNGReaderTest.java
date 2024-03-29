package com.optimasc.streams.png;

import java.io.FileNotFoundException;
import java.io.InputStream;

import com.optimasc.streams.ElementInfo;
import com.optimasc.streams.TestUtilities;
import com.optimasc.streams.DefaultStreamFilter;
import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.DocumentStreamReader;

import junit.framework.TestCase;

public class PNGReaderTest extends TestCase
{

  protected void setUp() throws Exception
  {
    super.setUp();
  }

  protected void tearDown() throws Exception
  {
    super.tearDown();
  }
  
  
  /** Main tree structure to parse **/
  public static final ElementInfo[] elements =
  {
    new ElementInfo("IHDR",13,null),
    new ElementInfo("tIME",7,null),
    new ElementInfo("pHYs",9,null,null),
    new ElementInfo("gAMA",4,null),
    new ElementInfo("IDAT",478106,null),
    new ElementInfo("IEND",0,null),
  };
  
  
  /** Test standard usage cases with a valid resource. 
   * 
   * @throws FileNotFoundException
   */
  public void testOne() throws FileNotFoundException
  {

    try
    {
      InputStream is = getClass().getResourceAsStream("/res/rembrant.png");
      DocumentStreamReader reader = new PNGReader();
      reader.setInput(is, null);
      TestUtilities.parse(reader,elements);
    } catch (DocumentStreamException e)
    {
      fail();
    }
  }


}
