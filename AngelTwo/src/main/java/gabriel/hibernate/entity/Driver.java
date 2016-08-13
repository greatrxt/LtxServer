package gabriel.hibernate.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "driver", uniqueConstraints = {@UniqueConstraint(columnNames = { "username" })})
public class Driver implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4399622404425985L;
	private long id;	
	private String name;	
	private String username;	
	private String password;	
	private String contactNumber;	
	private String image; //image as base64 String	
	private Date dateOfJoining;	
	private Date recordCreationTime;

	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	@Column(name="name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name="username", unique=true)
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	@Column(name="password")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@Column(name="contact_number")
	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	
	@Column(name="image", columnDefinition="TEXT")
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	@Temporal(TemporalType.DATE)		//@Temporal: must be used with a java.util.Date field to specify the actual SQL type of the column
	@Column(name="date_of_joining")
	public Date getDateOfJoining() {
		return dateOfJoining;
	}

	public void setDateOfJoining(Date dateOfJoining) {
		this.dateOfJoining = dateOfJoining;
	}

	@Temporal(TemporalType.DATE)		//@Temporal: must be used with a java.util.Date field to specify the actual SQL type of the column
	@Column(name="record_creation_time")
	public Date getRecordCreationTime() {
		return recordCreationTime;
	}

	public void setRecordCreationTime(Date recordCreationTime) {
		this.recordCreationTime = recordCreationTime;
	}
	
}
