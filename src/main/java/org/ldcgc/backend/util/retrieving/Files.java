package org.ldcgc.backend.util.retrieving;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Files {

    public static List<List<String>> getContentFromCSV(Resource file, char delimiter, boolean skipFirstLine) {
        List<List<String>> csvArrayList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
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

        } catch (IOException e) {
            log.warn("File %s not found".formatted(file.getFilename()));
        }

        return csvArrayList;

    }

}
