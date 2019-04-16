package zdl.office;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
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

public class ExcelParseUtil {

    private static final String SUFFIX_2003 = ".xls";
    private static final String SUFFIX_2007 = ".xlsx";

    public static Workbook initWorkBook(String fileName, InputStream is) throws IOException {
        Workbook workbook;
        if (fileName.endsWith(SUFFIX_2003)) {
            workbook = new HSSFWorkbook(is);
        } else if (fileName.endsWith(SUFFIX_2007)) {
            workbook = new XSSFWorkbook(is);
        } else {
            System.out.println("非EXCEL文件");
            throw new RuntimeException("非EXCEL文件");
        }
        return workbook;
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
            //定义每一个cell的数据类型
            cell.setCellType(CellType.STRING);
            //取出cell中的value
            rst.add(cell.getStringCellValue().replace(" ", "").trim());
        });

        return rst;
    }

    //判断某一行是否全部填写
    public static boolean rowIsFull(List<String> row) {

        if (row.size() == 0) {
            return false;
        }

        for (String cell : row) {
            if (StringUtils.isBlank(cell)) {
                return false;
            }
        }
        return true;
    }

    //判断某一行是否全空
    public static boolean rowIsBlank(List<String> row) {
        for (String cell : row) {
            if (StringUtils.isNotBlank(cell)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        File file = new File("C:\\Users\\zhangminghao5\\Desktop\\报告.xlsx");
        try (InputStream is = new FileInputStream(file)) {
            List<ExcelInfo> json = parseWorkbookToMatrix(initWorkBook("报告.xlsx", is));
            System.out.println(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
