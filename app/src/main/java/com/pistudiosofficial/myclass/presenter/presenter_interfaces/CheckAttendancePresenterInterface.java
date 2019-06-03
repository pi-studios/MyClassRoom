package com.pistudiosofficial.myclass.presenter.presenter_interfaces;

import java.util.ArrayList;

public interface CheckAttendancePresenterInterface {

    void adminCheckAttendanceDataDownloadSuccess(ArrayList<Double> arrayList);
    void adminCheckAttendanceDataDownloadFailed();

    void exportCsvSuccess();
    void exportCsvFailed();

    void broadcastSuccess();
    void broadcastFailed();

}
