package FileSystem;

import java.util.List;

public class HDirectory extends HNode {
    /**
     * Initialize a new directory as RW
     *
     * @param name dir name
     */
    public HDirectory(String name) {
        super();
        super.name = name;
        super.size = 0;
        super.createdTime = System.currentTimeMillis()/1000;
        super.accessedTime = super.modifiedTime = -1;
        super.status = HStatus.RW; //default as Read and Write
    }

    /**
     * Initialize a new directory in given status
     *
     * @param name dir name
     * @param status dir status
     */
    public HDirectory(String name, HStatus status) {
        super();
        super.name = name;
        super.size = 0;
        super.createdTime = System.currentTimeMillis()/1000;
        super.accessedTime = super.modifiedTime = -1;
        super.status = status;
    }

    /**
     * Get childNodeList
     * @return childNodeList
     */
    public List<HNode> getChildNodeList() {
        return super.childNodeList;
    }
}
