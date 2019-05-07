package com.haoyd.printerlib.manager;

import com.haoyd.printerlib.entities.BluetoothDeviceInfo;

import java.util.ArrayList;
import java.util.List;

public class GPPrinterListManager {

    private List<BluetoothDeviceInfo> totalList = new ArrayList<>();
    private List<BluetoothDeviceInfo> pairdList = new ArrayList<>();
    private List<BluetoothDeviceInfo> newList = new ArrayList<>();

    // 为了减少计算逻辑
    private boolean isChangedData = false;

    public void setLinkedDevice(BluetoothDeviceInfo info) {
        if (info == null) {
            return;
        }

        boolean isContains = false;

        if (pairdList.size() > 0) {
            for (BluetoothDeviceInfo bdi : pairdList) {
                if (bdi.name != null && info.name != null && bdi.name.equals(info.name)) {
                    bdi.isConnected = true;
                    isContains = true;
                }
            }
        }

        if (!isContains) {
            pairdList.add(info);
            isChangedData = true;
        }
    }

    public void addPairdDevice(BluetoothDeviceInfo info) {
        if (info == null) {
            return;
        }

        boolean isContains = false;

        if (pairdList.size() > 0) {
            for (BluetoothDeviceInfo bdi : pairdList) {
                if (bdi.name != null && info.name != null && bdi.name.equals(info.name)) {
                    isContains = true;
                }
            }
        }

        if (!isContains) {
            pairdList.add(info);
            isChangedData = true;
        }
    }

    public void addNewDevice(BluetoothDeviceInfo info) {
        if (info == null) {
            return;
        }

        boolean isContains = false;

        if (newList.size() > 0) {
            for (BluetoothDeviceInfo bdi : newList) {
                if (bdi.name != null && info.name != null && bdi.name.equals(info.name)) {
                    isContains = true;
                }
            }
        }

        if (!isContains) {
            newList.add(info);
            isChangedData = true;
        }
    }

    public List<BluetoothDeviceInfo> getData() {
        // 优化数据的获取
        if (!isChangedData) {
            return totalList;
        }

        if (pairdList.size() > 0) {
            for (int i = 0; i < pairdList.size(); i++) {
                BluetoothDeviceInfo info = pairdList.get(i);
                info.groupName = BluetoothDeviceInfo.GROUP_PAIRED;
                if (i == 0) {
                    info.isShowGroup = true;
                } else {
                    info.isShowGroup = false;
                }
            }
        }

        if (newList.size() > 0) {
            for (int i = 0; i < newList.size(); i++) {
                BluetoothDeviceInfo info = newList.get(i);
                info.groupName = BluetoothDeviceInfo.GROUP_OTHERS;
                if (i == 0) {
                    info.isShowGroup = true;
                } else {
                    info.isShowGroup = false;
                }
            }
        }

        if (totalList.size() > 0) {
            totalList.clear();
        }

        if (pairdList.size() > 0) {
            totalList.addAll(pairdList);
        }

        if (newList.size() > 0) {
            totalList.addAll(newList);
        }

        isChangedData = false;

        return totalList;
    }

    public int getTotalSize() {
        return totalList.size();
    }

    public void clear() {
        totalList.clear();
        pairdList.clear();
        newList.clear();
        isChangedData = true;
    }

    public List<BluetoothDeviceInfo> selectItem(int position) {
        // 已配对中所有状态改为未配对状态
        if (pairdList.size() > 0) {
            for (BluetoothDeviceInfo bdi : pairdList) {
                bdi.isConnected = false;
            }
        }

        // 选中的改为已经配对状态
        BluetoothDeviceInfo info = totalList.get(position);
        info.isConnected = true;
        info.groupName = BluetoothDeviceInfo.GROUP_PAIRED;

        totalList.remove(info);

        if (newList.contains(info)) {
            newList.remove(info);
        }

        if (!pairdList.contains(info)) {
            pairdList.add(info);
        }

        isChangedData = true;

        return getData();
    }

    public void selectByName(String name) {
        int result = -1;

        for (int i = 0; i < totalList.size(); i++) {
            if (totalList.get(i).name.equals(name)) {
                result = i;
            }
        }

        if (result != -1) {
            selectItem(result);
        }
    }

    public BluetoothDeviceInfo getItem(int position) {
        try {
            return totalList.get(position);
        } catch (Exception e) {
            return null;
        }
    }

}
