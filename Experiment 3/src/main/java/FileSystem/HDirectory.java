package FileSystem;

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
        super.createdTime = System.currentTimeMillis();
        super.accessedTime = super.modifiedTime = -1;
        super.status = HStatus.RW; //default as Read and Write
    }

    /**
     * Initialize a new directory as RW
     *
     * @param name dir name
     * @param status dir status
     */
    public HDirectory(String name, HStatus status) {
        super();
        super.name = name;
        super.size = 0;
        super.createdTime = System.currentTimeMillis();
        super.accessedTime = super.modifiedTime = -1;
        super.status = status;
    }
}
