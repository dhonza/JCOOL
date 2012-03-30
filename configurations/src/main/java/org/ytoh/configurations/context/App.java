/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ytoh.configurations.context;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author ytoh
 */
public class App {
    public static void main(String[] args) {
        DefaultContext context = new DefaultContext();
        List<String> strings = Arrays.asList("a", "b", "c");
        List<Integer> numbers = Arrays.asList(1, 2, 3);
        context.register(String.class, strings, "none");
        context.register(Integer.class, numbers, "");

        System.out.println("context.getOptions(String.class,\"none\") = " + context.getList(String.class,"none"));
        System.out.println("context.getOptions(String.class,\"\") = " + context.getList(String.class,""));
        System.out.println("context.getOptions(Integer.class,\"\") = " + context.getList(Integer.class,""));
        System.out.println("context.getOptions(Integer.class,\"none\") = " + context.getList(Integer.class,"none"));
        System.out.println("context.getOptions(Double.class,\"none\") = " + context.getList(Double.class,"none"));
    }
}
