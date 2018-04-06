import java.util.List;

/**
 * Represents a query used for retrieving results
 * queryID      : represents the query identifier
 * queryText    : represents the query
 *
 */

public interface Query {

    /**
     * @return ID of the given query
     */
    int queryID();

    /**
     * @return given query terms
     */
    List<String> queryTerms();

    /**
     * @return list of relevant documents to the corresponding query
     */
    List<RelevantDoc> getRelevantDocuments();

    /**
     * @return list of results to the corresponding query after running BM25 retrieval model
     */
    List<Result> getQueryResults();



}
