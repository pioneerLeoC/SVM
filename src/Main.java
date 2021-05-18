import java.util.Arrays;

public class Main {

    public double func(int c, String kernel, int degree, int gamma){
        return 0;
    }

    public double func(int c, String kernel, int degree, int gamma, int k){
        return 0;
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        ReadData.readExcel();// 读取数据
//        for (int i = 0; i < ReadData.getData().length; i++) {
//            System.out.println(Arrays.toString(ReadData.getData()[i]));
//        }
        Tools.splitData(ReadData.getData(),ReadData.getLabel(),10);// 将数据划分为10等分

        double avg = 0;
        //将数据分为训练集和测试集
        for (int i = 0; i < 10; i++) {
            // 每份的数据量
            int per = ReadData.getData().length / 10;
            // 属性数量
            int att = ReadData.getData()[0].length;
            // 存储训练数据集
            double[][] trainData = new double[per * 9][att];
            // 存储标签数据
            double[] trainLabel = new double[per * 9];
            // 存储测试数据集
            double[][] testData;
            // 存储测试标签
            double[] testLabel;
            testData = Tools.sDatas[i];
            testLabel = Tools.sTargets[i];

            int num = 0;
            for (int j = 0; j < 10; j++) {
                if (j != i){
                    for (int k = 0; k < per; k++) {
                        trainData[num] = Tools.sDatas[j][k];
                        trainLabel[num] = Tools.sTargets[j][k];
                        num++;
                    }//end of for
                }//end of if
            }//end of for

            double[] weight = Tools.calWeight(100, trainData, trainLabel);
            double[] weight2 = Tools.calWeight(1000, trainData, trainLabel,true, 3);
//            System.out.println(Arrays.toString(weight));
//            System.out.println(Arrays.toString(weight2));

            Algorithm temp = new Algorithm(20, weight, 0, "rbf", 2, 500);
            Algorithm temp2 = new Algorithm(20, weight2, 0, "rbf", 2, 1000);
            temp.fit(trainData, trainLabel);
            temp2.fit(trainData, trainLabel);
//            System.out.println(Arrays.toString(temp.getAlpha()));
//            System.out.println(Arrays.toString(temp2.getAlpha()));

            int right = 0;
            int right2 = 0;
            for (int k = 0; k < testData.length; k++) {
                int predict = temp.predict(testData[k]);
                int predict2 = temp2.predict(testData[k]);
                if (predict == testLabel[k]) right++;
                if (predict2 == testLabel[k]) right2++;
            }//end of for
            avg += right2 / 25.0;
            System.out.println(right / 25.0 + " " + right2 / 25.0);
            if (right2 / 25.0 < 0.5){
                System.out.println(Arrays.toString(temp2.getAlpha()));
            }
        }//end of for
        System.out.println("正确率为：" + (avg / 10));
        long endTime = System.currentTimeMillis();    // 获取结束时间
        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    // 输出程序运行时间
    }
}
