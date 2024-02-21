package ma.emsi.lpri;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HDFSFileRetrieval {

    public static void main(String[] args) throws IOException {
        String hdfsUri = "http://172.20.43.105:9870/webhdfs/v1";
        String hdfsFilePath = "/cv.pdf";
        String localFilePath = "src/main/resources/cv.pdf";

        retrieveFileFromHDFS(hdfsUri, hdfsFilePath, localFilePath);
    }

    public static void retrieveFileFromHDFS(String hdfsUri, String hdfsFilePath, String localFilePath)
            throws IOException {
        URL url = new URL(hdfsUri + hdfsFilePath + "?op=OPEN");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            InputStream inStream = new BufferedInputStream(connection.getInputStream());
            FileOutputStream outStream = new FileOutputStream(localFilePath);

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }

            System.out.println("File retrieved successfully!");
        } catch (IOException e) {
            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();

            System.err.println("Error retrieving file. Response code: " + responseCode + ", Message: " + responseMessage);
        } finally {
            connection.disconnect();
        }
    }
}
