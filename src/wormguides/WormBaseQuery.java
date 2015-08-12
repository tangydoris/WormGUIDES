package wormguides;

import java.io.BufferedReader;

public class WormBaseQuery {
	
	private String result;
	
	public  BufferedReader openUrl(String url){
		//result = resultBox;

		// Try to connect using Apache HttpClient Library
		try{
			httpclient = new DefaultHttpClient();
			request=new HttpGet(url);
			response = httpclient.execute(request);
		}
		catch (Exception e){
			//Code to handle exception
		}
		String line = "";
		String page = "";

		// response code
		BufferedReader rd = null;
		try{
			rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			//			while ((line = rd.readLine()) != null) {
			//				page = page + line;
			//			}
		}    
		catch (Exception e){
			//Code to handle exception
		}
		return rd;
	}
}
