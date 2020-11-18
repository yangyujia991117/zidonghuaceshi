import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.util.io.FileProvider;

import java.io.*;
import java.util.ArrayList;

/*
依赖图的定义（包括类依赖图和方法依赖图,一个类两张）
 */
public class Graph {

  ArrayList<String> fatherNodes;
  ArrayList<String> sonNodes;
  String name;
  int type; // 图的类型：0表示类依赖图，1表示方法依赖图

  public Graph(String s, int t) {
    fatherNodes = new ArrayList<>();
    sonNodes = new ArrayList<>();
    name = s;
    type = t;
  }

  public void addNodes(String fn, String sn) {
    fatherNodes.add(fn);
    sonNodes.add(sn);
  }

  public void delRedundancy() {
    // 去除冗余节点
    int i = 0;
    while (i < fatherNodes.size() - 1) {
      int j = i + 1;
      while (j < fatherNodes.size()) {
        if ((fatherNodes.get(i).equals(fatherNodes.get(j)))
            && sonNodes.get(i).equals(sonNodes.get(j))) {
          fatherNodes.remove(j);
          sonNodes.remove(j);
        } else {
          j++;
        }
      }
      i++;
    }
  }

  public void makeDotFile() throws IOException {
    if (type == 0) {
      BufferedWriter out = new BufferedWriter(new FileWriter("drawDOT.dot"));
      out.write("digraph " + name.toLowerCase() + "_class{\n");
      System.out.println("--------------开始打印类依赖图--------------");
      for (int i = 0; i < fatherNodes.size(); i++) {
        String s = "\t" + '"' + fatherNodes.get(i) + "\" -> \"" + sonNodes.get(i) + "\";\n";
        out.write(s);
        System.out.print(s);
      }
      out.write("}");
      out.close();
    } else {
      BufferedWriter out = new BufferedWriter(new FileWriter("drawDOT.dot"));
      out.write("digraph " + name.toLowerCase() + "_method{\n");
      System.out.println("--------------开始打印方法依赖图--------------");
      for (int i = 0; i < fatherNodes.size(); i++) {
        String s = "\t" + '"' + fatherNodes.get(i) + "\" -> \"" + sonNodes.get(i) + "\";\n";
        out.write(s);
        System.out.print(s);
      }
      out.write("}");
      out.close();
    }
  }
}

