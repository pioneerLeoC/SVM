public class Main {

    public static void main(String[] args) {
        ReadData.readExcel();
        double[] weight = Tools.calWeight(100, ReadData.getLabel().length);
        Algorithm temp = new Algorithm(5, weight, 0, "rbf", 2, 200);
        temp.fit(ReadData.getData(),ReadData.getLabel());

//        System.out.println(Arrays.toString(temp.getAlpha()));
        int right = 0;
        for (int i = 0; i < ReadData.getData().length; i++) {
            int predict = temp.predict(ReadData.getData()[i]);
            if (predict == ReadData.getLabel()[i])right++;
        }
        System.out.println(right);
    }
}
