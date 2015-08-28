/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless requig by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.join.ogles.lib;

public class GLColor {

    public  int r;
    public  int g;
    public  int b;
    public  int a;

    public GLColor(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public GLColor(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = 65535;
    }
    
    public GLColor() {
        this.r = 65535;
        this.g = 65535;
        this.b = 65535;
        this.a = 65535;
	}

		@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("[ red=");
		builder.append(r);
		builder.append(",green=" + this.g + ",blue=" + b + ",alpa=" + a);
		builder.append(" ]");
		return builder.toString();
	}
	

    @Override
    public boolean equals(Object other) {
        if (other instanceof GLColor) {
            GLColor color = (GLColor)other;
            return (r == color.r &&
                    g == color.g &&
                    b == color.b &&
                    a == color.a);
        }
        return false;
    }
}
