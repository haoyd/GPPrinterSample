package com.haoyd.printerlib.views;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.haoyd.printerlib.R;
import com.haoyd.printerlib.entities.BluetoothDeviceInfo;

public class GPPrinterListHolder extends RecyclerView.ViewHolder {

    private Context mContext;

    private TextView groupName;
    private TextView deviceName;
    private TextView connState;

    public GPPrinterListHolder(View itemView) {
        super(itemView);

        groupName = itemView.findViewById(R.id.tv_item_printer_group);
        deviceName = itemView.findViewById(R.id.tv_item_printer_name);
        connState = itemView.findViewById(R.id.tv_item_printer_state);
    }

    public void bindData(BluetoothDeviceInfo info) {
        if (info == null) {
            return;
        }

        if (info.isShowGroup) {
            groupName.setVisibility(View.VISIBLE);
            groupName.setText(info.groupName);
        } else {
            groupName.setVisibility(View.GONE);
        }

        if (info.groupName.equals(BluetoothDeviceInfo.GROUP_PAIRED)) {
            connState.setVisibility(View.VISIBLE);

            if (info.isConnected) {
                connState.setText("已连接");
                connState.setTextColor(Color.parseColor("#33c298"));
            } else {
                connState.setText("未连接");
                connState.setTextColor(Color.parseColor("#999999"));
            }
        } else {
            connState.setVisibility(View.GONE);
        }

        deviceName.setText(TextUtils.isEmpty(info.name) ? "未知蓝牙设备" : info.name);
    }
}
