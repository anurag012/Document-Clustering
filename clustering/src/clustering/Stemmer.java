package clustering;




import java.util.*;
import java.util.Map.Entry;
import java.io.*;

public class Stemmer {

 public static void main(String[] args)
 {
	 Map<String, ArrayList<String>> cluster= new HashMap<String, ArrayList<String>>();
	 long totalterm = 0;
     
	 try{
		 String datapath = args[0];
		 String outputpath = args[1];
	 String tsvfile = datapath+"jobs.tsv";
	 try{
    	 String tsvfile1 =datapath+"stopwords.txt";
      BufferedReader br1 = new BufferedReader(new FileReader(tsvfile1));

      while(br1.ready())
      {
       stopwords.add(br1.readLine());
      }
      
     }catch(Exception e){System.out.println(e);}
		BufferedReader br = new BufferedReader(new FileReader(tsvfile));
		String line = "";
		File file = new File(outputpath);
	     if (!file.exists()) {
	     file.createNewFile();
	     }
	     FileWriter fw = new FileWriter(file.getAbsoluteFile());
         BufferedWriter bw = new BufferedWriter(fw);
         
		while ((line = br.readLine()) != null){
			line = line.trim();
			String tab[] = line.split("\t");
			String desc = tab[1];
			 ArrayList<String> arrVal1 = cluster.get(tab[0]);
  for(Object word : Stemmer.words(desc)){
	  if (arrVal1 == null) {
          arrVal1 = new ArrayList<String>();
          cluster.put(tab[0],arrVal1);
      }
      arrVal1.add(word.toString());
  }
		}
		
		Map<String, Integer> calculate = new HashMap<String, Integer>();
        for (String key : cluster.keySet()) {
            List<String> sports = cluster.get(key);
            for (String sport : sports) {
            
          
                if (calculate.containsKey(sport)) {
                    Integer count = calculate.get(sport);
                    count += 1;
                    calculate.put(sport, count);
                }else {
                    calculate.put(sport,1);
                }
                totalterm++;
                
            }
        }
        
        HashMap<String, Long> docTermCounts = new HashMap<String, Long>();
        HashMap<String,Double> result = new HashMap<String, Double>();
        for (String key1 : cluster.keySet()) {
            List<String> parts = cluster.get(key1);
            for (String part : parts) {
            	if(part.isEmpty()) continue;
            	long road=1;
            	if (docTermCounts.containsKey(part)) {
                    road += docTermCounts.get(part).longValue();
                }	
            	docTermCounts.put(part, new Long(road));
            }
            for (String token : docTermCounts.keySet()) {
                double docTermCount =1+Math.log10( docTermCounts.get(token).doubleValue());
                double globalTermCount = calculate.get(token).doubleValue();
                double tfidf = docTermCount * Math.log10((double)totalterm / globalTermCount);
                result.put(token, tfidf);
            }
        }
        
    
		Map<String, String> rstMap = new HashMap<String, String>();
         for (String key : cluster.keySet()) {
             List<String> words = cluster.get(key);
             Integer ind = 0;

             for (int i = 0; i < words.size(); i++) {
            	 
            	 if(result.containsKey(words.get(i))){
            		 
                 if (result.get(words.get(i)) > result.get(words.get(ind)) ) {
                	 
                     ind = i;
                     
                 }
                 
             }
             }
             
             rstMap.put(key, words.get(ind));
            
         }
         
         Map<String,Integer> finalmap = new HashMap<String,Integer>();
         Map<String, Integer> valueMap = new HashMap<String,Integer>();
         int i = 0;
         for (String oldValue : rstMap.values()) {
             if ( ! valueMap.containsKey(oldValue )) {
                 valueMap.put(oldValue, ++i);
             }
         }
         
         // replace everything in data
      
         for (Entry<String, String> dataEntry : rstMap.entrySet()) {
             finalmap.put(dataEntry.getKey(), valueMap.get(dataEntry.getValue()));
         }
         
         
         bw.write("JobID\tCluster no.\n");
       
         for (Entry<String, Integer> entry2 : finalmap.entrySet()) {                        	
       	    bw.write(entry2.getKey()+"\t"+entry2.getValue());
       	    bw.newLine();							                            
         }
              
		System.out.println("output file is created");
//		br.close();
		bw.close();
	 }catch(Exception e){
		 System.err.println("TSV file cannot be read : " + e);
	 }
 }

 static HashSet<String> stopwords = new HashSet<String>();

    public static void addStopwords()
    {
     
    }

    public static ArrayList<String> words(String line)
    {
     if(stopwords.size() == 0)
      addStopwords();

     ArrayList<String> result = new ArrayList<String>();
     line=line.replaceAll("\\<.*?>","");
     String[] words = line.split("[ \t\n,\\.\"!?$~()\\[\\]\\{\\}:;/\\\\<>+=%*]");
     for(int i=0; i < words.length; i++)
     {
      if(words[i] != null && !words[i].equals(""))
      {
       String word = words[i].toLowerCase();
       if(!stopwords.contains(word))
       {
        result.add(Stemming.stem(word));
       }
      }
     }

     return result;
    }

}
