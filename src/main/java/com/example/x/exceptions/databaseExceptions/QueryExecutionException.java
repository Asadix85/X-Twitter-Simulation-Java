package com.example.x.exceptions.databaseExceptions;

public class QueryExecutionException extends DatabaseException {
    public QueryExecutionException(String query) {
        super("Failed to execute query: " + query);
    }

    public QueryExecutionException(String query, Throwable cause) {
        super("Failed to execute query: " + query, cause);
    }
}
