package org.fore.mesh;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/** CPU-side vertex and index data builder. Accumulates positions, normals, UVs, tangents, and triangle indices. */
public class MeshData {

    private final List<Vector3f> positions = new ArrayList<>();
    private final List<Vector3f> normals = new ArrayList<>();
    private final List<Vector2f> texCoords = new ArrayList<>();
    private final List<Vector3f> tangents = new ArrayList<>();
    private final List<Integer> indices = new ArrayList<>();

    public MeshData addVertex(Vector3f pos, Vector3f normal, Vector2f uv) {
        positions.add(pos);
        normals.add(normal);
        texCoords.add(uv);
        return this;
    }

    public MeshData addVertex(Vector3f pos, Vector3f normal, Vector2f uv, Vector3f tangent) {
        positions.add(pos);
        normals.add(normal);
        texCoords.add(uv);
        tangents.add(tangent);
        return this;
    }

    public MeshData addTriangle(int i0, int i1, int i2) {
        indices.add(i0);
        indices.add(i1);
        indices.add(i2);
        return this;
    }

    public void computeTangents() {
        if (positions.isEmpty() || indices.isEmpty()) return;

        Vector3f[] tans = new Vector3f[positions.size()];
        for (int i = 0; i < tans.length; i++) tans[i] = new Vector3f();

        for (int i = 0; i < indices.size(); i += 3) {
            int i0 = indices.get(i), i1 = indices.get(i + 1), i2 = indices.get(i + 2);
            Vector3f p0 = positions.get(i0), p1 = positions.get(i1), p2 = positions.get(i2);
            Vector2f uv0 = texCoords.get(i0), uv1 = texCoords.get(i1), uv2 = texCoords.get(i2);

            Vector3f edge1 = new Vector3f(p1).sub(p0);
            Vector3f edge2 = new Vector3f(p2).sub(p0);
            float du1 = uv1.x - uv0.x, dv1 = uv1.y - uv0.y;
            float du2 = uv2.x - uv0.x, dv2 = uv2.y - uv0.y;

            float f = 1.0f / (du1 * dv2 - du2 * dv1 + 1e-8f);
            Vector3f tangent = new Vector3f(
                    f * (dv2 * edge1.x - dv1 * edge2.x),
                    f * (dv2 * edge1.y - dv1 * edge2.y),
                    f * (dv2 * edge1.z - dv1 * edge2.z)
            ).normalize();

            tans[i0].add(tangent);
            tans[i1].add(tangent);
            tans[i2].add(tangent);
        }

        tangents.clear();
        for (int i = 0; i < positions.size(); i++) {
            Vector3f n = normals.get(i);
            Vector3f t = tans[i];
            Vector3f orthogonal = new Vector3f(t).sub(new Vector3f(n).mul(n.dot(t))).normalize();
            tangents.add(orthogonal);
        }
    }

    public float[] toInterleavedArray(VertexLayout layout) {
        int vertexCount = positions.size();
        int floatsPerVertex = layout.getFloatsPerVertex();
        float[] data = new float[vertexCount * floatsPerVertex];

        for (int v = 0; v < vertexCount; v++) {
            int base = v * floatsPerVertex;
            int offset = 0;

            for (VertexLayout.Attribute attr : layout.getAttributes()) {
                switch (attr) {
                    case POSITION -> {
                        Vector3f p = positions.get(v);
                        data[base + offset] = p.x;
                        data[base + offset + 1] = p.y;
                        data[base + offset + 2] = p.z;
                    }
                    case NORMAL -> {
                        Vector3f n = v < normals.size() ? normals.get(v) : new Vector3f(0, 1, 0);
                        data[base + offset] = n.x;
                        data[base + offset + 1] = n.y;
                        data[base + offset + 2] = n.z;
                    }
                    case TEXCOORD -> {
                        Vector2f uv = v < texCoords.size() ? texCoords.get(v) : new Vector2f();
                        data[base + offset] = uv.x;
                        data[base + offset + 1] = uv.y;
                    }
                    case TANGENT -> {
                        Vector3f t = v < tangents.size() ? tangents.get(v) : new Vector3f(1, 0, 0);
                        data[base + offset] = t.x;
                        data[base + offset + 1] = t.y;
                        data[base + offset + 2] = t.z;
                    }
                    case COLOR -> {
                        data[base + offset] = 1;
                        data[base + offset + 1] = 1;
                        data[base + offset + 2] = 1;
                        data[base + offset + 3] = 1;
                    }
                }
                offset += attr.componentCount;
            }
        }
        return data;
    }

    public int[] toIndexArray() {
        return indices.stream().mapToInt(Integer::intValue).toArray();
    }

    public int getVertexCount() {
        return positions.size();
    }

    public int getIndexCount() {
        return indices.size();
    }

    public List<Vector3f> getPositions() { return positions; }
    public List<Vector3f> getNormals() { return normals; }
    public List<Vector2f> getTexCoords() { return texCoords; }
    public List<Vector3f> getTangents() { return tangents; }
    public List<Integer> getIndices() { return indices; }
}
