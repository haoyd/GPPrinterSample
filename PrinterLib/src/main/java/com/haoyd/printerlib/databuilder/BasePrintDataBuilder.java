package com.haoyd.printerlib.databuilder;

import com.gprinter.command.EscCommand;

public class BasePrintDataBuilder {

    private EscCommand escCommand;

    public BasePrintDataBuilder() {
        this.escCommand =  new EscCommand();
    }

    protected void addLine() {

    }

}
