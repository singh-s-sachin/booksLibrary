package customer;

import token.Token;

import org.json.JSONException;
import org.json.JSONObject;

import invoice.Invoice;
import items.Item;

public class Customers {
	public String getCustomers() {
		Token token = new Token();
		Item ob=new Item();
		String endpoint="https://books.zoho.com/api/v3/contacts?organization_id="+token.getOrgId();
		String resp=ob.invokeAPIviaGet(endpoint);
		return resp;
	}
	public String addCustomers(String name) {
		Invoice ob=new Invoice();
		String body="";
		String endpoint="https://books.zoho.com/api/v3/contacts?organization_id=";
		JSONObject val = new JSONObject();
		try {
			val.put("contact_name",name);
			val.put("contact_type","customer");
			body=val.toString();
		}catch (JSONException e) {
			e.printStackTrace();
		}
		String resp=ob.invokeAPI(endpoint, body);
		return resp;
	}
}
