#ifndef __RS_HSL_RSH__
#define __RS_HSL_RSH__

// HSL to RGB helper routine
static float Hue_2_RGB(float v1, float v2, float vHue) {
    if (vHue < 0) {
        vHue += 1;
    }
    
    if (vHue > 1) {
        vHue -= 1;
    }
    
    if ((6 * vHue) < 1) {
        return (v1 + (v2 - v1) * 6 * vHue);
    }
    
    if ((2 * vHue) < 1) {
        return v2;
    }
    
    if ((3 * vHue) < 2) {
        return (v1 + (v2 - v1) * ((2.0 / 3) - vHue) * 6);
    }
    
    return v1;
}

static float3 HslToRgb(float3 hsl) {
	float3 rgb;
    if (hsl.x == 0) {
        // gray values
        rgb.r = hsl.z;
        rgb.g = hsl.z;
        rgb.b = hsl.z;
    } else {
        float v1, v2;
        float hue = hsl.x / 360.0f;

        v2 = (hsl.z < 0.5) ? (hsl.z * (1 + hsl.y)) : ((hsl.z + hsl.y) - (hsl.z * hsl.y));
        v1 = 2 * hsl.z - v2;

        rgb.r = Hue_2_RGB(v1, v2, hue + (1.0f / 3));
        rgb.g = Hue_2_RGB(v1, v2, hue);
        rgb.b = Hue_2_RGB(v1, v2, hue - (1.0f / 3));
    }
    return rgb;
}

static float3 Interpolate(float3 hsl, float3 c2, float amount) {
	float3 retHSL;
	retHSL.x = hsl.x + ((c2.x - hsl.x) * amount);
	retHSL.y = hsl.y + ((c2.y - hsl.y) * amount);
	retHSL.z = hsl.z + ((c2.z - hsl.z) * amount);
	return retHSL;
}

static float3 RgbToHsl(float3 rgb) {
	float3 hsl;

    float min = fmin(fmin(rgb.r, rgb.g), rgb.b);
    float max = fmax(fmax(rgb.r, rgb.g), rgb.b);
    float delta = max - min;

    // get luminance value
    hsl.z = (max + min) / 2;

    if (delta == 0) {
        // gray color
        hsl.x = 0;
        hsl.y = 0;
    } else {
        // get saturation value
        hsl.y = (hsl.z < 0.5f) ? (delta / (max + min)) : (delta / (2.0f - max - min));

        // get hue value
        float del_r = (((max - rgb.r) / 6) + (delta / 2)) / delta;
        float del_g = (((max - rgb.g) / 6) + (delta / 2)) / delta;
        float del_b = (((max - rgb.b) / 6) + (delta / 2)) / delta;
        float hue;

        if (rgb.r == max) {
            hue = del_b - del_g;
        } else if (rgb.g == max) {
            hue = (1.0f / 3) + del_r - del_b;
        } else {
            hue = (2.0f / 3) + del_g - del_r;
        }

        // correct hue if needed
        if (hue < 0) {
            hue += 1;
        }
        
        if (hue > 1) {
            hue -= 1;
        }

        hsl.x = hue * 360;
    }
    
    return hsl;
}

#endif //#ifndef __RS_HSL_RSH__
