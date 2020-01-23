package invoice;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import token.Token;
public class Invoice{
	public Items items[]=new Items[10];
	public ArrayList<Items> item = new ArrayList<Items>();
	int m=0;
	public String generateInvoice(long cust_id){
		JSONObject val = new JSONObject();
		String fval="",response;
		String a[],t;
		a=new String[]{};
		try {
			val.put("customer_id", cust_id);
			val.put("contact_persons", a);
			val.put("tax_treatment", "vat_registered");
			val.put("template_id", "");
			val.put("payment_terms",15);
			val.put("payment_terms_label","Net 15");
			val.put("discount",0);
			val.put("is_discount_before_tax",true);
			val.put("discount_type","item_level");
			val.put("exchange_rate",1);
			Gson g = new Gson();
			t=g.toJson(item);
			val.put("line_items",t);
			val.put("allow_partial_payments", false);
			val.put("notes","have a great day");
			val.put("terms","Terms & Conditions apply");
			val.put("shipping_charge",0);
			val.put("adjustment",0);
			fval+=val.toString();
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		response=invokeAPI("https://books.zoho.com/api/v3/invoices?organization_id=",fval);
		return(response);
	}
	public String invokeAPI(String endpoint,String body) {
		Token token = new Token();
		endpoint+=token.getOrgId();
		String resp = "",str;
		StringBuffer sb = new StringBuffer();
		try {
			String oAuth="Zoho-oauthtoken "+token.getToken();
			final HttpClient httpClient = new HttpClient();
			PostMethod postMethod = new PostMethod(endpoint);
			if(body!=null)
			postMethod.addParameter("JSONString",body);
			postMethod.addRequestHeader("Authorization", oAuth);
			postMethod.addRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
			try {
				int httpResponse=httpClient.executeMethod(postMethod);
				System.out.println("status:"+httpResponse);
		    } catch (HttpException e) {
		        e.printStackTrace();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
			if (postMethod.getStatusCode() != 400) {
		        InputStream res = postMethod.getResponseBodyAsStream();
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
	public void addItems(String itemid,double quantity) {
		items[m]=new Items();
		items[m].item_id=itemid;
		items[m].quantity=quantity;
		item.add(items[m]);
		m++;
	}
	public String getInvoice(String id) {
		String endpoint="https://books.zoho.com/api/v3/invoices/",resp="";
		Token token = new Token();
		endpoint+=id+"?organization_id="+token.getOrgId()+"&accept=pdf";
		String oAuth="Zoho-oauthtoken "+token.getToken();
		final HttpClient httpClient = new HttpClient();
		GetMethod getMethod = new GetMethod(endpoint);
		getMethod.addRequestHeader("Authorization", oAuth);
		getMethod.addRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
		try {
			int httpResponse=httpClient.executeMethod(getMethod);
		} catch (HttpException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		if (getMethod.getStatusCode() != 400) {
		    resp="Downloading";
		    InputStream res;
			try {
				res = getMethod.getResponseBodyAsStream();
				byte[] buffer = new byte[res.available()];
				res.read(buffer);
				File targetFile = new File("/Users/sachin-pt3366/Desktop/inv.pdf");
				FileOutputStream outStream = new FileOutputStream(targetFile);
				int read;
	            byte[] bytes = new byte[1024];
	            while ((read = res.read(bytes)) != -1) {
	                outStream.write(bytes, 0, read);
	            }
	            resp="/Users/sachin-pt3366/Desktop/inv.pdf";
			} catch (IOException e) {
				resp="error";
				e.printStackTrace();
			}
		} else {
		     resp="An error occured";
		}
		return resp;
	}

}
