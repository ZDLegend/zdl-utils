package zdl.office;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * excel表格信息(以sheet为单位)
 *
 * @author ZDLegend
 * @create 2018/5/7
 */

public class ExcelInfo {

    //sheet名
    private String sheetName;

    //表格的第一行标题
    private List<String> firstRow;

    //表格内容
    private List<List<String>> content;

    //获取表格某一行数组
    public List<String> getListForRow(int offset) {
        return content.get(offset);
    }

    //获取表格某一列数组
    public List<String> getListForColumn(int offset) {
        List<String> list = new ArrayList<>();
        content.forEach(strings -> {
            if (StringUtils.isNotBlank(strings.get(offset))) {
                list.add(strings.get(offset));
            }
        });

        return list;
    }

    //按某一列内容分块
    public HashMap<String, List<JSONObject>> partsForColumn(String title) {

        HashMap<String, List<JSONObject>> map = new HashMap<>();

        int validate = getIndexForColumn(title);
        String validateName = null;

        for (int i = 0; i < getRowNum(); i++) {
            List<String> row = content.get(i);
            JSONObject json = transKeyValue(i, validateName, title);
            if (StringUtils.isNotBlank(row.get(validate))) {
                List<JSONObject> lists = new ArrayList<>();
                lists.add(json);
                validateName = row.get(validate);
                map.put(validateName, lists);
            } else if (StringUtils.isNotBlank(validateName) && map.containsKey(validateName)) {
                map.get(validateName).add(json);
            }
        }

        return map;
    }

    //把某一行变成和第一行标题对应的键值对
    public JSONObject transKeyValue(int index, String validateName, String validate) {
        JSONObject json = new JSONObject();
        List<String> row = content.get(index);

        int offet = -1;
        if (StringUtils.isNotBlank(validate)) {
            offet = firstRow.indexOf(validate);
        }

        for (int i = 0; i < firstRow.size(); i++) {
            if (i < row.size()) {
                if (i == offet && StringUtils.isNotBlank(validateName) && StringUtils.isBlank(row.get(i))) {
                    json.put(firstRow.get(i), validateName);
                } else {
                    json.put(firstRow.get(i), row.get(i));
                }
            }
        }
        return json;
    }

    //获取某一标题所在列位置
    public int getIndexForColumn(String title) {
        return firstRow.indexOf(title);
    }

    /**
     * 获取表格某一标题下的整列数组
     *
     * @param title
     * @param is    是否去除空格
     * @return
     */
    public List<String> getListForColumn(String title, boolean is) {
        int offset = firstRow.indexOf(title);
        List<String> list = new ArrayList<>();
        if (is) {
            content.forEach(strings -> {
                if (StringUtils.isNotBlank(strings.get(offset))) {
                    list.add(strings.get(offset));
                }
            });
        } else {
            content.forEach(strings -> list.add(strings.get(offset)));
        }

        return list;
    }

    //获取除去标题总行数
    public int getRowNum() {
        return content.size();
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public List<String> getFirstRow() {
        return firstRow;
    }

    public void setFirstRow(List<String> firstRow) {
        this.firstRow = firstRow;
    }

    public List<List<String>> getContent() {
        return content;
    }

    public void setContent(List<List<String>> content) {
        this.content = content;
    }
}
