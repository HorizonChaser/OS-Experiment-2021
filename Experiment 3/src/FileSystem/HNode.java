package FileSystem;

import java.util.ArrayList;

public class HNode {
    public String name;
    long size; //File size, zero for folder node
    long createdTime, modifiedTime, accessedTime;
    HStatus status;
    ArrayList<HNode> childNodeList = new ArrayList<>();
    protected HNode() {
    }
}
