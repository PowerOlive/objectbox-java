package io.objectbox.query;

import io.objectbox.Box;
import io.objectbox.Property;
import io.objectbox.annotation.apihint.Experimental;
import io.objectbox.annotation.apihint.Internal;

/**
 * Created by Markus on 13.10.2016.
 */
@Experimental
public class QueryBuilder<T> {
    private final Box<T> box;

    private long handle;

    private static native long nativeCreate(long storeHandle, String entityName);

    private static native long nativeDestroy(long handle);

    private static native long nativeBuild(long handle);

    // ------------------------------ (Not)Null------------------------------

    private static native void nativeNull(long handle, int propertyId);

    private static native void nativeNotNull(long handle, int propertyId);

    // ------------------------------ Integers ------------------------------

    private static native void nativeEqual(long handle, int propertyId, long value);

    private static native void nativeNotEqual(long handle, int propertyId, long value);

    private static native void nativeLess(long handle, int propertyId, long value);

    private static native void nativeGreater(long handle, int propertyId, long value);

    private static native void nativeBetween(long handle, int propertyId, long value1, long value2);

    private static native void nativeIn(long handle, int propertyId, int[] values);

    private static native void nativeIn(long handle, int propertyId, long[] values);

    // ------------------------------ Strings ------------------------------

    private static native void nativeEqual(long handle, int propertyId, String value);

    private static native void nativeNotEqual(long handle, int propertyId, String value);

    private static native void nativeContains(long handle, int propertyId, String value);

    private static native void nativeStartsWith(long handle, int propertyId, String value);

    private static native void nativeEndsWith(long handle, int propertyId, String value);

    // ------------------------------ FPs ------------------------------
    private static native void nativeLess(long handle, int propertyId, double value);

    private static native void nativeGreater(long handle, int propertyId, double value);


    @Internal
    public QueryBuilder(Box<T> box, long storeHandle, String entityName) {
        this.box = box;

        // This ensures that all properties have been set
        box.getProperties();

        handle = nativeCreate(storeHandle, entityName);
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    public void close() {
        if (handle != 0) {
            nativeDestroy(handle);
            handle = 0;
        }
    }

    /**
     * Builds the query and closes this QueryBuilder.
     */
    public Query<T> build() {
        if (handle == 0) {
            throw new IllegalStateException("This QueryBuilder has already been closed. Please use a new instance.");
        }
        long queryHandle = nativeBuild(handle);
        Query<T> query = new Query<T>(box, queryHandle);
        close();
        return query;
    }

    public QueryBuilder<T> isNull(Property property) {
        nativeNull(handle, property.getId());
        return this;
    }

    public QueryBuilder<T> notNull(Property property) {
        nativeNotNull(handle, property.getId());
        return this;
    }

    public QueryBuilder<T> equal(Property property, long value) {
        nativeEqual(handle, property.getId(), value);
        return this;
    }

    public QueryBuilder<T> notEqual(Property property, long value) {
        nativeNotEqual(handle, property.getId(), value);
        return this;
    }

    public QueryBuilder<T> less(Property property, long value) {
        nativeLess(handle, property.getId(), value);
        return this;
    }

    public QueryBuilder<T> greater(Property property, long value) {
        nativeGreater(handle, property.getId(), value);
        return this;
    }

    public QueryBuilder<T> between(Property property, long value1, long value2) {
        nativeBetween(handle, property.getId(), value1, value2);
        return this;
    }

    // FIXME DbException: invalid unordered_map<K, T> key
    public QueryBuilder<T> in(Property property, long[] values) {
        nativeIn(handle, property.getId(), values);
        return this;
    }

    public QueryBuilder<T> in(Property property, int[] values) {
        nativeIn(handle, property.getId(), values);
        return this;
    }

    public QueryBuilder<T> equal(Property property, String value) {
        nativeEqual(handle, property.getId(), value);
        return this;
    }

    public QueryBuilder<T> notEqual(Property property, String value) {
        nativeNotEqual(handle, property.getId(), value);
        return this;
    }

    public QueryBuilder<T> contains(Property property, String value) {
        nativeContains(handle, property.getId(), value);
        return this;
    }

    public QueryBuilder<T> startsWith(Property property, String value) {
        nativeStartsWith(handle, property.getId(), value);
        return this;
    }

    public QueryBuilder<T> endsWith(Property property, String value) {
        nativeEndsWith(handle, property.getId(), value);
        return this;
    }


    public QueryBuilder<T> less(Property property, double value) {
        nativeLess(handle, property.getId(), value);
        return this;
    }

    public QueryBuilder<T> greater(Property property, double value) {
        nativeGreater(handle, property.getId(), value);
        return this;
    }

}