import java.util.List;
import java.util.Set;

public class Query1 implements Query {

    private int queryID;
    private List<String> queryTerms;
    private Set<String> RelevantDocs;
    private Set<Result> resultList;

    /**
     * @param queryID
     * @param queryTerms
     */
    public Query1(int queryID, List<String> queryTerms) {

        this.queryID = queryID;
        this.queryTerms = queryTerms;
    }


    /**
     * @return ID of the given query
     */
    public int queryID() {
        return this.queryID;
    }

    /**
     * @return given query terms
     */
    public List<String> queryTerms() {
        return this.queryTerms;
    }

    /**
     * @return list of relevant documents to the corresponding query
     */
    public Set<String> getRelevantDocuments() {
        return this.RelevantDocs;
    }


    public void setRelevantDocuments(Set<String> docList) {
       this.RelevantDocs = docList;
    }

    /**
     * @return list of results to the corresponding query after running BM25 retrieval model
     */
    public Set<Result> getQueryResults() {
        return this.resultList;
    }


    public void setQueryResults(Set<Result> results) {
        this.resultList = results;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        String queryText = " ";
        List<String> terms = this.queryTerms();
        for(String t: terms){
            queryText = queryText + t + " ";
        }
        return queryText.trim();
    }

    // ================== extra ====


}
