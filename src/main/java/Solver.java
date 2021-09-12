import java.util.Arrays;
import java.util.Scanner;

public class Solver {
    final static double D = 0.1;
    final static double V = 1;
    static double tau, h;

    Solver() {
        getData();
    }

    public static double getH() {
        return h;
    }

    void getData() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Pe:"); // 1
        double pe = scanner.nextDouble();
        h = pe * D / V;
        System.out.println("Enter Cu:"); // 1
        double cu = scanner.nextDouble();
        tau = h * cu / V;
        scanner.close();
    }

    public double[] multiply(double[] children, double number) {
        return Arrays.stream(children).map(i -> i * number).toArray();
    }

    double[] calculate(double time, double length) {

        int nLength = (int) (length / h);
        int nTime = (int) (time / tau);

        double[] data = new double[nLength];
        double coefA = (D / h / h + V / h / 2.0);
        double coefB = (D / h / h - V / h / 2.0);
        double coefC = coefA + coefB + 1 / tau;

        String msg = String.format("T = %s, L = %s, h = %s, tau = %s, nLength = %s, nTime = %s,\n" +
                "A = %s, B = %s, C = %s", time, length, h, tau, nLength, nTime, coefA, coefB, coefC);
        System.out.println(msg);

        double[] diagonal = new double[nLength];
        Arrays.fill(diagonal, coefC);
        diagonal[0] = 1;
        diagonal[nLength - 1] = 1;
        double[] lowDiagonal = new double[nLength];
        Arrays.fill(lowDiagonal, -coefA);
        lowDiagonal[0] = 0;
        lowDiagonal[nLength - 1] = 0;
        double[] upDiagonal = new double[nLength];
        Arrays.fill(upDiagonal, -coefB);
        upDiagonal[0] = 0;
        upDiagonal[nLength - 1] = 0;

//        System.out.println(Arrays.toString(diagonal));
//        System.out.println(Arrays.toString(upDiagonal));
//        System.out.println(Arrays.toString(lowDiagonal));
        //заповнення першого ряду
        data[0] = 1; //решта нулі
        System.out.println(Arrays.toString(data));

        for (int t = 1; t < nTime; t++) {
            double[] rightSide = multiply(data, curCoef(length));
            rightSide[0] = 1;
            rightSide[nLength - 1] = 0;
            data = thomasAlgorithm(lowDiagonal, diagonal, upDiagonal, rightSide);
//            System.out.println(Arrays.toString(data));
        }
        if (nTime * tau != time) {
            System.out.println("Last point fix");
            double[] rightSide = multiply(data, curCoef(length));
            rightSide[0] = 1;
            rightSide[nLength - 1] = 0;
            data = thomasAlgorithm(lowDiagonal, diagonal, upDiagonal, rightSide);
        }
        return data;
    }

    //TDMA, tridiagonal matrix algorithm
    private double[] thomasAlgorithm(double[] a, double[] b, double[] c, double[] d) {
        int n = a.length - 1;
        c[0] /= b[0];
        d[0] /= b[0];

        for (int i = 1; i < n; i++) {
            c[i] /= b[i] - a[i] * c[i - 1];
            d[i] = (d[i] - a[i] * d[i - 1]) / (b[i] - a[i] * c[i - 1]);
        }

        d[n] = (d[n] - a[n] * d[n - 1]) / (b[n] - a[n] * c[n - 1]);
        for (int i = n; i-- > 0; ) d[i] -= c[i] * d[i + 1];
        return d;
    }

    public double erfc(double z) {
        double t = 1.0 / (1.0 + 0.5 * Math.abs(z));

        // use Horner's method
        double ans = 1 - t * Math.exp(-z * z - 1.26551223 +
                t * (1.00002368 +
                        t * (0.37409196 +
                                t * (0.09678418 +
                                        t * (-0.18628806 +
                                                t * (0.27886807 +
                                                        t * (-1.13520398 +
                                                                t * (1.48851587 +
                                                                        t * (-0.82215223 +
                                                                                t * (0.17087277))))))))));
        if (z >= 0) return 1 - ans;
        else return 1 + ans;
    }

    double[] preciseSolution(double time, double length) {
        int nLength = (int) (length / h);
        double[] arr = new double[nLength];
        for (int i = 0; i < arr.length; i++) arr[i] = analyticalSolution(time, i * h);
        return arr;
    }

    void mse(double[] numeric, double time, double length) {
        double sum = 0;

        for (int i = 0; i < numeric.length; i++) {
            double precise = analyticalSolution(time, i * h);
            double close = numeric[i];
            System.out.format("precise: %s, estimated: %s\n", precise, close);
            sum += h * Math.pow(precise - close, 2);
        }
        double error = Math.sqrt(sum / (length)) * 100;
        System.out.println("E: " + error + "%");
    }

    private double analyticalSolution(double t, double x) {
        return .5 * (erfc((x - V * t) / Math.sqrt(D * t) / 2)
                + Math.exp(V * x / D) * erfc((x + V * t) / Math.sqrt(D * t) / 2));
    }

    public void analyticalArr(double t, double x) {
        double dx = x / 100;
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 100; i++) s.append(analyticalSolution(t, dx * i)).append(", ");
        System.out.println(s);

    }

    private double curCoef(double x) {
        if (x / h == 100)
            return 1.5 / tau;
        else {
            System.out.println("! :" + x / h);
            return 1 / tau;
        }
    }
}
