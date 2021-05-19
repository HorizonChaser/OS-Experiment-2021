package FileSystem;

import java.util.Arrays;

public class HActions {
    public static void touch(@org.jetbrains.annotations.NotNull String[] cmdArray, HNode root) {
        var cmdList = Arrays.asList(cmdArray);

        if (cmdArray.length == 1 || cmdList.contains("--help")) {
            showTouchHelp();
        }

        HNode currNode;
        if (cmdList.get(1).equals("-a") || cmdList.get(1).equals("-m")) {
            long time;
            try {
                time = Integer.parseInt(cmdList.get(2));
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Invalid time: " + cmdList.get(2));
                return;
            }
            currNode = getNode(cmdList.get(3).split("/"), root);
            if (currNode == null) {
                System.out.println("[ERROR] File not exist: " + cmdList.get(3));
                return;
            }
            if (currNode.status == HStatus.RO) {
                System.out.println("[ERROR] File is Read-Only");
                return;
            }
            if (currNode instanceof HDirectory) {
                System.out.println("[ERROR] Given path points to a dir");
            }
            if (cmdList.get(1).equals("-a")) {
                currNode.accessedTime = time;
            } else {
                currNode.modifiedTime = time;
            }
            return;
        }

        String[] pathArray = cmdList.get(1).split("/");
        HNode fatherNode = getFatherNode(pathArray, root);
        if (fatherNode == null) {
            System.out.println("[ERROR] Given path not exist");
            return;
        }
        if (fatherNode instanceof HFile) {
            System.out.println("[ERROR] Father node of given path is a file");
            return;
        }
        String fileName = pathArray[pathArray.length-1];
        for(HNode hNode : fatherNode.childNodeList) {
            if (hNode.name.equals(fileName)) {
                System.out.println("[ERROR] File with same name already exists");
                return;
            }
        }
        fatherNode.childNodeList.add(new HFile(fileName, 0));
    }

    /**
     * Show help info of touch command
     */
    public static void showTouchHelp() {
        System.out.println("touch:\n  change last access/modify time, or create file if it not exists");
        System.out.println("Usage: touch [options] file/dir");
        System.out.println("Options: ");
        System.out.println("  -a <time>  Change last access time in UNIX time stamp format");
        System.out.println("  -m <time>  Change last modify time in UNIX time stamp format");
        System.out.println("Note: if file given in args doesn't exist, it will be created.");
    }

    public static void mkdir(String[] cmdArray) {

    }

    public static void list(String[] cmdArray) {

    }

    public static void chmod(String[] cmdArray) {

    }

    public static void remove(String[] cmdArray) {

    }

    public static void edit(String[] cmdArray) {

    }

    /**
     * Get HNode of given path
     *
     * @param pathArray path string split by "/"
     * @param root      NodeList of "current" root node
     * @return Required HNode if found, or null if not
     */
    static HNode getNode(String[] pathArray, HNode root) {
        if (root == null || pathArray.length == 0) {
            return null;
        }

        for (HNode curr : root.childNodeList) {
            if (curr.name.equals(pathArray[0])) {
                if (pathArray.length == 1)
                    return curr;
                return getNode(Arrays.copyOfRange(pathArray, 1, pathArray.length), curr);
            }
        }
        return null;
    }

    /**
     * Get father node of given path
     *
     * @param pathArray path string split by "/"
     * @param root      NodeList of "current" root node
     * @return Required father node of given HNode if found, or null if not
     */
    static HNode getFatherNode(String[] pathArray, HNode root) {
        if (pathArray.length == 1) {
            for (HNode curr : root.childNodeList) {
                if (curr.name.equals(pathArray[0])) {
                    return root;
                }
            }
            return null;
        }
        return getNode(Arrays.copyOfRange(pathArray, 0, pathArray.length - 1), root);
    }
}
