import org.apache.commons.math3.stat.descriptive.summary.Sum;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class Tools {
    //划分后的数据
    public static double[][][] sDatas;
    //划分后的标签
    public static double[][] sTargets;
    /**
     * 划分数据集
     * @param data 待划分数据
     * @param target 待划分标签
     * @param KFold 份数
     */
    public static void splitData(double[][] data, double[] target, int KFold){
        int per = data.length / KFold;
        int att = data[0].length;
        sDatas = new double[KFold][per][att];
        sTargets = new double[KFold][per];

        Random rand = new Random();
        LinkedList<Double> currentTarget = new LinkedList<>();
        LinkedList<double[]> currentData = new LinkedList<>(Arrays.asList(data));
        for(double temp:target)currentTarget.add(temp);

        for (int i = 0; i < KFold; i++) {
            for (int j = 0; j < per; j++) {
                int site = rand.nextInt(currentTarget.size());
                sDatas[i][j] = currentData.remove(site);
                sTargets[i][j] = currentTarget.remove(site);
            }
        }
    }

    /**
     * 计算样本惩罚项权重
     * @param paraC 惩罚项系数
     * @return 样本权重
     */
    public static double[] calWeight(int paraC, double[][] data, double[] target){
        int len = target.length;
        double[] result = new double[len];
        for (int i = 0; i < len; i++) {
            result[i] = paraC;
        }// end of for
        return result;
    }

    /**
     *
     * @param paraC 惩罚项系数
     * @param data 数据集
     * @param target 标签
     * @param flag 是否采用FSVM
     * @param k 紧邻数
     * @return 权重
     */
    public static double[] calWeight(int paraC, double[][] data, double[] target, boolean flag, int k){
        int len = target.length;
        double[] result = new double[len];
        for (int i = 0; i < len; i++) {
            result[i] = paraC;
        }// end of for

        if (flag){
            double[] weight = new double[data.length];
            double max = 0;
            double[] p2p = calP2P(data, k);
            double[] p2c = calP2C(data, target);
//            System.out.println(Arrays.toString(p2p));
//            System.out.println(Arrays.toString(p2c));

            for (int i = 0; i < data.length; i++) {
                weight[i] = p2p[i] * p2c[i];
                if (weight[i] > max) max = weight[i];
            }//end of for
            // 归一化
            for (int i = 0; i < data.length; i++) {
                result[i] *= weight[i] / max;
            }//end of for
        }//end of if
        return result;
    }

    /**
     * 计算点到点的距离（权重）
     * @param data 数据
     * @param k 近邻数
     * @return n×n二维数组
     */
    private static double[] calP2P(double[][] data, int k){
        // 返回的结果（权重）
        double[] result = new double[data.length];
        //存储任意两点的距离
        double[][] distances = new double[data.length][data.length];
        //存储点到k个近邻点的距离和的倒数
        double[] kDis = new double[data.length];

        //计算任意两点的距离
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                double sum = 0;
                if(i == j){
                    distances[i][j] = 0;
                    continue;
                }
                for (int l = 0; l < data[j].length; l++) {
                    sum += Math.abs(data[i][l] - data[j][l]);
                }// end of for
                distances[i][j] = sum;
            }//end of for
        }//end of for

        //计算各点到k个近邻的距离倒数
        double max = 0;
        for (int i = 0; i < result.length; i++) {
            Arrays.sort(distances[i]);
            double sum = 0;
            for (int j = 1; j <= k; j++) {
                sum += distances[i][j];
            }//end of for
            result[i] = 1 / sum;
            if (result[i] > max)max = result[i];
        }//end of if


        //归一化处理
        for (int i = 0; i < result.length; i++) {
            result[i] = result[i] / max;
        }//end of for
        return result;
    }

    /**
     * 计算类中心
     * @param data 数据
     * @param target 标签
     * @return 2×attr数组
     */
    private static double[][] calCenter(double[][] data, double[] target) {
        int len = data.length;
        int attr = data[0].length;
        double[][] result = new double[2][attr];
        double[][] tol = new double[2][attr];
        int[] num = new int[2];
        for (int i = 0; i < len; i++) {
            if (target[i] == 1) {
                for (int k = 0; k < attr; k++) {
                    tol[0][k] += data[i][k];
                }// end of for
                num[0]++;
            } else {
                for (int k = 0; k < attr; k++) {
                    tol[1][k] += data[i][k];
                }// end of for
                num[1]++;
            }// end of if
        }// end of if
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < attr; j++) {
                result[i][j] = tol[i][j] / num[i];
            }//end of for
        }// end of for
        return result;
    }

    /**
     * 计算点到类中心的距离(权重)
     * @param data 数据
     * @param target 标签
     * @return 一维数组
     */
    private static double[] calP2C(double[][] data, double[] target){
        double[][] center = calCenter(data, target);
        double[] maxDis = new double[2];
        double[] p2c = new double[target.length];
        double[] result = new double[target.length];

        for (int i = 0; i < target.length; i++) {
            double sum = 0;
            for (int j = 0; j < data[i].length; j++) {
                if (target[i] == 1) sum += Math.abs(data[i][j] - center[0][j]);
                else sum += Math.abs(data[i][j] - center[1][j]);
            }//end of for
            if (target[i] == 1 && sum > maxDis[0]) maxDis[0] = sum;
            if (target[i] == -1 && sum > maxDis[1]) maxDis[1] = sum;
            p2c[i] = sum;
        }//end of for

        for (int i = 0; i < data.length; i++) {
            if (target[i] == 1){
                result[i] = 1 - p2c[i] / (maxDis[0]+0.001);
            }else{
                result[i] = 1 - p2c[i] / (maxDis[1]+0.001);
            }// end of if
        }// end of for
        return result;
    }
}
