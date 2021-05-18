import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Algorithm {
    //迭代次数
    private int epochs;

    //每个样本的惩罚项权重
    private double[] C;

    //提前中止训练时的误差值上限，避免迭代太久
    private double tol;

    //对象函数
    public KernelFunction fuc;

    //记录误差
    double[] error;

    //记录支持向量的编号
    private ArrayList<Integer> supportVectors = new ArrayList<>();

    //记录支持向量的x
    private ArrayList<double[]> supportVectors_x = new ArrayList<>();

    //记录支持向量的y
    private ArrayList<Double> supportVectors_y = new ArrayList<>();

    //记录alpha
    private double[] alpha;

    //参数b的值
    private double b;


    public Algorithm() {
    }

    public Algorithm(int epochs, double[] c, double tol, String kernel, int degree, int gamma) {
        this.epochs = epochs;
        C = c;
        this.tol = tol;
        this.fuc = new KernelFunction(kernel, degree, gamma);
    }

    public ArrayList<Integer> getSupportVectors() {
        return supportVectors;
    }

    public ArrayList<double[]> getSupportVectors_x() {
        return supportVectors_x;
    }

    public ArrayList<Double> getSupportVectors_y() {
        return supportVectors_y;
    }

    public double[] getAlpha() {
        return alpha;
    }

    /**
     * 初始化参数
     * @param paraX 数据
     * @param paraY 标签
     */
    private void initParams(double[][] paraX, double[] paraY){
        int samples = paraY.length;
        b = 0.0;
        alpha = new double[samples];
        error = new double[samples];
        //初始化E(初始时全为相反数)
        for (int i = 0; i < samples; i++) {
            error[i] = calValue(paraX[i]) - paraY[i];
        }//end of for
    }

    /**
     * 计算判别函数值
     * @param paraX 单个数据的参数数组
     * @return 判别函数值
     */
    public double calValue(double[] paraX){
        if(supportVectors.size() == 0){
            return 0;
        }//end of if
        else{
            double wx = 0;
            for (int i = 0; i < supportVectors_x.size(); i++) {
                wx += fuc.calculate(paraX, supportVectors_x.get(i)) * alpha[supportVectors.get(i)] *
                        supportVectors_y.get(i);
            }//end of for
            return wx + b;
        }//end of else
    }

    /**
     * 在支持向量中选择另一个最佳搭档
     * @param best_i 当前值位置
     * @return 搭档的位置
     */
    private int select(int best_i){
        int best = -1;
        ArrayList<Integer> validArr = new ArrayList<>();
        for (int i = 0; i < alpha.length; i++) {
            if(alpha[i] > 0 && i != best_i) validArr.add(i);
        } // end of for
//        System.out.println(validArr);

        if(!validArr.isEmpty()){
            double max = 0;
            for (int i:validArr) {
                double current = Math.abs(error[best_i] - error[i]);
                if (current > max) {
                    best = i;
                    max = current;
                }//end of if
            }// end of for
        }else{
            // 随机选择
            while (true){
                Random rand = new Random();
                int choose = rand.nextInt(alpha.length);
                if (choose != best_i){
                    best = choose;
                    break;
                }//end of if
            }//end of while
        }//end of if

        return best;
    }

    /**
     * 判断是否满足KKT条件
     * @param data 某一条数据
     * @param label 数据标签
     * @param site 数据位置
     * @return 是否满足KKT条件
     */
    private boolean meetKKT(double[] data, double label, int site){
        if (alpha[site] < C[site]) return label * calValue(data) >= 1 - tol;
        else return label * calValue(data) <= 1 + tol;
    }

    /**
     * 训练模型
     * @param paraX 数据
     * @param paraY 标签
     */
    public void fit(double[][] paraX, double[] paraY) {
        initParams(paraX, paraY);
        //SMO算法
        for (int k = 0; k < epochs; k++) {
            boolean flag = true;
            for (int i = 0; i < alpha.length; i++) {
                double[] x_i = paraX[i];
                double y_i = paraY[i];
                double alpha_i_old = alpha[i];

                // 外层循环：选择违反KKT条件的点
                if (!meetKKT(x_i, y_i, i)) {
                    flag = false;
                    //内层循环：选择使|Ei-Ej|最大的点j
                    int best_j = select(i);
                    double alpha_j_old = alpha[best_j];
                    double[] x_j = paraX[best_j];
                    double y_j = paraY[best_j];

                    // 进行更新操作
                    // 1. 获取无裁剪的最优alpha_2
                    double eta = fuc.calculate(x_i, x_i) + fuc.calculate(x_j, x_j) - 2.0 * fuc.calculate(x_i, x_j);
                    if (eta < 1e-3) {
                        continue;
                    }//end of if
                    double delta = y_j * (error[i] - error[best_j]) / eta;

                    double alpha_j_unc = alpha_j_old + delta;

                    // 3. 裁剪并得到new alpha_2
                    double tempL ;
                    double tempH ;
                    if (y_i == y_j) {
                        tempL = Math.max(0, alpha_i_old + alpha_j_old - C[i]);
                        tempH = Math.min(C[i], alpha_i_old + alpha_j_old);
                    } else {
                        tempL = Math.max(0, alpha_i_old - alpha_j_old);
                        tempH = Math.min(C[i], alpha_j_old - alpha_i_old + C[i]);
                    }// end of if

                    double alpha_j_new;
                    double alpha_i_new;
                    if (alpha_j_unc < tempL) alpha_j_new = tempL;
                    else alpha_j_new = Math.min(alpha_j_unc, tempH);

                    // 如果变化不大，则跳过
                    if (Math.abs(alpha_j_new - alpha_j_old) < 1e-6) {
                        continue;
                    }//end of if

                    // 4. 得到alpha_i_new
                    alpha_i_new = alpha_i_old + y_i * y_j * (alpha_j_old - alpha_j_new);

                    // 5. 更新alpha[i],alpha[j]
                    alpha[i] = alpha_i_new;
                    alpha[best_j] = alpha_j_new;

                    // 6. 更新b
                    double b_i_new = y_i - calValue(x_i) + b;
                    double b_j_new = y_j - calValue(x_j) + b;
//                    System.out.println(b_i_new + "," + b_j_new);

                    if (C[i] > alpha_i_new && alpha_i_new > 0) b = b_i_new;
                    else if (C[best_j] > alpha_j_new && alpha_j_new > 0) b = b_j_new;
                    else b = (b_i_new + b_j_new) / 2.0;

                    // 7. 更新error
                    for (int j = 0; j < error.length; j++) {
                        error[j] = calValue(paraX[j]) - paraY[j];
                    }
//                    System.out.println(Arrays.toString(x_i) + ":" + y_i);
//                    System.out.println(Arrays.toString(error));

                    // 8. 更新支持向量信息
                    supportVectors.clear();
                    supportVectors_x.clear();
                    supportVectors_y.clear();
                    for (int j = 0; j < alpha.length; j++) {
                        if (alpha[j] > 0 && alpha[j] < C[j]) {
                            supportVectors.add(j);
                            supportVectors_x.add(paraX[j]);
                            supportVectors_y.add(paraY[j]);
                        }//end of if
                    }//end of for
                }//end of if
            }// end of for
            if (flag)break;
        }//emd of for
    }

    /**
     * 概率预测
     * @param data 预测数据
     * @return 概率值
     */
    public double predictPro(double[] data){
        return 1 / (1 + Math.exp(-calValue(data)));
    }

    public int predict(double[] data){
        double pro = predictPro(data);
        if (pro >= 0.5) return 1;
        else return -1;
    }

    public static void main(String[] args) {
        double[] c = {3, 3, 3, 3};
        double[] y = {1, 1,-1,-1};
        double[][] x = {{0,0},{1,1},{1,0},{0,1}};
        Algorithm temp = new Algorithm(10, c, 0.0, "rbf",2,10);
        temp.fit(x, y);
        System.out.println(Arrays.toString(temp.getAlpha()));

        for (double[] doubles : x) {
            double predict = temp.predictPro(doubles);
            System.out.println(predict);
        }
//        double[] a = {1,0};
//        double[] b = {2,2};
//        double re = temp.fuc.calculate(a, a);
//        System.out.println(re);
    }
}
