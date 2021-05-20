package FileSystem;

import java.util.Arrays;
import java.util.List;

public class HActions {
    /**
     * touch command, set access/modify time or create new file
     *
     * @param cmdArray command line array
     * @param root     file system root node
     */
    public static void touch(String[] cmdArray, HNode root) {
        var cmdList = Arrays.asList(cmdArray);

        if (cmdArray.length == 1 || cmdList.contains("--help")) {
            showTouchHelp();
            return;
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
            String[] pathArray = cmdList.get(3).split("/");
            pathArray = Arrays.copyOfRange(pathArray, 1, pathArray.length);
            currNode = getNode(pathArray, root);
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
        pathArray = Arrays.copyOfRange(pathArray, 1, pathArray.length);
        HNode fatherNode = getFatherNode(pathArray, root);
        if (pathArray.length == 1) {
            for (HNode curr : root.childNodeList) {
                if (curr.name.equals(pathArray[0])) {
                    curr.modifiedTime = curr.accessedTime = System.currentTimeMillis() / 1000;
                    return;
                }
            }
            root.childNodeList.add(new HFile(pathArray[0], 0));
            return;
        }
        if (fatherNode == null) {
            System.out.println("[ERROR] Given path not exist");
            return;
        }
        if (fatherNode instanceof HFile) {
            System.out.println("[ERROR] Father node of given path is a file");
            return;
        }
        String fileName = pathArray[pathArray.length - 1];
        for (HNode hNode : fatherNode.childNodeList) {
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
        System.out.println("touch:\n  change last access and modify time, or create file if it not exists");
        System.out.println("Usage: touch [options] path");
        System.out.println("Options: ");
        System.out.println("  -a <time>  Only change last access time in UNIX time stamp format");
        System.out.println("  -m <time>  Only change last modify time in UNIX time stamp format");
        System.out.println("Note: if file given in args doesn't exist, it will be created.");
    }

    /**
     * mkdir, make a new dir
     * @param cmdArray cmd array
     * @param root root of FS
     */
    public static void mkdir(String[] cmdArray, HNode root) {
        var cmdList = Arrays.asList(cmdArray);

        if (cmdList.size() < 2 || cmdList.contains("--help")) {
            showMkdirHelp();
            return;
        }

        HNode fatherNode, pathNode;
        String[] pathArray = cmdList.get(1).split("/");
        pathArray = Arrays.copyOfRange(pathArray, 1, pathArray.length);
        fatherNode = getFatherNode(pathArray, root);
        if (pathArray.length == 1) {
            for(HNode curr: root.childNodeList) {
                if (curr.name.equals(pathArray[0])) {
                    System.out.println("mkdir: dir already exists");
                    return;
                }
            }
            HDirectory newDir = new HDirectory(pathArray[pathArray.length-1]);
            root.childNodeList.add(newDir);
            return;
        }
        if (fatherNode == null) {
            System.out.println("[ERROR] Father directory not exist");
            return;
        }
        pathNode = getNode(pathArray, root);
        if (pathNode != null) {
            System.out.println("mkdir: dir already exists");
            return;
        }
        HDirectory newDir = new HDirectory(pathArray[pathArray.length-1]);
        fatherNode.childNodeList.add(newDir);
    }

    /**
     * Show help info of mkdir
     */
    public static void showMkdirHelp() {
        System.out.println("mkdir: make a new directory based on a existing father node");
        System.out.println("usage: mkdir path");
    }

    /**
     * ls command, list content of a path
     * @param cmdArray command array
     * @param root root of FS
     */
    public static void list(String[] cmdArray, HNode root) {
        List<String> cmdList = Arrays.asList(cmdArray);
        HNode pathNode;

        if (cmdList.size() == 1 || cmdList.contains("--help")) {
            showListHelp();
            return;
        }

        if (cmdList.contains("-v")) {
            //TODO support verbose mode
        }
        if (cmdList.contains("-r")) {
            //TODO support recursive mode
        }

        //special judge for root dir
        if (cmdList.get(1).equals("/")) {
            System.out.println("  Name  " + " " + "  Type  " + " " + "  Size  " + " " + " Status ");
            System.out.println("--------" + " " + "--------" + " " + "--------" + " " + "--------");
            for (HNode curr : root.childNodeList) {
                listCore(curr, false);
            }
            return;
        }

        String[] pathArray = cmdList.get(1).split("/");
        pathArray = Arrays.copyOfRange(pathArray, 1, pathArray.length);
        pathNode = getNode(pathArray, root);
        if (pathNode == null) {
            System.out.println("[ERROR] Path not found: " + cmdList.get(1));
            return;
        }
        if (pathNode instanceof HFile) {
            System.out.println("ls: path points to a file");
            return;
        }

        System.out.println("  Name  " + " " + "  Type  " + " " + "  Size  " + " " + " Status ");
        System.out.println("--------" + " " + "--------" + " " + "--------" + " " + "--------");

        for (HNode curr : pathNode.childNodeList) {
            listCore(curr, false);
        }
    }

    /**
     * Print info of curr
     *
     * @param curr      Node to be listed
     * @param isVerbose Is -v enabled
     */
    public static void listCore(HNode curr, Boolean isVerbose) {
        if (curr == null) {
            return;
        }
        StringBuilder result = new StringBuilder();

        //name part
        StringBuilder namePart = new StringBuilder();
        if (curr.name.length() > 8) {
            namePart.append(curr.name.substring(0, 6)).append("..");
        } else {
            namePart.append(curr.name).append(" ".repeat(8 - curr.name.length()));
        }
        namePart.append(' ');

        //type part
        StringBuilder typePart = new StringBuilder();
        if (curr instanceof HDirectory) {
            typePart.append("Dir     ");
        } else {
            typePart.append("File    ");
        }
        typePart.append(' ');

        //size part
        StringBuilder sizePart = new StringBuilder();
        if (curr instanceof HFile) {
            String sizeString = String.valueOf(curr.size);
            if (sizeString.length() > 8) {
                //TODO Support for 1e8+ file
            } else {
                sizePart.append(curr.size).append(" ".repeat(8 - sizeString.length()));
            }
        } else {
            sizePart.append("0       ");
        }
        sizePart.append(' ');

        //status part
        StringBuilder statusPart = new StringBuilder();
        if (curr.status == HStatus.RW) {
            statusPart.append("RW      ");
        } else if (curr.status == HStatus.RO) {
            statusPart.append("RO      ");
        } else if (curr.status == HStatus.RWX) {
            statusPart.append("RWX     ");
        }
        statusPart.append(' ');

        if (!isVerbose) {
            result.append(namePart).append(typePart).append(sizePart).append(statusPart);
            System.out.println(result.toString());
            return;
        } else {
            result.append(namePart);//TODO support verbose mode
        }

    }

    /**
     * Show help info of ls
     */
    public static void showListHelp() {
        System.out.println("ls: list contents of a path");
        System.out.println("usage: ls [options] path");
        System.out.println("Options:");
        System.out.println("  -v  Show verbose information");
        System.out.println("  -r  Recursively show contents");
    }

    /**
     * chmod, change file/dir status
     * @param cmdArray cmd array
     * @param root root of FS
     */
    public static void chmod(String[] cmdArray, HNode root) {
        var cmdList = Arrays.asList(cmdArray);

        if (cmdList.size() < 3 || cmdList.contains("--help")) {
            showChmodHelp();
            return;
        }

        HStatus newStatus;
        if (cmdList.get(1).equals("RW")) {
            newStatus = HStatus.RW;
        } else if(cmdList.get(1).equals("RO")) {
            newStatus = HStatus.RO;
        } else if(cmdList.get(1).equals("RWX")) {
            newStatus = HStatus.RWX;
        } else {
            System.out.println("[ERROR] Invalid status: " + cmdList.get(1));
            return;
        }

        if (cmdList.get(2).equals("/")) {
            System.out.println("chmod: cannot change status of root");
            return;
        }
        String[] pathArray = cmdList.get(2).split("/");
        pathArray = Arrays.copyOfRange(pathArray, 1, pathArray.length);
        HNode pathNode = getNode(pathArray, root);
        if (pathNode == null) {
            System.out.println("[ERROR] Path not found: " + cmdList.get(2));
            return;
        }
        pathNode.status = newStatus;
    }

    /**
     * Show help info of chmod
     */
    public static void showChmodHelp() {
        System.out.println("chmod: change file/dir status");
        System.out.println("usage: chmod [status] path");
        System.out.println("Status:");
        System.out.println("  RW:   Read and Write");
        System.out.println("  RO:   Read-Only");
        System.out.println("  RWX:  Read, Write and eXecute (for File only)");
    }

    /**
     * rm command, remove a file or dir
     * @param cmdArray cmd array
     * @param root root of FS
     */
    public static void remove(String[] cmdArray, HNode root) {
        var cmdList = Arrays.asList(cmdArray);
        if (cmdList.size() == 1 || cmdList.contains("--help")) {
            showRemoveHelp();
            return;
        }
        if (cmdList.get(cmdList.size()-1).equals("/")) {
            System.out.println("rm: root cannot be removed");
            return;
        }

        if (cmdList.contains("-r")) {
            //TODO recursively delete
        }

        String[] pathArray = cmdList.get(cmdList.size()-1).split("/");
        pathArray = Arrays.copyOfRange(pathArray, 1, pathArray.length);

        HNode pathNode, fatherNode;
        pathNode = getNode(pathArray, root);
        if (pathNode == null) {
            System.out.println("[ERROR] Path not found: " + cmdList.get(cmdList.size()-1));
            return;
        }
        if (pathNode instanceof HDirectory) {
            System.out.println("rm: " + cmdList.get(cmdList.size()-1) + " is a directory, use -r if you want to remove it");
            return;
        }
        fatherNode = getFatherNode(pathArray, root);
        for (HNode curr: fatherNode.childNodeList) {
            if (curr.name.equals(pathArray[pathArray.length-1])) {
                fatherNode.childNodeList.remove(curr);
                if (cmdList.contains("-v")) {
                    System.out.println("rm: removed " + cmdList.get(cmdList.size()-1));
                }
                return;
            }
        }

    }

    /**
     * Print help info of rm command
     */
    public static void showRemoveHelp() {
        System.out.println("rm: remove file and directory");
        System.out.println("usage: rm [options] path");
        System.out.println("Options: ");
        System.out.println("  -v  Verbose mode: display what's being removed");
        System.out.println("  -r  recursively remove file/dir, which is needed when path is a Directory");
        System.out.println("Note: root(/) cannot be removed");
    }

    public static void edit(String[] cmdArray, HNode root) {
        var cmdList = Arrays.asList(cmdArray);

        if (cmdList.size() < 3 || cmdList.contains("--help")) {
            showEditHelp();
            return;
        }

        int newSize;
        try {
            newSize = Integer.parseInt(cmdList.get(1));
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid new size: " + cmdList.get(1));
            return;
        }
        String[] pathArray = cmdList.get(2).split("/");
        pathArray = Arrays.copyOfRange(pathArray, 1, pathArray.length);
        HNode pathNode = getNode(pathArray, root);
        if (pathNode == null) {
            System.out.println("[ERROR] Path not found" + cmdList.get(2));
            return;
        }
        if (pathNode instanceof HDirectory) {
            System.out.println("edit: cannot edit a directory");
            return;
        }
        pathNode.size = newSize;
    }

    public static void showEditHelp() {
        System.out.println("edit: edit given file to specific size");
        System.out.println("usage: edit <size> file");
        System.out.println("Args:");
        System.out.println("  <size>  new size of file");
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
    public static HNode getFatherNode(String[] pathArray, HNode root) {
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
