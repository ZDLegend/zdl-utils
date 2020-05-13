package zdl.util.office;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ZDLegend
 * @create 2018/5/7
 */

public class ExcelReader {

//    private static final String SUFFIX_2003 = ".xls";
//    private static final String SUFFIX_2007 = ".xlsx";

    public static Workbook initWorkBook(InputStream is, String password) throws IOException {
        return WorkbookFactory.create(is, password);
    }

    public static Workbook initWorkBook(File excelFile, String password) throws IOException {
        return WorkbookFactory.create(excelFile, password);
    }

    /**
     * 对于大数据的Xlsx文件，POI3.8提供了SXSSFSXSSFWorkbook类，采用缓存方式进行大批量写文件。
     */
    public static Workbook initBigExcel(InputStream is) throws IOException {
        return new SXSSFWorkbook(new XSSFWorkbook(is));
    }

    public static List<ExcelInfo> parseWorkbookToMatrix(Workbook workbook) {
        List<ExcelInfo> excelInfos = new ArrayList<>();

        //依次解析每一个Sheet
        workbook.forEach(rows -> {
            ExcelInfo excelInfo = new ExcelInfo();
            List<List<String>> array = new ArrayList<>();
            excelInfo.setSheetName(rows.getSheetName());
            excelInfos.add(excelInfo);
            rows.forEach(row -> {
                List<String> s = parseRow(row);
                if (excelInfo.getFirstRow() == null && rowIsFull(s)) {
                    excelInfo.setFirstRow(s);
                    return;
                }

                if (excelInfo.getFirstRow() != null) {
                    array.add(s);
                }
            });

            excelInfo.setContent(array);
        });

        return excelInfos;
    }

    //解析行
    private static List<String> parseRow(Row row) {
        List<String> rst = new ArrayList<>();
        row.forEach(cell -> {
            //取出cell中的value
            rst.add(cell.getStringCellValue().replace(" ", "").trim());
        });

        return rst;
    }

    //判断某一行是否全部填写
    public static boolean rowIsFull(List<String> row) {
        if (CollectionUtils.isEmpty(row)) {
            return false;
        } else {
            return row.stream().noneMatch(StringUtils::isBlank);
        }
    }

    //判断某一行是否全空
    public static boolean rowIsBlank(List<String> row) {
        if (CollectionUtils.isEmpty(row)) {
            return false;
        } else {
            return row.stream().noneMatch(StringUtils::isNotBlank);
        }
    }

    public static void main(String[] args) {
        File file = new File("C:\\Users\\zhangminghao5\\Desktop\\报告.xlsx");
        try (InputStream is = new FileInputStream(file)) {
            List<ExcelInfo> json = parseWorkbookToMatrix(initWorkBook(is, null));
            System.out.println(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
