package zdl.util.javassist;

import javassist.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 根据实体类生成CopyIn语句拼接代码
 *
 * Created by ZDLegend on 2020/06/17 10:06
 */
public class MethodGenerator {

    private MethodGenerator() {

    }

    public static void modifyMethod(String className) throws NotFoundException, CannotCompileException {
        ClassPool classPool = ClassPool.getDefault();

        classPool.appendClassPath(new LoaderClassPath(MethodGenerator.class.getClassLoader()));

        CtClass ct = classPool.getCtClass(className);

        String copyInDataName = "zdl.util.javassist.CopyInData";
        CtClass copyInDataCt = classPool.getCtClass(copyInDataName);
        if(!ct.subtypeOf(copyInDataCt)) {
            String msg = String.format("class %s is not inherited from %s", className, copyInDataName);
            throw new NotFoundException(msg);
        }

        CtField[] allFields = ct.getFields();
        CtField[] declareFields = ct.getDeclaredFields();

        Map<String, CtField> uniqueFieldNameMap = Arrays.stream(allFields).collect(Collectors.toMap(CtField::getName, f -> f));
        uniqueFieldNameMap.putAll(Arrays.stream(declareFields).collect(Collectors.toMap(CtField::getName, f -> f)));

        List<CtField> sortedFields = uniqueFieldNameMap.values()
                .stream()
                .filter(c -> c.hasAnnotation(FieldOrder.class))
                .map(c -> {
                    FieldOrder fieldOrder;
                    try {
                        fieldOrder = (FieldOrder)c.getAnnotation(FieldOrder.class);
                    } catch (ClassNotFoundException e) {
                        fieldOrder = null;
                    }
                    return Pair.of(fieldOrder, c);
                })
                .filter(p -> p.getLeft() != null)
                .sorted(Comparator.comparingInt(p -> p.getLeft().order()))
                .map(Pair::getRight)
                .collect(Collectors.toList());

        //拼接字符串
        StringBuilder methodBody = null;
        for (CtField f : sortedFields) {
            if(methodBody == null) {
                methodBody = new StringBuilder("return new StringBuilder()");
            } else {
                methodBody.append(".append(getFieldDelimiter())");
            }
            methodBody.append(".append(transferNullOrEmptyData(").append(f.getName()).append("))");
        }

        if (methodBody == null) {
            throw new NullPointerException("method body is null");
        }
        methodBody.append(".toString();");

        CtMethod copyMethod;
        CtMethod[] allMethods = ct.getMethods();

        //javassist可以调用父类或接口的default方法，但是如果要修改，则必须创建新建的
        try {
            copyMethod = ct.getDeclaredMethod("generateCopyString");
            copyMethod.setBody(methodBody.toString());
        } catch (NotFoundException e) {
            //子类没有重写方法，新增子类方法重写父类
            CtMethod methodFromSuper = Arrays.stream(allMethods)
                    .filter(m -> m.getName().contains("generateCopyString"))
                    .findFirst()
                    .orElse(null);
            //复制接口或者重写父类方法
            copyMethod = CtNewMethod.copy(methodFromSuper, ct, null);
            copyMethod.setBody(methodBody.toString());
            ct.addMethod(copyMethod);
        }

        ct.toClass();
    }
}
