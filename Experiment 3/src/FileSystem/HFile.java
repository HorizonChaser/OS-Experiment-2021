package FileSystem;

public class HFile extends HNode{

    /**
     * Initialize a new File as RW
     * @param name File name
     * @param size File size
     */
    public HFile(String name, long size) {
        super();
        super.name = name;
        super.size = size;
        super.createdTime = super.accessedTime = super.modifiedTime = System.currentTimeMillis()/1000;
        super.status = HStatus.RW; //default as Read and Write
    }

    /**
     * Initialize a new file as given status
     * @param name File name
     * @param size File size
     * @param status File status
     */
    public HFile(String name, long size, HStatus status) {
        super();
        super.name = name;
        super.size = size;
        super.createdTime = super.accessedTime = super.modifiedTime = System.currentTimeMillis()/1000;
        super.status = status;
    }
}
