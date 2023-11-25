package com.optimasc.streams.internal;

import java.util.Date;
import java.util.Vector;

import com.optimasc.archive.ArchiveEntry;


/** Represents a generic resource and defines standard attributes for
    resource types. */
public interface ResourceType extends ArchiveEntry
{
  public static final String ATTRIBUTE_NAME_ENCRYPTION = "encryption";
  public static final String ATTRIBUTE_NAMESPACE_ENCRYPTION = "";
  
  /** Identification of algorithm used to encrypt the resource/chunk data. If this
      attribute is not present, the resource is not encrypted or uses unknown
      encryption. */
  public static final String ATTRIBUTE_NAME_ENCRYPTION_TYPE = "encryption.type";
  public static final String ATTRIBUTE_NAMESPACE_ENCRYPTION_TYPE = "";
  
  public static final String ATTRIBUTE_NAME_ENCRYPTION_KEYLENGTH = "encryption.keylength";
  public static final String ATTRIBUTE_NAMESPACE_ENCRYPTION_KEYLENGTH = "";
  
  /** Identification of codec used to compress the resource/chunk data. If this
      attribute is not present, the resource is not compressed or uses unknown
      compression. */
  public static final String ATTRIBUTE_NAME_COMPRESSION_TYPE = "compression.codec";
  public static final String ATTRIBUTE_NAMESPACE_COMPRESSION_TYPE = "";

  /** Comment or note associated with this resource/chunk. If this attribute is not
      present, then no comment is associated with this chunk data. */
  public static final String ATTRIBUTE_NAME_COMMENT = "comment";
  public static final String ATTRIBUTE_NAMESPACE_COMMENT = "";
  
  /** Creation date of the data associated with this resource/chunk. If this
      attribute is not present, then creation date is not available. This is represented
      as an ISO 8601 formatted timestamp. */
  public static final String ATTRIBUTE_NAME_DATE_CREATED = "date.created";
  public static final String ATTRIBUTE_NAMESPACE_DATE_CREATED = "";
  
  /** Last modification date of the data associated with this resource/chunk. If this
      attribute is not present, then last modification date is not available. This is represented
      as an ISO 8601 formatted timestamp. */
  public static final String ATTRIBUTE_NAME_DATE_MODIFIED = "date.modified";
  public static final String ATTRIBUTE_NAMESPACE_DATE_MODIFIED = "";
  
  /** Last accessed date of the data associated with this resource/chunk.  If this
      attribute is not present, then last accessed date is not available. This is represented
      as an ISO 8601 formatted timestamp. */
  public static final String ATTRIBUTE_NAME_DATE_ACCESSED = "date.accessed";
  public static final String ATTRIBUTE_NAMESPACE_DATE_ACCESSED = "";
  
  /** Original size in octets of the uncompressed/unencrypted data. */
  public static final String ATTRIBUTE_NAME_SIZE = "size";
  public static final String ATTRIBUTE_NAMESPACE_SIZE = "size";
  
  /** Hash/checksum value associated with the data of this chunk data as a Hexbinary
      string. */
  public static final String ATTRIBUTE_NAME_HASH_VALUE = "hash";
  public static final String ATTRIBUTE_NAMESPACE_HASH_VALUE = "";
  
  /** Hash/checksum algorithm. */
  public static final String ATTRIBUTE_NAME_HASH_TYPE = "hash.type";
  public static final String ATTRIBUTE_NAMESPACE_HASH_TYPE = "";

  /** Returns true if this resource is encrypted. 
      If this is unknown, the value returned is NULL.
  */ 
  public Boolean isEncrypted();
  
  /** Returns the last modification timestamp of the resource. 
   *  If this is not supported or is unknown, the value returned is NULL. 
   */
  public Date getLastModifiedDate();
  
  /** Returns the last access timestamp of the resource. If this
   *  is not supported, the value returned is NULL. 
   */
  public Date getLastAccessedDate();
  
  /** Returns the creation timestamp of the resource. If this
   *  is not supported, the value returned is NULL. 
   */
  public Date getCreatedDate();
  
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
  public long getSize();
  
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
