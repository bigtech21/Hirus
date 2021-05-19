package Interface;

import android.widget.TextView;

public interface ExcelInterface {
    void getExcelData(String addr, String addr2);
    int selectDesease(String desease);
    String copyExcelDataToDatabase(TextView address, String desease);
    void getData();
}
