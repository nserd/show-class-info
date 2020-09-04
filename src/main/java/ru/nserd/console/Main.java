package ru.nserd.console;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("No argument found.");
            System.exit(-1);
        }

        try {
            printParentsAndInterfaces(Class.forName(args[0]));
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found.");
            System.exit(-1);
        }
    }

    public static void printParentsAndInterfaces(Class<?> c) {
        String interfacesString = Arrays.stream(c.getInterfaces())
                .map(Class::getName)
                .collect(Collectors.joining(", "));

        String parentsString = getParent(new LinkedList<>(), c).stream()
                .map(Class::getName)
                .collect(Collectors.joining(" <- "));

        interfacesString = interfacesString.length() > 0 ? interfacesString : "none";
        parentsString = parentsString.length() > 0 ? parentsString : "none";

        String classString = "Class: " + c.getName();

        System.out.println(classString);
        System.out.println("-".repeat(classString.length()));
        System.out.printf("%-11s: %s%n", "Interfaces", interfacesString);
        System.out.printf("%-11s: %s%n", "Parents", parentsString);
    }

    private static LinkedList<Class<?>> getParent(LinkedList<Class<?>> classes, Class<?> c) {
        if (c.getSuperclass() != null) {
            classes.add(c.getSuperclass());
            return getParent(classes, c.getSuperclass());
        } else {
            return classes;
        }
    }
}