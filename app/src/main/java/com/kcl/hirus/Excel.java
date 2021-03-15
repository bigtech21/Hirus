package com.kcl.hirus;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class Excel {
  /*
  * 난잡하게 흩어져있는 엑셀 메소드들을 static으로 한번에 사용하게 하기 위한 클래스
  * 마지막 테스트 과정에서 작성
  */

    static Context context;
    static InputStream is;
    static InputStream cis;
    static Workbook wb;
    static Workbook cwb;

    static int bestDesease;
    static int secondDesease;
    static int thirdDesease;

    static String bestDeseaseName;
    static String secondDeseaseName;
    static String thirdDeseaseName;

    static String[] deseases = new String[67];
    static int arr[] = new int[67];

    public Excel(Context context){
        try {
        this.context = context;
        is = context.getResources().getAssets().open("database.xls");
        cis = context.getResources().getAssets().open("corona.xls");

        wb = Workbook.getWorkbook(is);
        cwb = Workbook.getWorkbook(cis);


        }catch (Exception e){}
    }

    public static void arrinit() {
        for(int i = 0; i < arr.length; i++){ //초기화
            arr[i] = 0;
        }
        for(int i = 0; i < deseases.length; i++){ //초기화
            deseases[i] = "";
        }
    }

    public static void getExcelData(String addr, String si) {
        try {
            String str_do;
            arrinit();
            //배열 초기화
            for(int i = 0; i < deseases.length ; i++){
                deseases[i] = "";
            }
            if(addr.length() > 3) { //충청북도 ~~

                str_do = addr.substring(0,4);
            }
            else { //강원도 ~~ 서울 ~~
                str_do = addr.substring(0, 2);
            }
            String str_si = si.substring(0,2);

            int key, k, l;
            int addressPosition = 0;
            if(wb != null){
                Sheet sheet = wb.getSheet(0);
                if(sheet != null){
                    int rowTotal = sheet.getRows();
                    int colTotal = sheet.getColumns(); // 전체 컬럼

                    for(int i = 1; i < rowTotal; i++){
                        String contents = sheet.getCell(0, i).getContents();
                        Log.d(str_do+" "+str_si,contents);
                        if(str_do.equals(contents) || str_si.equals(contents)){//도 혹은 시/군을 찾을 경우
                            addressPosition = i;
                            break;
                        }
                    }

                    for(int j = 1; j < colTotal; j++){
                        Cell iCnt = sheet.getCell(j, addressPosition); //감염병 환자 수
                        Cell DName = sheet.getCell(j, 0); //감염병 이름
                        arr[j-1] = Integer.parseInt(iCnt.getContents());
                        deseases[j-1] = DName.getContents();
                        Log.d("excel",deseases[j-1]);
                    }

                    for(k = 1; k< arr.length; k++) { //삽입 정렬
                        key = arr[k];
                        for(l = k-1; l>=0 && arr[l]>key; l--) {
                            arr[l + 1] = arr[l];
                        }
                        arr[l+1] = key;
                        Log.d("test","Excel");
                    }
                    bestDesease = arr[arr.length-1];
                    secondDesease = arr[arr.length-2];
                    thirdDesease = arr[arr.length-3];

                    for(int i = 1; i < colTotal; i++){
                        Cell iCnt = sheet.getCell(i, addressPosition);
                        if(bestDesease == Integer.parseInt(iCnt.getContents())){
                            Cell D = sheet.getCell(i, 0);
                            bestDeseaseName = D.getContents();
                        }
                        if(secondDesease == Integer.parseInt(iCnt.getContents())){
                            Cell D = sheet.getCell(i, 0);
                            secondDeseaseName = D.getContents();
                        }
                        if(thirdDesease == Integer.parseInt(iCnt.getContents())){
                            Cell D = sheet.getCell(i, 0);
                            thirdDeseaseName = D.getContents();
                        }

                    }

                }
            }
        }catch (Exception e){e.printStackTrace();}
    }

    public static int selectDesease(String desease) { //AboutInfection 클래스에서 사용
        try {
            if(wb != null){

                Sheet sheet = wb.getSheet(0);

                if(sheet != null){
                    int colTotal = sheet.getColumns();    // 전체 컬럼

                    for(int i = 1; i < colTotal; i++){
                        String contents = sheet.getCell(i, 0).getContents();
                        if(desease.equals(contents)){

                            return i;
                        }
                    }
                }
            }
        }catch (Exception e){}
        return 0;
    }

    public static String copyExcelDataToDatabase(TextView address, String desease) { //AboutInfection 클래스에서 사용

        String addresses = null;
        String dataAddress = null;
        String newaddress = null;
        int positionD = selectDesease(desease);
        Log.d("newaddress", positionD+"");
        try {

            Log.d("주소", address.getText().toString());
            addresses = address.getText().toString();//현주소
            newaddress = addresses.substring(0, 2);//현주소자른

            if(wb != null) {
                Sheet sheet = wb.getSheet(0);   // 시트 불러오기

                if(sheet != null) {
                    int colTotal = sheet.getColumns();    // 전체 컬럼
                    int rowIndexStart = 0;                  // row 인덱스 시작
                    int rowTotal = sheet.getColumn(colTotal-1).length;

                    StringBuilder sb;
                    for(int row=rowIndexStart;row<rowTotal;row++) {
                        sb = new StringBuilder();
                        for(int col=0;col<colTotal;col++) {
                            String contents = sheet.getCell(col, row).getContents();
                            sb.append("col"+col+" : "+contents+" , ");


                            if(newaddress.equals(contents)){
                                Cell iCnt = sheet.getCell(positionD, row); //감염병 환자 수
                                Cell iName = sheet.getCell(positionD, 0); //감염병 이름
                                Log.d("newaddress", row+"");
                                String result = "현재 감염된 "+iName.getContents() +"환자 수"+ " : "+iCnt.getContents() + "명";
                                return result;
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
