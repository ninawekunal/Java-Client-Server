package finalproject.entities;

public class Person implements java.io.Serializable {

	private static final long serialVersionUID = 4190276780070819093L;

	// this is a person object that you will construct with data from the DB
	// table. The "sent" column is unnecessary. It's just a person with
	// a first name, last name, age, city, and ID.
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private String firstName, lastName, age, city, id;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Person(String firstName, String lastName, String age, String city, String id) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.age = age;
		this.city = city;
		this.id = id;
	}
	
	public Person() {
		this.firstName = "";
		this.lastName = "";
		this.age = "";
		this.city = "";
		this.id = "";
	}
	
	public String toString() {
		String info = "[";
		info += id + " - ";
		info += firstName + " - ";
		info += lastName + " - ";
		info += age + " - ";
		info += city + "]";
		
		
		return info;
	}
	
	
}
