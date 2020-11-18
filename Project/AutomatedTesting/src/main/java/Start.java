import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Start {

  public static void main(String args[])
      throws IOException, WalaException, IllegalArgumentException, InvalidClassFileException,
          CancelException {
    /*分析输入参数的过程*/
    String type=args[0];//取得本次进行测试用例选择的级别 -c:类级，-m:方法级
    String path = args[1];//项目路径
    String change_info_path = args[2];//变更文件路径
    System.out.println("选择类型为"+type+",\n项目路径为"+path+"\n变更文件路径为"+change_info_path);

    ArrayList<String> file_paths = new ArrayList<>(); // 该链表存所有.class文件路径
    initialize(path, file_paths); // 遍历找到所有.class文件

    /* 构建分析域（AnalysisScope）对象scope的过程 */
    SomeClass someClass = new SomeClass();
    AnalysisScope scope = someClass.loadClass(file_paths);

    /*创建类级、方法级依赖图的过程*/
    MakeGraph makeGraph = new MakeGraph();
    ArrayList<Object> result = makeGraph.analysis(scope, "xxx");

    /*取得之前创建的两个依赖图和测试用例的class-method对集，用于测试用例选择*/
    Graph classGraph = (Graph) result.get(0);//取得类依赖图
    Graph methodGraph = (Graph) result.get(1);//取得方法依赖图
    ArrayList<ClassMethodPair> classMethodPairs = (ArrayList<ClassMethodPair>) result.get(2);//取得测试用例的class-method对集

    /*根据依赖图进行测试用例选择的过程*/
    Selector selector =
        new Selector(
            change_info_path, classGraph, methodGraph, classMethodPairs);
    selector.decodeChange();//解析文件变更
    if (type.equals("-c")) {
      selector.exec_class_select(); // 执行类级测试用例选择
    } else if (type.equals("-m")) {
      selector.exec_method_select(); // 执行方法级测试用例选择
      }
  }

  public static void initialize(String path, ArrayList<String> file_paths) {
    // 递归遍历找到所有的.class文件
    File file = new File(path);
    if (file.exists()) {
      File[] files = file.listFiles();
      if (null != files) {
        for (File file2 : files) {
          String s = file2.getAbsolutePath();
          if (file2.isDirectory()) {
            initialize(file2.getAbsolutePath(), file_paths);
          } else {
            if (s.endsWith(".class")) {
              file_paths.add(s);
            }
          }
        }
      }
    } else {
      System.out.println("文件不存在!");
    }

    //    // 5-MoreTriangle:
    //        file_paths.add("Data/5-MoreTriangle/target/classes/net/mooctest/Edge.class");
    //        file_paths.add("Data/5-MoreTriangle/target/classes/net/mooctest/MoreTriangle.class");
    //        file_paths.add("Data/5-MoreTriangle/target/classes/net/mooctest/Node.class");
    //        file_paths.add("Data/5-MoreTriangle/target/classes/net/mooctest/Vector.class");
    //        file_paths.add("Data/5-MoreTriangle/target/test-classes/net/mooctest/AreaTest.class");
    //
    // file_paths.add("Data/5-MoreTriangle/target/test-classes/net/mooctest/InsideTest.class");
    //
    // file_paths.add("Data/5-MoreTriangle/target/test-classes/net/mooctest/MoreTriangleTest.class");
    //
    // file_paths.add("Data/5-MoreTriangle/target/test-classes/net/mooctest/PerimeterTest.class");
    //
    // file_paths.add("Data/5-MoreTriangle/target/test-classes/net/mooctest/VectorTest.class");
    //
    //    //    // 4-NextDay:
    //        file_paths.add("Data/4-NextDay/target/classes/net/mooctest/CalendarUnit.class");
    //        file_paths.add("Data/4-NextDay/target/classes/net/mooctest/Date.class");
    //        file_paths.add("Data/4-NextDay/target/classes/net/mooctest/Day.class");
    //        file_paths.add("Data/4-NextDay/target/classes/net/mooctest/Month.class");
    //        file_paths.add("Data/4-NextDay/target/classes/net/mooctest/Nextday.class");
    //        file_paths.add("Data/4-NextDay/target/classes/net/mooctest/Year.class");
    //
    // file_paths.add("Data/4-NextDay/target/test-classes/net/mooctest/CalendarUnitTest.class");
    //        file_paths.add("Data/4-NextDay/target/test-classes/net/mooctest/DateTest.class");
    //        file_paths.add("Data/4-NextDay/target/test-classes/net/mooctest/DayTest.class");
    //        file_paths.add("Data/4-NextDay/target/test-classes/net/mooctest/MonthTest.class");
    //        file_paths.add("Data/4-NextDay/target/test-classes/net/mooctest/NextdayTest.class");
    //        file_paths.add("Data/4-NextDay/target/test-classes/net/mooctest/YearTest.class");
    //
    //        // 3-BinaryHeap:
    //        file_paths.add("Data/3-BinaryHeap/target/classes/net/mooctest/BinaryHeap.class");
    //        file_paths.add("Data/3-BinaryHeap/target/classes/net/mooctest/Overflow.class");
    //        file_paths.add(
    //
    //     "Data/3-BinaryHeap/target/test-classes/net/mooctest/BinaryHeapForDeleteMinTest.class");
    //        file_paths.add(
    //
    // "Data/3-BinaryHeap/target/test-classes/net/mooctest/BinaryHeapForFindMinTest.class");
    //        file_paths.add(
    //
    // "Data/3-BinaryHeap/target/test-classes/net/mooctest/BinaryHeapForIsEmptyTest.class");
    //        file_paths.add(
    //            "Data/3-BinaryHeap/target/test-classes/net/mooctest/BinaryHeapOtherTest.class");
    //
    //        // 2-DataLog:
    //        file_paths.add("Data/2-DataLog/target/classes/net/mooctest/Argument.class");
    //        file_paths.add("Data/2-DataLog/target/classes/net/mooctest/Datalog.class");
    //        file_paths.add("Data/2-DataLog/target/classes/net/mooctest/Fact.class");
    //        file_paths.add("Data/2-DataLog/target/classes/net/mooctest/Predicate.class");
    //        file_paths.add("Data/2-DataLog/target/classes/net/mooctest/Program.class");
    //        file_paths.add("Data/2-DataLog/target/classes/net/mooctest/Rule.class");
    //        file_paths.add("Data/2-DataLog/target/classes/net/mooctest/Substitution.class");
    //        file_paths.add("Data/2-DataLog/target/classes/net/mooctest/Value.class");
    //        file_paths.add("Data/2-DataLog/target/classes/net/mooctest/Variable.class");
    //
    // file_paths.add("Data/2-DataLog/target/test-classes/net/mooctest/DatalogOtherTest.class");
    //        file_paths.add("Data/2-DataLog/target/test-classes/net/mooctest/DatalogTest.class");
    //        file_paths.add("Data/2-DataLog/target/test-classes/net/mooctest/DatalogTest1.class");
    //        file_paths.add("Data/2-DataLog/target/test-classes/net/mooctest/DatalogTest2.class");
    //        file_paths.add("Data/2-DataLog/target/test-classes/net/mooctest/DatalogTest3.class");
    //        file_paths.add("Data/2-DataLog/target/test-classes/net/mooctest/DatalogTest4.class");
    //        file_paths.add("Data/2-DataLog/target/test-classes/net/mooctest/DatalogTest5.class");
    //
    //     1-ALU:
    //     file_paths.add("Data/1-ALU/target/classes/net/mooctest/ALU.class");
    //    file_paths.add("Data/1-ALU/target/test-classes/net/mooctest/ALUAdderTest.class");
    //    file_paths.add("Data/1-ALU/target/test-classes/net/mooctest/ALUFloatTest.class");
    //    file_paths.add("Data/1-ALU/target/test-classes/net/mooctest/ALUGateTest.class");
    //    file_paths.add("Data/1-ALU/target/test-classes/net/mooctest/ALUIntegerTest.class");
    //    file_paths.add("Data/1-ALU/target/test-classes/net/mooctest/ALUOtherTest1.class");
    //    file_paths.add("Data/1-ALU/target/test-classes/net/mooctest/ALUOtherTest2.class");
    //
    //    0-CMD:
    //      file_paths.add("Data/0-CMD/target/classes/net/mooctest/CMD.class");
    //      file_paths.add("Data/0-CMD/target/test-classes/net/mooctest/CMDTest.class");
    //      file_paths.add("Data/0-CMD/target/test-classes/net/mooctest/CMDTest1.class");
    //      file_paths.add("Data/0-CMD/target/test-classes/net/mooctest/CMDTest2.class");
    //      file_paths.add("Data/0-CMD/target/test-classes/net/mooctest/CMDTest3.class");
  }
}
