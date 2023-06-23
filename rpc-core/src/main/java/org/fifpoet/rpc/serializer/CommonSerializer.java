package org.fifpoet.rpc.serializer;

/**
 * a universal serialize & deserialize interface
 */
public interface CommonSerializer {

    // convert obj to byte array
    byte[] serialize(Object obj);

    /**
     * convert bytes to obj
     * @param bytes bytes
     * @param clazz
     * @return java obj
     */
    Object deserialize(byte[] bytes, Class<?> clazz);

    int getCode();

    static CommonSerializer getByCode(int code) {
        switch (code) {
            case 0:
                return new KryoSerializer();
            case 1:
                return new JsonSerializer();
            default:
                return null;
        }
    }

}
