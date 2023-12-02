/*
 * 
 * See License.txt for more information on the licensing terms
 * for this source code.
 * 
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * OPTIMA SC INC. OR ITS CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.optimasc.streams.internal;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.optimasc.streams.Attribute;

/**
 * Container to store information on a header of a chunk or group.
 * 
 * @author Carl Eric Codere
 */
public class ChunkInfo
{
  /**
   * This chunk header is actually a container for other chunks.
   */
  public static final int TYPE_GROUP = 0;

  /**
   * This chunk header is actually a leaf node that does not contain any other
   * data.
   */
  public static final int TYPE_CHUNK = 1;

  /** This indicates an undefined value */
  public static final int UNDEFINED_VALUE = -1;

  /**
   * This is the type of chunk this is. or UNDEFINED_VALUE if not known.
   */
  public int type;

  /**
   * This is the position of the start of this chunk, or UNDEFINED_VALUE if not
   * known. The value here should point to the actual data offset from that
   * start of the stream to the data chunk.
   */
  public long offset;

  /**
   * This is the size of the DATA in this chunk, excluding any chunk header.
   * UNDEFINED_VALUE if this is not known.
   */
  public long size;

  /**
   * This is the extra size of the data to point to the next chunk, on top of
   * the user data set in size. This is the data at the end of the chunk that
   * should be skipped it is usually zero, but in certain cases for example
   * padding it can be more than zero.
   * 
   */
  public long extraSize;

  /**
   * This contains the actual identifier for this chunk.
   */
  public Object id;

  /**
   * A table of attributes associated with this chunk.
   * 
   */
  protected Vector attributes;
  

  /**
   *  An internal object that can be used by the parser / writer objects
   *  for this chunk.
   * 
   */
  public Object internalObject;

  public ChunkInfo()
  {
    super();
    attributes = new Vector();
    reset();
  }

  /** Public constructor with initializers used mainly for validation
   *  and unit testing.
   *   
   * @param id The id of this value.
   * @param size The size of this group or chunk.
   * @param type The type of this group or chunk.
   */
  public ChunkInfo(String id, long size, int type)
  {
    attributes = new Vector();
    reset();
    this.id = id;
    this.size = size;
    this.type = type;
  }

  /** Reset the chunk to all its default values. */
  public void reset()
  {
    id = null;
    attributes.removeAllElements();
    size = 0;
    offset = UNDEFINED_VALUE;
    type = UNDEFINED_VALUE;
    extraSize = 0;
  }

  /**
   * Copies the attributes from the specified ChunkInfo into this ChunkInfo.
   * 
   * @param f
   *          The value to copy from.
   */
  public void copy(ChunkInfo f)
  {
    id = f.id;
    size = f.size;
    offset = f.offset;
    type = f.type;
    extraSize = f.extraSize;
    // Copy all the elements of the hashtable to the new chunk.
    for (int i = 0; i < f.attributes.size(); i++)
    {
      Object o = f.attributes.elementAt(i);
      attributes.addElement(o);
    }
  }

  public Object clone()
  {
    ChunkInfo chunk = new ChunkInfo();
    chunk.copy(this);
    return chunk;
  }

  public boolean equals(Object obj)
  {
    if ((obj instanceof ChunkInfo) == false)
    {
      return false;
    }
    ChunkInfo other = (ChunkInfo) obj;
    if (id != other.id)
      return false;
    if (size != other.size)
      return false;
    if (extraSize != other.extraSize)
      return false;
    if (type != other.type)
      return false;
    if (offset != other.offset)
      return false;
    // Check if all elements are equals
    Enumeration e1 = attributes.elements();
    Enumeration e2 = other.attributes.elements();
    while(e1.hasMoreElements() && e2.hasMoreElements()) 
    {
        Object o1 = e1.nextElement();
        Object o2 = e2.nextElement();
        if (!(o1==null ? o2==null : o1.equals(o2)))
        return false;
    }    
    return true;
  }
  
  public Vector toAttributes()
  {
    return attributes;
  }

  public Vector getAttributes()
  {
    return attributes;
  }

  /** Copies the elements of this vector to this attributes */
  public void setAttributes(Attribute[] attrs)
  {
    this.attributes.removeAllElements();
    if (attrs == null)
      return;
    for (int i = 0; i < attrs.length; i++)
    {
       this.attributes.addElement(attrs[i]);
    }
  }
  
}
