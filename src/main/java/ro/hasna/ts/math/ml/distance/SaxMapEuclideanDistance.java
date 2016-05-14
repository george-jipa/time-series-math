package ro.hasna.ts.math.ml.distance;

import org.apache.commons.math3.util.FastMath;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by George on 5/9/2016.
 */
public class SaxMapEuclideanDistance implements GenericDistanceMeasure<Map<String, Integer>>{


    @Override
    public double compute(Map<String, Integer> a, Map<String, Integer> b) {

        Map<String, Integer> differenceAB = clone(a);
        Map<String, Integer> differenceBA = clone(b);

        int difference = 0;
        for(Map.Entry<String, Integer> elemA : a.entrySet()) {
            Integer valueFromB = differenceBA.remove(elemA.getKey());
            if (valueFromB != null) {
                Integer valueFromA = differenceAB.remove(elemA.getKey());
                difference += (FastMath.pow(valueFromA - valueFromB, 2));
            }
        }
        for(Map.Entry<String, Integer> difABelem : differenceAB.entrySet()) {
            difference += FastMath.pow(difABelem.getValue(), 2);
        }
        for(Map.Entry<String, Integer> difBAelem : differenceAB.entrySet()) {
            difference += FastMath.pow(difBAelem.getValue(), 2);
        }
        return difference * 1.0;
    }

    @Override
    public double compute(Map<String, Integer> a, Map<String, Integer> b, double cutOffValue) {
        return 0;
    }

    private Map<String, Integer> clone(Map<String, Integer> hash) {
        HashMap<String, Integer> result = new HashMap<>();

        for(Map.Entry<String, Integer> elem : hash.entrySet()) {
            result.put(elem.getKey(), elem.getValue());
        }

        return result;
    }
}
