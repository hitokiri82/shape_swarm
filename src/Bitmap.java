import java.io.FileInputStream;
import java.io.DataInputStream;

public class Bitmap
{
  public boolean[][] map;
  public int         width;
  public int         height;

  //////////////////////////////////////////////////////////////////
  //
  // Bitmap
  //
  public Bitmap (String filename) throws Exception {
    map    = loadImage(filename);
    width  = map.length;
    height = map[0].length;
  }

  //////////////////////////////////////////////////////////////////
  //
  // loadImage
  //
  protected static boolean[][] loadImage (String filename)
    throws Exception {

    boolean[][] imgarray = null;

    FileInputStream infile = new FileInputStream(filename);
    DataInputStream instream = new DataInputStream(infile);
    
    instream.skipBytes(10);
    int data_offset = intelInt(instream.readInt());
    instream.skipBytes(4);
    int width  = intelInt(instream.readInt());
    int height = intelInt(instream.readInt());
    instream.skipBytes(2);
    int bitcount = intelShort(instream.readShort());
    instream.skipBytes(4);
    int imgsize = intelInt(instream.readInt());
    instream.skipBytes(data_offset - 38);
    
    int b;
    int x = 0;
    int y = height-1;
    int bytes_read = 0;
    
    imgarray = new boolean[width][height];
    double bitCount = 0;
    
    while ((b = instream.read()) != -1 && y >= 0){
      bytes_read++;
      for (int i = 7 ; i >= 0 ; i--, x++){
	if (x >= width){
	  x = 0;
	  y--;
	  
	  int skip = (4 - bytes_read%4)%4;
	  bytes_read += skip;
	  instream.skipBytes(skip);
	  
	  break;
	} else if ((b & (1 << i)) == (1 << i)){
	  imgarray[x][y] = true;
	  bitCount++;
	} else {
	  imgarray[x][y] = false;
	}
      }
    }
    instream.close();
    infile.close();
    
    return imgarray;
  }

  //////////////////////////////////////////////////////////////////
  //
  // intelShort
  //
  // converts a 16-bit number stored in intel byte order into
  // the local host format
  //
  protected static int intelShort(int i){
    return ((i >> 8) & 0xff) + ((i << 8) & 0xff00);
  }

  //////////////////////////////////////////////////////////////////
  //
  // intelInt
  //
  // converts a 32-bit number stored in intel byte order into
  // the local host format
  //
  protected static int intelInt(int i){
    return ((i & 0xff) << 24) + ((i & 0xff00) << 8) +
      ((i & 0xff0000) >> 8) + ((i >> 24) & 0xff);
  }
}
