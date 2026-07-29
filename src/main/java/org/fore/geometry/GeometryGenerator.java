package org.fore.geometry;

import org.fore.mesh.MeshData;
import org.fore.math.MathUtil;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Procedural mesh generators for built-in shapes. All generators produce
 * {@link org.fore.mesh.MeshData} with positions, normals, texture coordinates,
 * and tangents. Available shapes: box, sphere, plane, torus, cylinder, cone.
 */
public final class GeometryGenerator {

    private GeometryGenerator() {}

    public static MeshData box(float width, float height, float depth) {
        MeshData data = new MeshData();
        float hw = width * 0.5f, hh = height * 0.5f, hd = depth * 0.5f;

        Vector3f[] faceNormals = {
                new Vector3f(0, 0, 1), new Vector3f(0, 0, -1),
                new Vector3f(1, 0, 0), new Vector3f(-1, 0, 0),
                new Vector3f(0, 1, 0), new Vector3f(0, -1, 0)
        };

        float[][] faceVerts = {
                {-hw, -hh, hd, hw, -hh, hd, hw, hh, hd, -hw, hh, hd},
                {hw, -hh, -hd, -hw, -hh, -hd, -hw, hh, -hd, hw, hh, -hd},
                {hw, -hh, hd, hw, -hh, -hd, hw, hh, -hd, hw, hh, hd},
                {-hw, -hh, -hd, -hw, -hh, hd, -hw, hh, hd, -hw, hh, -hd},
                {-hw, hh, hd, hw, hh, hd, hw, hh, -hd, -hw, hh, -hd},
                {-hw, -hh, -hd, hw, -hh, -hd, hw, -hh, hd, -hw, -hh, hd}
        };

        Vector2f[] faceUVs = {
                new Vector2f(0, 0), new Vector2f(1, 0), new Vector2f(1, 1), new Vector2f(0, 1)
        };

        for (int face = 0; face < 6; face++) {
            int base = data.getVertexCount();
            for (int v = 0; v < 4; v++) {
                data.addVertex(
                        new Vector3f(faceVerts[face][v * 3], faceVerts[face][v * 3 + 1], faceVerts[face][v * 3 + 2]),
                        new Vector3f(faceNormals[face]),
                        new Vector2f(faceUVs[v])
                );
            }
            data.addTriangle(base, base + 1, base + 2);
            data.addTriangle(base, base + 2, base + 3);
        }

        data.computeTangents();
        return data;
    }

    public static MeshData sphere(float radius, int sectors, int stacks) {
        MeshData data = new MeshData();

        for (int i = 0; i <= stacks; i++) {
            float stackAngle = MathUtil.HALF_PI - (float) i / stacks * MathUtil.PI;
            float xy = radius * (float) Math.cos(stackAngle);
            float z = radius * (float) Math.sin(stackAngle);

            for (int j = 0; j <= sectors; j++) {
                float sectorAngle = (float) j / sectors * MathUtil.TWO_PI;
                float x = xy * (float) Math.cos(sectorAngle);
                float y = xy * (float) Math.sin(sectorAngle);

                Vector3f pos = new Vector3f(x, z, y);
                Vector3f normal = new Vector3f(x, z, y).normalize();
                Vector2f uv = new Vector2f((float) j / sectors, (float) i / stacks);

                data.addVertex(pos, normal, uv);
            }
        }

        for (int i = 0; i < stacks; i++) {
            for (int j = 0; j < sectors; j++) {
                int first = i * (sectors + 1) + j;
                int second = first + sectors + 1;

                if (i != 0) {
                    data.addTriangle(first, second, first + 1);
                }
                if (i != stacks - 1) {
                    data.addTriangle(first + 1, second, second + 1);
                }
            }
        }

        data.computeTangents();
        return data;
    }

    public static MeshData plane(float width, float depth, int segW, int segD) {
        MeshData data = new MeshData();
        float hw = width * 0.5f, hd = depth * 0.5f;
        float stepW = width / segW, stepD = depth / segD;

        for (int z = 0; z <= segD; z++) {
            for (int x = 0; x <= segW; x++) {
                float px = -hw + x * stepW;
                float pz = -hd + z * stepD;
                float u = (float) x / segW;
                float v = (float) z / segD;

                data.addVertex(
                        new Vector3f(px, 0, pz),
                        new Vector3f(0, 1, 0),
                        new Vector2f(u, v)
                );
            }
        }

        for (int z = 0; z < segD; z++) {
            for (int x = 0; x < segW; x++) {
                int topLeft = z * (segW + 1) + x;
                int topRight = topLeft + 1;
                int bottomLeft = (z + 1) * (segW + 1) + x;
                int bottomRight = bottomLeft + 1;

                data.addTriangle(topLeft, bottomLeft, topRight);
                data.addTriangle(topRight, bottomLeft, bottomRight);
            }
        }

        data.computeTangents();
        return data;
    }

    public static MeshData torus(float majorRadius, float minorRadius, int majorSegments, int minorSegments) {
        MeshData data = new MeshData();

        for (int i = 0; i <= majorSegments; i++) {
            float theta = (float) i / majorSegments * MathUtil.TWO_PI;
            float cosTheta = (float) Math.cos(theta);
            float sinTheta = (float) Math.sin(theta);

            for (int j = 0; j <= minorSegments; j++) {
                float phi = (float) j / minorSegments * MathUtil.TWO_PI;
                float cosPhi = (float) Math.cos(phi);
                float sinPhi = (float) Math.sin(phi);

                float x = (majorRadius + minorRadius * cosPhi) * cosTheta;
                float y = minorRadius * sinPhi;
                float z = (majorRadius + minorRadius * cosPhi) * sinTheta;

                float nx = cosPhi * cosTheta;
                float ny = sinPhi;
                float nz = cosPhi * sinTheta;

                float u = (float) i / majorSegments;
                float v = (float) j / minorSegments;

                data.addVertex(new Vector3f(x, y, z), new Vector3f(nx, ny, nz), new Vector2f(u, v));
            }
        }

        for (int i = 0; i < majorSegments; i++) {
            for (int j = 0; j < minorSegments; j++) {
                int first = i * (minorSegments + 1) + j;
                int second = first + minorSegments + 1;

                data.addTriangle(first, second, first + 1);
                data.addTriangle(first + 1, second, second + 1);
            }
        }

        data.computeTangents();
        return data;
    }

    public static MeshData cylinder(float radius, float height, int segments) {
        MeshData data = new MeshData();
        float halfH = height * 0.5f;

        for (int i = 0; i <= segments; i++) {
            float angle = (float) i / segments * MathUtil.TWO_PI;
            float x = radius * (float) Math.cos(angle);
            float z = radius * (float) Math.sin(angle);
            float nx = (float) Math.cos(angle);
            float nz = (float) Math.sin(angle);
            float u = (float) i / segments;

            data.addVertex(new Vector3f(x, -halfH, z), new Vector3f(nx, 0, nz), new Vector2f(u, 0));
            data.addVertex(new Vector3f(x, halfH, z), new Vector3f(nx, 0, nz), new Vector2f(u, 1));
        }

        for (int i = 0; i < segments; i++) {
            int b = i * 2;
            data.addTriangle(b, b + 2, b + 1);
            data.addTriangle(b + 1, b + 2, b + 3);
        }

        int topCenter = data.getVertexCount();
        data.addVertex(new Vector3f(0, halfH, 0), new Vector3f(0, 1, 0), new Vector2f(0.5f, 0.5f));
        int bottomCenter = data.getVertexCount();
        data.addVertex(new Vector3f(0, -halfH, 0), new Vector3f(0, -1, 0), new Vector2f(0.5f, 0.5f));

        for (int i = 0; i <= segments; i++) {
            float angle = (float) i / segments * MathUtil.TWO_PI;
            float x = radius * (float) Math.cos(angle);
            float z = radius * (float) Math.sin(angle);

            data.addVertex(new Vector3f(x, halfH, z), new Vector3f(0, 1, 0),
                    new Vector2f(0.5f + 0.5f * (float) Math.cos(angle), 0.5f + 0.5f * (float) Math.sin(angle)));
            data.addVertex(new Vector3f(x, -halfH, z), new Vector3f(0, -1, 0),
                    new Vector2f(0.5f + 0.5f * (float) Math.cos(angle), 0.5f + 0.5f * (float) Math.sin(angle)));
        }

        int capBase = topCenter + 2;
        for (int i = 0; i < segments; i++) {
            data.addTriangle(topCenter, capBase + i * 2, capBase + (i + 1) * 2);
            data.addTriangle(bottomCenter, capBase + (i + 1) * 2 + 1, capBase + i * 2 + 1);
        }

        data.computeTangents();
        return data;
    }

    public static MeshData cone(float radius, float height, int segments) {
        MeshData data = new MeshData();
        float halfH = height * 0.5f;
        float slope = radius / height;

        int tipIndex = data.getVertexCount();
        for (int i = 0; i <= segments; i++) {
            float angle = (float) i / segments * MathUtil.TWO_PI;
            float nx = (float) Math.cos(angle);
            float nz = (float) Math.sin(angle);
            Vector3f normal = new Vector3f(nx, slope, nz).normalize();
            data.addVertex(new Vector3f(0, halfH, 0), normal, new Vector2f((float) i / segments, 1));
        }

        int baseRingStart = data.getVertexCount();
        for (int i = 0; i <= segments; i++) {
            float angle = (float) i / segments * MathUtil.TWO_PI;
            float x = radius * (float) Math.cos(angle);
            float z = radius * (float) Math.sin(angle);
            float nx = (float) Math.cos(angle);
            float nz = (float) Math.sin(angle);
            Vector3f normal = new Vector3f(nx, slope, nz).normalize();
            data.addVertex(new Vector3f(x, -halfH, z), normal, new Vector2f((float) i / segments, 0));
        }

        for (int i = 0; i < segments; i++) {
            data.addTriangle(tipIndex + i, baseRingStart + i, baseRingStart + i + 1);
        }

        int centerIndex = data.getVertexCount();
        data.addVertex(new Vector3f(0, -halfH, 0), new Vector3f(0, -1, 0), new Vector2f(0.5f, 0.5f));
        int discStart = data.getVertexCount();
        for (int i = 0; i <= segments; i++) {
            float angle = (float) i / segments * MathUtil.TWO_PI;
            float x = radius * (float) Math.cos(angle);
            float z = radius * (float) Math.sin(angle);
            data.addVertex(new Vector3f(x, -halfH, z), new Vector3f(0, -1, 0),
                    new Vector2f(0.5f + 0.5f * (float) Math.cos(angle), 0.5f + 0.5f * (float) Math.sin(angle)));
        }
        for (int i = 0; i < segments; i++) {
            data.addTriangle(centerIndex, discStart + i + 1, discStart + i);
        }

        data.computeTangents();
        return data;
    }
}
