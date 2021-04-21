package filesystem;

public class IsFileException extends Exception {
    private File file;
    private Folder father;

    public IsFileException(File f, Folder fo) {
        super();
        file = f;
        father = fo;
    }

    public File getFile() {
        return file;
    }

    public Folder getFather() {
        return father;
    }
}
