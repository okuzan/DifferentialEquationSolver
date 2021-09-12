import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class Graph extends ApplicationFrame {

    public Graph(String applicationTitle, String chartTitle, DefaultCategoryDataset dataset) {
        super(applicationTitle);
        JFreeChart lineChart = ChartFactory.createLineChart(
                chartTitle,
                "Length", "Function value",
                dataset, PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
        setContentPane(chartPanel);
    }

    private static DefaultCategoryDataset createDataset(double[] numeric, double[] precise, double h) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < numeric.length; i++) {
            dataset.addValue(numeric[i], "numeric", "" + h * i);
            dataset.addValue(precise[i], "precise", "" + h * i);
        }

        return dataset;
    }

    public static void main(String[] args) {
        Solver solver = new Solver();
        double length = 10;
        double time = 5;
        double h = Solver.getH();
        double[] numeric = solver.calculate(time, length);
        double[] precise = solver.preciseSolution(time, length);

        DefaultCategoryDataset dataset = createDataset(numeric, precise, h);
        Graph chart = new Graph(
                "Graph",
                "Differential equation", dataset);

        chart.pack();

        RefineryUtilities.centerFrameOnScreen(chart);
        chart.setVisible(true);
    }
}