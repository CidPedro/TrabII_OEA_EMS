import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class ExternalMergeSort {
	
	//lista de blocos do arquivo original
    public static List<File> blocos(File file, Comparator<String> comp) throws IOException {
        List<File> files = new ArrayList<File>();
        BufferedReader fbr = new BufferedReader(new FileReader(file));
        long tam_bloco = 15000000; // Tamanho em bytes dos blocos
        try{
            List<String> lista_temp =  new ArrayList<String>();
            String linha = "";
            try {
                while(linha != null) {
                    long tam_bloco_atual = 0;
                    while((tam_bloco_atual < tam_bloco) 
                    &&(   (linha = fbr.readLine()) != null) ){
                    	lista_temp.add(linha);
                    	tam_bloco_atual += linha.length(); 
                    }
                    files.add(ajeitaLista(lista_temp,comp));
                    lista_temp.clear();
                }
            } catch(EOFException oef) {
                if(lista_temp.size()>0) {
                    files.add(ajeitaLista(lista_temp,comp));
                    lista_temp.clear();
                }
            }
        } finally {
            fbr.close();
        }
        return files;
    }
 
    //ordena blocos
    public static File ajeitaLista(List<String> lista_temp, Comparator<String> comp) throws IOException  {
        Collections.sort(lista_temp,comp);  // 
        File novotemp = File.createTempFile("blocos", "flatfile");
        novotemp.deleteOnExit();
        BufferedWriter fbw = new BufferedWriter(new FileWriter(novotemp));
        try {
            for(String r : lista_temp) {
                fbw.write(r);
                fbw.newLine();
            }
        } finally {
            fbw.close();
        }
        return novotemp;
    }
 
    //junta blocos ordenados
    public static int mergeSortedFiles(List<File> files, File outputfile, final Comparator<String> comp) throws IOException {
        PriorityQueue<ArqBinBuffer> pq = new PriorityQueue<ArqBinBuffer>(11, 
            new Comparator<ArqBinBuffer>() {
              public int compare(ArqBinBuffer i, ArqBinBuffer j) {
                return comp.compare(i.peek(), j.peek());
              }
            }
        );
        for (File f : files) {
        	ArqBinBuffer bfb = new ArqBinBuffer(f);
            pq.add(bfb);
        }
        BufferedWriter fbw = new BufferedWriter(new FileWriter(outputfile));
        int conta = 0;
        try {
            while(pq.size()>0) {
            	ArqBinBuffer bfb = pq.poll();
                String r = bfb.pop();
                fbw.write(r);
                fbw.newLine();
                ++conta;
                if(bfb.vazio()) {
                    bfb.fbr.close();
                    bfb.original.delete();
                } else {
                    pq.add(bfb); 
                }
            }
        } finally { 
            fbw.close();
            for(ArqBinBuffer bfb : pq ) bfb.close();
        }
        return conta;
    }
 
    
    //main
    public static void main(String[] args) throws IOException {
    	
    	System.out.println("Ïniciando... Aguarde...");
        String inputfile = "cep.dat";
        String outputfile = "ordenado_ems.dat";
        Comparator<String> comparator = new Comparator<String>() {
            public int compare(String r1, String r2){
            	return r1.substring(290, 298).compareTo(r2.substring(290, 298));}};
        List<File> l = blocos(new File(inputfile), comparator) ;
        mergeSortedFiles(l, new File(outputfile), comparator);
        System.out.println("Fim. Cheque o arquivo criado 'ordenado_ems.dat'.");
        
    }
}
