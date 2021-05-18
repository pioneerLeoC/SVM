import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

public class ReadData {

    private static double[][] data;
    private static double[] label;

    public static double[][] getData() {
        return data;
    }

    public static double[] getLabel() {
        return label;
    }

    /**
     * 读取Excel测试，兼容 Excel 2003/2007/2010
     */
    public static void readExcel()
    {
        try {
            //同时支持Excel 2003、2007
            File excelFile = new File(".\\src\\data\\data.xls"); //创建文件对象
            if (!excelFile.isFile()) System.out.println("文件不存在");
            FileInputStream is = new FileInputStream(excelFile); //文件流
            Workbook workbook = WorkbookFactory.create(is); // 这种方式 Excel 2003/2007/2010 都是可以处理的

            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows(); //获取总行数
            Row row = sheet.getRow(0);
            int cellCount = row.getPhysicalNumberOfCells(); //获取总列数
            data = new double[rowCount][cellCount - 1];
            label = new double[rowCount];

            //遍历每一行
//            System.out.println(rowCount + " " + cellCount);
            for (int r = 0; r < rowCount; r++) {
                row = sheet.getRow(r);
                //遍历每一个单元格
                for (int c = 0; c < cellCount; c++) {
                    Cell cell = row.getCell(c);
                    CellType cellType = cell.getCellType();
                    double cellValue = cell.getNumericCellValue();
                    if (c != (cellCount - 1)) data[r][c] = cellValue;
                    else label[r] = cellValue;
                }
            }

//            double[] max = new double[label.length];
//            for (int i = 0; i < data[0].length; i++) {
//                for (double[] datum : data) {
//                    if (datum[i] > max[i]) max[i] = datum[i];
//                }
//            }

            //归一化
//            for (int i = 0; i < data.length; i++) {
//                for (int j = 0; j < data[i].length; j++) {
//                    data[i][j] /= max[j];
//                }
//            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ReadData.readExcel();
        System.out.println(Arrays.toString(ReadData.label));
    }
}
