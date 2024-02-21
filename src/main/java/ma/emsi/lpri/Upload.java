package ma.emsi.lpri;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

public class Upload {
    private HttpURLConnection httpConn;
    private DataOutputStream request;
    private final String boundary =  "*****";
    private final String crlf = "\r\n";
    private final String twoHyphens = "--";

    /**
     * This constructor initializes a new HTTP POST request with content type
     * is set to multipart/form-data
     *
     * @param requestURL
     * @throws IOException
     */
    public Upload(String requestURL, String requestMethod) throws IOException {

        // creates a unique boundary based on time stamp
        URL url = new URL(requestURL);
        //open a connection to remote server HTTP
        httpConn = (HttpURLConnection) url.openConnection();
        //Http connection should no use any cache
        httpConn.setUseCaches(false);
        // HttpURLConnection will be used for output (sending data to the server)/
        //Setting this to true means that you intend to write data to the URL connection,
        // typically for methods like POST or PUT, where you are sending data to the server in
        // the request body.
        httpConn.setDoOutput(true); // indicates POST method

        //HttpURLConnection will be used for input (reading data from the server).
        // Setting this to true means that you intend to read data from the URL connection,
        // typically for methods like GET, where you are retrieving data from the server.
        httpConn.setDoInput(true);
        //expected to receive request method: post, get, put, delete
        httpConn.setRequestMethod(requestMethod);

        // it's setting the "Connection" property to "Keep-Alive."
        // Keep-Alive is for reusing the existing connection for multiple requests to the same server, rather than establishing a new connection for each request
        httpConn.setRequestProperty("Connection", "Keep-Alive");
        httpConn.setRequestProperty("Cache-Control", "no-cache");
        //bounday: helps distinguish between different part of the data
        httpConn.setRequestProperty(
                // Multipart form data is a format used to send binary and textual data as part of an HTTP request, commonly used when uploading files like images or documents with binary data.
                "Content-Type", "multipart/form-data;boundary=" + this.boundary);

        request =  new DataOutputStream(httpConn.getOutputStream());
        System.out.println("Request:  "+ request.size());
    }

    /**
     * Adds a form field to the request
     *
     * @param name  field name
     * @param value field value
     */
    public void addFormField(String name, String value)throws IOException  {
        request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"" + name + "\""+ this.crlf);
        request.writeBytes("Content-Type: text/plain; charset=UTF-8" + this.crlf);
        request.writeBytes(this.crlf);
        request.writeBytes(value+ this.crlf);
        request.flush();
    }

    /**
     * Adds a upload file section to the request
     *
     * @param fieldName  name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public void addFilePart(String fieldName, File uploadFile) throws IOException {
        String fileName = uploadFile.getName();
        System.out.println("File Name: " + fileName);
        request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"" +
                fieldName + "\";filename=\"" +
                fileName + "\"" + this.crlf);
        request.writeBytes(this.crlf);

        byte[] bytes = Files.readAllBytes(uploadFile.toPath());
        request.write(bytes);
        System.out.println("End of addFilePart");
    }

    /**
     * Completes the request and receives response from the server.
     *
     * @return a list of Strings as response in case the server returned
     * status OK, otherwise an exception is thrown.
     * @throws IOException
     */
    public String finish() throws IOException {
        String response ="";

        request.writeBytes(this.crlf);
        request.writeBytes(this.twoHyphens + this.boundary +
                this.twoHyphens + this.crlf);

        request.flush();
        request.close();

        // checks server's status code first
        int status = httpConn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            InputStream responseStream = new
                    BufferedInputStream(httpConn.getInputStream());

            BufferedReader responseStreamReader =
                    new BufferedReader(new InputStreamReader(responseStream));

            String line = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();

            response = stringBuilder.toString();
            httpConn.disconnect();
        } else {
            throw new IOException("Server returned non-OK status: " + status);
        }

        return response;
    }

}
