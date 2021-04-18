import java.io.*;
import com.xeiam.xchart.CSVExporter;
import com.xeiam.xchart.CSVImporter;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.CSVImporter.DataOrientation;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.SeriesColor;
import com.xeiam.xchart.SeriesMarker;
import com.xeiam.xchart.StyleManager.ChartTheme;
import com.xeiam.xchart.SeriesLineStyle;
import com.xeiam.xchart.StyleManager.LegendPosition;
import com.xeiam.xchart.SwingWrapper;
import java.util.Map;

public class AgChalcogenideHysteresisPlot{
    public static void main(String[] args){
        AgChalcogenideHysteresisPlot agChalcogenideHysteresisPlot = new AgChalcogenideHysteresisPlot();
        agChalcogenideHysteresisPlot.go(args);
    }

    private void go(String[] args){
        double frequency = 10;
        double timeStep = 1E-6;
        double amplitude = .98;
        double totalTime = 5E-2;

        try {
            frequency = Double.parseDouble(args[0]);
            timeStep = Double.parseDouble(args[1]);
            amplitude = Double.parseDouble(args[2]);
            totalTime = Double.parseDouble(args[3]);
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
             // just ignore
        }


        MSSMemristor memristor = new AgChalcMemristor(0);
        int numTimeSteps = (int) (totalTime / timeStep);

        double[] current = new double[numTimeSteps];
        double[] voltage = new double[numTimeSteps];
        double[] time = new double[numTimeSteps];
        double[] resistance = new double[numTimeSteps];

        //  System.out.println(memristor.schottkeyReverseAlpha);
        //  System.out.println(memristor.schottkeyAlpha);

        for (int i = 0; i < numTimeSteps; i++) {
            time[i] = (i + 1) * timeStep;
            voltage[i] = amplitude * Math.sin(time[i] * 2 * Math.PI * frequency);
            current[i] = memristor.getCurrent(voltage[i]) * 1000; // in mA
            memristor.dG(voltage[i], timeStep);
            resistance[i] = voltage[i] / current[i] * 1000; // in Ohm
        }


        // File csvFile = new File("./datas1.csv");
        // String[] xAndYData;
        // xAndYData = CSVImporter.getSeriesDataFromCSVRows(csvFile);       
       
        Chart chart = CSVImporter.getChartFromCSVDir("./data", DataOrientation.Columns, 300, 270, ChartTheme.Matlab);
        chart.setYAxisTitle("Current [mA]");
        chart.setXAxisTitle("Voltage [V]");
        chart.getStyleManager().setLegendPosition(LegendPosition.InsideNW);
        chart.getStyleManager().setPlotGridLinesVisible(false);

        Map<String, Series> seriesMap = chart.getSeriesMap();
        Series series0 = seriesMap.get("device_data");
        // Series series1 = seriesMap.get("device_data2");

        series0.setLineStyle(SeriesLineStyle.NONE);
        series0.setMarker(SeriesMarker.CIRCLE);
        series0.setMarkerColor(SeriesColor.PINK);

        // series1.setMarker(SeriesMarker.NONE);
        // series1.setLineColor(SeriesColor.BLUE);
        // Chart chart = new Chart(600, 600, ChartTheme.Matlab);
        // chart.setChartTitle("Hysteresis Loop " + frequency + " Hz");
        // chart.setYAxisTitle("Current [mA]");
        // chart.setXAxisTitle("Voltage [V]");
        chart.getStyleManager().setLegendPosition(LegendPosition.InsideSE);
        // chart.addSeries("Gaussian Blob 1", getAxisData(xAndYData[0]),getAxisData(xAndYData[1]));

        Series series = chart.addSeries(((int) frequency + " Hz"), voltage, current);

        series.setMarker(SeriesMarker.CIRCLE);
        new SwingWrapper(chart).displayChart();
        CSVExporter.writeCSVColumns(series, "./results/");

    }

}