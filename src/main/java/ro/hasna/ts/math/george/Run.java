package ro.hasna.ts.math.george;

import ro.hasna.ts.math.ml.distance.SaxMapEuclideanDistance;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Created by George on 5/3/2016.
 */
public class Run {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedIOException{
        int wordSize = 2;
        int horizontalSegments = 16;
        int alphabetSize = 16;
        int periodsPerSample = 3;

        MeasurementFlow flow = new MeasurementFlow();

        for (int i = 3; i < 7; i++) {
//            flow.readData("data_sets_input", i, "data_sets_output");
        }


        SaxRepresentation sr = new SaxRepresentation(wordSize, horizontalSegments, alphabetSize);

//        sr.transform(values);


        Path inputPath  = Paths.get("data_sets_output");
        List<Path> casesPaths = Files.walk(inputPath, 1, FileVisitOption.FOLLOW_LINKS).collect(Collectors.toList());

        ArrayList<String> cases = new ArrayList<>();
        cases.add("190v");
        cases.add("230v");
        cases.add("250v");
        cases.add("harm3");
        cases.add("harm4");
        cases.add("harm37");

        ArrayList<String> states = new ArrayList<>();
        states.add("n1");
        states.add("n2");
        states.add("n3");
        states.add("no_load");
        states.add("short_circuit");
        states.add("un_triggered");

        ArrayList<Instance> train = new ArrayList<>();
        ArrayList<Instance> test = new ArrayList<>();

        for (String caseName : cases) {
            for(String state : states) {
                List<String> lines = Files.readAllLines(Paths.get("data_sets_output/"+ caseName + "/" + periodsPerSample + "p_" + state + ".txt"));
//                System.out.println("data_sets_output/"+ caseName + "/3p_" + state + ".txt");

                for(String line : lines) {
//                    System.out.println(line);
                    String[] valuesString = line.split(",");
                    double[] valuesDouble = new double[valuesString.length];
                    for (int i = 0; i < valuesString.length; i++) {
                        valuesDouble[i] = Double.parseDouble(valuesString[i]);
                    }
                    Map<String, Integer> hashSax = sr.transform(valuesDouble);
//                    if(hashSax.size() < 15) System.out.println(hashSax.toString());
                    Instance inst = new Instance(hashSax, state);
                    if (caseName.equals("230v")) {
                        train.add(inst);
                    } else {
                        test.add(inst);
                    }
                }
            }
        }

        int matches = 0;
        SaxMapEuclideanDistance smed = new SaxMapEuclideanDistance();
        for(Instance testInstance : test) {
            Instance best = train.get(0);
            double dist = smed.compute(best.getHashMap(), testInstance.getHashMap());
            for(Instance trainInstance : train) {
                double currentDist = smed.compute(testInstance.getHashMap(), trainInstance.getHashMap());
                if (currentDist < dist) {
                    best = trainInstance;
                    dist = currentDist;
                }
            }
            if (best.getClassLabel().equals(testInstance.getClassLabel())) {
                matches++;
            }
        }
        double accuracy = matches / (test.size() * 1.0);
        System.out.println(accuracy);
    }
}
