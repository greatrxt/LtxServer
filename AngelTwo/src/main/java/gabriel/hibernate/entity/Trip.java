package gabriel.hibernate.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	long id;
	
	@Column(name="trip_id")
	private String tripId;	//Some UUID
	
	@Column(name="trip_date")
	private Date tripDate;
	
	@Column(name="vehicle")
	private Vehicle vehicle;
	
	@Column(name="driver")
	private Driver driver;
	
	@Column(name="start_time")
	private Date startTime;
	
	@Column(name="start_latitude")
	private double startLatitude;
	
	@Column(name="start_longitude")
	private double startLongitude;
	
	@Column(name="last_known_time")
	private Date lastKnownTime;
	
	@Column(name="last_known_latitude")
	private double lastKnownLatitude;
	
	@Column(name="last_known_longitude")
	private double lastKnownLongitude;

	@Column(name="trip_over")
	private boolean tripOver;
	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTripId() {
		return tripId;
	}

	public void setTripId(String tripId) {
		this.tripId = tripId;
	}

	public Date getTripDate() {
		return tripDate;
	}

	public void setTripDate(Date tripDate) {
		this.tripDate = tripDate;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	@Temporal(TemporalType.DATE)		//@Temporal: must be used with a java.util.Date field to specify the actual SQL type of the column
	@Column(name="start_time")
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public double getStartLatitude() {
		return startLatitude;
	}

	public void setStartLatitude(double startLatitude) {
		this.startLatitude = startLatitude;
	}

	public double getStartLongitude() {
		return startLongitude;
	}

	public void setStartLongitude(double startLongitude) {
		this.startLongitude = startLongitude;
	}

	public Date getLastKnownTime() {
		return lastKnownTime;
	}

	@Temporal(TemporalType.DATE)		//@Temporal: must be used with a java.util.Date field to specify the actual SQL type of the column
	@Column(name="last_known_time")
	public void setLastKnownTime(Date lastKnownTime) {
		this.lastKnownTime = lastKnownTime;
	}

	public double getLastKnownLatitude() {
		return lastKnownLatitude;
	}

	public void setLastKnownLatitude(double lastKnownLatitude) {
		this.lastKnownLatitude = lastKnownLatitude;
	}

	public double getLastKnownLongitude() {
		return lastKnownLongitude;
	}

	public void setLastKnownLongitude(double lastKnownLongitude) {
		this.lastKnownLongitude = lastKnownLongitude;
	}

	public boolean isTripOver() {
		return tripOver;
	}

	public void setTripOver(boolean tripOver) {
		this.tripOver = tripOver;
	}
	
}
