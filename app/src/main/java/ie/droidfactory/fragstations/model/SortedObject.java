package ie.droidfactory.fragstations.model;

/**
 * Created by kudlaty on 16/06/2016.
 */
public class SortedObject {

    private String key;
    private String value1;
    private String value2;
    private Float valueDecimal;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value) {
        this.value1 = value;
    }
    public String getValue2() {
        return value2;
    }

    public void setValue2(String value) {
        this.value2 = value;
    }

    public Float getValueDecimal() {
        return valueDecimal;
    }

    public void setValueDecimal(Float valueDecimal) {
        this.valueDecimal = valueDecimal;
    }

    public SortedObject(String key, String value1, String value2){
        this.key=key;
        this.value1=value1;
        this.value2=value2;
    }
    public SortedObject(String key, String value){
        this.key=key;
        this.value1=value;
    }
    public SortedObject(String key, Float value){
        this.key=key;
        this.valueDecimal=value;
    }
}