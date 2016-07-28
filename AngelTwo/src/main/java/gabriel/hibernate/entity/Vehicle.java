package gabriel.hibernate.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "vehicle")
public class Vehicle implements Serializable  {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	int id;
	
	@Column(name="uniqueId")
	String uniqueId;
	
	@Column(name="lastKnownLatitude")
	double mLatitude;

	@Column(name="lastKnownLongitude")
	double mLongitude;
	
	@Column(name="lastKnownSnappedLatitude")
	double snappedLatitude;
	
	@Column(name="lastKnownSnappedLongitude")
	double snappedLongitude;	
	
	@Column(name="packet_created_time")
	Date packetCreatedTime;
	
	@Column(name="packet_received_time")
	Date packetReceivedTime;

}
