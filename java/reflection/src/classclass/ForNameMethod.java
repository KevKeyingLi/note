package classclass;

import classclass.common.Gum;

public class ForNameMethod {
    public static void main(String[] args) {

        try{
            //通过Class.forName获取Gum类的Class对象
            Class clazz=Class.forName("classclass.common.Gum");
            System.out.println("forName=clazz:" + clazz.getName());
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }

        //通过实例对象获取Gum的Class对象
        Gum gum = new Gum();
        Class clazz2=gum.getClass();
        System.out.println("new=clazz2:" + clazz2.getName());

    }
}
