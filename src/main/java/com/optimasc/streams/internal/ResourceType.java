package com.optimasc.streams.internal;

import java.util.Date;
import java.util.Vector;


/** Represents a generic resource */
public interface ResourceType
{
  public static final String ATTRIBUTE_NAME_ENCRYPTION = "encryption";
  public static final String ATTRIBUTE_NAMESPACE_ENCRYPTION = "";
  
  public static final String ATTRIBUTE_NAME_ENCRYPTION_TYPE = "encryption.type";
  public static final String ATTRIBUTE_NAMESPACE_ENCRYPTION_TYPE = "";
  
  public static final String ATTRIBUTE_NAME_ENCRYPTION_KEYLENGTH = "encryption.keylength";
  public static final String ATTRIBUTE_NAMESPACE_ENCRYPTION_KEYLENGTH = "";
  
  public static final String ATTRIBUTE_NAME_COMPRESSION_TYPE = "compression.codec";
  public static final String ATTRIBUTE_NAMESPACE_COMPRESSION_TYPE = "";

  public static final String ATTRIBUTE_NAME_COMMENT = "comment";
  public static final String ATTRIBUTE_NAMESPACE_COMMENT = "";
  
  public static final String ATTRIBUTE_NAME_DATE_CREATED = "date.created";
  public static final String ATTRIBUTE_NAMESPACE_DATE_CREATED = "";
  
  public static final String ATTRIBUTE_NAME_DATE_MODIFIED = "date.modified";
  public static final String ATTRIBUTE_NAMESPACE_DATE_MODIFIED = "";
  
  public static final String ATTRIBUTE_NAME_DATE_ACCESSED = "date.accessed";
  public static final String ATTRIBUTE_NAMESPACE_DATE_ACCESSED = "";
  
  public static final String ATTRIBUTE_NAME_SIZE = "size";
  public static final String ATTRIBUTE_NAMESPACE_SIZE = "size";
  
  public static final String ATTRIBUTE_NAME_HASH_VALUE = "hash";
  public static final String ATTRIBUTE_NAMESPACE_HASH_VALUE = "";
  
  public static final String ATTRIBUTE_NAME_HASH_TYPE = "hash.type";
  public static final String ATTRIBUTE_NAMESPACE_HASH_TYPE = "";

  /** Returns true if this resource is encrypted. 
      If this is unknown, the value returned is NULL.
  */ 
  public Boolean isEncrypted();
  
  /** Returns the last modification timestamp of the resource. 
   *  If this is not supported or is unknown, the value returned is NULL. 
   */
  public Date getLastModified();
  
  /** Returns the last access timestamp of the resource. If this
   *  is not supported, the value returned is NULL. 
   */
  public Date getLastAccessed();
  
  /** Returns the creation timestamp of the resource. If this
   *  is not supported, the value returned is NULL. 
   */
  public Date getCreated();
  
  /** Returns the hash of the resource. If this is
   *  is not supported, the value returned is NULL.
   *  
   *  The format is returned as a big endian byte stream.
   */
  public byte[] getHash();
  
  /** Returns the hash type of the resource. If this is
   *  is not supported, the value returned is NULL. 
   */
  public String getHashType();
  
  /** Returns the compression algorithm associated with 
   *  the data of the resource. If the compression is
   *  unknown, the value is null, if there is no compression
   *  an empty string is returned, otherwise the
   *  compression algorithm is returned, according
   *  to the metadata_terms document.
   */
  public String getCompression();
  
  /** Return the real decoded (uncompressed/unencrypted) size of this resource or -1 
   *  if not known.
   *  
   */
  public long getRealSize();
  
  /** Return the comment associated with this resource, or NULL 
   *  if not known or unsupported.
   *  
   */
  public String getComment();
  
  /** Return the encryption type of this resource, or NULL 
   *  if not known or unsupported.
   *  
   */
  public String getEncryptionType();
  
  /** Return the encryption key Length of this resource, return
   *  -1 if the value is unknown.
   *  
   */
  public int getEncryptionKeyLength();
  
  
  
  /** Return a vector of attributes describing all these elements as attributes.
   *  The format of the value of the attributes should conform to the XML Schema canonical
   *  representation.
   */
  public Vector toAttributes();

}
