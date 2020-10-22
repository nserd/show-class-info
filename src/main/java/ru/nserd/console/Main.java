package ru.nserd.console;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;

public class Main {
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
                Class<?> c = searchClass(0, args[0]);

                if (c != null) {
                    printParentsAndInterfaces(c);
                } else {
                    System.err.println("Class not found.");
                    System.exit(-1);
                }
            }
        }
    }

    private static void argHandler(String arg) {
        if (arg.equals("-h") || arg.equals("--help")) {
            System.out.println(loadHelp());
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

    private static Class<?> searchClass(int i, String className) {
        Class<?> c = null;

        if (i < Package.getPackages().length) {
            try {
                c = Class.forName(Package.getPackages()[i].getName() + "." + className);
            } catch (ClassNotFoundException e) {
                return searchClass(i + 1, className);
            }
        }

        return c;
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

        String classString = getType(c) + ": " + c.getName();

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

    private static String getType(Class<?> c) {
        String type = "Class";

        if (c.isInterface()) type = "Interface";
        if (c.isEnum()) type = "Enumeration";
        if (c.isAnnotation()) type = "Annotation";

        return type;
    }

    private static String loadHelp() {
        String helpStr = null;

        try (InputStream is = Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream("Help"));
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int length;

            while ((length = is.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }

            helpStr = out.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Objects.requireNonNull(helpStr);
    }
}