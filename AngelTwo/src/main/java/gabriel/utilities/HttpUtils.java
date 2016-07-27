package gabriel.utilities;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by Rakshit on 9/1/2015.
 */

public class HttpUtils {


    /**
     * Taken from http://androidexample.com/Upload_File_To_Server_-_Android_Example/index.php?view=article_discription&aid=83&aaid=106
     * Method to upload ZIP files to server
     *
     * @param fileName
     * @param sourceFileUri
     * @param upLoadServerUri
     * @return
     * @throws IOException
     */
    public static String uploadFile(String fileName, String sourceFileUri, String upLoadServerUri) throws IOException {

        //String fileName = sourceFileUri;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        try {

            // open a URL connection to the Servlet
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(upLoadServerUri);

            // Open a HTTP  connection to  the URL
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            //conn.setRequestProperty("Content-Type", "multipart/form-data");
            conn.setRequestProperty("uploaded_file", fileName);
            //conn.setRequestProperty("Accept-Encoding", "gzip");

            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=uploaded_file;filename="
                            + fileName + lineEnd);

            dos.writeBytes(lineEnd);

            // create a buffer of  maximum size
            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            long bytesUploaded = 0;
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesUploaded+= bufferSize;
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            // send multipart form data necessary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            int serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();

            System.out.println("uploadFile"+ "HTTP Response is : "
                    + serverResponseMessage + ": " + serverResponseCode);

            //close the streams //
            fileInputStream.close();
            dos.flush();
            dos.close();

            if(serverResponseCode == 200 || serverResponseCode == 201){
                return "Success";
            } else {
                return conn.getResponseMessage();
            }

        }  catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * Fetch string from URL using GET
     * @param url
     * @return
     */
    public static String doGetToFetchString(String url) {
        String result = "";
        HttpURLConnection urlConnection = null;
        // make HTTP request
        try {

            URL getUrl = new URL(url);
            urlConnection = (HttpURLConnection) getUrl.openConnection();
            //urlConnection.setRequestProperty("Accept-Encoding", "gzip");

            urlConnection.setDoOutput(false);

            InputStream is;// = new BufferedInputStream(urlConnection.getInputStream());
            int status = urlConnection.getResponseCode();
            if(status >= 400)
                is = urlConnection.getErrorStream();
            else
                is = urlConnection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();

            int statusCode = urlConnection.getResponseCode();

//            if(statusCode == 200 || statusCode ==201){
                return result;
/*            } else {
                String message = urlConnection.getResponseMessage();
                return message;
            }*/

        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            if(urlConnection!=null){
                urlConnection.disconnect();
            }
        }
    }

    /**
     * Send JSON via POST
     * @param url
     * @param json
     * @return
     */
    public static String doPostToSendJson(String url, String json){
        String result = "";
        HttpURLConnection urlConnection = null;
        // make HTTP request
        try {
            URL getUrl = new URL(url);
            urlConnection = (HttpURLConnection) getUrl.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            //urlConnection.setRequestProperty("Accept-Encoding", "gzip");
            urlConnection.setRequestProperty("Content-type", "application/json");

            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            writer.write(json);
            writer.flush();

            InputStream is;// = new BufferedInputStream(urlConnection.getInputStream());
            int status = urlConnection.getResponseCode();
            if(status >= 400)
                is = urlConnection.getErrorStream();
            else
                is = urlConnection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();

            int statusCode = urlConnection.getResponseCode();

            //if(statusCode==201 || statusCode==202){
                return result;
/*            } else {
                String message = urlConnection.getResponseMessage();
                return message;
            }*/

        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            if(urlConnection!=null){
                urlConnection.disconnect();
            }
        }
    }
    /**
     * Fetch string from URL using POST
     * @param url
     * @param params
     * @return
     */
    public static String doPostToFetchKeycloakToken(String url, Map<String, Object> params) {
        String result = "";
        HttpURLConnection urlConnection = null;
        // make HTTP request
        try {
            URL getUrl = new URL(url);
            urlConnection = (HttpURLConnection) getUrl.openConnection();
            urlConnection.setDoOutput(false);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String,Object> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            urlConnection.getOutputStream().write(postDataBytes);
            urlConnection.getOutputStream().flush();

            InputStream is;// = new BufferedInputStream(urlConnection.getInputStream());
            int status = urlConnection.getResponseCode();
            if(status >= 400)
                is = urlConnection.getErrorStream();
            else
                is = urlConnection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();

            //int statusCode = urlConnection.getResponseCode();

            return result;
/*            if(statusCode== 200 || statusCode==201){
                return result;
            } else {
                String message = urlConnection.getResponseMessage();
                if(!TextUtils.isEmpty(SystemUtils.attemptFetchingErrorMessage(result))){
                    message+= " - "+SystemUtils.attemptFetchingErrorMessage(result);
                }
                return message;
            }*/

        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            if(urlConnection!=null){
                urlConnection.disconnect();
            }
        }
    }

}
