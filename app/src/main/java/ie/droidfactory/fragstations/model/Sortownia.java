package ie.droidfactory.fragstations.model;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import ie.droidfactory.fragstations.utils.RailMath;
import ie.droidfactory.fragstations.utils.RailSingleton;

/**
 * Created by kudlaty on 16/06/2016.
 */
public class Sortownia {

    private final static String TAG = Sortownia.class.getSimpleName();
    private HashMap<String, Float> distanceToStations;
    private HashMap<String, Train> distanceToTrain; //nonsense, not used
    private LatLng myLocation;
    private ArrayList<SortedObject> sortedList;//Store sorted stations or trains code(id) list

    public Sortownia(){
        this.myLocation = RailSingleton.getMyLocation();
        this.distanceToStations = new HashMap<>();

    }

    public ArrayList<SortedObject> getSortedListByDistance() throws Exception {
        this.sortedList = new ArrayList<>();
        HashMap<String, Station> insert = RailSingleton.getStationMap();
        if(insert==null) throw new Exception("empty input, stations list is null");
        ArrayList<SortedObject> unsortedList = new ArrayList<>();
        for(String key: insert.keySet()){
            double lat = insert.get(key).getStationLatitude();
            double lng = insert.get(key).getStationLongitude();
            unsortedList.add(new SortedObject(key, getDistance(lat, lng)));
        }
        SortedObject[] myObjects = unsortedList.toArray(new SortedObject[unsortedList.size()]);

        CompareObjects sorted = new CompareObjects(myObjects);
        sorted.sortByDistance();
        for(SortedObject so: sorted.getList()){
            float result = so.getValueDecimal();
            String output="";
            if(result>=10000) output = String.valueOf(RailMath.round(result/1000,0))+" km";
            else if(result>2000) output = String.valueOf(RailMath.round(result/1000,1))+" km";
            else output = String.valueOf(RailMath.round(result,0))+" m";
            sortedList.add(new SortedObject(so.getKey(), output));
        }
        return sortedList;
    }

    public ArrayList<SortedObject> getSorteDListByName() throws Exception {
        this.sortedList = new ArrayList<>();
        HashMap<String, Station> insert = RailSingleton.getStationMap();
        if(insert==null) throw new Exception("empty input, stations list is null");
        ArrayList<SortedObject> unsortedList = new ArrayList<>();
        for(String key: insert.keySet()){
            unsortedList.add(new SortedObject(key, insert.get(key).getStationDesc()));
        }
        SortedObject[] myObjects = unsortedList.toArray(new SortedObject[unsortedList.size()]);
        CompareObjects sorted = new CompareObjects(myObjects);
        sorted.sortByName();
        for(SortedObject so: sorted.getList()){
            double lat = insert.get(so.getKey()).getStationLatitude();
            double lng = insert.get(so.getKey()).getStationLongitude();
            float result = getDistance(lat, lng);
            String output="";
            if(result>=10000) output = String.valueOf(RailMath.round(result/1000,0))+" km";
            else if(result>2000) output = String.valueOf(RailMath.round(result/1000,1))+" km";
            else output = String.valueOf(RailMath.round(result,0))+" m";
            sortedList.add(new SortedObject(so.getKey(), so.getValue1(),output) );
        }
        return sortedList;
    }

    private float getDistance(LatLng from, LatLng to){
        Location locA = new Location("");
        Location locB = new Location("");
        locA.setLatitude(from.latitude);
        locA.setLongitude(from.longitude);
        locB.setLatitude(to.latitude);
        locB.setLongitude(to.longitude);
        return (locA.distanceTo(locB));

    }
    private float getDistance(LatLng from){
        Location locA = new Location("");
        Location locB = new Location("");
        locA.setLatitude(from.latitude);
        locA.setLongitude(from.longitude);
        locB.setLatitude(myLocation.latitude);
        locB.setLongitude(myLocation.longitude);
        return (locA.distanceTo(locB));
    }
    private float getDistance(double lat, double lng){
        Location locA = new Location("");
        Location locB = new Location("");
        locA.setLatitude(lat);
        locA.setLongitude(lng);
        locB.setLatitude(myLocation.latitude);
        locB.setLongitude(myLocation.longitude);
        return (locA.distanceTo(locB));
    }



}

class CompareObjects{
     public SortedObject[] myObjectArray;
     static Comparator<SortedObject> byName;
     static Comparator<SortedObject> byDistance;

     static{
         byName = new Comparator<SortedObject>() {
             @Override
             public int compare(SortedObject o1, SortedObject o2) {
                 return o1.getValue1().compareTo(o2.getValue1());
             }
         };
     }
     static {
         byDistance = new Comparator<SortedObject>() {
             @Override
             public int compare(SortedObject o1, SortedObject o2) {
                 return o1.getValueDecimal().compareTo(o2.getValueDecimal());
             }
         };
     }

     public CompareObjects(SortedObject[] list){
         this.myObjectArray = list;
     }
     public SortedObject[] getList(){
         return myObjectArray;
     }
     public  void sortByDistance(){
         Arrays.sort(myObjectArray, byDistance);
     }

    public void sortByName(){
        Arrays.sort(myObjectArray,byName);
    }
}
