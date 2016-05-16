package ro.hasna.ts.math.george;

import java.util.Map;

/**
 * Created by George on 5/13/2016.
 */
public class Instance {
    private Map<String, Integer> hashMap;
    private String classLabel;

    public Instance(Map<String, Integer> hashMap, String classLabel) {
        this.hashMap = hashMap;
        if (classLabel.equals("n1") || classLabel.equals("n2") || classLabel.equals("n3")) {
            this.classLabel = "n";
        } else {
            this.classLabel = classLabel;
        }
    }

    public Map<String, Integer> getHashMap() {
        return hashMap;
    }

    public String getClassLabel() {
        return classLabel;
    }
}
