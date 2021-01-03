package PlotUI;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.SamplingXYLineRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import static java.lang.StrictMath.abs;


public class PlotUIElectrodes {
    protected JPanel mainPanel;
    protected XYDataset dataset;
    protected ArrayList<JFreeChart> chart;
    protected Double[][] electrodeData;
    protected int rotation;


    public PlotUIElectrodes(ReadExcel readExcel) {
        mainPanel = new JPanel();


        //Initialize data from electrodes
        electrodeData = readExcel.getDataset();
        Double[] oneElectrode = new Double[electrodeData.length];
        chart = new ArrayList<JFreeChart>();

        //Set Layouts
        mainPanel.setLayout(new GridLayout(4, 4));

        rotation = 1;
        int electrode2Draw;
        int[] transMatrix;
        int[] equivalenceMatrix;
        String[] electrodeLabel;
        electrodeLabel = new String[]{"A1", "A2", "A3", "A4",
                "B1", "B2", "B3", "B4",
                "C1", "C2", "C3", "C4",
                "D1", "D2", "D3", "D4"};

        transMatrix = new int[]{0, 1, 2, 3, 11, 0, 1, 4, 10, 3, 2, 5, 9, 8, 7, 6};
        //{0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15}
        equivalenceMatrix = new int[]{0, 1, 2, 3, 7, 11, 15, 14, 13, 12, 8, 4, 5, 6, 10, 9};

        for (int i = 0; i < 16; i++) {
            // Mathematical algorithm to rotate the charts by 30deg
            if ((i == 5) || (i == 6) || (i == 9) || (i == 10)) {
                int core;
                core = (rotation + 1) / 3;
                electrode2Draw = equivalenceMatrix[((transMatrix[i] + core) % 4) + 12];
            } else {
                electrode2Draw = equivalenceMatrix[((rotation % 12) + transMatrix[i]) % 12];
            }
            for (int j = 0; j < oneElectrode.length; j++) {
                oneElectrode[j] = electrodeData[j][electrode2Draw];
            }
            dataset = createDataset(oneElectrode);
            //JFreeChart chart_info= createChart(dataset,"Electrode "+(i+1),oneElectrode);
            JFreeChart chart_info = createChart(dataset,
                    "Electrode " + (i + 1) + " - " + electrodeLabel[electrode2Draw],
                    oneElectrode);

            chart.add(chart_info); //Add charts for each electrode
            ChartPanel chartPanel = new ChartPanel(chart.get(i));

            chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            chartPanel.setBackground(Color.white);
            chartPanel.setMouseWheelEnabled(true);
            //chartPanel.add(createZoom(chartPanel,oneElectrode,chart_info));

            //Rearrange panels
            //mainPanel.add(rotateAntiC(chartPanel, chart_info));
            mainPanel.add(chartPanel);
        }
    }


    //forms dataset
    public JPanel getMainPanel() {
        return mainPanel;
    }

    private XYDataset createDataset(Double[] electrode) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series1 = new XYSeries("");

        for (double i = 0; i < electrode.length; i++) {
            series1.add(i, electrode[(int) i]);
        }
        dataset.addSeries(series1);

        return dataset;
    }

    //creates chart
    private JFreeChart createChart(XYDataset dataset, String title, Double[] electrode) {

        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                "", "",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        XYPlot plot = chart.getXYPlot();

        SamplingXYLineRenderer renderer = new SamplingXYLineRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        chart.getLegend().setFrame(BlockBorder.NONE);


        ValueAxis xAxis = plot.getDomainAxis();
        // Set label font size
        xAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 9));
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        // Set label font size
        yAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 9));

        //Finding maximum value in data to set yaxis range equal for all graphs
        double maxElement = 0;

        for (int i = 0; i < 16; i++) {

            for (int j = 0; j < electrode.length; j++) {
                if (abs(electrodeData[j][i]) > maxElement) {
                    maxElement = abs(electrodeData[j][i]);
                }
            }
        }
        yAxis.setRange(-maxElement, maxElement);

        chart.setTitle(new TextTitle(title,
                new Font("Calibri", Font.BOLD, 12)
                )
        );

        return chart;
    }
}
    /*
    public JButton rotateAntiC(ChartPanel chartPanel, JFreeChart chart){
        JButton rotateButton = new JButton(new AbstractAction("Rotate Axis") {
        @Override
        public void actionPerformed(ActionEvent e) {
        rotation++;
        }
        });
        return rotateButton;
    }

        ; public JButton createZoom(ChartPanel chartPanel, Double[] electrode, JFreeChart chart){
        double maxElement = 0;

        for(int i=0; i<16;i++) {

            for (int j = 0; j < electrode.length; j++) {
                if(abs(electrodeData[j][i]) > maxElement){
                    maxElement = abs(electrodeData[j][i]);
                }
            }
        }

        double finalMaxElement = maxElement;
        JButton autoZoom= new JButton(new AbstractAction("Restore Axis") {
            @Override
            public void actionPerformed(ActionEvent e) {
                chartPanel.restoreAutoDomainBounds();
                chart.getXYPlot().getRangeAxis().setRange(-finalMaxElement, finalMaxElement);


            }
        });

        return autoZoom;
    }

}
  */

