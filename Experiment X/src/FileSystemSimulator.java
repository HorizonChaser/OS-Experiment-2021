import FileSystem.HActions;
import FileSystem.HDirectory;
import FileSystem.HFile;

import java.util.Scanner;

public class FileSystemSimulator {
    public static final HDirectory root = new HDirectory("root");

    public static void main(String[] args) throws InterruptedException {
        Scanner in = new Scanner(System.in);
        String cmdLine;
        String[] cmdArray;

        System.out.print("Formatting virtual disk");
        for (int i = 0; i < 5; i++) {
            Thread.sleep(200);
            System.out.print(". ");
        }
        System.out.println("Done");
        Thread.sleep(700);

        showMenu();

        while (true) {
            printPrompt();
            cmdLine = in.nextLine();
            cmdArray = cmdLine.split(" ");
            if (cmdArray.length == 0) {
                continue;
            }

            switch (cmdArray[0]) {
                case "touch" -> HActions.touch(cmdArray, root);
                case "ls" -> HActions.list(cmdArray, root);
                case "rm" -> HActions.remove(cmdArray, root);
                case "chmod" -> HActions.chmod(cmdArray, root);
                case "mkdir" -> HActions.mkdir(cmdArray, root);
                case "edit" -> HActions.edit(cmdArray, root);
                case "exit" -> acknowledgement();
                default -> System.out.println("[ERROR] Unrecognized command: " + cmdLine);
            }
        }
    }

    /**
     * Show menu
     */
    public static void showMenu() {
        System.out.println("┌---------------| File System Simulator |---------------┐");
        System.out.println("|   Welcome to File System Simulator by Horizon Chaser  |");
        System.out.println("├-------------------------------------------------------┤");
        System.out.println("|Supported commands:                                    |");
        System.out.println("|    ls, mkdir, touch, chmod, rm, edit, exit            |");
        System.out.println("├-------------------------------------------------------┤");
        System.out.println("|Description of commands:                               |");
        System.out.println("|    ls: List what's inside a given path                |");
        System.out.println("|    mkdir: Make a new dir in a given path              |");
        System.out.println("|    touch: Change the atime/mtime                      |");
        System.out.println("|    chmod: Change file/dir status                      |");
        System.out.println("|    rm: Delete given file/dir                          |");
        System.out.println("|    edit: Edit given file to given size                |");
        System.out.println("├    exit: Exit the simulator                           ┤");
        System.out.println("├-------------------------------------------------------┤");
        System.out.println("|Most commands are similar to its original behavior in  |");
        System.out.println("|Linux, you can always use --help arg for help of a cmd |");
        System.out.println("├-------------------------------------------------------┤");
        System.out.println("| Note: All commands and parameters are case-sensitive  |");
        System.out.println("└-------------------------------------------------------┘");
    }

    /**
     * Print prompt "> "
     */
    public static void printPrompt() {
        System.out.print("> ");
    }

    /**
     * Initialize a test dir tree, for test only
     * / - a0 - b0
     * - B1
     * - a1
     * - A2
     */
    public static void initializeTestDirTree() {
        HDirectory d1 = new HDirectory("a0");
        d1.getChildNodeList().add(new HDirectory("b0"));
        d1.getChildNodeList().add(new HFile("B1", 1204));
        HDirectory d2 = new HDirectory("a1");
        root.getChildNodeList().add(d1);
        root.getChildNodeList().add(d2);
        root.getChildNodeList().add(new HFile("A2", 2021));
    }

    public static void acknowledgement() {
        System.out.println("File System Simulator by Horizon Chaser");
        System.out.println("For experiment of Operating System Class in 2021 Summer");
        System.out.println("Motivated by my teacher, IDEA, Windows 10 and myself.");

        System.exit(0);
    }
}
