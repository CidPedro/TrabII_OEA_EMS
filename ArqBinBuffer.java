import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ArqBinBuffer {
	public static int Tam_buffer = 2048;
    public BufferedReader fbr;
    public File original;
    private String cache;
    private boolean vazio;
     
    public ArqBinBuffer(File f) throws IOException {
        original = f;
        fbr = new BufferedReader(new FileReader(f), Tam_buffer);
        reload();
    }
     
    public boolean vazio() {
        return vazio;
    }
     
    private void reload() throws IOException {
        try {
          if((this.cache = fbr.readLine()) == null){
        	  vazio = true;
        	  cache = null;
          }
          else{
        	  vazio = false;
          }
      } catch(EOFException oef) {
    	  vazio = true;
    	  cache = null;
      }
    }
     
    public void close() throws IOException {
        fbr.close();
    }
     
     
    public String peek() {
        if(vazio()) return null;
        return cache.toString();
    }
    public String pop() throws IOException {
      String ans = peek();
        reload();
      return ans;
    }
     
     
}
