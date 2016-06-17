package ie.droidfactory.fragstations.model;

/**
 * Created by kudlaty on 09/06/2016.
 * TODO: to be continue....
 */
public enum TrainType {

    TYPE_A ("A", "All"),
    TYPE_M ("M","Mainline"),
    TYPE_S ("S","suburban"),
    TYPE_D ("D","DART");

    private String type, name;

    TrainType(String type, String name){
        this.type=type;
        this.name=name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
