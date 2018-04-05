import java.util.List;

public class Result1 implements Result {

    private int queryID;
    private String literal;
    private String documentID;
    private double BM25Score;
    private int rank;
    private String systemName;

    /**
     * @param queryID
     * @param documentID
     */
    public Result1(int queryID, String documentID) {

        this.queryID = queryID;
        this.literal = "Q0";
        this.documentID = documentID;
        this.rank = 0;
        this.BM25Score = 0.0;
        this.systemName = "Okapi_NoStem";   //////// CROSSS CHECK...
    }

    /**
     * @return queryID
     */
    public int queryId() {
        return this.queryID;
    }

    /**
     * @return literal
     */
    public String literal() {
        return this.literal;
    }

    /**
     * @return document ID
     */
    public String documentID() {
        return this.documentID;
    }

    /**
     * @return rank of document as per BM25 score
     */
    public int getRank() {
        return this.rank;
    }

    public void setRank(int r) {
        this.rank =r;

    }


    /**
     * @return BM25 score of the document for the given query
     */
    public double getBM25Score() {
        return this.BM25Score;
    }

    public void setBM25Score(double score){
        this.BM25Score = score;
    }

    /**
     * @return name of the system
     */
    public String systemName() {
        return this.systemName;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        String result = String.valueOf(queryID)+" "+
                        literal+" "+documentID+" "+ String .valueOf(rank)+" "+
                        String.valueOf(BM25Score)+" "+systemName;
        return result.trim();
    }

}
