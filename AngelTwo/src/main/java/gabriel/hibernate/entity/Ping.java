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

@Entity
@Table(name = "ping")
public class Ping implements Serializable {

	private static final long serialVersionUID = -856136193900710764L;
	private long id;	
	private double mLatitude;	
	private double mLongitude;
	private double snappedLatitude;
	private double snappedLongitude;
	private double lastKnownLocationAccuracy;
	private double batteryCharge;
	private Date packetCreatedTime;
	private Date packetReceivedTime;

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ping_packet_id")
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	@Column(name="lastKnownLatitude")
	public double getmLatitude() {
		return mLatitude;
	}

	public void setmLatitude(double mLatitude) {
		this.mLatitude = mLatitude;
	}
	
	@Column(name="lastKnownLongitude")
	public double getmLongitude() {
		return mLongitude;
	}

	public void setmLongitude(double mLongitude) {
		this.mLongitude = mLongitude;
	}
	
	@Column(name="lastKnownSnappedLatitude")
	public double getSnappedLatitude() {
		return snappedLatitude;
	}

	public void setSnappedLatitude(double snappedLatitude) {
		this.snappedLatitude = snappedLatitude;
	}

	@Column(name="lastKnownSnappedLongitude")
	public double getSnappedLongitude() {
		return snappedLongitude;
	}

	public void setSnappedLongitude(double snappedLongitude) {
		this.snappedLongitude = snappedLongitude;
	}
	
	@Column(name="lastKnownLocationAccuracy")
	public double getLastKnownLocationAccuracy() {
		return lastKnownLocationAccuracy;
	}

	public void setLastKnownLocationAccuracy(double lastKnownLocationAccuracy) {
		this.lastKnownLocationAccuracy = lastKnownLocationAccuracy;
	}
	
	@Column(name="battery_charge")
	public double getBatteryCharge() {
		return batteryCharge;
	}

	public void setBatteryCharge(double batteryCharge) {
		this.batteryCharge = batteryCharge;
	}

	//@Temporal(TemporalType.DATE)		//@Temporal: must be used with a java.util.Date field to specify the actual SQL type of the column
	@Column(name="packet_created_time")
	public Date getPacketCreatedTime() {
		return packetCreatedTime;
	}

	public void setPacketCreatedTime(Date packetCreatedTime) {
		this.packetCreatedTime = packetCreatedTime;
	}
	
	//@Temporal(TemporalType.DATE)		//@Temporal: must be used with a java.util.Date field to specify the actual SQL type of the column
	@Column(name="packet_received_time")
	public Date getPacketReceivedTime() {
		return packetReceivedTime;
	}

	public void setPacketReceivedTime(Date packetReceivedTime) {
		this.packetReceivedTime = packetReceivedTime;
	}
	
	
}
