package data;

import activity.CurMapActivity;

public class GeoVariableData {
    public static double latitude; // static 클래스 변수 위도

    public static double longitube; // static 클래스 변수 경도

    public static String searchedGu; // static 클래스 변수 역지오해서 자른 구

    public static String address;



    public static String getEcoment() {

        return ecoment;

    }



    public static void setEcoment(String ecoment) {

        GeoVariableData.ecoment = ecoment;

    }



    public static String ecoment;



    public static String getAirEco() {

        return airEco;

    }



    public static void setAirEco(String airEco) {

        GeoVariableData.airEco = airEco;

    }



    public static String getMise() {

        return mise;

    }



    public static void setMise(String mise) {

        GeoVariableData.mise = mise;

    }



    public static String getChomise() {

        return chomise;

    }



    public static void setChomise(String chomise) {

        GeoVariableData.chomise = chomise;

    }



    public static String airEco; // static 환경상태

    public static String mise; // static 미세먼지

    public static String chomise; // static 초미세먼지



    public static String getSearchedGu() {

        return searchedGu;

    }

    public static void setSearchedGu(String searchedGu) {

        GeoVariableData.searchedGu = searchedGu;

    }



    public static double getLatitude() {

        return CurMapActivity.curlatitude;

    }



    public static void setLatitude(double latitude) {

        CurMapActivity.curlatitude= latitude;

    }

    public static void setAddress(String string){
        GeoVariableData.address = string;
    }

    public static String getAddress(){
        return address;
    }



    public static double getLongitude() {

        return CurMapActivity.curlongitude;

    }



    public static void setLongitude(double longitude) {

       CurMapActivity.curlongitude = longitube;

    }
}
