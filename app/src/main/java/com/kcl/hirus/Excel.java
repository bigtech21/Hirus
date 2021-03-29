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
    static int patientcnt;

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

    public static void getExcelData(String addr, String sii) {
        try {
            String si = sii;
            arrinit();
            //배열 초기화
            for(int i = 0; i < deseases.length ; i++){
                deseases[i] = "";
            }

            int key, k, l;
            int addressPosition = 0;
            if(wb != null){
                Sheet sheet = wb.getSheet(0);
                if(sheet != null){
                    int rowTotal = sheet.getRows();
                    int colTotal = sheet.getColumns(); // 전체 컬럼

                    for(int i = 1; i < rowTotal; i++){
                        String contents = sheet.getCell(0, i).getContents().replaceAll("\\P{Print}","").trim();
                        /*데이터 양이 많아지니 유니코드 문제로 equls가 false나옴, replace구문으로 해결, 유니코드 문자 제거하는 구문*/

                        if(addr.contains(contents)||si.contains(contents)){//구를 찾을 경우
                            addressPosition = i;
                            break;
                        }
                        System.gc();
                    }

                    for(int j = 1; j < colTotal; j++){
                        Cell iCnt = sheet.getCell(j, addressPosition); //감염병 환자 수
                        Cell DName = sheet.getCell(j, 0); //감염병 이름
                        arr[j-1] = Integer.parseInt(iCnt.getContents());
                        deseases[j-1] = DName.getContents();
                    }

                    for(k = 1; k< arr.length; k++) { //삽입 정렬
                        key = arr[k];
                        for(l = k-1; l>=0 && arr[l]>key; l--) {
                            arr[l + 1] = arr[l];
                        }
                        arr[l+1] = key;
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
        String newaddress[] = null;
        int positionD = selectDesease(desease);
        patientcnt = 0;

        try {

            addresses = address.getText().toString();//현주소
            newaddress = addresses.split(" ");//현주소자른, 0 = ~시, 1 = ~구,동
            String str1 = newaddress[0].replaceAll("\\P{Print}","");
            String str2 = newaddress[1].replaceAll("\\P{Print}","");


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
                            String contents = sheet.getCell(0, row).getContents().replaceAll("\\P{Print}","");
                            sb.append("col"+col+" : "+contents+" , ");

                            if(contents.contains(str2)){
                                Cell iCnt = sheet.getCell(positionD, row); //감염병 환자 수
                                Cell iName = sheet.getCell(positionD, 0); //감염병 이름
                                String result = "현재 감염된 "+iName.getContents() +"환자 수"+ " : "+iCnt.getContents() + "명";
                                patientcnt = Integer.parseInt(iCnt.getContents());
                                return result;
                            }

                            else if(contents.contains(str1)){
                                Cell iCnt = sheet.getCell(positionD, row); //감염병 환자 수
                                Cell iName = sheet.getCell(positionD, 0); //감염병 이름
                                String result = "현재 감염된 "+iName.getContents() +"환자 수"+ " : "+iCnt.getContents() + "명";
                                patientcnt = Integer.parseInt(iCnt.getContents());
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
