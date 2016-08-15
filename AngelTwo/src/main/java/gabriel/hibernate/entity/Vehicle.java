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
@Table(name = "vehicle", uniqueConstraints = {@UniqueConstraint(columnNames = { "unique_id" })})
public class Vehicle implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3347253417572977303L;
	private long id;
	private String image;
	private String uniqueId;
	private String registrationNumber; 
	private Date vehicleCreationTime;

	@Temporal(TemporalType.DATE)		//@Temporal: must be used with a java.util.Date field to specify the actual SQL type of the column
	@Column(name="vehicle_creation_time")
	public Date getVehicleCreationTime() {
		return vehicleCreationTime;
	}

	public void setVehicleCreationTime(Date vehicleCreationTime) {
		this.vehicleCreationTime = vehicleCreationTime;
	}
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	@Column(name="image", columnDefinition="TEXT")
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	@Column(name="unique_id")
	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	
	@Column(name="registration_number")
	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}
}
