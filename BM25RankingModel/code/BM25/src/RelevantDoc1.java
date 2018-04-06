import java.util.List;

public class RelevantDoc1 implements RelevantDoc {

    private String docID;


    public RelevantDoc1(String docID){
        this.docID = docID;
    }

    /**
     * @return document ID
     */
    public String documentID() {
        return this.docID;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return docID;
    }
}
