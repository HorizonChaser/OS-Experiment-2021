import FileSystem.HActions;
import FileSystem.HDirectory;
import FileSystem.HNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileSystemSimulator {
    public static HDirectory root = new HDirectory("root");

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String cmdLine;
        String[] cmdArray;

        showMenu();
        while (true) {
            printPrompt();
            cmdLine = in.next();
            cmdArray = cmdLine.split(" ");
            if (cmdArray.length == 0) {
                continue;
            }
            if (cmdArray[0].equals("touch")) {
                HActions.touch(cmdArray, root);
            } else if(cmdArray[0].equals("mkdir")) {

            }
        }
    }

    /**
     * Show menu
     */
    public static void showMenu() {
        System.out.println("----------------| File System Simulator |----------------");
        System.out.println("|   Welcome to File System Simulator by Horizon Chaser  |");
        System.out.println("|Supported commands:                                    |");
        System.out.println("|    ls, mkdir, touch, chmod, rm, edit                  |");
        System.out.println("|-------------------------------------------------------|");
        System.out.println("|Description of commands:                               |");
        System.out.println("|    ls: List what's inside a given path                |");
        System.out.println("|    mkdir: Make a new dir in a given path              |");
        System.out.println("|    touch: Change the atime/mtime                      |");
        System.out.println("|    chmod: Change file/dir status                      |");
        System.out.println("|    rm: Delete given file/dir                          |");
        System.out.println("|    edit: Edit given file to given size                |");
        System.out.println("|-------------------------------------------------------|");
        System.out.println("|Most commands are similar to its original behavior in  |");
        System.out.println("|Linux, you can always use --help arg for help of a cmd |");
        System.out.println("|-------------------------------------------------------|");
    }

    /**
     * Print prompt "> "
     */
    public static void printPrompt() {
        System.out.print("> ");
    }
}
