package ma.emsi.lpri;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HDFSFileUpload {

    public static void main(String[] args) throws IOException {
        String hdfsUri = "http://172.20.43.105:9870/webhdfs/v1";
        String localFilePath = "src/main/resources/.jpg";
        String hdfsFilePath = "/dir/photo.jpg";

        uploadFileToHDFS(hdfsUri, localFilePath, hdfsFilePath);
    }

    public static void uploadFileToHDFS(String hdfsUri, String localFilePath, String hdfsFilePath)
            throws IOException {
        URL url = new URL(hdfsUri + hdfsFilePath + "?op=CREATE");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("PUT");

        try (DataOutputStream outStream = new DataOutputStream(connection.getOutputStream());
             FileInputStream fileInputStream = new FileInputStream(new File(localFilePath))) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }

            int responseCode = connection.getResponseCode();
            System.out.println(responseCode);
            if (responseCode >= 200 && responseCode < 300) {
                System.out.println("File uploaded successfully!");
            } else {
                printErrorDetails(connection);
            }

        } catch (IOException e) {
            System.err.println("Error uploading file: " + e.getMessage());
        } finally {
            connection.disconnect();
        }
    }

    private static void printErrorDetails(HttpURLConnection connection) {
        try (InputStream errorStream = connection.getErrorStream()) {
            if (errorStream != null) {
                byte[] buffer = new byte[4096];
                int bytesRead;

                System.err.println("Error uploading file. Server response:");

                while ((bytesRead = errorStream.read(buffer)) != -1) {
                    System.err.write(buffer, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading server error stream: " + e.getMessage());
        }
    }
}
