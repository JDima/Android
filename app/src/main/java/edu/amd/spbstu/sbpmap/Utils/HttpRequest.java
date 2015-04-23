package edu.amd.spbstu.sbpmap.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class HttpRequest {

	public static String SEND(HttpUriRequest httpUriRequest) {
		String replyString = "";
		
		HttpClient httpclient = new DefaultHttpClient();

		try {
			
			HttpResponse response = httpclient.execute(httpUriRequest);
		    InputStream is = response.getEntity().getContent();
		    BufferedInputStream bis = new BufferedInputStream(is);
		    ByteArrayBuffer baf = new ByteArrayBuffer(20);
		    int current;
		    while ((current = bis.read()) != -1) {
		    	baf.append((byte) current);
		    }
		    replyString = new String(baf.toByteArray());
		 } catch (Exception e) {
		    	e.printStackTrace();
		 }
		 return replyString.trim();
	}

    public static String gerSourcePage(String url) {
        URL site;
        StringBuilder sb = new StringBuilder();
        try {
            site = new URL(url);

            URLConnection yc = site.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    yc.getInputStream(), "UTF-8"));
            String inputLine;

            while ((inputLine = in.readLine()) != null)
                sb.append(inputLine);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

}
