package reflection.src.classloading.common;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

public class NetClassLoader extends ClassLoader {

    private String url;//class文件的URL

    public NetClassLoader(String url) {
        this.url = url;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classData = getClassDataFromNet(name);
        if (classData == null) {
            throw new ClassNotFoundException();
        } else {
            return defineClass(name, classData, 0, classData.length);
        }
    }

    /**
     * 从网络获取class文件
     * @param className
     * @return
     */
    private byte[] getClassDataFromNet(String className) {
        String path = classNameToPath(className);
        try {
            URL url = new URL(path);
            InputStream ins = url.openStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int bufferSize = 4096;
            byte[] buffer = new byte[bufferSize];
            int bytesNumRead = 0;
            // 读取类文件的字节
            while ((bytesNumRead = ins.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesNumRead);
            }
            //这里省略解密的过程.......
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String classNameToPath(String className) {
        // 得到类文件的URL
        return url + "/" + className.replace('.', '/') + ".class";
    }

    public static void main(String[] args) throws ClassNotFoundException {
        String rootDir="/Users/zhezhuang/LEARNING/note/java/reflection/src/";
        //创建自定义文件类加载器
        FileClassLoader loader = new FileClassLoader(rootDir);
        FileClassLoader loader2 = new FileClassLoader(rootDir);

        try {
            //加载指定的class文件,调用loadClass()
            Class<?> object1=loader.loadClass("classloading.common.DemoObj");
            Class<?> object2=loader2.loadClass("classloading.common.DemoObj");

            System.out.println("loadClass->obj1:"+object1.hashCode());
            System.out.println("loadClass->obj2:"+object2.hashCode());

            //加载指定的class文件,直接调用findClass(),绕过检测机制，创建不同class对象。
            Class<?> object3=loader.findClass("classloading.common.DemoObj");
            Class<?> object4=loader2.findClass("classloading.common.DemoObj");

            System.out.println("loadClass->obj3:"+object3.hashCode());
            System.out.println("loadClass->obj4:"+object4.hashCode());

            /**
             * 输出结果:
             * loadClass->obj1:644117698
             loadClass->obj2:644117698
             findClass->obj3:723074861
             findClass->obj4:895328852
             */

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
