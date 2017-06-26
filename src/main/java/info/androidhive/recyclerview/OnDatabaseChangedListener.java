package info.androidhive.recyclerview;


public interface OnDatabaseChangedListener{
    void onNewDatabaseEntryAdded();
    void onDatabaseEntryRenamed();
    void onDatabaseImageChanged();
}