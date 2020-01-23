import items.Item;
import customer.Customers;
class testItem{
	public static void main(String[] args) {
		Item ob= new Item();
		Customers cust=new Customers();
		//String response=ob.addItem("Floppy d", "1.5", "1 TB hard disk drive with encryption feature", "goods");
		String response=cust.getCustomers();
		System.out.println(response);
	}
}