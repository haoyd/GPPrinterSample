package com.haoyd.printerlib.views;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.haoyd.printerlib.R;
import com.haoyd.printerlib.entities.BluetoothDeviceInfo;
import com.haoyd.printerlib.interfaces.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class GPPrinterListAdapter extends RecyclerView.Adapter<GPPrinterListHolder> {

    private Activity mActivity;
    private List<BluetoothDeviceInfo> data = new ArrayList<>();
    private RecyclerItemClickListener itemClickListener;

    public GPPrinterListAdapter(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    public GPPrinterListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mActivity, R.layout.gp_item_printer, null);
        return new GPPrinterListHolder(view);
    }

    @Override
    public void onBindViewHolder(GPPrinterListHolder holder, final int position) {
        holder.bindData(data.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    public void setData(List<BluetoothDeviceInfo> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setItemClickListener(RecyclerItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
