package ru.nserd.console;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class Main {
    private static final String HELP_TEXT = "Usage:\n" +
            "java -jar ShowClassInfo-1.0.jar [option] OR java -jar ShowClassInfo-1.0.jar [Class]\n" +
            "\n" +
            "Options:\n" +
            "-h, --help display this help and exit\n" +
            "-l, --list display a list of available packages\n" +
            "\n" +
            "Example:\n" +
            "java -jar ShowClassInfo-1.0.jar java.lang.Object";

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("No argument found.");
            System.exit(-1);
        }

        if (args[0].charAt(0) == '-') {
            argHandler(args[0]);
        } else {
            try {
                printParentsAndInterfaces(Class.forName(args[0]));
            } catch (ClassNotFoundException e) {
                System.err.println("Class not found.");
                System.exit(-1);
            }
        }
    }

    private static void argHandler(String arg) {
        if (arg.equals("-h") || arg.equals("--help")) {
            System.out.println(HELP_TEXT);
        } else if (arg.equals("-l") || arg.equals("--list")) {
            String packageList = Arrays.stream(Package.getPackages())
                    .map(Package::toString)
                    .collect(Collectors.joining("\n"));

            System.out.println("Package List:\n\n" + packageList);
        } else {
            System.err.println("Invalid argument.");
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