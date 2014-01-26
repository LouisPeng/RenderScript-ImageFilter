#ifndef __RS_IMAGE_BLEND_RSH__
#define __RS_IMAGE_BLEND_RSH__

#include "Clamp.rsh"

typedef enum _BlendMode {
    Normal = 0,
    Additive,
    Subtractive,
    Multiply,
    Overlay,
    ColorDodge,
    ColorBurn,
    Lighten,
    Darken,
    Reflect,
    Glow,
    LinearLight,
    Frame			/* photo frame */
}BlendMode;

static float3 FBlend(const float3 srcRGB, const float3 blendRGB, const BlendMode blendMode, const float mixture) {
	float3 retRGB;
	
	switch(blendMode) {	
	case Additive:{
		retRGB = srcRGB + blendRGB;
		retRGB = FClamp01Float3(retRGB);
        break;
    }
	case Subtractive:{
        retRGB = srcRGB + blendRGB;
        retRGB.r = (retRGB.r < 1.0f) ? 0 : (retRGB.r - 1.0f);
        retRGB.g = (retRGB.g < 1.0f) ? 0 : (retRGB.g - 1.0f);
        retRGB.b = (retRGB.b < 1.0f) ? 0 : (retRGB.b - 1.0f);
        break;
    }
	case Multiply:{
		retRGB = srcRGB * blendRGB;
		break;
	}
	case Overlay:{
		retRGB.r = (blendRGB.r < 0.5f) ? (((2 * srcRGB.r) * blendRGB.r))
                : (1.0f - (((2 * (1.0f - srcRGB.r)) * (1.0f - blendRGB.r))));
        retRGB.g = (blendRGB.g < 0.5f) ? (((2 * srcRGB.g) * blendRGB.g))
                : (1.0f - (((2 * (1.0f - srcRGB.g)) * (1.0f - blendRGB.g))));
        retRGB.b = (blendRGB.b < 0.5f) ? (((2 * srcRGB.b) * blendRGB.b))
                : (1.0f - (((2 * (1.0f - srcRGB.b)) * (1.0f - blendRGB.b))));
		break;
	}
	case ColorDodge:{
		retRGB.r = (srcRGB.r) / (1.0f - ((blendRGB.r != 1.0f) ? blendRGB.r : 0.9961f));
        retRGB.g = (srcRGB.g) / (1.0f - ((blendRGB.g != 1.0f) ? blendRGB.g : 0.9961f));
        retRGB.b = (srcRGB.b) / (1.0f - ((blendRGB.b != 1.0f) ? blendRGB.b : 0.9961f));
        
        retRGB.r = (blendRGB.r == 1.0f) ? blendRGB.r : ((retRGB.r < 1.0f) ? retRGB.r : 1.0f);
        retRGB.g = (blendRGB.g == 1.0f) ? blendRGB.g : ((retRGB.g < 1.0f) ? retRGB.g : 1.0f);
        retRGB.b = (blendRGB.b == 1.0f) ? blendRGB.b : ((retRGB.b < 1.0f) ? retRGB.b : 1.0f);
		break;
	}
	case ColorBurn:{
		retRGB.r = 1.0f - ((1.0f - srcRGB.r) / ((blendRGB.r != 0) ? blendRGB.r : 1));
        retRGB.g = 1.0f - ((1.0f - srcRGB.g) / ((blendRGB.g != 0) ? blendRGB.g : 1));
        retRGB.b = 1.0f - ((1.0f - srcRGB.b) / ((blendRGB.b != 0) ? blendRGB.b : 1));
        retRGB.r = (blendRGB.r == 0) ? blendRGB.r : ((retRGB.r > 0) ? retRGB.r : 0);
        retRGB.g = (blendRGB.g == 0) ? blendRGB.g : ((retRGB.g > 0) ? retRGB.g : 0);
        retRGB.b = (blendRGB.b == 0) ? blendRGB.b : ((retRGB.b > 0) ? retRGB.b : 0);
		break;
	}
	case Lighten:{
		retRGB.r = (blendRGB.r > srcRGB.r) ? blendRGB.r : srcRGB.r;
	    retRGB.g = (blendRGB.g > srcRGB.g) ? blendRGB.g : srcRGB.g;
	    retRGB.b = (blendRGB.b > srcRGB.b) ? blendRGB.b : srcRGB.b;
		break;
	}
	case Darken:{
		retRGB.r = (blendRGB.r > srcRGB.r) ? srcRGB.r : blendRGB.r;
        retRGB.g = (blendRGB.g > srcRGB.g) ? srcRGB.g : blendRGB.g;
        retRGB.b = (blendRGB.b > srcRGB.b) ? srcRGB.b : blendRGB.b;
		break;
	}
	case Reflect:{
		retRGB.r = (srcRGB.r * srcRGB.r) / (1.0f - ((blendRGB.r != 1.0f) ? blendRGB.r : 0.9961f));
        retRGB.g = (srcRGB.g * srcRGB.g) / (1.0f - ((blendRGB.g != 1.0f) ? blendRGB.g : 0.9961f));
        retRGB.b = (srcRGB.b * srcRGB.b) / (1.0f - ((blendRGB.b != 1.0f) ? blendRGB.b : 0.9961f));
        retRGB.r = (blendRGB.r == 1.0f) ? blendRGB.r : ((retRGB.r < 1.0f) ? retRGB.r : 1.0f);
        retRGB.g = (blendRGB.g == 1.0f) ? blendRGB.g : ((retRGB.g < 1.0f) ? retRGB.g : 1.0f);
        retRGB.b = (blendRGB.b == 1.0f) ? blendRGB.b : ((retRGB.b < 1.0f) ? retRGB.b : 1.0f);
		break;
	}
	case Glow:{
		retRGB.r = (blendRGB.r * blendRGB.r) / (1.0f - ((srcRGB.r != 1.0f) ? srcRGB.r : 0.9961f));
        retRGB.g = (blendRGB.g * blendRGB.g) / (1.0f - ((srcRGB.g != 1.0f) ? srcRGB.g : 0.9961f));
        retRGB.b = (blendRGB.b * blendRGB.b) / (1.0f - ((srcRGB.b != 1.0f) ? srcRGB.b : 0.9961f));
        retRGB.r = (srcRGB.r == 1.0f) ? srcRGB.r : ((retRGB.r < 1.0f) ? retRGB.r : 1.0f);
        retRGB.g = (srcRGB.g == 1.0f) ? srcRGB.g : ((retRGB.g < 1.0f) ? retRGB.g : 1.0f);
        retRGB.b = (srcRGB.b == 1.0f) ? srcRGB.b : ((retRGB.b < 1.0f) ? retRGB.b : 1.0f);
		break;
	}
	case LinearLight:{
		if (blendRGB.r < 0.5f) {
            retRGB.r = srcRGB.r + (2 * blendRGB.r);
            retRGB.r = (retRGB.r < 1.0f) ? 0 : (retRGB.r - 1.0f);
        } else {
            retRGB.r = srcRGB.r + (2 * (blendRGB.r - 0.5f));
            retRGB.r = (retRGB.r > 1.0f) ? 1.0f : retRGB.r;
        }
        if (blendRGB.g < 0.5f) {
            retRGB.g = srcRGB.g + (2 * blendRGB.g);
            retRGB.g = (retRGB.g < 1.0f) ? 0 : (retRGB.g - 1.0f);
        } else {
            retRGB.g = srcRGB.g + (2 * (blendRGB.g - 0.5f));
            retRGB.g = (retRGB.g > 1.0f) ? 1.0f : retRGB.g;
        }
        if (blendRGB.b < 0.5f) {
            retRGB.b = srcRGB.b + (2 * blendRGB.b);
            retRGB.b = (retRGB.b < 1.0f) ? 0 : (retRGB.b - 1.0f);
        } else {
            retRGB.b = srcRGB.b + (2 * (blendRGB.b - 0.5f));
            retRGB.b = (retRGB.b > 1.0f) ? 1.0f : retRGB.b;
        }
		break;
	}
	case Frame:{
		if ((blendRGB.r == 0 && blendRGB.g == 0 && blendRGB.r == 0)) {
            // 探测边框颜色(blendRGB.r > 230 && blendRGB.g > 230 && blendRGB.b > 230)
            retRGB.r = srcRGB.r;
            retRGB.g = srcRGB.g;
            retRGB.b = srcRGB.b;
        } else {
            retRGB.r = blendRGB.r;
            retRGB.g = blendRGB.g;
            retRGB.b = blendRGB.b;
        }
		break;
	}
	case Normal: 
	default:{
		retRGB = srcRGB;
		break;
	}
	}
	
	retRGB = (srcRGB * ( - mixture)) + (retRGB * mixture);
	
	return retRGB;
}

#endif //#ifndef __RS_IMAGE_BLEND_RSH__
