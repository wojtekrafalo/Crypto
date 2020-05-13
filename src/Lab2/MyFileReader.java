package Lab2;

import java.io.*;

public class MyFileReader {
    public static String readFile (String inputPath) throws IOException {
        FileInputStream inputStream = new FileInputStream(inputPath);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void writeToFile (String outputPath, String fileContent) throws IOException {
        FileWriter writer = new FileWriter(outputPath);
        writer.write(fileContent);
        writer.close();
    }
}
