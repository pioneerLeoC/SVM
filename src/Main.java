import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        ReadData.readExcel();
        Tools.splitData(ReadData.getData(),ReadData.getLabel(),10);

        for (int i = 0; i < 10; i++) {
            int per = ReadData.getData().length / 10;
            int att = ReadData.getData()[0].length;
            double[][] trainData = new double[per * 9][att];
            double[] trainLabel = new double[per * 9];
            double[][] testData;
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
                    }
                }
            }

            double[] weight = Tools.calWeight(100,trainData,trainLabel,false);
            Algorithm temp = new Algorithm(10, weight, 1e-3, "rbf", 2, 50);
            temp.fit(trainData, trainLabel);

            int right = 0;
            for (int k = 0; k < testData.length; k++) {
                int predict = temp.predict(testData[k]);
                if (predict == testLabel[k]) right++;
            }
            System.out.println(right / 25.0);
        }

    }
}
