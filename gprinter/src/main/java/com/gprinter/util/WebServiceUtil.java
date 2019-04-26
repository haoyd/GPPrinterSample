// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.util;

import org.json.JSONException;
import org.json.JSONArray;
import java.text.SimpleDateFormat;
import org.json.JSONObject;
import com.gprinter.model.DataInfoModel;
import java.util.List;
import com.gprinter.model.DeviceInfoModel;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.serialization.SoapObject;
import com.gprinter.interfaces.CallBackInterface;

public class WebServiceUtil
{
    private static final String targetNameSpace = "http://tempuri.org/";
    private static final String WSDL = "http://61.143.38.128:8080/Service.asmx";
    private static final String uploadData = "UploadData";
    
    public static void callWebService(final String json, final CallBackInterface callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final SoapObject soapObject = new SoapObject("http://tempuri.org/", "UploadData");
                soapObject.addProperty("json", (Object)json);
                final SoapSerializationEnvelope envelop = new SoapSerializationEnvelope(100);
                envelop.dotNet = true;
                envelop.setOutputSoapObject((Object)soapObject);
                final HttpTransportSE httpSE = new HttpTransportSE("http://61.143.38.128:8080/Service.asmx");
                try {
                    httpSE.call("http://tempuri.org/UploadData", (SoapEnvelope)envelop);
                    final Object resultObj = envelop.getResponse();
                    if (resultObj == null) {
                        LogInfo.out("WebService\u8fd4\u56de\u7ed3\u679c\u4e3a\u7a7a");
                    }
                    else {
                        LogInfo.out("WebService\u8fd4\u56de\u7ed3\u679c\uff1a " + resultObj.toString());
                        if (callBack != null) {
                            callBack.onCallBack(true);
                        }
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                    if (callBack != null) {
                        callBack.onCallBack(false);
                    }
                }
                catch (XmlPullParserException e2) {
                    e2.printStackTrace();
                }
            }
        }).start();
    }
    
    public static JSONObject createJSONObject(final DeviceInfoModel deviceInfoModel, final List<DataInfoModel> dataInfoModelList, final List<Integer> statusList) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobilename", (Object)deviceInfoModel.getMobileName());
            jsonObject.put("androidid", (Object)deviceInfoModel.getAndroidId());
            jsonObject.put("osversion", (Object)deviceInfoModel.getOsVersion());
            jsonObject.put("deviceid", (Object)deviceInfoModel.getDeviceId());
            jsonObject.put("iccid", (Object)deviceInfoModel.getIccid());
            jsonObject.put("macaddress", (Object)deviceInfoModel.getMacAddress());
            jsonObject.put("ipaddress", (Object)deviceInfoModel.getIpAddress());
            jsonObject.put("uptime", (Object)deviceInfoModel.getUpTime());
            jsonObject.put("allappnum", deviceInfoModel.getAllAppNum());
            jsonObject.put("installedapp", (Object)deviceInfoModel.getInstalledApp());
            jsonObject.put("uuid", (Object)deviceInfoModel.getUuid());
            jsonObject.put("datetime", (Object)new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(deviceInfoModel.getDateTime()));
            final JSONObject dataEle1 = new JSONObject();
            final DataInfoModel dataModel1 = dataInfoModelList.get(0);
            dataEle1.put("datetime", (Object)new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dataModel1.getDateTime()));
            dataEle1.put("processcpurate", dataModel1.getProcessCpuRate());
            dataEle1.put("appmem", dataModel1.getAppMem());
            dataEle1.put("systemavailablemem", dataModel1.getSystemAvailableMem());
            dataEle1.put("memrate", dataModel1.getMemRate());
            dataEle1.put("status", (Object)statusList.get(0));
            final JSONObject dataEle2 = new JSONObject();
            final DataInfoModel dataModel2 = dataInfoModelList.get(1);
            dataEle2.put("datetime", (Object)new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dataModel2.getDateTime()));
            dataEle2.put("processcpurate", dataModel2.getProcessCpuRate());
            dataEle2.put("appmem", dataModel2.getAppMem());
            dataEle2.put("systemavailablemem", dataModel2.getSystemAvailableMem());
            dataEle2.put("memrate", dataModel2.getMemRate());
            dataEle2.put("status", (Object)statusList.get(1));
            final JSONObject dataEle3 = new JSONObject();
            final DataInfoModel dataModel3 = dataInfoModelList.get(2);
            dataEle3.put("datetime", (Object)new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dataModel3.getDateTime()));
            dataEle3.put("processcpurate", dataModel3.getProcessCpuRate());
            dataEle3.put("appmem", dataModel3.getAppMem());
            dataEle3.put("systemavailablemem", dataModel3.getSystemAvailableMem());
            dataEle3.put("memrate", dataModel3.getMemRate());
            dataEle3.put("status", (Object)statusList.get(2));
            final JSONArray jsonArray = new JSONArray();
            jsonArray.put(0, (Object)dataEle1);
            jsonArray.put(1, (Object)dataEle2);
            jsonArray.put(2, (Object)dataEle3);
            jsonObject.put("data", (Object)jsonArray);
        }
        catch (JSONException ex) {}
        return jsonObject;
    }
    
    public static JSONObject createJSONObject(final DeviceInfoModel deviceInfoModel, final List<DataInfoModel> dataInfoModelList) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobilename", (Object)deviceInfoModel.getMobileName());
            jsonObject.put("androidid", (Object)deviceInfoModel.getAndroidId());
            jsonObject.put("osversion", (Object)deviceInfoModel.getOsVersion());
            jsonObject.put("deviceid", (Object)deviceInfoModel.getDeviceId());
            jsonObject.put("iccid", (Object)deviceInfoModel.getIccid());
            jsonObject.put("macaddress", (Object)deviceInfoModel.getMacAddress());
            jsonObject.put("ipaddress", (Object)deviceInfoModel.getIpAddress());
            jsonObject.put("uptime", (Object)deviceInfoModel.getUpTime());
            jsonObject.put("allappnum", deviceInfoModel.getAllAppNum());
            jsonObject.put("installedapp", (Object)deviceInfoModel.getInstalledApp());
            jsonObject.put("uuid", (Object)deviceInfoModel.getUuid());
            jsonObject.put("datetime", (Object)new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(deviceInfoModel.getDateTime()));
            final JSONObject dataEle1 = new JSONObject();
            final DataInfoModel dataModel1 = dataInfoModelList.get(0);
            dataEle1.put("datetime", (Object)new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dataModel1.getDateTime()));
            dataEle1.put("processcpurate", dataModel1.getProcessCpuRate());
            dataEle1.put("appmem", dataModel1.getAppMem());
            dataEle1.put("systemavailablemem", dataModel1.getSystemAvailableMem());
            dataEle1.put("memrate", dataModel1.getMemRate());
            final JSONObject dataEle2 = new JSONObject();
            final DataInfoModel dataModel2 = dataInfoModelList.get(1);
            dataEle2.put("datetime", (Object)new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dataModel2.getDateTime()));
            dataEle2.put("processcpurate", dataModel2.getProcessCpuRate());
            dataEle2.put("appmem", dataModel2.getAppMem());
            dataEle2.put("systemavailablemem", dataModel2.getSystemAvailableMem());
            dataEle2.put("memrate", dataModel2.getMemRate());
            final JSONObject dataEle3 = new JSONObject();
            final DataInfoModel dataModel3 = dataInfoModelList.get(2);
            dataEle3.put("datetime", (Object)new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dataModel3.getDateTime()));
            dataEle3.put("processcpurate", dataModel3.getProcessCpuRate());
            dataEle3.put("appmem", dataModel3.getAppMem());
            dataEle3.put("systemavailablemem", dataModel3.getSystemAvailableMem());
            dataEle3.put("memrate", dataModel3.getMemRate());
            final JSONArray jsonArray = new JSONArray();
            jsonArray.put(0, (Object)dataEle1);
            jsonArray.put(1, (Object)dataEle2);
            jsonArray.put(2, (Object)dataEle3);
            jsonObject.put("data", (Object)jsonArray);
        }
        catch (JSONException ex) {}
        return jsonObject;
    }
}
