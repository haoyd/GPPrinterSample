package com.haoyd.printerlib.manager;

import android.app.Activity;

import com.haoyd.printerlib.databuilder.PrintCommand;
import com.haoyd.printerlib.formats.GoodsColumnConfiger;
import com.haoyd.printerlib.formats.GoodsFormatTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PrinterManager extends BasePrinterManager {

    public PrinterManager(Activity mActivity) {
        super(mActivity);
    }

    public void printTestTicket() {
        PrintCommand cmd = new PrintCommand();

//        cmd.setAbsolutePosition(1)
//                .addText("商品名")
//                .setAbsolutePosition(26)
//                .addText("单价")
//                .setAbsolutePosition(32)
//                .addText("数量")
//                .setAbsolutePosition(42)
//                .addText("金额");
        cmd.addText("123456789012345678901234567890123456789012345678");

        GoodsFormatTemplate goodsFormatTemplate = new GoodsFormatTemplate(cmd);
        goodsFormatTemplate.addConfig(new GoodsColumnConfiger("商品名", 1, 22, true));
        goodsFormatTemplate.addConfig(new GoodsColumnConfiger("单价", 30, false));
        goodsFormatTemplate.addConfig(new GoodsColumnConfiger("数量", 36, false));
        goodsFormatTemplate.addConfig(new GoodsColumnConfiger("金额", 48, false));

        List<List<String>> data = new ArrayList<>();
        data.add(new ArrayList<>(Arrays.asList("1.芬达汽水橙味600ml*24", "49.00", "9包", "441.00")));
        data.add(new ArrayList<>(Arrays.asList("2.芬达汽水橙味", "49.00", "9包", "441.00")));
        data.add(new ArrayList<>(Arrays.asList("3.芬达汽水橙味600ml*24", "49.00", "9包", "441.00")));
        data.add(new ArrayList<>(Arrays.asList("4.芬达汽水橙味600ml*24芬达汽水橙味600ml*24芬达汽水橙味600ml*24", "49.00", "9包", "441.00")));

        goodsFormatTemplate.build(data);
        cmd.addMutiFeedLines(2);

        printTicket(cmd.build());
    }

}
