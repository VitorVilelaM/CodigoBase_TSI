package Strategys;

import DataManipulation.MarketData;
import DataManipulation.WriteData;
import com.tictactec.ta.lib.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author vitor
 */
// WEKA -> Para ML
public class DataBase implements Strategy {

    public static final int LONG = 20;
    public static final int SHORT = 7;
    private DecimalFormat formatador = new DecimalFormat("0.00");
    private WriteData wd = new WriteData();

    private Core c = new Core();

    private double closePrices[];
    private double highPrices[];
    private double lowPrices[];
    private Date dates[];

    private double outSMAl[];
    private double outEMAl[];
    private double outWMAl[];

    private double outSMAc[];
    private double outEMAc[];
    private double outWMAc[];

    private double outRSIL[];
    private double outRSIC[];

    private double outADXL[];
    private double outADXC[];

    private double outSAR[];

    private double oftenPrice;

    // Nao lembro o nome das saidas
    private double diff[];
    private int binary[];

    private int startIdx, endIdx, atualDay, days;

    private MInteger beginSMAL = new MInteger();
    private MInteger beginEMAL = new MInteger();
    private MInteger beginWMAL = new MInteger();

    private MInteger beginSMAC = new MInteger();
    private MInteger beginEMAC = new MInteger();
    private MInteger beginWMAC = new MInteger();

    private MInteger beginRSIL = new MInteger();
    private MInteger beginRSIC = new MInteger();

    private MInteger beginADXL = new MInteger();
    private MInteger beginADXC = new MInteger();

    private MInteger beginSAR = new MInteger();

    private MInteger lengthSMAL = new MInteger();
    private MInteger lengthEMAL = new MInteger();
    private MInteger lengthWMAL = new MInteger();

    private MInteger lengthSMAC = new MInteger();
    private MInteger lengthEMAC = new MInteger();
    private MInteger lengthWMAC = new MInteger();

    private MInteger lengthRSIL = new MInteger();
    private MInteger lengthRSIC = new MInteger();

    private MInteger lengthADXL = new MInteger();
    private MInteger lengthADXC = new MInteger();

    private MInteger lengthSAR = new MInteger();

    private RetCode retCodeSmaL, retCodeEmaL, retCodeWmaL, retCodeEmaC, retCodeSmaC, retCodeWmaC, retCodeRSIL, retCodeRSIC, retCodeADXL, retCodeADXC, retCodeSAR;

    public DataBase(int totalDays) {
        this.closePrices = new double[totalDays];
        this.highPrices = new double[totalDays];
        this.lowPrices = new double[totalDays];
        this.dates = new Date[totalDays];

        this.outSMAl = new double[totalDays];
        this.outEMAl = new double[totalDays];
        this.outWMAl = new double[totalDays];

        this.outSMAc = new double[totalDays];
        this.outEMAc = new double[totalDays];
        this.outWMAc = new double[totalDays];

        this.outRSIL = new double[totalDays];
        this.outRSIC = new double[totalDays];

        this.outADXL = new double[totalDays];
        this.outADXC = new double[totalDays];

        this.outSAR = new double[totalDays];

        this.diff = new double[totalDays];
        this.binary = new int[totalDays];

        this.days = totalDays;
        this.atualDay = 0;
        this.startIdx = 0;
        this.endIdx = totalDays - 1;
    }

    @Override
    public void Create() {
        this.oftenPrice = 0;
        wd.Write("Date,CP,CP -1,CP -2,CP -3,CP -4,CP -5,R -1,R -2,R -3,R -4,R -5,SMAL,EMAL,WMAL,SMAC,EMAC,WMAC,RSIC,RSIL,ADXC,ADXL,SAR,R,B");
    }

    // LAG FEATURES     
    @Override
    public void Start(Date init) {
    }

    @Override
    public void Update(MarketData update) {

        this.closePrices[this.atualDay] = update.getClose();
        this.highPrices[this.atualDay] = update.getHigh();
        this.lowPrices[this.atualDay] = update.getLow();
        this.dates[this.atualDay] = update.getDate();

        if (this.atualDay > 0) {

            this.diff[this.atualDay - 1] = this.closePrices[this.atualDay - 1] - this.closePrices[this.atualDay];

            if (diff[this.atualDay - 1] > 0) {
                this.binary[this.atualDay - 1] = 1;
            } else {
                this.binary[this.atualDay - 1] = 0;
            }
        }

        this.retCodeSmaL = c.sma(startIdx, endIdx, closePrices, LONG, beginSMAL, lengthSMAL, outSMAl);
        this.retCodeEmaL = c.ema(startIdx, endIdx, closePrices, LONG, beginEMAL, lengthEMAL, outEMAl);
        this.retCodeWmaL = c.wma(startIdx, endIdx, closePrices, LONG, beginWMAL, lengthWMAL, outWMAl);

        this.retCodeSmaC = c.sma(startIdx, endIdx, closePrices, SHORT, beginSMAC, lengthSMAC, outSMAc);
        this.retCodeEmaC = c.ema(startIdx, endIdx, closePrices, SHORT, beginEMAC, lengthEMAC, outEMAc);
        this.retCodeWmaC = c.wma(startIdx, endIdx, closePrices, SHORT, beginWMAC, lengthWMAC, outWMAc);

        this.retCodeRSIL = c.rsi(startIdx, endIdx, closePrices, LONG, beginRSIL, lengthRSIL, outRSIL);
        this.retCodeRSIC = c.rsi(startIdx, endIdx, closePrices, SHORT, beginRSIC, lengthRSIC, outRSIC);

        this.retCodeADXC = c.adx(startIdx, endIdx, highPrices, lowPrices, closePrices, SHORT, beginADXC, lengthADXC, outADXC);
        this.retCodeADXL = c.adx(startIdx, endIdx, highPrices, lowPrices, closePrices, LONG, beginADXL, lengthADXL, outADXL);

        this.retCodeSAR = c.sar(startIdx, endIdx, highPrices, lowPrices, 0.00, 5.87, beginSAR, lengthSAR, outSAR);

        // smaL, emaL, wmaL, smaC, emaC, wmaC, rsiC, rciL, adxL, adxC, sar
        this.oftenPrice = update.getClose();
    }

    @Override
    public void Finish(MarketData finish) {
        if (atualDay != days && atualDay > 0) {
            if (retCodeSmaL == RetCode.Success && retCodeEmaL == RetCode.Success && retCodeWmaL == RetCode.Success
                    && retCodeSmaC == RetCode.Success && retCodeEmaC == RetCode.Success && retCodeWmaC == RetCode.Success
                    && retCodeRSIC == RetCode.Success && retCodeRSIL == RetCode.Success
                    && retCodeADXC == RetCode.Success && retCodeADXL == RetCode.Success
                    && retCodeSAR == RetCode.Success) {

                StringBuilder line = new StringBuilder();
                SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd");

                line.append(sdf.format(dates[atualDay - 1]));
                line.append(",");

                line.append(closePrices[atualDay - 1]);
                line.append(",");

                beforeClosePrices(line);
                beforeRegressionPrices(line);

                if (atualDay >= beginSMAL.value) {
                    line.append(round(outSMAl[atualDay - beginSMAL.value], 2));
                    line.append(",");
                } else {
                    line.append("NA,");
                }
                if (atualDay >= beginEMAL.value) {
                    line.append(round(outEMAl[atualDay - beginEMAL.value], 2));
                    line.append(",");
                } else {
                    line.append("NA,");
                }
                if (atualDay >= beginWMAL.value) {
                    line.append(round(outWMAl[atualDay - beginWMAL.value], 2));
                    line.append(",");
                } else {
                    line.append("NA,");
                }
                if (atualDay >= beginSMAC.value) {
                    line.append(round(outSMAc[atualDay - beginSMAC.value], 2));
                    line.append(",");
                } else {
                    line.append("NA,");
                }
                if (atualDay >= beginEMAC.value) {
                    line.append(round(outEMAc[atualDay - beginEMAC.value], 2));
                    line.append(",");
                } else {
                    line.append("NA,");
                }
                if (atualDay >= beginWMAC.value) {
                    line.append(round(outWMAc[atualDay - beginWMAC.value], 2));
                    line.append(",");
                } else {
                    line.append("NA,");
                }
                if (atualDay >= beginRSIC.value) {
                    line.append(round(outRSIC[atualDay - beginRSIC.value], 2));
                    line.append(",");
                } else {
                    line.append("NA,");
                }
                if (atualDay >= beginRSIL.value) {
                    line.append(round(outRSIL[atualDay - beginRSIL.value], 2));
                    line.append(",");
                } else {
                    line.append("NA,");
                }
                if (atualDay >= beginADXC.value) {
                    line.append(round(outADXC[atualDay - beginADXC.value], 2));
                    line.append(",");
                } else {
                    line.append("NA,");
                }
                if (atualDay >= beginADXL.value) {
                    line.append(round(outADXL[atualDay - beginADXL.value], 2));
                    line.append(",");
                } else {
                    line.append("NA,");
                }
                if (atualDay >= beginSAR.value) {
                    line.append(round(outSAR[atualDay - beginSAR.value], 2));
                    line.append(",");
                } else {
                    line.append("NA,");
                }
                line.append(diff[atualDay - 1]);
                line.append(",");
                line.append(binary[atualDay - 1]);
                //System.out.println(line.toString());
                wd.Write(line.toString());
            } else {
                System.out.println("Error!");
            }
        }
        this.atualDay++;

    }

    public void beforeClosePrices(StringBuilder line) {
        if (this.atualDay > 5) {
            for (int j = this.atualDay - 1; j > this.atualDay - 6; j--) {
                line.append(closePrices[j - 1]);
                line.append(",");
            }
        } else {
            for (int j = 0; j < 5; j++) {
                line.append("NA");
                line.append(",");
            }
        }
    }

    public void beforeRegressionPrices(StringBuilder line) {
        if (this.atualDay > 5) {
            for (int j = this.atualDay - 1; j > this.atualDay - 6   ; j--) {
                line.append((diff[j - 1]));
                line.append(",");
            }

        } else {
            for (int j = 0; j < 5; j++) {
                line.append("NA");
                line.append(",");
            }
        }

    }

    @Override
    public void Venda(MarketData update) {
    }

    @Override
    public void Compra(MarketData update) {
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
