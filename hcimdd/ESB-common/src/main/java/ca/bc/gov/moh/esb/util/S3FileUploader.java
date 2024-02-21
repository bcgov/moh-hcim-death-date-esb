package ca.bc.gov.moh.esb.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Uploads files to an AWS S3 bucket
 *
 * @author CGI Information Management Consultants Inc.
 */
public class S3FileUploader {

    private final String apiUrl;
    private final String apiKey;

    public S3FileUploader(String apiUrl, String apiKey) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    /**
     * Uploads the provided message body to the S3 bucket as defined in the constructor
     *
     * @param body - the contents of the file to be uploaded
     * @param fileName - the name of the file to be uploaded
     */
    public void uploadFile(String body, String fileName) {

        // Get the pre-signed URL to upload the file to S3
        if (apiUrl != null) {
            try {
                // Put together the API endpoint to request a pre-signed URL
                URL url = URI.create(apiUrl + "?key=" + URLEncoder.encode(fileName, "UTF-8")).toURL();

                // Initialize the connection
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setRequestMethod("GET");

                // Add the API key to the request
                if (apiKey != null && !apiKey.isBlank()) {
                    httpConnection.setRequestProperty("x-api-key", apiKey);
                }

                // Get the response which contains the presigned URL
                InputStream content = (InputStream) httpConnection.getContent();
                StringBuilder response = new StringBuilder();
                int nextChar = content.read();

                // Read the pre-signed URL from the response stream
                while (nextChar >= 0) {
                    // It's wrapped in quotes by default, remove them
                    if ((char) nextChar != '"') {
                        response.append((char) nextChar);
                    }

                    nextChar = content.read();
                }

                // Convert to a URL object
                URL uploadUrl = URI.create(response.toString()).toURL();

                // Set up the request to the S3 bucket over the pre-signed URL
                HttpURLConnection uploadConnection = (HttpURLConnection) uploadUrl.openConnection();
                uploadConnection.setRequestMethod("PUT");
                uploadConnection.setDoOutput(true);

                // Add the file data
                try (DataOutputStream requestStream = new DataOutputStream(uploadConnection.getOutputStream())) {
                    requestStream.writeBytes(body);
                    requestStream.flush();
                }

                // Upload the file and get the response
                int responseCode = uploadConnection.getResponseCode();

                // Check the response code for an error response
                if (responseCode != 200) {
                    Logger.getLogger(S3FileUploader.class.getName()).log(
                            Level.SEVERE,
                            "Response code is {0} from S3 PUT request: {1}",
                            new Object[]{responseCode, uploadConnection.getResponseMessage()}
                    );
                }
            } catch (IOException ex) {
                Logger.getLogger(S3FileUploader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
