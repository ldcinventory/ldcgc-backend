package org.ldcgc.backend.util.retrieving;

import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Files {

    public static List<List<String>> getContentFromCSV(Resource file, char delimiter, boolean skipFirstLine) throws IOException {
        List<List<String>> csvArrayList = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String line;
        if(skipFirstLine) reader.readLine(); // skip first line
        while ((line = reader.readLine()) != null) {
            List<String> values = new ArrayList<>();
            boolean inQuotedField = false;
            StringBuilder currentValue = new StringBuilder();
            for (char c : line.toCharArray()) {
                if (c == '"') {
                    inQuotedField = !inQuotedField;
                } else if (c == delimiter && !inQuotedField) {
                    values.add(currentValue.toString());
                    currentValue = new StringBuilder();
                } else {
                    currentValue.append(c);
                }
            }
            values.add(currentValue.toString());
            csvArrayList.add(values);
        }

        return csvArrayList;

    }

}
