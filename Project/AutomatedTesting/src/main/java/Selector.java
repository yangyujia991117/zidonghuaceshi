import java.io.*;
import java.util.ArrayList;

public class Selector {
  ArrayList<String> changedClasses;
  ArrayList<String> changedMethods;
  String change_info_path;
  Graph classGraph;
  Graph methodGraph;
  ArrayList<ClassMethodPair> classMethodPairs;

  public Selector(String f, Graph c, Graph m, ArrayList<ClassMethodPair> cp) {
    changedClasses = new ArrayList<>();
    changedMethods = new ArrayList<>();
    change_info_path = f;
    classGraph = c;
    methodGraph = m;
    classMethodPairs = cp;
  }

  // 解析变化的类和方法
  public void decodeChange() throws IOException {
    // 读取change_info文件
    System.out.println("--------开始解析变化的类和方法-------");
    FileInputStream inputStream = new FileInputStream(change_info_path);
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

    String str = null;
    while ((str = bufferedReader.readLine()) != null) {
      System.out.println(str);
      int index = str.indexOf(' ');
      String changed_c = str.substring(0, index);
      String changed_m = str.substring(index + 1);
      if (!changedClasses.contains(changed_c)) {
        changedClasses.add(changed_c);
      }
      if (!changedMethods.contains(changed_m)) {
        changedMethods.add(changed_m);
      }
    }
    // close
    inputStream.close();
    bufferedReader.close();
  }

  // 执行类级选择
  public void exec_class_select() throws IOException {
    ArrayList<String> result = new ArrayList<>(); // 存放选出来的测试用例类

    String this_class;
    System.out.println("--------------改变的类--------------");
    for (int i = 0; i < changedClasses.size(); i++) {
      this_class = changedClasses.get(i);
      System.out.println(this_class);
      for (int j = 0; j < classGraph.fatherNodes.size(); j++) {
        if (classGraph.fatherNodes.get(j).equals(this_class)) {
          if (!result.contains(classGraph.sonNodes.get(j))) { // 判断该类是否已写进result,避免出现重复
            result.add(classGraph.sonNodes.get(j));
          }
        }
      }
    }

    BufferedWriter out = new BufferedWriter(new FileWriter("section-class.txt"));
    System.out.println("--------------开始进行类级测试用例选择--------------");
    String line;
    for (int i = 0; i < result.size(); i++) {
      for (int j = 0; j < classMethodPairs.size(); j++) {

        if ((classMethodPairs.get(j).getClassName()).equals(result.get(i))) {

          line = result.get(i) + " " + classMethodPairs.get(j).getMethodName() + "\n";
          out.write(line);
          System.out.print(line);
        }
      }
    }
    out.close();
  }

  // 执行方法级选择
  public void exec_method_select() throws IOException {
    ArrayList<String> result = new ArrayList<>(); // 存放选出来的测试用例方法

    String this_method;
    System.out.println("--------------改变的方法--------------");
    for (int i = 0; i < changedMethods.size(); i++) {
      this_method = changedMethods.get(i);
      System.out.println(this_method);
      analysis_method_select(result, this_method);
    }
    BufferedWriter out = new BufferedWriter(new FileWriter("section-method.txt"));
    System.out.println("--------------开始进行方法级测试用例选择--------------");
    String line;
    for (int i = 0; i < result.size(); i++) {
      for (int j = 0; j < classMethodPairs.size(); j++) {
        // 注意间接调用
        if ((classMethodPairs.get(j).getMethodName()).equals(result.get(i))) {
          line = classMethodPairs.get(j).getClassName() + " " + result.get(i) + "\n";
          out.write(line);
          System.out.print(line);
        }
      }
    }
    out.close();
  }

  private void analysis_method_select(ArrayList<String> arrayList, String this_method) {
    // 循环+递归执行方法级测试用例选择
    if (methodGraph.fatherNodes.indexOf(this_method) >= 0) {
      for (int i = 0; i < methodGraph.fatherNodes.size(); i++) {
        if (methodGraph.fatherNodes.get(i).equals(this_method)) {
          if (!arrayList.contains(methodGraph.sonNodes.get(i))) { // 判断该类是否已写进result,避免出现重复
            arrayList.add(methodGraph.sonNodes.get(i));
            analysis_method_select(arrayList, methodGraph.sonNodes.get(i));
          }
        }
      }
    }
  }

  private boolean end_with(String s1, String s2) {
    // 看s1是否以s2结尾
    int len1 = s1.length();
    int len2 = s2.length();
    return s1.substring(len1 - len2).equals(s2);
  }
}
