package TestCGandMultiVector;

public class testarraycopy {
    public static void main(String[] args) {
        double[] x = new double[10];
        double[] y = new double[120];
        for(int i = 0; i < x.length; i++) x[i] = i * 100;
        for(int i = 0; i < y.length; i++) y[i] = i;
        System.arraycopy(x, 0, y, 10, x.length);
        for(int i = 0; i < y.length; i++){
            System.out.print(y[i] + "  ");
        }
    }
}
