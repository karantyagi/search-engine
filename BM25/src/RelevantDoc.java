public interface RelevantDoc {

    /**
     * @return Query Term
     */
    String Term();

    /**
     * @return document ID
     */
    String documentID();

    /**
     * @return Term Frequency
     */
    int termFreq();

}
