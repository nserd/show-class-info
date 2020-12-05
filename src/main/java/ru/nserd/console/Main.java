package ru.nserd.console;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Main {
    private static boolean addMethods = false;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("No argument found.");
            System.out.println(loadHelp());
            System.exit(-1);
        }

        List<String> arguments = Arrays.stream(args)
                .filter(arg -> arg.charAt(0) == '-')
                .collect(Collectors.toList());

        argHandler(arguments);

        try {
            printInfo(Class.forName(args[0]));
        } catch (ClassNotFoundException e) {
            Class<?> c = searchClass(0, args[0]);

            if (c != null) {
                printInfo(c);
            } else {
                System.err.println("Class not found.");
                System.exit(-1);
            }
        }
    }

    private static void argHandler(List<String> arguments) {
        if (arguments.contains("--help") || arguments.contains("-h")) {
            System.out.println(loadHelp());
            System.exit(0);
        } else if (arguments.contains("--list") || arguments.contains("-l")) {
            printPackages();
            System.exit(0);
        } else {
            for (String arg : arguments) {
                switch (arg) {
                    case "--methods":
                    case "-m":
                        addMethods = true;
                        break;
                    default:
                        System.err.println("Invalid argument: " + arg);
                        System.exit(-1);
                }
            }
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

    private static void printInfo(Class<?> c) {
        printParentsAndInterfaces(c);
        if (addMethods) printMethods(c);
    }

    private static void printParentsAndInterfaces(Class<?> c) {
        String interfacesString = Arrays.stream(c.getInterfaces())
                .map(Class::getName)
                .collect(Collectors.joining(", "));

        String parentsString = getParent(new LinkedList<>(), c).stream()
                .map(Class::getName)
                .collect(Collectors.joining(" <- "));

        interfacesString = interfacesString.length() > 0 ? interfacesString : "none";
        parentsString = parentsString.length() > 0 ? parentsString : "none";

        String classString = getType(c) + ": " + c.getName();

        System.out.println(stringWithBorders(classString));
        System.out.printf("%-11s: %s%n", "Interfaces", interfacesString);
        System.out.printf("%-11s: %s%n", "Parents", parentsString);
    }

    private static void printMethods(Class<?> c) {
        String methods = Arrays.stream(c.getDeclaredMethods())
                .map(Method::toGenericString)
                .map(m -> m.replaceAll(c.getPackage().getName() + "\\." , ""))
                .map(m -> m.replaceAll(c.getSimpleName() + "\\.", ""))
                .collect(Collectors.joining("\n"));

        System.out.println();
        System.out.println("Methods");
        System.out.println("─".repeat("Methods" .length()));
        System.out.println(methods);
    }

    private static void printPackages() {
        String packageList = Arrays.stream(Package.getPackages())
                .map(Package::toString)
                .map(packageName -> packageName.replaceAll("package ", ""))
                .collect(Collectors.joining("\n"));

        System.out.println(stringWithBorders("Package List"));
        System.out.println(packageList);
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

    private static String stringWithBorders(String str) {
        return "┌─" + "─".repeat(str.length()) + "─┐" + "\n" +
                "│ " + str + " │" + "\n" +
                "└─" + "─".repeat(str.length()) + "─┘" + "\n";
    }
}