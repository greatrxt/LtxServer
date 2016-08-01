package gabriel.hibernate.dao;

import gabriel.hibernate.entity.Ping;
import gabriel.map.Osrm;
import gabriel.hibernate.entity.Ping;
import gabriel.utilities.HibernateUtil;
import gabriel.utilities.SystemUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.json.JSONArray;
import org.json.JSONObject;

public class PingDao {

	/**
	 * Stores ping packets in DB
	 * @param mLatitude
	 * @param mLongitude
	 * @param lastKnownLocationAccuracy
	 * @param batteryCharge
	 * @param packetCreatedTime
	 * @return
	 */
	public static boolean storePingPacket(double mLatitude, double mLongitude, double lastKnownLocationAccuracy, double batteryCharge, long packetCreatedTime){
		Session session = HibernateUtil.getSessionAnnotationFactory().openSession();
		
		try {
			session.beginTransaction();
			Ping ping = new Ping();
			ping.setmLatitude(mLatitude);
			ping.setmLongitude(mLongitude);
			ping.setSnappedLatitude(Osrm.STATUS_SNAP_PENDING);
			ping.setSnappedLongitude(Osrm.STATUS_SNAP_PENDING);
			ping.setBatteryCharge(batteryCharge);
			ping.setLastKnownLocationAccuracy(lastKnownLocationAccuracy);
			SimpleDateFormat parserSDF=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date packetCreatedDate = null;
			try {
				packetCreatedDate = parserSDF.parse(SystemUtils.convertMsSinceEpochToDate(packetCreatedTime));
				ping.setPacketCreatedTime(packetCreatedDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			ping.setPacketReceivedTime(new Date());
			session.save(ping);
			session.getTransaction().commit();
			return true;
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			session.close();
		}
		return false;
	}

	/**
	 * Retrieves ping records from DB
	 * @param numOfRecords
	 * @return
	 */
	public static JSONArray getPingJson(int numOfRecords){
		
		JSONArray pingArray = new JSONArray();
		
		Session session = HibernateUtil.getSessionAnnotationFactory().openSession();
		try {
			session.beginTransaction();
			Criteria count = session.createCriteria(Ping.class);
	        count.setProjection(Projections.rowCount());
	        long total = (long) count.uniqueResult();
	        if(total < numOfRecords){
	        	numOfRecords = (int) total;
	        }
	        Criteria criteria = session.createCriteria(Ping.class);
	        criteria.setFirstResult((int) (total - numOfRecords));
	        criteria.setMaxResults(numOfRecords);

	        List<Ping> list = criteria.list();
	        Iterator<Ping> pings = list.iterator();
	        while(pings.hasNext()){
	        	Ping ping = pings.next();
	        	JSONObject pingJson = new JSONObject();
	        	pingJson.put("mLatitude", ping.getmLatitude());
	        	pingJson.put("mLongitude", ping.getmLongitude());
	        	pingJson.put("snappedLatitude", ping.getSnappedLatitude());
	        	pingJson.put("snappedLongitude", ping.getSnappedLongitude());
	        	pingJson.put("lastKnownLocationAccuracy", ping.getLastKnownLocationAccuracy());
	        	pingJson.put("batteryCharge", ping.getBatteryCharge());
	        	pingJson.put("packetCreatedTime", ping.getPacketCreatedTime());
	        	
	        	pingArray.put(pingJson);
	        }
	        return pingArray;
		} finally {
			session.close();
		}
	}
}
