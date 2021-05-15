import java.awt.image.Kernel;

public class KernelFunction {
    /**
     * 核函数对象
     */
    //核函数
    private final String kernel;

    //kernel='poly'时生效
    private final int degree;

    //kernel='rbf'时生效
    private final int gamma;

    public KernelFunction(String kernel, int degree, int gamma) {
        this.kernel = kernel;
        this.degree = degree;
        this.gamma = gamma;
    }

    /**
     * 计算核函数值
     * @param para1 参数1
     * @param para2 参数2
     * @return 核函数值
     */
    public double calculate(double[] para1, double[] para2){
        double result = 0;
        switch (kernel){
            case "rbf":
                result = rbf(gamma, para1, para2);
                break;
            case "poly":
                result = poly(degree, para1, para2);
                break;
            default:
                break;
        }//end of switch
        return result;
    }

    /**
     * 多项式核函数
     * @param p 次数
     * @param x 参数1
     * @param y 参数2
     * @return 核函数值
     */
    private double poly(int p, double[] x,double[] y){
        double sum = 0;
        for (int i = 0; i < x.length; i++) {
            sum += x[i] * y[i];
        }//end of for
        return Math.pow(sum + 1, p);
    }

    /**
     * 径向基核函数
     * @param gamma 系数
     * @param x 参数1
     * @param y 参数2
     * @return 核函数值
     */
    private double rbf(int gamma, double[] x, double[] y){
        double sum = 0;
        for (int i = 0; i < x.length; i++) {
            sum += (x[i] - y[i]) * (x[i] - y[i]) ;
        }//end of for
        return Math.exp(-gamma * sum);
    }
}
