package ca.bcit.assignment.assignment1.database;

public class DataStorageImp implements IDataStore {
    public String state_ = null;

    public void saveState(String state) {
        state_ = state;
    }

    public String getState() {
        return state_;
    }
}
