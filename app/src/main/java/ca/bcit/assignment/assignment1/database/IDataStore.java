package ca.bcit.assignment.assignment1.database;

public interface IDataStore {
    void saveState(String state);
    String getState();
}
