package com.optimasc.streams.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;
import java.util.Vector;

import com.optimasc.streams.DefaultStreamFilter;
import com.optimasc.streams.DocumentInfo;
import com.optimasc.streams.DocumentStreamConstants;
import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.DocumentStreamReader;
import com.optimasc.streams.ErrorHandler;
import com.optimasc.streams.IllegalStateException;
import com.optimasc.streams.Location;
import com.optimasc.streams.StreamFilter;
import com.optimasc.streams.Attribute;

/**
 * Class that implements a basic generic parser for chunk based streams. This
 * API is loosely based on the STAX API.
 * 
 * @author Carl Eric Codere
 * 
 */
public abstract class AbstractDocumentReader implements DocumentStreamReader,
    ErrorHandler
{

  public static final int RADIX_HEX = 16;

  /** Maximum allowed nesting level */
  protected int maxNestingLevel = 0;

  /** The current state of the state machine */
  protected int currentState;
  /** The next state that should be returned */
  protected int nextState;

  protected ChunkInfo currentChunk;
  protected ChunkInfo nextChunk;
  /** Contains information on elements that should be skipped */
  protected StreamFilter filter;

  /** In the DATA chunk indicates the number of bytes left to read */
  protected long dataSizeLeft;

  /** Contains information on nesting of chunks */
  protected Stack nestingInfo;

  /** Data reader implementation based on inputStream */
  protected DataReader reader;
  /** Document information */
  protected DocumentInfo document;
  /** Current error handler registered - default one fails on fatal errors */
  protected ErrorHandler errorHandler;

  /**
   * Creates this instance of a linear parser that accepts nesting levels up to
   * the specified level.
   *
   * @param maxNesting Maximum nesting level allowed for this structured format.
   * @param inputStream The inputstream to read from
   * @param filter
   * @throws DocumentStreamException
   */
  public AbstractDocumentReader(int maxNesting, InputStream inputStream, StreamFilter filter)
      throws DocumentStreamException
  {
    //   nestingInfoRoot = new IElement[maxNesting+1];
    this.maxNestingLevel = maxNesting;
    reader = new DataReader(inputStream, this);
    nestingInfo = new Stack();
    nextState = DocumentStreamConstants.START_DOCUMENT;
    currentState = DocumentStreamConstants.START_DOCUMENT;
    currentChunk = null;
    nextChunk = newChunkInfo();
    if (filter==null)
    {
      this.filter = new DefaultStreamFilter();
    } else
    {
      this.filter = filter; 
    }
    errorHandler = this;
  }

  /**
   * This method should read the next chunk and populate the ChunkInfo value
   * accordingly. Upon exiting the stream position should be set to the actual
   * data of the chunk or the next group if this was a group chunk.
   * 
   * @param dataReader
   *          The reader api containing the stream to read from
   * @param header
   *          The actual structure containing information on the chunk that
   *          should be filled in.
   */
  protected abstract void readChunkHeader(DataReader dataReader,
      ChunkInfo header) throws DocumentStreamException;

  /**
   * This method should read the signature of the document, validate if the
   * header is valid and return information on the document if it is valid, it
   * should then point the document to the first chunk of the document.
   * 
   * It can either be called directly by itself or can be called by the next()
   * method.
   * 
   * @param dataReader
   *          The reader API containing the stream to read from
   * @return null if this document is not of this type, otherwise information on
   *         this document
   * @throws DocumentStreamException
   */
  protected abstract DocumentInfo readDocumentHeader(DataReader dataReader)
      throws DocumentStreamException;

  public Object getProperty(String name) throws IllegalArgumentException
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  /** This routine should return an instance of a Chunk, it can be overriden
   *  to return more specialized chunk types.
   * 
   * @return Allocated ChunkInfo structure
   */
  protected ChunkInfo newChunkInfo()
  {
    return new ChunkInfo();
  }

  /**
   * Possible states
   * 
   * START_DOCUMENT -> START_GROUP |START_ELEMENT
   * 
   * START_GROUP -> START_ELEMENT |START_GROUP | END_GROUP
   * 
   * START_ELEMENT -> DATA
   * 
   * DATA -> END_ELEMENT
   * 
   * END_GROUP -> START_GROUP |START_ELEMENT |END_DOCUMENT
   * 
   * END_ELEMENT -> START_GROUP |START_ELEMENT | END_DOCUMENT
   * 
   */
  public int next() throws DocumentStreamException
  {
    // Go to next state
    currentState = nextState;

    switch (currentState)
    {
    case DocumentStreamConstants.START_DOCUMENT:
      // If the document information already has been read, do not re-read it.
      if (document == null)
      {
      document = readDocumentHeader(reader);
      if (document == null)
      {
        errorHandler.fatalError(new DocumentStreamException(
            DocumentStreamException.ERR_INVALID_STREAM));
      }
      }
      readChunkHeader(reader, nextChunk);
      if (nextChunk.type == ChunkInfo.TYPE_CHUNK)
      {
        nextState = DocumentStreamConstants.START_ELEMENT;
      } else
      {
        nextState = DocumentStreamConstants.START_GROUP;
      }
      break;
    case DocumentStreamConstants.DATA:
      dataSizeLeft = currentChunk.size;
      if (filter.accept(this) == false)
      {
        skipData(currentChunk.size+currentChunk.extraSize);
      }
      nextState = DocumentStreamConstants.END_ELEMENT;
      break;

    // Verify if there is more data or not.  
    case DocumentStreamConstants.END_GROUP:
      //----- Verify if one or more groups have not been finished
      if ((maxNestingLevel > 0) && (nestingInfo.empty() == false))
      {
        // verify if we closed a group
        do
        {
          ChunkInfo info = (ChunkInfo) nestingInfo.peek();
          if ((info != null) && (isGroupEnd(currentChunk, info)))
          {
            // Now we should point to the end group
            currentChunk = (ChunkInfo) nestingInfo.pop();
            currentState = DocumentStreamConstants.END_GROUP;
            nextState = DocumentStreamConstants.END_GROUP;
            return currentState;
          } else
          {
            break;
          }
        } while (false);
      }
      // Check if end of document
      if ((document != null) && (isDocumentEnd(currentChunk)))
      {
        nextState = DocumentStreamConstants.END_DOCUMENT;
        currentState = nextState;
        verifyEndOfDocument();
        return nextState;
      }
      readChunkHeader(reader, nextChunk);
      if (nextChunk.type == ChunkInfo.TYPE_CHUNK)
      {
        nextState = DocumentStreamConstants.START_ELEMENT;
        currentState = nextState;
        processStartElement();
      } else
      {
        nextState = DocumentStreamConstants.START_GROUP;
        currentState = nextState;
        processStartGroup();
      }
      break;
    case DocumentStreamConstants.START_GROUP:
      processStartGroup();
      break;
    case DocumentStreamConstants.END_ELEMENT:
      // Skip all the data that we did not read.
      skipData(dataSizeLeft+currentChunk.extraSize);
      // Hack: next state is POSSIBLY an END group
      nextState = DocumentStreamConstants.END_GROUP;
      break;
    case DocumentStreamConstants.START_ELEMENT:
      processStartElement();
      break;
    case DocumentStreamConstants.END_DOCUMENT:
      break;
    }
    return currentState;
  }

  private void processStartElement() throws DocumentStreamException
  {
    currentChunk = (ChunkInfo) nextChunk.clone();
    if (filter.accept(this))
    {
      nextState = DocumentStreamConstants.DATA;
      /* This is a normal chunk */
      if (currentChunk.type == ChunkInfo.TYPE_CHUNK)
      {
        nextState = DocumentStreamConstants.DATA;
        // The number of bytes to skip normally if the value is accepted.
      }
    } else
    {
      nextState = DocumentStreamConstants.END_ELEMENT;
      skipData(currentChunk.size+currentChunk.extraSize);
    }
  }

  private void processStartGroup() throws DocumentStreamException
  {
    currentChunk = (ChunkInfo) nextChunk.clone();
    nestingInfo.push(currentChunk);
    if (filter.accept(this))
    {
      readChunkHeader(reader, nextChunk);
      if (nextChunk.type == ChunkInfo.TYPE_CHUNK)
      {
        nextState = DocumentStreamConstants.START_ELEMENT;
      } else
      {
        nextState = DocumentStreamConstants.START_GROUP;
      }
    } else
    {
      nextState = DocumentStreamConstants.END_GROUP;
      skipData(currentChunk.size+currentChunk.extraSize);
    }
  }

  public Object getId()
  {
    return currentChunk.id;
  }

  public boolean hasNext() throws DocumentStreamException
  {
    if (currentState != DocumentStreamConstants.END_DOCUMENT)
      return true;
    return false;
  }

  public void close() throws DocumentStreamException
  {
    // TODO Auto-generated method stub

  }

  public String getAttributeValue(String namespaceURI, String localName) throws IllegalStateException
  {
    Vector v;
    Attribute attr;
    int i;
    // Return the value given by the current chunk - only if this is a START_ELEMEMT or START_GROUP section
    if (((currentState == DocumentStreamConstants.START_GROUP) || (currentState == DocumentStreamConstants.DATA) || (currentState == DocumentStreamConstants.START_ELEMENT))==false)
      throw new IllegalStateException("Invalid state.");
    
    v = currentChunk.getAttributes();
    
    for (i = 0; i < v.size(); i++)
    {
      attr = (Attribute)v.elementAt(i);
      if (namespaceURI != null)
      {
        if (namespaceURI.equals(attr.getNamespaceURI()))
        {
          if (localName.equals(attr.getLocalName()))
            return attr.getValue();
        }
      } else
      {
        if (localName.equals(attr.getLocalName()))
          return attr.getValue();
      }
    }
    return null;
  }

  public int getAttributeCount() throws IllegalStateException
  {
    // Return the value given by the current chunk - only if this is a START_ELEMEMT or START_GROUP section
    if (((currentState == DocumentStreamConstants.START_GROUP) || (currentState == DocumentStreamConstants.DATA) || (currentState == DocumentStreamConstants.START_ELEMENT))==false)
      throw new IllegalStateException("Invalid state.");
    return currentChunk.getAttributes().size();
  }

  public String getAttributeValue(int index) throws IllegalStateException
  {
    Attribute attr;
    // Return the value given by the current chunk - only if this is a START_ELEMEMT or START_GROUP section
    if (((currentState == DocumentStreamConstants.START_GROUP) || (currentState == DocumentStreamConstants.DATA) || (currentState == DocumentStreamConstants.START_ELEMENT))==false)
      throw new IllegalStateException("Invalid state.");
    attr = (Attribute)currentChunk.getAttributes().elementAt(index);
    if (attr != null)
    {
      return attr.getValue();
    }
    return null;
  }

  public int getEventType()
  {
    return currentState;
  }
  
  public int getData(byte[] target, int targetStart, int length)
      throws DocumentStreamException
  {
    // Return the value given by the current chunk - only if this is a DATA section
    if (currentState != DocumentStreamConstants.DATA)
      errorHandler.fatalError(new DocumentStreamException(
          "Trying to read non-data values."));

    // Verify if trying to read past end of stream
    if ((length) > (currentChunk.size))
    {
      errorHandler.error(new DocumentStreamException(
          "Trying to read beyond end of element."));
    }

    // Verify if trying to read back - we can only read forward.
/*    if ((currentChunk.offset) < (reader.position))
    {
      errorHandler.error(new DocumentStreamException(
          "Trying to data that has already been read."));

    }*/

    read(target, targetStart, length);
    dataSizeLeft = dataSizeLeft - length;
    return length;
  }

  public long getDataSize() throws DocumentStreamException
  {
    // Return the value given by the current chunk - only if this is a DATA section
    if (currentState != DocumentStreamConstants.DATA)
      errorHandler.error(new DocumentStreamException(""));
    return currentChunk.size;
  }

  public String getEncoding()
  {
    return null;
  }

  public Location getLocation()
  {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * This method verifies if the stream has reached the end of the document, it
   * bases itself on the document, and also checks if some groups have not been
   * closed.
   * 
   * @throws DocumentStreamException
   * 
   */
  protected void verifyEndOfDocument() throws DocumentStreamException
  {
    if (nestingInfo.empty() == false)
      errorHandler.warning(new DocumentStreamException(
          DocumentStreamException.ERR_INVALID_NESTING));

    if ((reader.position > reader.size))
      errorHandler.warning(new DocumentStreamException(
          DocumentStreamException.ERR_EXTRA_DATA));
  }

  public void setErrorHandler(ErrorHandler handler)
  {
    this.errorHandler = handler;
  }

  public ErrorHandler getErrorHandler()
  {
    return this.errorHandler;
  }

  public void warning(DocumentStreamException exception)
      throws DocumentStreamException
  {
    return;
  }

  public void error(DocumentStreamException exception)
      throws DocumentStreamException
  {
    return;
  }

  public void fatalError(DocumentStreamException exception)
      throws DocumentStreamException
  {
    throw exception;
  }
  
  
  
  public String getAttributeNamespace(int index) throws IllegalStateException
  {
    // Return the value given by the current chunk - only if this is a START_ELEMEMT or START_GROUP section
    if (((currentState == DocumentStreamConstants.START_GROUP) || (currentState == DocumentStreamConstants.DATA) || (currentState == DocumentStreamConstants.START_ELEMENT))==false)
      throw new IllegalStateException("Invalid state.");
    Attribute attr;
    attr = (Attribute)currentChunk.toAttributes().elementAt(index);
    if (attr != null)
    {
      return attr.getNamespaceURI();
    }
    return null;
  }

  public String getAttributeLocalName(int index) throws IllegalStateException
  {
    Attribute attr;
    // Return the value given by the current chunk - only if this is a START_ELEMEMT or START_GROUP section
    if (((currentState == DocumentStreamConstants.START_GROUP) || (currentState == DocumentStreamConstants.DATA) || (currentState == DocumentStreamConstants.START_ELEMENT))==false)
      throw new IllegalStateException("Invalid state.");
    attr = (Attribute)currentChunk.toAttributes().elementAt(index);
    if (attr != null)
    {
      return attr.getLocalName();
    }
    return null;
  }

  //----------------------------------- Needs to be overriden for special formats -------------------------------------
  protected void skipData(long size) throws DocumentStreamException
  {
    reader.skip(size);
  }
  
  protected void read(byte[] target, int targetOffset, int length) throws DocumentStreamException
  {
    reader.read(target, targetOffset, length);
  }
  
  /** Checks if the group is ended or not.
   * 
   * @param currentChunk The current / last chunk processed
   * @param info The group chunk on the stack
   * @return true if the group is ended otherwise false.
   */
  protected boolean isGroupEnd(ChunkInfo current, ChunkInfo info) throws DocumentStreamException
  {
    if (reader.position >= (info.offset + info.size))
      return true;
    return false;
  }
  
  protected boolean isDocumentEnd(ChunkInfo current) throws DocumentStreamException
  {
    if (reader.position >= document.getSize())
      return true;
    return false;
  }
  
  public DocumentInfo getDocumentInfo() throws DocumentStreamException
  {
    if (document == null)
    {
      document = readDocumentHeader(reader);
    }
    return document;
  }

  public Attribute getAttribute(int index) throws IllegalStateException
  {
    return (Attribute)currentChunk.getAttributes().elementAt(index);
    
  }
  
}
