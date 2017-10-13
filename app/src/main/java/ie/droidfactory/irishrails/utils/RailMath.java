package ie.droidfactory.irishrails.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by kudlaty on 16/06/2016.
 */
public class RailMath {

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
