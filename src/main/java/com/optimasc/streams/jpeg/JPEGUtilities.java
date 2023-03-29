package com.optimasc.streams.jpeg;

import com.optimasc.streams.internal.ChunkUtilities;

public class JPEGUtilities extends ChunkUtilities
{ 
        /* Markers defined here were taken from ITU T.81 as well as
         * 
         */
  
  
        public static final int JPEG_ID_TEM  = 0x01;            /* unknown */
        
        /* The following segment markers have a 2 byte length following them. See ITU T.81 B.2.2 */
        public static final int JPEG_ID_SOF0 = 0xc0;            /* start of FRAME */
        public static final int JPEG_ID_SOF1 = 0xc1;            /* """""""""""""" */
        public static final int JPEG_ID_SOF2 = 0xc2;            /* following SOF usually unsupported */
        public static final int JPEG_ID_SOF3 = 0xc3;
        public static final int JPEG_ID_SOF4 = 0xc4;
        public static final int JPEG_ID_SOF5 = 0xc5;
        public static final int JPEG_ID_SOF6 = 0xc6;
        public static final int JPEG_ID_SOF7 = 0xc7;
        public static final int JPEG_ID_SOF9 = 0xc9;            /* sof9 : for arithmetic coding - taboo! */
        public static final int JPEG_ID_SOF10= 0xca;
        public static final int JPEG_ID_SOF11= 0xcb;
        public static final int JPEG_ID_SOF13= 0xcd;
        public static final int JPEG_ID_SOF14= 0xce;
        public static final int JPEG_ID_SOF15= 0xcf;
        

        public static final int JPEG_ID_DHT  = 0xc4;            /* Define huffman Table, followed by a 2 byte length */
        public static final int JPEG_ID_JPG  = 0xc8;            /* undefined/ reserved =Error? */
        public static final int JPEG_ID_DAC  = 0xcc;            /* Define arithmetic table, followed by a 2 byte length  */
        public static final int JPEG_ID_RST0 = 0xd0;            /* Used for resync [?] ignored */
        public static final int JPEG_ID_rst1 = 0xd1;
        public static final int JPEG_ID_rst2 = 0xd2;
        public static final int JPEG_ID_rst3 = 0xd3;
        public static final int JPEG_ID_rst4 = 0xd4;
        public static final int JPEG_ID_rst5 = 0xd5;
        public static final int JPEG_ID_rst6 = 0xd6;
        public static final int JPEG_ID_rst7 = 0xd7;
        public static final int JPEG_ID_SOI  = 0xd8;            /* start of image */
        public static final int JPEG_ID_EOI  = 0xd9;            /* end   of image */
        public static final int JPEG_ID_SOS  = 0xda;            /* start of scan, followed by a 2 byte length  */
        public static final int JPEG_ID_DQT  = 0xdb;            /* Define Quantization Table, followed by a 2 byte length */
        public static final int JPEG_ID_DNL  = 0xdc;            /* Define number of lines, followed by a 2 byte length */
        public static final int JPEG_ID_DRI  = 0xdd;            /* Define Restart Interval, followed by a 2 byte length */
        public static final int JPEG_ID_DHP  = 0xde;            /* DHP, followed by a 2 byte length  */
        public static final int JPEG_ID_EXP  = 0xdf;            /* Expand reference, followed by a 2 byte length */
        public static final int JPEG_ID_APP0 = 0xe0;            /* Application segment marker, followed by a 2 byte length */
        public static final int JPEG_ID_APP1 = 0xe1;            /* Application segment marker, followed by a 2 byte length */
        public static final int JPEG_ID_APP2 = 0xe2;            /* Application segment marker, followed by a 2 byte length */
        public static final int JPEG_ID_APP3 = 0xe3;            /* Application segment marker, followed by a 2 byte length */
        public static final int JPEG_ID_APP4 = 0xe4;            /* Application segment marker, followed by a 2 byte length */
        public static final int JPEG_ID_APP5 = 0xe5;            /* Application segment marker, followed by a 2 byte length */
        public static final int JPEG_ID_APP6 = 0xe6;            /* Application segment marker, followed by a 2 byte length */
        public static final int JPEG_ID_APP7 = 0xe7;            /* Application segment marker, followed by a 2 byte length */
        public static final int JPEG_ID_APP8 = 0xe8;            /* Application segment marker, followed by a 2 byte length */
        public static final int JPEG_ID_APP9 = 0xe9;            /* Application segment marker, followed by a 2 byte length */
        public static final int JPEG_ID_APP10 = 0xea;           /* Application segment marker, followed by a 2 byte length */
        public static final int JPEG_ID_APP11 = 0xeb;           /* Application segment marker, followed by a 2 byte length */
        public static final int JPEG_ID_APP12 = 0xec;           /* Application segment marker, followed by a 2 byte length */
        public static final int JPEG_ID_APP13 = 0xed;           /* Application segment marker, followed by a 2 byte length */
        public static final int JPEG_ID_APP14 = 0xee;           /* Application segment marker, followed by a 2 byte length */
        public static final int JPEG_ID_APP15= 0xef;            /* Application segment marker, followed by a 2 byte length */
        
        public static final int JPEG_ID_VER = 0xf0;            /* VER, Version information, followed by a 2 byte length */ 
        public static final int JPEG_ID_DTI = 0xf1;            /* DTI, Define tiled image, followed by a 2 byte length */
        public static final int JPEG_ID_DTT = 0xf2;            /* DTT, Define tile, followed by a 2 byte length */
        public static final int JPEG_ID_SRF = 0xf3;            /* SRF, Selectively refined frame, followed by a 2 byte length */
        public static final int JPEG_ID_SRS = 0xf4;            /* SRS, Selectively refined scan, followed by a 2 byte length */
        public static final int JPEG_ID_DCR = 0xf5;            /* DCR, Define component registration, followed by a 2 byte length */
        public static final int JPEG_ID_DQS = 0xf6;            /* DQS, Define quantizer scale selection, followed by a 2 byte length */
        public static final int JPEG_ID_SOF55= 0xf7;           /*  JPEG-LS identifier, followed by a 2 byte length  */
        public static final int JPEG_ID_LSE  = 0xf8;           /*  JPEG-LS identifier, followed by a 2 byte length  */
        public static final int JPEG_ID_JPG9 = 0xf9;
        public static final int JPEG_ID_JPG10 = 0xfa;
        public static final int JPEG_ID_JPG11 = 0xfb;
        public static final int JPEG_ID_JPG12 = 0xfc;
        public static final int JPEG_ID_JPG13= 0xfd;
        public static final int JPEG_ID_COM  = 0xfe;            /* Comment, followed by a 2 byte length */
        /*  This block is added by the JPEG parser and contains the scanline information  */
        public static final int JPEG_ID_DATA = 0xff;
        
        public static final int JPEG_MAGIC_SOI_SIGNATURE = 0xffd8;
        public static final int JPEG_MAGIC_EOI_SIGNATURE = 0xffd9;
        // All jpeg chunks start with this marker 
        public static final int JPEG_MARKER = 0xff;
        
        // Maximum segment size except for the JPEG DATA segment
        public static final int JPEG_MAX_DATA_SEGMENT_SIZE = 65533;

        /** IANA MIME Type */
        public static final String MIME_TYPE = "image/jpeg";
        
        
        
        public String chunkIDToObject(Object obj)
            throws IllegalArgumentException
        {
          int value;
          if (obj instanceof Integer)
          {
              Integer vi = (Integer)obj;
              value = (int)(vi.intValue() & 0xFFFFFFF);
          } else
          if (obj instanceof Long)
          {
              Long vl = (Long)obj;
              value = (int)(vl.longValue() & 0xFFFFFFF);
          }
          else
          if (obj instanceof Short)
          {
              Short vs = (Short)obj;
              value = (int)(vs.shortValue() & 0xFFFF); 
          } else
          if (obj instanceof Byte)
          {
              Byte vb = (Byte)obj;
              value = (int)(vb.byteValue() & 0xFF);
          } else
          if (obj instanceof byte[])
          {
                byte[] barr = (byte[])obj;
                value = (int)(barr[0] & 0xFF);
          } else
          if (obj instanceof short[])
          {
                  short[] barr = (short[])obj;
                  value = (int)(barr[0] & 0xFFFF);
          } else
            if (obj instanceof int[])
            {
                    int[] barr = (int[])obj;
                    value = (int)(barr[0]);
            } else
            {
              throw new IllegalArgumentException("Invalid identifier type object - it should be a number or numeric array!");
            }
         if ((value < 0x00) || (value > 0xff))
         {
            throw new IllegalArgumentException("Illegal segment identifier!"); 
         }
         return new Integer(value).toString();
        }
        
}
