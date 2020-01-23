package token;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.TimeZone;
import java.sql.*;

public class Token {
	String cid,csc,rt,ru;
	public String getToken() {
		Calendar ob = Calendar.getInstance(TimeZone.getTimeZone("Asia/Calcutta")); 
		try{  
			Class.forName("com.mysql.jdbc.Driver");  
			Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/credentials","root","");  
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select value,time from token where param =\"auth\"");
			rs.next();
			Timestamp last=rs.getTimestamp("time"); //No I18N
			Timestamp now = new Timestamp(ob.getTimeInMillis());
			if(((now.getTime()-last.getTime())/1000)<3600) {
			return(rs.getString("value"));
			}
			else
			{
				rs=stmt.executeQuery("select value from token where param =\"client_id\"");
				rs.next();
				cid=rs.getString("value");
				rs=stmt.executeQuery("select value from token where param =\"client_sec\"");
				rs.next();
				csc=rs.getString("value");
				rs=stmt.executeQuery("select value from token where param =\"uri\"");
				rs.next();
				ru=rs.getString("value");
				rs=stmt.executeQuery("select value from token where param =\"refresh\"");
				rs.next();
				rt=rs.getString("value");
				String resp = "",str;
				StringBuffer sb = new StringBuffer();
				try {
					String endpoint="https://accounts.zoho.com/oauth/v2/token";
					final HttpClient httpClient = new HttpClient();
					PostMethod postMethod = new PostMethod(endpoint);
					postMethod.addParameter("client_id",cid);
					postMethod.addParameter("client_secret", csc);
					postMethod.addParameter("refresh_token",rt);
					postMethod.addParameter("redirect_uri",ru);
					postMethod.addParameter("grant_type","refresh_token");
					try {
						int httpResponse=httpClient.executeMethod(postMethod);
						System.out.println("status:"+httpResponse);
						//System.out.println("GET Response Status:: "+ httpResponse.getStatusLine().getStatusCode());
				    } catch (HttpException e) {
				        e.printStackTrace();
				    } catch (IOException e) {
				        e.printStackTrace();
				    }
					if (postMethod.getStatusCode() == 200) {
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
				JSONObject jsonObject = new JSONObject(resp);
				String k="Update token set value=\""+jsonObject.getString("access_token")+"\",time=\""+now+"\" where param=\"auth\"";    //No I18N
				stmt.executeUpdate(k);
				return jsonObject.getString("access_token");
			}
	}
		catch(java.sql.SQLException | JSONException | ClassNotFoundException e) {
			return("\"message\":\"Setup before using this function\""); //No I18N
		}
	}
	public String setup(String client_id,String client_secret,String refresh_token,String redirect_uri, String org_id) {
		try{  
			Class.forName("com.mysql.jdbc.Driver");  
			Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/credentials","root","");  
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select value from token where param=\"org_id\"");
			rs.next();
			String g="\"message\":\"User already exists, remove it to add new\",\"org_id\":\""+rs.getString("value")+"\"";
			return(g);
		}
		catch(java.sql.SQLException | ClassNotFoundException e) {
			try {
			Class.forName("com.mysql.jdbc.Driver");  
			Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/credentials","root","");  
			Statement stmt=con.createStatement();
			resetSetup();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR, -2);
			Timestamp onehourback = new Timestamp(cal.getTimeInMillis());
			stmt.executeUpdate("INSERT INTO TOKEN(param,value,time)values(\"auth\",\"khsbcfiafiagifnqemxshfcnex\",\""+onehourback+"\")");
			stmt.executeUpdate("INSERT INTO TOKEN(param,value,time)values(\"client_id\",\""+client_id+"\",\""+onehourback+"\")");
			stmt.executeUpdate("INSERT INTO TOKEN(param,value,time)values(\"client_sec\",\""+client_secret+"\",\""+onehourback+"\")");
			stmt.executeUpdate("INSERT INTO TOKEN(param,value,time)values(\"refresh\",\""+refresh_token+"\",\""+onehourback+"\")");
			stmt.executeUpdate("INSERT INTO TOKEN(param,value,time)values(\"uri\",\""+redirect_uri+"\",\""+onehourback+"\")");
			stmt.executeUpdate("INSERT INTO TOKEN(param,value,time)values(\"org_id\",\""+org_id+"\",\""+onehourback+"\")");
			String t=getToken();
			//System.out.println(t);
			return t;
			}
			catch(Exception e1) {
				return("An error occured");
			}

		}
	}
		public boolean resetSetup() {
			try {
				Class.forName("com.mysql.jdbc.Driver");  
				Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/credentials","root","");  
				Statement stmt=con.createStatement();
				stmt.executeUpdate("TRUNCATE TABLE token");
				return true;
			} catch(Exception e) {
				System.out.println(e);
				return(false);
			}
		}
		public String getOrgId() {
			try {
				Class.forName("com.mysql.jdbc.Driver");  
				Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/credentials","root","");  
				Statement stmt=con.createStatement();
				ResultSet rs=stmt.executeQuery("select value from token where param=\"org_id\"");
				rs.next();
				return rs.getString("value");
			}
			catch(java.sql.SQLException | ClassNotFoundException e) {
				return("Try setting up");
			}
		}
		public boolean preSetup() {
			try {
				Class.forName("com.mysql.jdbc.Driver");  
				Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/credentials","root","");  
				Statement stmt=con.createStatement();
				stmt.executeUpdate("CREATE TABLE token(param varchar(11) NOT NULL,value varchar(150) NOT NULL,time timestamp)");
				return true;
			} catch(Exception e) {
				System.out.println(e);
				return(false);
			}
		}

}
