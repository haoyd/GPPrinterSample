package com.haoyd.printerlib.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.haoyd.printerlib.R;


public class PrinterEmptyView extends FrameLayout {

    private View confirm;

    public PrinterEmptyView(@NonNull Context context) {
        this(context, null);
    }

    public PrinterEmptyView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PrinterEmptyView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View.inflate(getContext(), R.layout.view_printer_empty, this);

        confirm = findViewById(R.id.tv_printer_empty_confirm);
    }

    public void setConfirmClickListener(OnClickListener listener) {
        if (listener == null) {
            return;
        }

        confirm.setOnClickListener(listener);
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }


}
