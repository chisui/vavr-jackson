package javaslang.jackson.datatype;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.*;
import javaslang.collection.*;

import java.lang.reflect.Type;

public class JavaslangTypeModifier extends TypeModifier {

    @Override
    public JavaType modifyType(JavaType type, Type jdkType, TypeBindings bindings, TypeFactory typeFactory)
    {
        final Class<?> raw = type.getRawClass();
        if (Seq.class.isAssignableFrom(raw) && CharSeq.class != raw) {
            return CollectionLikeType.upgradeFrom(type, type.containedTypeOrUnknown(0));
        }
        if (Set.class.isAssignableFrom(raw)) {
            return CollectionLikeType.upgradeFrom(type, type.containedTypeOrUnknown(0));
        }
        if (PriorityQueue.class.isAssignableFrom(raw)) {
            return CollectionLikeType.upgradeFrom(type, type.containedTypeOrUnknown(0));
        }
        if (Map.class.isAssignableFrom(raw)) {
            return MapLikeType.upgradeFrom(type, type.containedTypeOrUnknown(0), type.containedTypeOrUnknown(1));
        }
        if (Multimap.class.isAssignableFrom(raw)) {
            return MapLikeType.upgradeFrom(type, type.containedTypeOrUnknown(0), type.containedTypeOrUnknown(1));
        }
        return type;
    }
}
