package filesystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tool {
    public static List<String> getDirectories(String absolutePath) {
        boolean flag = false;
        if (absolutePath.endsWith("/")) {
            flag = true;
        }
        String[] src = absolutePath.split("/+");
        ArrayList<String> ret = new ArrayList<>(Arrays.asList(src));
        if (flag && ret.size()!=0) {
            ret.add(".");
        }
        for (int i = ret.size() - 1; i >= 0; i--) {
            if (ret.get(i).isEmpty()) {
                ret.remove(i);
            }
        }

//        if (ret.size() == 0) {
//            //!!!!!!!!WARNING!!!!!!!!!!!
//            ret.add(".");
//            //!!!!!!!!WARNING!!!!!!!!!!
//        }
        return ret;
    }

    public static String pathToString(List<String> path) {
        String ret = "";
        for (String s : path) {
            if (s.isEmpty()) {
                continue;
            }
            ret = ret.concat("/" + s);
        }
        return ret;
    }

    public static boolean isAbsolutePath(String src) {
        return src.charAt(0) == '/';
    }

    public static List<String> clonePath(List<String> src) {
        return new ArrayList<>(src);
    }

    public static String simplifyPath(String s) {     //input must be absolute path
        String[] src = s.split("/+");
        ArrayList<String> ret = new ArrayList<>(Arrays.asList(src));
        boolean flag = true;
        ArrayList<String> r = new ArrayList<>();
        for (String str : ret) {
            if (str.equals("")) {
                continue;
            }
            if (str.equals(".")) {
                continue;
            }
            if (str.equals("..")) {
                if (flag) {
                    continue;
                } else {
                    r.remove(r.size() - 1);
                    if (r.size() == 0) {
                        flag = true;
                    }
                }
            } else {
                r.add(str);
                flag = false;
            }
        }
        StringBuilder result = new StringBuilder();
        for (String str : r) {
            result.append("/");
            result.append(str);
        }
        if (result.length() == 0) {
            result.append("/");
        }
        return result.toString();
    }

}
