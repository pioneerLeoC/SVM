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
     * @param flag 是否采用模糊支持向量机
     * @return 样本权重
     */
    public static double[] calWeight(int paraC, double[][] data, double[] target, boolean flag){
        int len = target.length;
        double[] result = new double[len];
        for (int i = 0; i < len; i++) {
            result[i] = paraC;
        }// end of if
        if (flag){

        }
        return result;
    }
}
