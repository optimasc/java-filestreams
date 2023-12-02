package com.optimasc.streams.png;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.optimasc.io.FileDataOutputStream;
import com.optimasc.streams.TestUtilities;
import com.optimasc.streams.DefaultStreamFilter;
import com.optimasc.streams.DocumentInfo;
import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.DocumentStreamReader;
import com.optimasc.streams.DocumentStreamWriter;

import junit.framework.TestCase;

public class PNGWriterTest extends TestCase
{

  protected void setUp() throws Exception
  {
    super.setUp();
  }

  protected void tearDown() throws Exception
  {
    super.tearDown();
  }
  
  public void testTwo()
  {
      DocumentStreamReader reader;
      
    try {
        InputStream is = this.getClass().getResourceAsStream("/res/rembrant.png");
        reader = new PNGReader();
        reader.setInput(is, null);
        FileDataOutputStream stream = new FileDataOutputStream("rembrant.png");
        DocumentStreamWriter writer = new PNGWriter();
        writer.setOutput(stream, null);
        TestUtilities.copy(reader, writer);
        stream.close();
        
        InputStream res1 = getClass().getResourceAsStream("/res/rembrant.png");
        InputStream res2 = new FileInputStream("rembrant.png");
        
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
