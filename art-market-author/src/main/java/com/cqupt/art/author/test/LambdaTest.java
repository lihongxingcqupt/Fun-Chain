package com.cqupt.art.author.test;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class LambdaTest {


    private static void updateLocalVariableLambda() {
        List<String> list = new ArrayList<>();
        list.add("444");
        list.add("4444");
        list.add("090");

//        Stream.iterate(0, i -> i + 1).limit(list.size()).forEach(index -> {
//            String str = list.get(index);
//            System.out.println("当前是第" + index + "次循环" + str);
//        });
        Stream.iterate(0, i -> i + 1).limit(list.size()).forEach(idx -> {
            System.out.println(idx);
            System.out.println(list.get(idx));
        });
    }

    public static void main(String[] args) {
        updateLocalVariableLambda();
    }
}
