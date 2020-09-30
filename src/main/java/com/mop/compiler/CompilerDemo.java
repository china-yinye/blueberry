package com.mop.compiler;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;


import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class CompilerDemo {

    private static final Logger log = Logger.getLogger(CompilerDemo.class);

    public static void main(String[] args) throws Exception {
//        String javaFilePath = "E:\\plan\\OpenTest_.java";
//        //获取编译器
//        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//
//        int compilationResult = compiler.run(null,null,null,javaFilePath);
//        if (compilationResult == 0){
//            log.info("success");
//        } else {
//            log.info("fail");
//        }

        //外部类加载器
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{
                new URL("file:E:\\LogRule-1.0-SNAPSHOT.jar")
        });
        Class<?> clazz = classLoader.loadClass("com.Match");
        Object o = clazz.newInstance();
        Method method = o.getClass().getMethod("match",JSONObject.class);
        JSONObject map = JSONObject.parseObject("");
        Object result = method.invoke(o,map);
        log.info(result.toString());

        //本地类加载器
//        ClassLoader loader = ClassLoader.getSystemClassLoader();
//        Class<?> aClass = loader.loadClass("");

        //Class<?> testJava = Class.forName("com.mop.compiler.TestJava");
        //log.info(Class.forName("com.mop.compiler.TestJava"));

    }
}
