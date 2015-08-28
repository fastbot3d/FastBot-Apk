/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lkj.unicron.object;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.join.ogles.lib.GLColor;

import lkj.unicron.util.IOUtils;
import lkj.unicron.util.M4;

public class GLVertex {

    public float x;
    public float y;
    public float z;
    final short index; // index in vertex table
    GLColor color;

    GLVertex() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.index = -1;
        color = new GLColor(65535, 0, 0);
    }

    GLVertex(float x, float y, float z, int index) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.index = (short)index;
        color = new GLColor(65535, 0, 0);
    }
    
    
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("[ x=");
		builder.append(x);
		builder.append(",y=" + this.y + ",z=" + z + ",index=" + index + ",color=" + color);
		builder.append("\n  ");
		builder.append(" ]");
		return builder.toString();
	}

    @Override
    public boolean equals(Object other) {
        if (other instanceof GLVertex) {
            GLVertex v = (GLVertex)other;
            return (x == v.x && y == v.y && z == v.z);
        }
        return false;
    }

    static public int toFixed(float x) {
        return (int)(x * 65536.0f);
    }

    public void put(IntBuffer vertexBuffer, IntBuffer colorBuffer) {
        vertexBuffer.put(toFixed(x));
        vertexBuffer.put(toFixed(y));
        vertexBuffer.put(toFixed(z));
        if (color == null) {
            colorBuffer.put(0);
            colorBuffer.put(0);
            colorBuffer.put(0);
            colorBuffer.put(0);
        } else {
            colorBuffer.put(color.r);
            colorBuffer.put(color.g);
            colorBuffer.put(color.b);
            colorBuffer.put(color.a);
        }
    }

    public void update(IntBuffer vertexBuffer, M4 transform) {
        // skip to location of vertex in mVertex buffer
        vertexBuffer.position(index * 3);

        if (transform == null) {
            vertexBuffer.put(toFixed(x));
            vertexBuffer.put(toFixed(y));
            vertexBuffer.put(toFixed(z));
        } else {
            GLVertex temp = new GLVertex();
            transform.multiply(this, temp);
            vertexBuffer.put(toFixed(temp.x));
            vertexBuffer.put(toFixed(temp.y));
            vertexBuffer.put(toFixed(temp.z));
        }
    }
    
    public void put(FloatBuffer vertexBuffer, IntBuffer colorBuffer) {
        vertexBuffer.put(x);
        vertexBuffer.put(y);
        vertexBuffer.put(z);
        if (color == null) {
            colorBuffer.put(0);
            colorBuffer.put(0);
            colorBuffer.put(0);
            colorBuffer.put(0);
        } else {
            colorBuffer.put(color.r);
            colorBuffer.put(color.g);
            colorBuffer.put(color.b);
            colorBuffer.put(color.a);
        }
    }
    
    public void transform(M4 transform) {
        // skip to location of vertex in mVertex buffer
        //vertexBuffer.position(index * 3);

        if (transform != null) {
            GLVertex temp = new GLVertex();
            transform.multiply(this, temp);
            x = temp.x;
            y = temp.y;
            z = temp.z;
        }
    }
    

    
    public void move(int direction, float x, float y, float z) {
        // skip to location of vertex in mVertex buffer
        //vertexBuffer.position(index * 3);

        if ( (direction & (IOUtils.DIRECTION_LEFT)) > 0 ) {
            this.x = this.x - Math.abs(x);
        }
        if ( (direction & (IOUtils.DIRECTION_RIGHT)) > 0 ) {
            this.x = this.x + Math.abs(x);
        }
        if ( (direction & (IOUtils.DIRECTION_FRONT)) > 0 ) {
            this.z = this.z + Math.abs(z);
        }
        if ( (direction & (IOUtils.DIRECTION_BACK)) > 0 ) {
            this.z = this.z - Math.abs(z);
        }
    }
    
}
