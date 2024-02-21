package ma.emsi.lpri;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Afficher {

    public static void main(String[] args) throws IOException {
        System.out.println("Welcome to our example program to get metadata of a file");

        Scanner keyboard = new Scanner(System.in);
        System.out.println(" **** Please insert the absolute path to the file in HDFS: ");
        String hdfsFilePath = keyboard.nextLine();
        System.out.println("Your HDFS File Path is: " + hdfsFilePath);

        if (hdfsFilePath != null) {
            String metadataResponse = retrieveMetadata("http://172.20.43.105:9870/webhdfs/v1" + hdfsFilePath);

            // Specify the local destination path for the metadata file
            //String localDestinationPath = "C:\\Desktop\\test";

            // Write metadata to the local file
            //writeToFile(metadataResponse, localDestinationPath);
            // Do something with the retrieved metadata, for example, print it
            System.out.println("Retrieved Metadata:\n" + metadataResponse);
        }
    }

    private static String retrieveMetadata(String hdfsUrl) throws IOException {
        URL url = new URL(hdfsUrl + "?op=GETFILESTATUS");
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        try {
            // Check server's status code
            int status = httpURLConnection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                // Read the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line).append("\n");
                }
                reader.close();

                return response.toString();
            } else {
                throw new IOException("Server returned non-OK status: " + status);
            }
        } finally {
            httpURLConnection.disconnect();
        }
    }

    private static void writeToFile(String content, String filePath) {
        try (PrintWriter writer = new PrintWriter(filePath)) {
            writer.print(content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
