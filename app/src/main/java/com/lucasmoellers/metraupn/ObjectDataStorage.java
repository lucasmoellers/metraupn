package com.lucasmoellers.metraupn;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class ObjectDataStorage<T> {
    protected Context context;
    public ObjectDataStorage(Context context) {
        this.context = context;
    }

    public abstract String getFileName();

    public void save(T obj) throws IOException {
        FileOutputStream fos = context.openFileOutput(getFileName(), Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(obj);
    }

    public T fetch() throws IOException, ClassNotFoundException {
        FileInputStream fis = context.openFileInput(getFileName());
        ObjectInputStream ois = new ObjectInputStream(fis);
        return (T) ois.readObject();
    }
}
