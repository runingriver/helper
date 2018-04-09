package org.helper.common.objsize2;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import sun.misc.Unsafe;

/**
 * 实现计算对象大小的类
 */
public class ClassIntrospector {

    private static final Unsafe unsafe;

    /**
     * 引用大小
     */
    private static final int objectRefSize;

    public static int getObjectRefSize() {
        return objectRefSize;
    }

    /**
     * 获取unsafe对象
     */
    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);

            objectRefSize = unsafe.arrayIndexScale(Object[].class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sizes of all primitive values
     */
    private static final Map<Class, Integer> primitiveSizes;

    static {
        primitiveSizes = new HashMap<>(10);
        primitiveSizes.put(byte.class, 1);
        primitiveSizes.put(char.class, 2);
        primitiveSizes.put(int.class, 4);
        primitiveSizes.put(long.class, 8);
        primitiveSizes.put(float.class, 4);
        primitiveSizes.put(double.class, 8);
        primitiveSizes.put(boolean.class, 1);
    }

    public ObjectInfo introspect(final Object obj) throws IllegalAccessException {
        try {
            return introspect(obj, null);
        } finally {
            // 获得对象大小后，清空缓存，以便对象重用
            visited.clear();
        }
    }

    private IdentityHashMap<Object, Boolean> visited = new IdentityHashMap<>(100);

    private ObjectInfo introspect(final Object obj, final Field fld) throws IllegalAccessException {
        boolean isPrimitive = fld != null && fld.getType().isPrimitive();
        // will be set to true if we have already
        boolean isRecursive = false;
        // seen this object
        if (!isPrimitive) {
            if (visited.containsKey(obj)) {
                isRecursive = true;
            }
            visited.put(obj, true);
        }

        final Class type = (fld == null || (obj != null && !isPrimitive)) ? obj.getClass() : fld.getType();
        int arraySize = 0;
        int baseOffset = 0;
        int indexScale = 0;
        if (type.isArray() && obj != null) {
            baseOffset = unsafe.arrayBaseOffset(type);
            indexScale = unsafe.arrayIndexScale(type);
            arraySize = baseOffset + indexScale * Array.getLength(obj);
        }

        final ObjectInfo root;
        if (fld == null) {
            root = new ObjectInfo("", type.getCanonicalName(), getContents(obj,
                    type), 0, getShallowSize(type), arraySize, baseOffset,
                    indexScale);
        } else {
            final int offset = (int) unsafe.objectFieldOffset(fld);
            root = new ObjectInfo(fld.getName(), type.getCanonicalName(),
                    getContents(obj, type), offset, getShallowSize(type),
                    arraySize, baseOffset, indexScale);
        }

        if (!isRecursive && obj != null) {
            if (isObjectArray(type)) {
                // introspect object arrays
                final Object[] ar = (Object[]) obj;
                for (final Object item : ar) {
                    if (item != null) {
                        root.addChild(introspect(item, null));
                    }
                }
            } else {
                for (final Field field : getAllFields(type)) {
                    if ((field.getModifiers() & Modifier.STATIC) != 0) {
                        continue;
                    }
                    field.setAccessible(true);
                    root.addChild(introspect(field.get(obj), field));
                }
            }
        }

        // sort by offset
        root.sort();
        return root;
    }

    /**
     * get all fields for this class, including all superclasses fields
     */
    private static List<Field> getAllFields(final Class type) {
        if (type.isPrimitive()) {
            return Collections.emptyList();
        }
        Class cur = type;
        final List<Field> res = new ArrayList<Field>(10);
        while (true) {
            Collections.addAll(res, cur.getDeclaredFields());
            if (cur == Object.class) {
                break;
            }
            cur = cur.getSuperclass();
        }
        return res;
    }

    /**
     * check if it is an array of objects. I suspect there must be a more
     * API-friendly way to make this check.
     */
    private static boolean isObjectArray(final Class type) {
        if (!type.isArray()) {
            return false;
        }
        return type != byte[].class && type != boolean[].class
                && type != char[].class && type != short[].class
                && type != int[].class && type != long[].class
                && type != float[].class && type != double[].class;
    }

    private static String getContents(final Object val, final Class type) {
        //advanced toString logic
        if (val == null) {
            return "null";
        }
        if (type.isArray()) {
            if (type == byte[].class) {
                return Arrays.toString((byte[]) val);
            } else if (type == boolean[].class) {
                return Arrays.toString((boolean[]) val);
            } else if (type == char[].class) {
                return Arrays.toString((char[]) val);
            } else if (type == short[].class) {
                return Arrays.toString((short[]) val);
            } else if (type == int[].class) {
                return Arrays.toString((int[]) val);
            } else if (type == long[].class) {
                return Arrays.toString((long[]) val);
            } else if (type == float[].class) {
                return Arrays.toString((float[]) val);
            } else if (type == double[].class) {
                return Arrays.toString((double[]) val);
            } else {
                return Arrays.toString((Object[]) val);
            }
        }
        return val.toString();
    }

    /**
     * obtain a shallow size of a field of given class (primitive or object reference size)
     */
    private static int getShallowSize(final Class type) {
        if (type.isPrimitive()) {
            final Integer res = primitiveSizes.get(type);
            return res != null ? res : 0;
        } else {
            return objectRefSize;
        }
    }

    public static class ObjectInfo {

        public final String name;

        public final String type;

        public final String contents;

        public final int offset;

        public final int length;

        public final int arrayBase;

        public final int arrayElementSize;

        public final int arraySize;

        public final List<ObjectInfo> children;

        public ObjectInfo(String name, String type, String contents, int offset, int length, int arraySize,
                          int arrayBase, int arrayElementSize) {
            this.name = name;
            this.type = type;
            this.contents = contents;
            this.offset = offset;
            this.length = length;
            this.arraySize = arraySize;
            this.arrayBase = arrayBase;
            this.arrayElementSize = arrayElementSize;
            children = new ArrayList<ObjectInfo>(1);
        }

        public void addChild(final ObjectInfo info) {
            if (info != null) {
                children.add(info);
            }
        }


        public long getDeepSize() {
            return addPaddingSize(arraySize + getUnderlyingSize(arraySize != 0));
        }

        long size = 0;

        private long getUnderlyingSize(final boolean isArray) {
            //long size = 0;
            for (final ObjectInfo child : children) {
                size += child.arraySize + child.getUnderlyingSize(child.arraySize != 0);
            }
            if (!isArray && !children.isEmpty()) {
                int tempSize = children.get(children.size() - 1).offset + children.get(children.size() - 1).length;
                size += addPaddingSize(tempSize);
            }

            return size;
        }

        private static final class OffsetComparator implements Comparator<ObjectInfo> {
            @Override
            public int compare(final ObjectInfo o1, final ObjectInfo o2) {
                //safe because offsets are small non-negative numbers
                return o1.offset - o2.offset;
            }
        }

        public void sort() {
            //sort all children by their offset
            Collections.sort(children, new OffsetComparator());
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            toStringHelper(sb, 0);
            return sb.toString();
        }

        private void toStringHelper(final StringBuilder sb, final int depth) {
            depth(sb, depth).append("name=").append(name).append(", type=").append(type)
                    .append(", contents=").append(contents).append(", offset=").append(offset)
                    .append(", length=").append(length);
            if (arraySize > 0) {
                sb.append(", arrayBase=").append(arrayBase);
                sb.append(", arrayElemSize=").append(arrayElementSize);
                sb.append(", arraySize=").append(arraySize);
            }
            for (final ObjectInfo child : children) {
                sb.append('\n');
                child.toStringHelper(sb, depth + 1);
            }
        }

        private StringBuilder depth(final StringBuilder sb, final int depth) {
            for (int i = 0; i < depth; ++i) {
                sb.append("\t");
            }
            return sb;
        }

        private long addPaddingSize(long size) {
            if (size % 8 != 0) {
                return (size / 8 + 1) * 8;
            }
            return size;
        }

    }
}