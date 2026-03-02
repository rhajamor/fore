package org.fore.mesh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.lwjgl.opengl.GL41.*;

public class VertexLayout {

    public enum Attribute {
        POSITION(0, 3, GL_FLOAT),
        NORMAL(1, 3, GL_FLOAT),
        TEXCOORD(2, 2, GL_FLOAT),
        TANGENT(3, 3, GL_FLOAT),
        COLOR(4, 4, GL_FLOAT);

        public final int location;
        public final int componentCount;
        public final int glType;

        Attribute(int location, int componentCount, int glType) {
            this.location = location;
            this.componentCount = componentCount;
            this.glType = glType;
        }

        public int byteSize() {
            return componentCount * Float.BYTES;
        }
    }

    public static final VertexLayout POS = new VertexLayout(Attribute.POSITION);
    public static final VertexLayout POS_NORMAL = new VertexLayout(Attribute.POSITION, Attribute.NORMAL);
    public static final VertexLayout POS_NORMAL_UV = new VertexLayout(Attribute.POSITION, Attribute.NORMAL, Attribute.TEXCOORD);
    public static final VertexLayout POS_NORMAL_UV_TANGENT = new VertexLayout(Attribute.POSITION, Attribute.NORMAL, Attribute.TEXCOORD, Attribute.TANGENT);
    public static final VertexLayout FULL = new VertexLayout(Attribute.POSITION, Attribute.NORMAL, Attribute.TEXCOORD, Attribute.TANGENT, Attribute.COLOR);

    private final List<Attribute> attributes;
    private final int stride;

    public VertexLayout(Attribute... attrs) {
        List<Attribute> list = new ArrayList<>();
        int s = 0;
        for (Attribute a : attrs) {
            list.add(a);
            s += a.byteSize();
        }
        this.attributes = Collections.unmodifiableList(list);
        this.stride = s;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public int getStride() {
        return stride;
    }

    public int getFloatsPerVertex() {
        return stride / Float.BYTES;
    }

    public int getOffset(Attribute attr) {
        int offset = 0;
        for (Attribute a : attributes) {
            if (a == attr) return offset;
            offset += a.byteSize();
        }
        return -1;
    }

    public boolean has(Attribute attr) {
        return attributes.contains(attr);
    }

    public void apply() {
        int offset = 0;
        for (Attribute attr : attributes) {
            glEnableVertexAttribArray(attr.location);
            glVertexAttribPointer(attr.location, attr.componentCount, attr.glType, false, stride, offset);
            offset += attr.byteSize();
        }
    }
}
