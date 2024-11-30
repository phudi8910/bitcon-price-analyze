package myapp.bitcoin_analyze.util;

public class Common {
    public static String stripDescription(String e,int maxLength) {
        if(maxLength==-1)
            return e;
        int stringLength = e.length();
        String stripDescription = e;
        if(stringLength>=maxLength) {
            stripDescription = stripDescription.substring(0,maxLength);
        }
        return stripDescription;
    }
}
