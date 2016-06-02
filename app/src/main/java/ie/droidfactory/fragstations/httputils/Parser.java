package ie.droidfactory.fragstations.httputils;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ie.droidfactory.fragstations.model.Station;
import ie.droidfactory.fragstations.model.StationDetails;
/**
 * Created by kudlaty on 02/06/2016.
 */
public class Parser {
	
	private final static String TAG = Parser.class.getSimpleName();
	private final static String ARRAY_STATION_DATA = "ArrayOfObjStationData", OBJECT_STATION_DATA="objStationData",
								ARRAY_TRAIN_POSITION="ArrayOfObjTrainPositions", OBJECT_TRAIN_POSITION="objTrainPositions",
								ARRAY_STATIONS="ArrayOfObjStation", OBJECT_STATION="objStation";
	//Get All Stations with Type 
	private final static String STATION_DESC="StationDesc",// STATION_CODE="StaionCode", 
								STATION_ID="StationId", STATION_ALIAS="StationAlias",
								STATION_LATITUDE="StationLatitude", STATION_LONGITUDE="StationLongitude";
	//Get Station Data by StationCode with number of minutes usage
	private final static String SERVER_TIME="Servertime",TRAIN_CODE="Traincode",STATION_FULL_NAME="Stationfullname", STATION_CODE="StationCode",
							QUERY_TIME="Querytime",TRAIN_DATE="Traindate", ORIGIN="Origin", DESTINATION="Destination", ORIGIN_TIME="Origintime",
							DESTINATION_TIME="Destinationtime", TRAIN_STATUS="Status",LAST_LOCATION="Lastlocation",DUE_IN="Duein",LATE="Late",
							EXP_ARRIVAL="Exparrival", EXP_DEPART="Expdepart", SCHEDULE_ARRIVAL="Scharrival", SCHEDULE_DEPART="Schdepart",
							DIRECTION="Direction", TRAIN_TYPE="Traintype", LOCATION_TYPE="Locationtype", STATION_CODE_DETAILS="Stationcode";
	//Get Current Trains with Type
	private final static String TRAIN_LATITUDE="TrainLatitude", TRAIN_LONGITUDE="TrainLongitude", PUBLIC_MESSAGE="PublicMessage";
	
	public final static String OBJECT_ERROR="error", ERR_CODE="ErrorCode", ERR_MESSAGE="ErrorMessage";
	private final static String errNoStations="no data from ire rail server";

	
	public static ArrayList<Station> parseAllStations_old(String myXml){
		ArrayList<Station> stations  = new ArrayList<Station>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(myXml.getBytes()));
			doc.getDocumentElement().normalize();
			NodeList list = doc.getElementsByTagName(OBJECT_STATION);
			
			if(list.getLength()>0){
				for(int i=0;i<list.getLength();i++){
					Node node = list.item(i);
					if(node.getNodeType()==Node.ELEMENT_NODE){
						Element e = (Element) node;
						String id = e.getElementsByTagName(STATION_ID).item(0).getTextContent();
						String name = e.getElementsByTagName(STATION_DESC).item(0).getTextContent();
						String alias="";
						try{
							alias = e.getElementsByTagName(STATION_ALIAS).item(0).getTextContent();
						}catch(Exception e1){}//return default alias
						double lat = Double.parseDouble(e.getElementsByTagName(STATION_LATITUDE).item(0).getTextContent());
						double lon = Double.parseDouble(e.getElementsByTagName(STATION_LONGITUDE).item(0).getTextContent());
						String code = e.getElementsByTagName(STATION_CODE).item(0).getTextContent();
						String type="-";
						try{
							type  = e.getElementsByTagName(LOCATION_TYPE).item(0).getTextContent();
						}catch(Exception e2){}//return default station type
						
						stations.add(Station.makeStation(name, code, id, alias, lat, lon, type));
					}
				}
			}else{
				stations.add(Station.makeStation(null,errNoStations));
			}
			
		} catch (SAXException | IOException | ParserConfigurationException e) {
			stations.add(Station.makeStation(null,e.getMessage()));
			e.printStackTrace();
		}
		 
		return stations;
	}
	
	public static HashMap<String, Station> parseAllStationsMap(String myXml){
		HashMap<String, Station> stations  = new HashMap<>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(myXml.getBytes()));
			doc.getDocumentElement().normalize();
			NodeList list = doc.getElementsByTagName(OBJECT_STATION);
			
			if(list.getLength()>0){
				for(int i=0;i<list.getLength();i++){
					Node node = list.item(i);
					if(node.getNodeType()==Node.ELEMENT_NODE){
						Element e = (Element) node;
						String id = e.getElementsByTagName(STATION_ID).item(0).getTextContent();
						String name = e.getElementsByTagName(STATION_DESC).item(0).getTextContent();
						String alias="";
						try{
							alias = e.getElementsByTagName(STATION_ALIAS).item(0).getTextContent();
						}catch(Exception e1){}//return default alias
						double lat = Double.parseDouble(e.getElementsByTagName(STATION_LATITUDE).item(0).getTextContent());
						double lon = Double.parseDouble(e.getElementsByTagName(STATION_LONGITUDE).item(0).getTextContent());
						String code = e.getElementsByTagName(STATION_CODE).item(0).getTextContent();
						String type="-";
						try{
							type  = e.getElementsByTagName(LOCATION_TYPE).item(0).getTextContent();
						}catch(Exception e2){}//return default station type
						
						stations.put(code, Station.makeStation(name, code, id, alias, lat, lon, type));
					}
				}
			}else{
				stations.put(null,Station.makeStation(null,errNoStations));
			}
			
		} catch (SAXException | IOException | ParserConfigurationException e) {
			stations.put(null,Station.makeStation(null,e.getMessage()));
			e.printStackTrace();
		}
		 
		return stations;
	}


	public static ArrayList<StationDetails> parseTimetableForStation(String myXml) throws Exception{
		ArrayList<StationDetails> timetable  = new ArrayList<StationDetails>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(myXml.getBytes()));
			doc.getDocumentElement().normalize();
			NodeList list = doc.getElementsByTagName(OBJECT_STATION_DATA);
			Log.d(TAG, "timetable size: "+list.getLength());
			if(list.getLength()>0){
				for(int i=0;i<list.getLength();i++){
					Node node = list.item(i);
					if(node.getNodeType()==Node.ELEMENT_NODE){
						Element e = (Element) node;
						String serverTime = e.getElementsByTagName(SERVER_TIME).item(0).getTextContent();
						String trainCode = e.getElementsByTagName(TRAIN_CODE).item(0).getTextContent();
						
						String stationFullName = e.getElementsByTagName(STATION_FULL_NAME).item(0).getTextContent();
						String stationCode = e.getElementsByTagName(STATION_CODE_DETAILS).item(0).getTextContent();
						String queryTime = e.getElementsByTagName(QUERY_TIME).item(0).getTextContent();
						String trainDate = e.getElementsByTagName(TRAIN_DATE).item(0).getTextContent();
						String origin = e.getElementsByTagName(ORIGIN).item(0).getTextContent();
						String destination = e.getElementsByTagName(DESTINATION).item(0).getTextContent();
						String originTime = e.getElementsByTagName(ORIGIN_TIME).item(0).getTextContent();
						String destinationTime = e.getElementsByTagName(DESTINATION_TIME).item(0).getTextContent();
						String status = e.getElementsByTagName(TRAIN_STATUS).item(0).getTextContent();
						String lastLocation = e.getElementsByTagName(LAST_LOCATION).item(0).getTextContent();
						String dueIn = e.getElementsByTagName(DUE_IN).item(0).getTextContent();
						String late = e.getElementsByTagName(LATE).item(0).getTextContent();
						String expArrival = e.getElementsByTagName(EXP_ARRIVAL).item(0).getTextContent();
						String expDepart = e.getElementsByTagName(EXP_DEPART).item(0).getTextContent();
						String schArrival = e.getElementsByTagName(SCHEDULE_ARRIVAL).item(0).getTextContent();
						String schDepart = e.getElementsByTagName(SCHEDULE_DEPART).item(0).getTextContent();
						String direction = e.getElementsByTagName(DIRECTION).item(0).getTextContent();
						String trainType = e.getElementsByTagName(TRAIN_TYPE).item(0).getTextContent();
						String locationType = e.getElementsByTagName(LOCATION_TYPE).item(0).getTextContent();
								
						
						
						timetable.add(new StationDetails(serverTime, trainCode, stationFullName, stationCode, queryTime, trainDate,
								origin, destination, originTime, destinationTime, status, lastLocation, dueIn, 
								late, expArrival, expDepart, schArrival, schDepart, direction, trainType, locationType));
					}
				}
			}else{
				throw new Exception("No trains");
			}
			
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		 
		return timetable;
	}
}
