package com.haoyd.printerlib.formats;

import com.haoyd.printerlib.databuilder.PrintCommand;
import com.haoyd.printerlib.utils.StringLengthUtil;
import com.haoyd.printerlib.utils.SubByteString;

import java.util.ArrayList;
import java.util.List;

public class GoodsFormatTemplate {

    private PrintCommand printCommand;
    private List<GoodsColumnConfiger> configers;

    public GoodsFormatTemplate(PrintCommand printCommand) {
        this.printCommand = printCommand;
        configers = new ArrayList<>();
    }

    public void addConfig(GoodsColumnConfiger configer) {
        configers.add(configer);
    }

    public PrintCommand build(List<List<String>> datas) {
        for (int i = 0; i < configers.size(); i++) {
            printCommand.setAbsolutePosition(configers.get(i).getPrintPosition());
            printCommand.addText(configers.get(i).getName());
        }

        printCommand.lineFeed();

        for (int i = 0; i < datas.size(); i++) {
            List<String> rowData = datas.get(i);
            int lineFeedIndex = 0;
            String lineFeedData[] = null;
            boolean lineFeedDataAdded = false;

            for (int i1 = 0; i1 < rowData.size(); i1++) {
                String msg = rowData.get(i1);
                GoodsColumnConfiger configer = configers.get(i1);

                int startIndex = configers.get(i1).getStartIndex();

                // 如果有限制最大宽度，需要计算是否超出范围
                if (configer.getMaxLength() != 0 && StringLengthUtil.getTextLength(msg) > configer.getMaxLength()) {
                    lineFeedData = SubByteString.getSubedStrings(msg, configer.getMaxLength());
                    lineFeedIndex = configer.getStartIndex();
                } else {
                    startIndex = configer.getPrintPosition(msg);
                }

                printCommand.setAbsolutePosition(startIndex);

                // 如果需要打印多行数据，则先打印第1行，否则打印全部数据
                if (lineFeedData != null && !lineFeedDataAdded) {
                    printCommand.addText(lineFeedData[0]);
                    lineFeedDataAdded = true;
                } else {
                    printCommand.addText(msg);
                }
            }

            printCommand.lineFeed();

            // 如果不需要换行，直接进行下一次循环
            if (lineFeedData == null) {
                continue;
            }

            for (int i1 = 1; i1 < lineFeedData.length; i1++) {
                printCommand.setAbsolutePosition(lineFeedIndex)
                        .addText(lineFeedData[i1])
                        .lineFeed();
            }
        }

        return printCommand;
    }


}
