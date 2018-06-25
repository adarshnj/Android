package mc.nj.proj;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ಆದರ್ಶ್ NJ on 4/10/2017.
 */

public class recentData {
    String DateTime;
    String Activity;
    String typeURL;

    recentData(String date, String act, String type) {
        this.DateTime = date;
        this.Activity = act;
        this.typeURL = type;
    }

    private static List<recentData> datum;

    public static List<recentData> test(){

        DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        datum = new ArrayList<recentData>();
        datum.add(new recentData(format.format(date),"Water Fountain nearby","drink"));
        datum.add(new recentData(format.format(date),"Gym nearby!","gym"));
        datum.add(new recentData(format.format(date),"High Pollen Alert","pollen"));
        datum.add(new recentData(format.format(date),"Time to eat something healthy","food"));
        datum.add(new recentData(format.format(date),"Fitness event nearby","cycle"));
        return datum;
    }
}
