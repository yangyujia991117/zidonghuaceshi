
public class ClassMethodPair {
    //该类记录一个class-method对
    String className;
    String methodName;
    public ClassMethodPair(String c,String m){
        className=c;
        methodName=m;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }
}
