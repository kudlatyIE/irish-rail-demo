package ie.droidfactory.fragstations.httputils;

import android.content.Context;
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
import ie.droidfactory.fragstations.model.StationType;
import ie.droidfactory.fragstations.model.Train;
import ie.droidfactory.fragstations.model.TrainDetails;

/**
 * Created by kudlaty on 02/06/2016.
 */
public class Parser {
	
	private final static String TAG = Parser.class.getSimpleName();
	private static XmlKeyHolder h;

	

	
	public static HashMap<String, Station> parseAllStationsMap(Context ac, String myXml){
		HashMap<String, Station> stations  = new HashMap<>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		h= new XmlKeyHolder();

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(myXml.getBytes()));
			doc.getDocumentElement().normalize();
			NodeList list = doc.getElementsByTagName(h.OBJECT_STATION);
			
			if(list.getLength()>0){
				for(int i=0;i<list.getLength();i++){
					Node node = list.item(i);
					if(node.getNodeType()==Node.ELEMENT_NODE){
						Element e = (Element) node;
						String id = e.getElementsByTagName(h.STATION_ID).item(0).getTextContent();
						String name = e.getElementsByTagName(h.STATION_DESC).item(0).getTextContent();
						String alias=h.EMPTY;
						try{
							alias = e.getElementsByTagName(h.STATION_ALIAS).item(0).getTextContent();
						}catch(Exception e1){}//return default alias
						double lat = Double.parseDouble(e.getElementsByTagName(h.STATION_LATITUDE).item(0).getTextContent());
						double lon = Double.parseDouble(e.getElementsByTagName(h.STATION_LONGITUDE).item(0).getTextContent());
						String code = e.getElementsByTagName(h.STATION_CODE).item(0).getTextContent();
						String type=h.EMPTY;
						try{
							type  = e.getElementsByTagName(h.LOCATION_TYPE).item(0).getTextContent();
						}catch(Exception e2){}//return default station type

						stations.put(code, Station.makeStation(ac, name, code, id, alias, lat, lon, type));
					}
				}
			}else{
				stations.put(null,Station.makeStation(null,h.errNoData));
			}
			
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
            stations.put(null,Station.makeStation(null,e.getMessage()));
		}
		 
		return stations;
	}


//    public static HashMap<String,StationDetails> parseTimetableForStation(String myXml) throws
//            Exception{
//		HashMap<String, StationDetails> timetable  = new HashMap<>();
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//		h= new XmlKeyHolder();
//		try {
//			DocumentBuilder builder = factory.newDocumentBuilder();
//			Document doc = builder.parse(new ByteArrayInputStream(myXml.getBytes()));
//			doc.getDocumentElement().normalize();
//			NodeList list = doc.getElementsByTagName(h.OBJECT_STATION_DATA);
//			Log.d(TAG, "timetable size: "+list.getLength());
//			if(list.getLength()>0){
//				for(int i=0;i<list.getLength();i++){
//					Node node = list.item(i);
//					if(node.getNodeType()==Node.ELEMENT_NODE){
//						Element e = (Element) node;
//						String serverTime = e.getElementsByTagName(h.SERVER_TIME).item(0).getTextContent();
//						String trainCode = e.getElementsByTagName(h.STATION_TRAIN_CODE).item(0)
//								.getTextContent();
//
//						String stationFullName = e.getElementsByTagName(h.STATION_FULL_NAME).item(0).getTextContent();
//						String stationCode = e.getElementsByTagName(h.STATION_CODE_DETAILS).item(0).getTextContent();
//						String queryTime = e.getElementsByTagName(h.QUERY_TIME).item(0).getTextContent();
//						String trainDate = e.getElementsByTagName(h.STATION_TRAIN_DATE).item(0)
//								.getTextContent();
//						String origin = e.getElementsByTagName(h.ORIGIN).item(0).getTextContent();
//						String destination = e.getElementsByTagName(h.DESTINATION).item(0).getTextContent();
//						String originTime = e.getElementsByTagName(h.ORIGIN_TIME).item(0).getTextContent();
//						String destinationTime = e.getElementsByTagName(h.DESTINATION_TIME).item(0).getTextContent();
//						String status = e.getElementsByTagName(h.STATION_TRAIN_STATUS).item(0)
//								.getTextContent();
//						String lastLocation = e.getElementsByTagName(h.LAST_LOCATION).item(0).getTextContent();
//						String dueIn = e.getElementsByTagName(h.DUE_IN).item(0).getTextContent();
//						String late = e.getElementsByTagName(h.LATE).item(0).getTextContent();
//						String expArrival = e.getElementsByTagName(h.EXP_ARRIVAL).item(0).getTextContent();
//						String expDepart = e.getElementsByTagName(h.EXP_DEPART).item(0).getTextContent();
//						String schArrival = e.getElementsByTagName(h.SCHEDULE_ARRIVAL).item(0).getTextContent();
//						String schDepart = e.getElementsByTagName(h.SCHEDULE_DEPART).item(0).getTextContent();
//						String direction = e.getElementsByTagName(h.DIRECTION).item(0).getTextContent();
//						String trainType = e.getElementsByTagName(h.TRAIN_TYPE).item(0).getTextContent();
//						String locationType = e.getElementsByTagName(h.LOCATION_TYPE).item(0).getTextContent();
//
//						timetable.put(trainCode, new StationDetails(serverTime, trainCode,
//                                stationFullName, stationCode, queryTime, trainDate,
//								origin, destination, originTime, destinationTime, status, lastLocation, dueIn,
//								late, expArrival, expDepart, schArrival, schDepart, direction, trainType, locationType));
//					}
//				}
//			}else{
//				throw new Exception(h.errNoData);
//			}
//
//		} catch (SAXException | IOException | ParserConfigurationException e) {
//			e.printStackTrace();
//			throw new Exception(e.getMessage());
//		}
//
//		return timetable;
//	}

	public static ArrayList<StationDetails> parseTimetableForStation(String myXml) throws
			Exception{
//		HashMap<String, StationDetails> timetable  = new HashMap<>();
		ArrayList<StationDetails> timetable = new ArrayList<>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		h= new XmlKeyHolder();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(myXml.getBytes()));
			doc.getDocumentElement().normalize();
			NodeList list = doc.getElementsByTagName(h.OBJECT_STATION_DATA);
			Log.d(TAG, "timetable size: "+list.getLength());
			if(list.getLength()>0){
				for(int i=0;i<list.getLength();i++){
					Node node = list.item(i);
					if(node.getNodeType()==Node.ELEMENT_NODE){
						Element e = (Element) node;
						String serverTime = e.getElementsByTagName(h.SERVER_TIME).item(0).getTextContent();
						String trainCode = e.getElementsByTagName(h.STATION_TRAIN_CODE).item(0)
								.getTextContent();

						String stationFullName = e.getElementsByTagName(h.STATION_FULL_NAME).item(0).getTextContent();
						String stationCode = e.getElementsByTagName(h.STATION_CODE_DETAILS).item(0).getTextContent();
						String queryTime = e.getElementsByTagName(h.QUERY_TIME).item(0).getTextContent();
						String trainDate = e.getElementsByTagName(h.STATION_TRAIN_DATE).item(0)
								.getTextContent();
						String origin = e.getElementsByTagName(h.ORIGIN).item(0).getTextContent();
						String destination = e.getElementsByTagName(h.DESTINATION).item(0).getTextContent();
						String originTime = e.getElementsByTagName(h.ORIGIN_TIME).item(0).getTextContent();
						String destinationTime = e.getElementsByTagName(h.DESTINATION_TIME).item(0).getTextContent();
						String status = e.getElementsByTagName(h.STATION_TRAIN_STATUS).item(0)
								.getTextContent();
						String lastLocation = e.getElementsByTagName(h.LAST_LOCATION).item(0).getTextContent();
						String dueIn = e.getElementsByTagName(h.DUE_IN).item(0).getTextContent();
						String late = e.getElementsByTagName(h.LATE).item(0).getTextContent();
						String expArrival = e.getElementsByTagName(h.EXP_ARRIVAL).item(0).getTextContent();
						String expDepart = e.getElementsByTagName(h.EXP_DEPART).item(0).getTextContent();
						String schArrival = e.getElementsByTagName(h.SCHEDULE_ARRIVAL).item(0).getTextContent();
						String schDepart = e.getElementsByTagName(h.SCHEDULE_DEPART).item(0).getTextContent();
						String direction = e.getElementsByTagName(h.DIRECTION).item(0).getTextContent();
						String trainType = e.getElementsByTagName(h.TRAIN_TYPE).item(0).getTextContent();
						String locationType = e.getElementsByTagName(h.LOCATION_TYPE).item(0).getTextContent();

						timetable.add(new StationDetails(serverTime, trainCode,
								stationFullName, stationCode, queryTime, trainDate,
								origin, destination, originTime, destinationTime, status, lastLocation, dueIn,
								late, expArrival, expDepart, schArrival, schDepart, direction, trainType, locationType));
					}
				}
			}else{
				throw new Exception(h.errNoData);
			}

		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}

		return timetable;
	}


	public static HashMap<String, Train> parseRunningTrains(String myXml) throws Exception {
		HashMap<String, Train> trains  = new HashMap<>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        h= new XmlKeyHolder();
        try{
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(myXml.getBytes()));
            doc.getDocumentElement().normalize();
            NodeList list = doc.getElementsByTagName(h.OBJECT_TRAIN_POSITION);

            if(list.getLength()>0){
                for(int i=0;i<list.getLength();i++){
                    Node node = list.item(i);
                    if(node.getNodeType()==Node.ELEMENT_NODE){
                        Element e = (Element) node;
                        String status = e.getElementsByTagName(h.TR_TRAIN_STATUS).item(0)
                                .getTextContent();

                        double lat = Double.parseDouble(e.getElementsByTagName(h.TR_TRAIN_LATITUDE).item(0)
                                .getTextContent());
                        double lon = Double.parseDouble(e.getElementsByTagName(h.TR_TRAIN_LONGITUDE).item(0)
                                .getTextContent());
                        String code = e.getElementsByTagName(h.TR_TRAIN_CODE).item(0).getTextContent();
                        String date  = e.getElementsByTagName(h.TR_TRAIN_DATE).item(0)
                                .getTextContent();
                        String msg = e.getElementsByTagName(h.TR_PUBLIC_MSG).item(0)
                                .getTextContent();
                        String direction = e.getElementsByTagName(h.TR_DIRECTION).item(0)
                                .getTextContent();
                        trains.put(code, Train.makeTrain(status,lat, lon, code, date, msg,
                                direction));
                    }
                }
            }else{
                trains.put(null,Train.makeTrain(null,h.errNoData));
                throw new Exception(h.errNoData);
            }

        } catch (SAXException | ParserConfigurationException | IOException e) {
            e.printStackTrace();
//            trains.put(null,Train.makeTrain(null,e.getMessage()));
            throw new Exception(e.getMessage());
        }
        return trains;
	}

	public static ArrayList<TrainDetails> parseTrainDetails(String myXml) throws Exception {
		ArrayList<TrainDetails> trainRoute  = new ArrayList<>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		h= new XmlKeyHolder();
		try{
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(myXml.getBytes()));
			doc.getDocumentElement().normalize();
			NodeList list = doc.getElementsByTagName(h.OBJECT_TRAIN_MOVEMENTS);

			if(list.getLength()>0){
				for(int i=0;i<list.getLength();i++){
					Node node = list.item(i);
					if(node.getNodeType()==Node.ELEMENT_NODE){
						Element e = (Element) node;

						String code = e.getElementsByTagName(h.TD_TRAIN_CODE).item(0)
								.getTextContent();
						String date  = e.getElementsByTagName(h.TD_TRAIN_DATE).item(0)
								.getTextContent();
						String locationCode = e.getElementsByTagName(h.TD_LOCATION_CODE).item(0)
								.getTextContent();
						String locationName = e.getElementsByTagName(h.TD_LOCATION_FULL_NAME).item(0)
								.getTextContent();
						Integer order = Integer.parseInt(e.getElementsByTagName(h.TD_LOCATION_ORDER).item(0)
								.getTextContent());
						String locationType = e.getElementsByTagName(h.TD_LOCATION_TYPE).item(0)
								.getTextContent();
						String origin = e.getElementsByTagName(h.TD_TRAIN_ORIGIN).item(0)
								.getTextContent();
						String destination = e.getElementsByTagName(h.TD_TRAIN_DESTINATION).item(0)
								.getTextContent();
						String schArrival = e.getElementsByTagName(h.TD_SCHEUDLE_ARRIVAL).item(0)
								.getTextContent();
						String schDepart = e.getElementsByTagName(h.TD_SCHEUDLE_DEPARTURE).item(0)
								.getTextContent();
						String expArrival = e.getElementsByTagName(h.TD_EXPECTED_ARRIVAL).item(0)
								.getTextContent();
						String expDepart = e.getElementsByTagName(h.TD_EXPECTED_DEPARTURE).item(0)
								.getTextContent();
						//can be null - use try-catch
						//get data only id Location type !=T (timing point, train not stopping), hm
						//but sometimes is stopping, WHY?
						String arrival = h.EMPTY;
						try{
							arrival =e.getElementsByTagName(h.TD_ARRIVAL).item(0)
									.getTextContent();
						}catch(Exception e1){}
						String depart = h.EMPTY;
						try{
							depart = e.getElementsByTagName(h.TD_DEPARTURE).item(0)
									.getTextContent();
						}catch (Exception e1){}
						String autoArrival = h.EMPTY;
						try{
							autoArrival = e.getElementsByTagName(h.TD_AUTOARRIVAL).item(0)
									.getTextContent();
						}catch (Exception e1){}
						String autoDepart = h.EMPTY;
						try{
							autoDepart = e.getElementsByTagName(h.TD_AUTODEPART).item(0)
									.getTextContent();
						}catch (Exception e1){}
						// until this line
						String stopType = e.getElementsByTagName(h.TD_STOP_TYPE).item(0)
								.getTextContent();

						//grab data only if stations is not timing_point type
						if(!locationType.equals(StationType.TYPE_T.getType())){
							trainRoute.add(TrainDetails.makeTrainDetails(code,date,locationCode,
									locationName,order,locationType,origin,destination,schArrival,
									schDepart,expArrival, expDepart, arrival,depart,autoArrival,
									autoDepart, stopType));
						}
//                        Log.w(TAG,trainRoute.get(order).toString());
					}
				}
//                Log.d(TAG, "map size: "+trainRoute.size());
			}else{
//                trainRoute.put(null,TrainDetails.makeTrainDetails(null,h.errNoData));
				throw new Exception(h.errNoData);
			}

		} catch (SAXException | ParserConfigurationException | IOException e) {
			e.printStackTrace();
//            trainRoute.put(null,TrainDetails.makeTrainDetails(null,h.errNoData))
			throw new Exception(e.getMessage());
		}
		return trainRoute;
	}

	/**
	 *
	 * @param myXml
	 * @return hashmap location order is a key
	 * @throws Exception
     */
    public static HashMap<Integer, TrainDetails> parseTrainDetails_NOT_USED(String myXml) throws Exception {
        HashMap<Integer, TrainDetails> trainRoute  = new HashMap<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        h= new XmlKeyHolder();
        try{
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(myXml.getBytes()));
            doc.getDocumentElement().normalize();
            NodeList list = doc.getElementsByTagName(h.OBJECT_TRAIN_MOVEMENTS);

            if(list.getLength()>0){
                for(int i=0;i<list.getLength();i++){
                    Node node = list.item(i);
                    if(node.getNodeType()==Node.ELEMENT_NODE){
                        Element e = (Element) node;

                        String code = e.getElementsByTagName(h.TD_TRAIN_CODE).item(0)
                                .getTextContent();
                        String date  = e.getElementsByTagName(h.TD_TRAIN_DATE).item(0)
                                .getTextContent();
                        String locationCode = e.getElementsByTagName(h.TD_LOCATION_CODE).item(0)
                                .getTextContent();
                        String locationName = e.getElementsByTagName(h.TD_LOCATION_FULL_NAME).item(0)
                                .getTextContent();
                        Integer order = Integer.parseInt(e.getElementsByTagName(h.TD_LOCATION_ORDER).item(0)
                                .getTextContent());
                        String locationType = e.getElementsByTagName(h.TD_LOCATION_TYPE).item(0)
                                .getTextContent();
                        String origin = e.getElementsByTagName(h.TD_TRAIN_ORIGIN).item(0)
                                .getTextContent();
                        String destination = e.getElementsByTagName(h.TD_TRAIN_DESTINATION).item(0)
                                .getTextContent();
                        String schArrival = e.getElementsByTagName(h.TD_SCHEUDLE_ARRIVAL).item(0)
                                .getTextContent();
                        String schDepart = e.getElementsByTagName(h.TD_SCHEUDLE_DEPARTURE).item(0)
                                .getTextContent();
                        String expArrival = e.getElementsByTagName(h.TD_EXPECTED_ARRIVAL).item(0)
                                .getTextContent();
                        String expDepart = e.getElementsByTagName(h.TD_EXPECTED_DEPARTURE).item(0)
                                .getTextContent();
                        //can be null - use try-catch
                    //get data only id Location type !=T (timing point, train not stopping), hm
                    //but sometimes is stopping, WHY?
                        String arrival = h.EMPTY;
                        try{
                            arrival =e.getElementsByTagName(h.TD_ARRIVAL).item(0)
                                    .getTextContent();
                        }catch(Exception e1){}
                        String depart = h.EMPTY;
                        try{
                            depart = e.getElementsByTagName(h.TD_DEPARTURE).item(0)
                                    .getTextContent();
                        }catch (Exception e1){}
                        String autoArrival = h.EMPTY;
                        try{
                            autoArrival = e.getElementsByTagName(h.TD_AUTOARRIVAL).item(0)
                                    .getTextContent();
                        }catch (Exception e1){}
                        String autoDepart = h.EMPTY;
                        try{
                            autoDepart = e.getElementsByTagName(h.TD_AUTODEPART).item(0)
                                    .getTextContent();
                        }catch (Exception e1){}
                    // until this line
                        String stopType = e.getElementsByTagName(h.TD_STOP_TYPE).item(0)
                                .getTextContent();

                        //grab data only if stations is not timing_point type
                        if(!locationType.equals(StationType.TYPE_T.getType())){
							trainRoute.put(order, TrainDetails.makeTrainDetails(code,date,locationCode,
									locationName,order,locationType,origin,destination,schArrival,
									schDepart,expArrival, expDepart, arrival,depart,autoArrival,
									autoDepart, stopType));
						}
//                        Log.w(TAG,trainRoute.get(order).toString());
                    }
                }
//                Log.d(TAG, "map size: "+trainRoute.size());
            }else{
//                trainRoute.put(null,TrainDetails.makeTrainDetails(null,h.errNoData));
                throw new Exception(h.errNoData);
            }

        } catch (SAXException | ParserConfigurationException | IOException e) {
            e.printStackTrace();
//            trainRoute.put(null,TrainDetails.makeTrainDetails(null,h.errNoData))
            throw new Exception(e.getMessage());
        }
        return trainRoute;
    }
}
