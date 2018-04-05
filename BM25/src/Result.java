
/**
 * Represents the document which is relevant to the given query
 */
public interface Result {

    /**
     * @return queryID
     */
    int queryId();

    /**
     * @return literal
     */
    String literal();

    /**
     * @return document ID
     */
    String documentID();

    /**
     * @return rank of document as per BM25 score
     */
    int getRank();

    void setRank(int rank);

    /**
     * @return BM25 score of the document for the given query
     */
    double getBM25Score();

    void setBM25Score(double score);

    /**
     * @return name of the system
     */
    String systemName();
}
