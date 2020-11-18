import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;
import com.ibm.wala.ipa.callgraph.impl.AllApplicationEntrypoints;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.viz.DotUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class MakeGraph {
  public ArrayList<Object> analysis(AnalysisScope scope, String s)
      throws WalaException, CancelException, IOException {
    System.out.println("----------------开始输出所有类和方法-----------------");
    // 1.生成类层次关系对象
    ClassHierarchy cha = ClassHierarchyFactory.makeWithRoot(scope);
    // 2.生成进入点
    Iterable<Entrypoint> eps = new AllApplicationEntrypoints(scope, cha);
    // 3.利用CHA算法构建调用图
    CHACallGraph cg = new CHACallGraph(cha);
    cg.init(eps);

    // 构建属于该项目的图
    Graph classGraph = new Graph(s, 0); // 类依赖图
    Graph methodGraph = new Graph(s, 1); // 方法依赖图

    // 构建属于该项目的测试用例的class-method对集
    ArrayList<ClassMethodPair> classMethodPairs = new ArrayList<>();

    // 4.遍历cg中所有的节点
    for (CGNode node : cg) {
      // node中包含了很多信息，包括类加载器、方法信息等，这里只筛选出需要的信息

      if (node.getMethod() instanceof ShrikeBTMethod) {
        // node.getMethod()返回一个比较泛化的IMethod实例，不能获取到我们想要的信息
        // 一般地，本项目中所有和业务逻辑相关的方法都是ShrikeBTMethod对象
        ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
        // 使用Primordial类加载器加载的类都属于Java原生类，我们一般不关心。
        if ("Application".equals(method.getDeclaringClass().getClassLoader().toString())) {
          // 获取声明该方法的类的内部表示
          String classInnerName = method.getDeclaringClass().getName().toString();
          // 获取方法签名
          String signature = method.getSignature();

          //System.out.println("方法类型是：" + node.getMethod().getAnnotations());
          // 对于测试用例——添加这个class-method对：
          if (node.getMethod()
              .getAnnotations()
              .toString()
              .contains("Annotation type <Application,Lorg/junit/Test>")) {
            //System.out.println("该方法是一个测试用例!");
            classMethodPairs.add(new ClassMethodPair(classInnerName, signature));
          }

          System.out.println(classInnerName + " " + signature);

          Iterator<CGNode> iterator = cg.getPredNodes(node);
          while (iterator.hasNext()) {
            CGNode node1 = iterator.next();
            String node1classInnerName = node1.getMethod().getDeclaringClass().getName().toString();
            String node1signature = node1.getMethod().getSignature();
            if (!(node1classInnerName.startsWith("Ljava")||node1classInnerName.startsWith("Ljavax"))){
              // 排除掉exclusion没有排除的JAVA原生类
              //System.out.println("被该方法调用：" + node1signature);
              //System.out.println("该方法属于类：" + node1classInnerName);

              classGraph.addNodes(classInnerName, node1classInnerName);
              methodGraph.addNodes(signature, node1signature);
            }
          }
        }
      }
    }
    draw(classGraph, methodGraph); // 开始画图
    ArrayList<Object> result = new ArrayList<>();
    result.add(classGraph);
    result.add(methodGraph);
    result.add(classMethodPairs);
    return result;//返回的ArrayList中，第一个元素是类依赖图classGraph，第二个元素是方法依赖图methodGraph，第三个元素是测试用例class-method对集classMethodPairs
  }

  private void draw(Graph classGraph, Graph methodGraph) throws IOException {
    // 去除冗余
    classGraph.delRedundancy();
    methodGraph.delRedundancy();
    // 生成.dot文件
    //classGraph.makeDotFile();//生成类依赖图dot文件
    //methodGraph.makeDotFile();//生成方法依赖图dot文件
  }
}
