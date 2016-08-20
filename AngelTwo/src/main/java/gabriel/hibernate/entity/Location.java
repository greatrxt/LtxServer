package gabriel.hibernate.entity;

import java.io.Serializable;
//Refer http://www.tutorialspoint.com/hibernate/hibernate_annotations.htm
//http://www.codejava.net/frameworks/hibernate/hibernate-one-to-one-mapping-with-foreign-key-annotations-example
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


//@Entity: is required for every model class.
//@Table: maps the class with the corresponding database table. If omitted, Hibernate will use the class name.
//@Column: maps the field with the corresponding table column. If omitted, Hibernate will infer the column name and type based on signatures of the getter/setter.
//@Id and @GeneratedValue: are used in conjunction for a field that maps to the primary key. The values for this field are auto generated.
//@Temporal: must be used with a java.util.Date field to specify the actual SQL type of the column.
//@OneToOne and @JoinColumn: are used together to specify a one-to-one association and the join column

@Entity
@Table(name = "location")
public class Location implements Serializable {
	
	private static final long serialVersionUID = -6668691617561812702L;
	private long id;	
	private double mLatitude;	
	private double mLongitude;	
	private double snappedLatitude;	
	private double snappedLongitude;	
	private double mAccuracy;	
	private double mSpeed;	
	private double mDistance;
	private double mAltitude;
	private Date mTime;		
	private double mBearing;
	private Date packetReceivedTime;
	private Vehicle vehicle;
	private double batteryCharge;
	private Driver driver;
	private int signalStrength;//1, 2, 3 or 4
	private String rawPacket;
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="location_packet_id")
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	@Column(name="mLatitude")
	public double getmLatitude() {
		return mLatitude;
	}

	public void setmLatitude(double mLatitude) {
		this.mLatitude = mLatitude;
	}
	
	
	@Column(name="mLongitude")
	public double getmLongitude() {
		return mLongitude;
	}

	public void setmLongitude(double mLongitude) {
		this.mLongitude = mLongitude;
	}
	
	@Column(name="snappedLatitude")
	public double getSnappedLatitude() {
		return snappedLatitude;
	}

	public void setSnappedLatitude(double snappedLatitude) {
		this.snappedLatitude = snappedLatitude;
	}
	
	
	@Column(name="snappedLongitude")
	public double getSnappedLongitude() {
		return snappedLongitude;
	}

	public void setSnappedLongitude(double snappedLongitude) {
		this.snappedLongitude = snappedLongitude;
	}
	
	@Column(name="mAccuracy")
	public double getmAccuracy() {
		return mAccuracy;
	}

	public void setmAccuracy(double mAccuracy) {
		this.mAccuracy = mAccuracy;
	}
	
	@Column(name="mSpeed")
	public double getmSpeed() {
		return mSpeed;
	}

	public void setmSpeed(double mSpeed) {
		this.mSpeed = mSpeed;
	}
	
	@Column(name="mDistance")
	public double getmDistance() {
		return mDistance;
	}

	public void setmDistance(double mDistance) {
		this.mDistance = mDistance;
	}
	
	@Column(name="mAltitude")
	public double getmAltitude() {
		return mAltitude;
	}

	public void setmAltitude(double mAltitude) {
		this.mAltitude = mAltitude;
	}

	//@Temporal(TemporalType.DATE)		//@Temporal: must be used with a java.util.Date field to specify the actual SQL type of the column
	@Column(name="mTime")
	public Date getmTime() {
		return mTime;
	}

	public void setmTime(Date mTime) {
		this.mTime = mTime;
	}
	
	@Column(name="mBearing")
	public double getmBearing() {
		return mBearing;
	}

	public void setmBearing(double mBearing) {
		this.mBearing = mBearing;
	}

	//@Temporal(TemporalType.DATE)		//@Temporal: must be used with a java.util.Date field to specify the actual SQL type of the column
	@Column(name="packet_received_time")
	public Date getPacketReceivedTime() {
		return packetReceivedTime;
	}

	public void setPacketReceivedTime(Date packetReceivedTime) {
		this.packetReceivedTime = packetReceivedTime;
	}
	
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name="vehicle_unique_id", nullable = false, referencedColumnName = "unique_id")
	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}
	
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name="username", nullable = false, referencedColumnName = "username")
	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}
	
	@Column(name="battery_charge")
	public double getBatteryCharge() {
		return batteryCharge;
	}

	public void setBatteryCharge(double batteryCharge) {
		this.batteryCharge = batteryCharge;
	}

	@Column(name="signal_strength") 
	public int getSignalStrength() {
		return signalStrength;
	}

	public void setSignalStrength(int signalStrength) {
		this.signalStrength = signalStrength;
	}
	
	@Column(name="raw_packet", columnDefinition="TEXT")
	public String getRawPacket() {
		return rawPacket;
	}

	public void setRawPacket(String rawPacket) {
		this.rawPacket = rawPacket;
	}

	
}
