import java.util.Arrays;


public class Main {


    public static void main(String[] args) {
        Solver solver = new Solver();
        double length = 10;
        double time = 5;
        double[] numeric = solver.calculate(time,length);
        double[] precise = solver.preciseSolution(time, length);
        solver.mse(numeric, time, length);
//        solver.analyticalArr(5,10);
    }
}
