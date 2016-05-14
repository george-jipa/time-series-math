package ro.hasna.ts.math.george;

import org.apache.commons.math3.util.Pair;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Created by George on 5/7/2016.
 */
public class MeasurementFlow {
    public void readData(String inputFolder, int periodsPerSample, String outputFolder) throws IOException, ExecutionException, InterruptedIOException {
        Path resultPath = Paths.get(outputFolder);
        if (!Files.exists(resultPath)) {
            Files.createDirectories(resultPath);
        }

        Path inputPath = Paths.get(inputFolder);
        List<Path> casesPaths = Files.walk(inputPath, 1, FileVisitOption.FOLLOW_LINKS).collect(Collectors.toList());

        for(Path casesPath : casesPaths) {
            if (casesPath.equals(inputPath)) {
                continue;
            }

            String caseName = casesPath.getFileName().toString();
//            System.out.println("prelucrare " + caseName);
            Path caseResultPath = Paths.get(outputFolder, caseName);
            if (!Files.exists(caseResultPath)) {
                Files.createDirectories(caseResultPath);
            }

            List<List<Double>> instancesValues = new ArrayList<>();
            List<String> instancesClasses = new ArrayList<>();

            List<Path> statesPaths = Files.walk(casesPath, 1).collect(Collectors.toList());

            for(Path statesPath : statesPaths) {
                if (statesPath.equals(casesPath)) {
                    continue;
                }
                String state = getState(statesPath);
//                System.out.println("prelucrare " + state);

                List<Path> measurementsPaths = Files.walk(statesPath).filter(p -> Files.isRegularFile(p) && p.toFile().getAbsolutePath().endsWith(".csv")).collect(Collectors.toList());
                List<String> stringValues = new ArrayList<>();
                for(Path measurementsPath : measurementsPaths) {
                    List<List<Double>> lists = getInstances(measurementsPath, periodsPerSample);
                    for(List<Double> list : lists) {
                        String stringValue = convertToString(list);
                        stringValues.add(stringValue);
                    }
//                    System.exit(-1);

                }
                String outputFilePath = resultPath.toString() + "/" + caseName + "/" + periodsPerSample + "p_" + state + ".txt";
//                System.out.println("()()()" + outputFilePath);
                Path outputFile= Paths.get(outputFilePath);
                Files.write(outputFile,stringValues);
            }


        }


    }

    private String getState(Path statesPath) {
        String state = statesPath.getFileName().toString().toLowerCase(Locale.ENGLISH);
        if (state.contains("15")) {
            state = "n3";
        } else if (state.contains("10")) {
            state = "n2";
        } else if (state.contains("5")) {
            state = "n1";
        } else if (state.contains("load")) {
            state = "no_load";
        } else if (state.contains("trig")) {
            state = "un_triggered";
        } else {
            state = "short_circuit";
        }
        return state;
    }

    private List<List<Double>> getInstances(Path measurementsPath, int periods) throws IOException {
//        System.out.println(measurementsPath.toString());
        List<List<Double>> instancesValues = new ArrayList<>();
        List<String> lines = Files.readAllLines(measurementsPath);
        int maxSize = lines.size() - 15;
        double[] current = new double[maxSize];
        boolean ok = true;
        int n = 0;
        for (int i = 15; i < lines.size(); i++) {
            String line = lines.get(i);
            if(!line.isEmpty()) {
                String[] v = line.split(",");
                if (v.length != 3) {
                    ok = false;
                } else {
                    current[n] = Double.parseDouble(v[2]);
                    n++;
                }
            }
        }
//        System.out.println(n);
        if (!ok) {
            return instancesValues;
        }

        List<Pair<Integer, Integer>> currentMargins = getMargins(current, periods);

        for(Pair<Integer, Integer> currentPair : currentMargins) {
            List<Double> instance = new ArrayList<>();
            for (int i = currentPair.getFirst(); i < currentPair.getSecond(); i++) {
                instance.add(current[i]);
            }
            instancesValues.add(instance);
        }

        return instancesValues;
    }

    private List<Pair<Integer, Integer>> getMargins(double[] v, int periods) {
        List<Pair<Integer, Integer>> tempMargins = new ArrayList<>();
        List<Pair<Integer, Integer>> margins = new ArrayList<>();

        List<Integer> breakpionts = new ArrayList<>();
        int i = 1;
        //find breakpoints
        while(i < v.length - 125) {
            if (v[i-1] < 0 && v[i] >= 0 && v[i + 125] > 0) {
//                System.out.print(i + " ");
                breakpionts.add(i);
                i += 375;
            } else {
                i++;
            }
        }
//        System.out.println();
        //create pairs from breakpoints
        for (i = 0; i < breakpionts.size() - 1; i++) {
            int start = breakpionts.get(i);
            int end = breakpionts.get(i+1);
            int equalValues = 0;
            int j;
            tempMargins.add(new Pair<>(start, end));

//            for(j = start; j < end && equalValues < 10; j++) {
//                if(j > 0 && FastMath.abs(v[j-1] - v[j]) < 0.0001) {
//                    equalValues++;
//                } else {
//                    equalValues = 0;
//                }
//            }
//            if (equalValues < 10) {
//                margins.add(new Pair<>(start, end));
//            } else {
//                System.out.println(j + " " + (start + 15) + "[" + v[start] + "]-" + (end  + 15) + "[" + v[end] + "]");
//            }

        }

//        if(margins.size() < 16) {
//            for(Integer breakpoint : breakpionts) {
//                System.out.println((breakpoint + 15) + "[" + v[breakpoint - 1] + "]" + "[" + v[breakpoint] + "]");
//            }
//        }
        int marginsSize = tempMargins.size() / periods;
        for (i = 0; i < marginsSize; i++) {
            margins.add(new Pair<>(tempMargins.get(i * periods).getFirst(), tempMargins.get((i + 1) * periods - 1).getSecond()));
//            System.out.print("[" + tempMargins.get(i * periods).getFirst() + " " + tempMargins.get((i + 1) * periods - 1).getSecond() + "] ");
        }
//        System.out.println();
        return margins;
    }

    private String convertToString(List<Double> list) {
        String value = "";
        for (int i = 0; i < list.size(); i++) {
            value += list.get(i) + ",";
        }
        value = value.substring(0, value.length() - 1);
        return value;
    }
}
