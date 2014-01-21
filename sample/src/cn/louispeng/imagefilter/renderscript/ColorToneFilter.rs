#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

#include "Clamp.rsh"

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;

rs_script gScript;
float3 gTone;
float gSaturation;

// Magic factors

// Static variables
static float _hue;
static float _saturation;
static float _lum_tab[256];


static float3 RGBtoHLS(float3 rgb) {
	float H = 0, L = 0, S = 0;
	float3 f3;
    float cmax = fmax(rgb.r, fmax(rgb.g, rgb.b));
    float cmin = fmin(rgb.r, fmin(rgb.g, rgb.b));

    L = (cmax + cmin) / 2;
    if (cmax == cmin) {
        S = 0.0f;
        H = 0.0f;
    } else {
	    float delta = cmax - cmin;
	
	    if (L < 0.5f) {
	        S = delta / (cmax + cmin);
	    } else {
	        S = delta / (2.0f - cmax - cmin);
	    }
	
	    if (rgb.r == cmax) {
	        H = (rgb.g - rgb.b) / delta;
        } else if (rgb.g == cmax) {
	        H = 2.0f + (rgb.b - rgb.r) / delta;
        } else {
	        H = 4.0f + (rgb.r - rgb.g) / delta;
        }
	
	    H /= 6.0f;
	
	    if (H < 0.0f) {
	        H += 1.0f;
        }
	}
	
    f3.r = H;
    f3.g = L;
    f3.b = S;
    return f3;
}

static float HLS_Value(float n1, float n2, float h) {
    if (h > 6.0f) {
        h -= 6.0f;
    } else if (h < 0.0f) {
        h += 6.0f;
    }

    if (h < 1.0f) {
        return n1 + (n2 - n1) * h;
    } else if (h < 3.0f) {
        return n2;
    } else if (h < 4.0f) {
        return n1 + (n2 - n1) * (4.0f - h);
    }
        
    return n1;
}

// / HLS --> RGB.
static float3 HLStoRGB(float H, float L, float S) {
	float3 rgb;
    if ((!(S > 0.0f)) && (!(S < 0.0f))) {
    	rgb.r = L;
    	rgb.g = L;
    	rgb.b = L;
    } else {
	    float m1, m2;
	    if (L > 0.5f) {
	        m2 = L + S - L * S;
        } else {
	        m2 = L * (1.0f + S);
        }
	    
	    m1 = 2.0f * L - m2;
	
	    rgb.r = HLS_Value(m1, m2, H * 6.0f + 2.0f);
	    rgb.g = HLS_Value(m1, m2, H * 6.0f);
	    rgb.b = HLS_Value(m1, m2, H * 6.0f - 2.0f);
    }
    return rgb;
}

static int32_t GetGrayscale(float3 rgb) {
    return (int32_t)((0.3f * rgb.r + 0.59f * rgb.g + 0.11f * rgb.b) * 255);
}

static void setup() {
	float l = 0.0f;
    float3 result = RGBtoHLS(gTone);
    _hue = result.x;
    _saturation = result.z;
    _saturation = _saturation * (gSaturation * gSaturation);
    _saturation = ((_saturation < 1) ? _saturation : 1);

    for (int32_t i = 0; i < 256; i++) {
    	float3 cr = {i, i, i};
    	cr = cr / 255.0f;
    	
        float ll = 0.0f;
        result = RGBtoHLS(cr);
        ll = result.y;
        ll = ll * (1 + (128.0f - fabs(gSaturation * 255.0f - 128.0f)) / 128.0f / 9.0f);
        _lum_tab[i] = ((ll < 1.0f) ? ll : 1.0f);
    }
}

void filter() {
	setup();
    rsForEach(gScript, gIn, gOut, 0, 0);	// for each element of the input allocation,
    										// call root() method on gScript
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	float4 f4 = rsUnpackColor8888(*v_in);	// extract RGBA values, see rs_core.rsh
    
    double l = _lum_tab[GetGrayscale(f4.rgb)];
    float3 f3 = HLStoRGB(_hue, l, _saturation);
    
    *v_out = rsPackColorTo8888(f3);
}