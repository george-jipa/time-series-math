package ro.hasna.ts.math.george;

import ro.hasna.ts.math.representation.GenericTransformer;
import ro.hasna.ts.math.representation.SymbolicAggregateApproximation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by George on 5/3/2016.
 */
public class SaxRepresentation implements GenericTransformer<double[], Map<String, Integer>> {
    private int wordSize;
    private SymbolicAggregateApproximation sax;

    public SaxRepresentation(int wordSize, int segments, int alphabetSize) {
        this.wordSize = wordSize;
        this.sax = new SymbolicAggregateApproximation(segments, alphabetSize);
    }

    public SaxRepresentation(int wordSize, SymbolicAggregateApproximation sax) {
        this.wordSize = wordSize;
        this.sax = sax;
    }

    public Map<String, Integer> transform(double[] values) {

        int[] resInt = sax.transform(values);

        String resString = "";
        for(int i = 0; i < resInt.length; i++) {
            resString += (char)(resInt[i] + 'a');
        }
        StringBuilder searchedWord=new StringBuilder();
        for (int i = 0; i < wordSize; i++) {
            searchedWord.append(resString.charAt(i));
        }

        Map<String, Integer> resMap = new HashMap<String, Integer>();
        //first pair<word, occurences> inserted in hashmap
        resMap.put(searchedWord.toString(),1);

        for (int i = wordSize; i < resString.length(); i++) {
            searchedWord.deleteCharAt(0);
            searchedWord.append(resString.charAt(i));

            Integer count = resMap.get(searchedWord.toString());
            if (count==null){
                count=0;
            }
            count++;
            resMap.put(searchedWord.toString(),count);
        }


//        System.out.println("reprezentarea sub forma de caractere:");
//        System.out.println(resString);
//        System.out.println("reprezentarea sub forma de HashMap:");
//        Iterator it = resMap.entrySet().iterator();
//        while(it.hasNext()) {
//            Map.Entry pair = (Map.Entry)it.next();
//            System.out.print(pair.getKey() + " = " + pair.getValue() + "; ");
//            it.remove();
//        }

        return resMap;
    }
}
