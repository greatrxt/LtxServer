package gabriel.hibernate.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Trip definition - Trip is the journey undertaken by a Vehicle in a day
 * Ends when day ends or if vehicle-driver combination changes
 *
 * Rules - 
 * 
 * Fetch trip using vehicle-driver-date combination. If not found create new trip. End previous trip for Vehicle while creating new trip
 * While trip creation - same startLatitude, startLongitude, startTime, lastKnownLatitude, lastKnownLongitude. lastKnownTime 
 * Then keep updating lastKwownLatitude, lastKnowLongitude, lastKnownTime 
 */
@Entity
@Table(name = "trip")
public class Trip {

	long id;	
	private String tripId;	//Some UUID	
	private Date tripDate;		//http://stackoverflow.com/questions/20466354/hibernate-map-a-foreign-key-that-points-to-unique-key
	private Vehicle vehicle;
	private Driver driver;	
	private Date startTime;	
	private double startLatitude;
	private double startLongitude;	
	private Date lastKnownTime;	
	private double lastKnownLatitude;	
	private double lastKnownLongitude;
	private boolean tripOver;
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	@Column(name="trip_id")
	public String getTripId() {
		return tripId;
	}

	public void setTripId(String tripId) {
		this.tripId = tripId;
	}
	
	//@Temporal(TemporalType.DATE) //truncates the time part in the date	
	@Column(name="trip_date")
	public Date getTripDate() {
		return tripDate;
	}

	public void setTripDate(Date tripDate) {
		this.tripDate = tripDate;
	}	
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="unique_id", nullable = false, referencedColumnName = "unique_id")
	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="username", nullable = false, referencedColumnName = "username")
	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	@Column(name="start_time")
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	@Column(name="start_latitude")
	public double getStartLatitude() {
		return startLatitude;
	}

	public void setStartLatitude(double startLatitude) {
		this.startLatitude = startLatitude;
	}
	
	@Column(name="start_longitude")
	public double getStartLongitude() {
		return startLongitude;
	}

	public void setStartLongitude(double startLongitude) {
		this.startLongitude = startLongitude;
	}

	@Column(name="last_known_time")
	public Date getLastKnownTime() {
		return lastKnownTime;
	}
	
	public void setLastKnownTime(Date lastKnownTime) {
		this.lastKnownTime = lastKnownTime;
	}
	
	@Column(name="last_known_latitude")
	public double getLastKnownLatitude() {
		return lastKnownLatitude;
	}

	public void setLastKnownLatitude(double lastKnownLatitude) {
		this.lastKnownLatitude = lastKnownLatitude;
	}
	
	@Column(name="last_known_longitude")
	public double getLastKnownLongitude() {
		return lastKnownLongitude;
	}

	public void setLastKnownLongitude(double lastKnownLongitude) {
		this.lastKnownLongitude = lastKnownLongitude;
	}
	
	@Column(name="trip_over")
	public boolean isTripOver() {
		return tripOver;
	}

	public void setTripOver(boolean tripOver) {
		this.tripOver = tripOver;
	}
	
}
