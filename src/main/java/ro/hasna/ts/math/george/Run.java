package ro.hasna.ts.math.george;

import java.util.ArrayList;

/**
 * Created by George on 5/3/2016.
 */
public class Run {
    public static void main(String[] args) {
        int wordSize = 4;
        int horizontalSegments = 16;
        int alphabetSize = 16;

        initWords(alphabetSize, wordSize);
        for(String s : Constant.words) {
            System.out.println(s);
        }

        double[] values = new double[50];
        values[0] = 1.0;
        values[1] = 2.0;
        values[2] = 4.1;
        values[3] = 10.0;
        values[4] = 0.0;
        values[5] = 1.1;
        values[6] = 3.0;
        values[7] = 2.0;
        values[8] = 0.1;
        values[9] = 3.5;
        values[10] = 1.0;
        values[11] = 2.0;
        values[12] = 4.1;
        values[13] = 10.0;
        values[14] = 0.0;
        values[15] = 1.1;
        values[16] = 3.0;
        values[17] = 2.0;
        values[18] = 0.1;
        values[19] = 3.5;

        SaxRepresentation sr = new SaxRepresentation(wordSize, horizontalSegments, alphabetSize);

        sr.transform(values);
    }

    private static void initWords(int alphabetSize, int wordSize) {
        ArrayList<String> words = new ArrayList<String>();
        StringBuilder sb = new StringBuilder(wordSize);
        int combinations =(int) Math.pow(alphabetSize * 1.0, wordSize * 1.0);
//        System.out.print(alphabetSize);
        for(int i = 0; i < combinations; i++) {
            sb.setLength(0);
            for(int j = 0, i2 = i; j < wordSize; j++, i2 /= alphabetSize)
                sb.insert(0, (char)('a' + i2 % alphabetSize));
            words.add(sb.toString());
        }

        Constant.words = words;
    }
}
