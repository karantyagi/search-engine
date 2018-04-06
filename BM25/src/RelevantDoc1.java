import java.util.List;

public class RelevantDoc1 implements RelevantDoc {

    private String term;
    private String docID;
    private int tf;

    public RelevantDoc1(String term, String docID, int tf){
        this.docID = docID;
        this.term = term;
        this.tf = tf;
    }


    /**
     * @return Query Term
     */
    public String Term() {
        return this.term;
    }

    /**
     * @return document ID
     */
    public String documentID() {
        return this.docID;
    }

    /**
     * @return Term Frequency
     */
    public int termFreq() {
        return this.tf;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return term+" "+docID+" "+String.valueOf(tf);
    }
}
