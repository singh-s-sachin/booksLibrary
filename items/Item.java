package items;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream ;
import java.io.InputStreamReader;

import invoice.Invoice;
import token.Token;
public class Item {
public String addItem(String name,String rate,String description,String tax_id,String product_type) {
	String item_type="sales_and_purchases",fval="",response;
	Invoice ob=new Invoice();
	JSONObject val = new JSONObject();
	try {
		val.put("name",name);
		val.put("rate",rate);
		val.put("description",description);
		val.put("tax_id",tax_id);
		val.put("product_type",product_type);
		val.put("item_type",item_type);
		fval=val.toString();
	}catch (JSONException e) {
		e.printStackTrace();
	}
	response=ob.invokeAPI("https://books.zoho.com/api/v3/items?organization_id=",fval);
	return response;
}
public String getItems() {
	String endpoint="https://books.zoho.com/api/v3/items?organization_id=";
	Token token = new Token();
	endpoint+=token.getOrgId();
	String resp = invokeAPIviaGet(endpoint);
	return resp;
	
}
public String getItem(String item_id) {
	Token token = new Token();
	String endpoint="https://books.zoho.com/api/v3/items/";
	endpoint+=item_id+"?organization_id=" + token.getOrgId();
	String resp = "",str;
	StringBuffer sb = new StringBuffer();
	try {
		String oAuth="Zoho-oauthtoken "+token.getToken();
		final HttpClient httpClient = new HttpClient();
		GetMethod getMethod = new GetMethod(endpoint);
		getMethod.addRequestHeader("Authorization", oAuth);
		getMethod.addRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
		try {
			int httpResponse=httpClient.executeMethod(getMethod);
			System.out.println("status:"+httpResponse);
	    } catch (HttpException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		if (getMethod.getStatusCode() == 200) {
	        InputStream res = getMethod.getResponseBodyAsStream();
	        InputStreamReader isReader = new InputStreamReader(res);
	        BufferedReader reader = new BufferedReader(isReader);
	        while((str = reader.readLine())!= null){
	            sb.append(str);
	         }
	        resp=sb.toString();
	    } else {
	         resp="An error occured";
	    }
	} catch (IOException e) {
		System.out.println("Unable to reach endpoint");
		e.printStackTrace();
		resp="An error occured";
	}
	return resp;
}
public String addItem(String name,String rate,String description,String product_type) {
	String response=addItem(name,rate,description,"2110385000000069247",product_type);
	return response;
}
public String invokeAPIviaGet(String endpoint) {
	Token token = new Token();
	String resp="",str;
	StringBuffer sb = new StringBuffer();
	try {
		String oAuth="Zoho-oauthtoken "+token.getToken();
		final HttpClient httpClient = new HttpClient();
		GetMethod getMethod = new GetMethod(endpoint);
		getMethod.addRequestHeader("Authorization", oAuth);
		getMethod.addRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
		try {
			int httpResponse=httpClient.executeMethod(getMethod);
			System.out.println("status:"+httpResponse);
	    } catch (HttpException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		if (getMethod.getStatusCode() == 200) {
	        InputStream res = getMethod.getResponseBodyAsStream();
	        InputStreamReader isReader = new InputStreamReader(res);
	        BufferedReader reader = new BufferedReader(isReader);
	        while((str = reader.readLine())!= null){
	            sb.append(str);
	         }
	        resp=sb.toString();
	    } else {
	         resp="An error occured";
	    }
	} catch (IOException e) {
		System.out.println("Unable to reach endpoint");
		e.printStackTrace();
		resp="An error occured";
	}
	return resp;
}
}


//default:"2110385000000069247"