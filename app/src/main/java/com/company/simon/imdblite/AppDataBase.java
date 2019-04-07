package com.company.simon.imdblite;

import androidx.room.Database;
import androidx.room.RoomDatabase;


@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {

    public abstract MovieDao movieDao();
}
