import java.io.Serializable;

public class Order implements Serializable{
    private static final long serialVersionUid = 01L;

    private String custName;
    private String custAddress;
    private String custEmail;
    private int itemNum;
    private int quantity;

    public Order(String name, String address, String email, int itemNum, int quantity, long serialVersionUid){
        this.custName = name;
        this.custAddress = address;
        this.custEmail = email;
        this.itemNum = itemNum;
        this.quantity = quantity;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCustAddress() {
        return custAddress;
    }

    public void setCustAddress(String custAddress) {
        this.custAddress = custAddress;
    }

    public String getCustEmail() {
        return custEmail;
    }

    public void setCustEmail(String custEmail) {
        this.custEmail = custEmail;
    }

    public int getItemNum() {
        return itemNum;
    }

    public void setItemNum(int itemNum) {
        this.itemNum = itemNum;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getAll(){
        return "Order Properties:\n" + "Cust name: " + this.custName + "\nCust Address: " + this.custAddress + "\nCust email: "
                + this.custEmail + "\n Item Id: " + this.itemNum + "\nQuantity: " + this.quantity;
    }
}