#ifndef __RS_YUV_RSH__
#define __RS_YUV_RSH__

static const int32_t MAX_GRAYSCALE_COUNT = 256;

static float GetY(float3 rgb) {
    return rgb.r * 0.299f + rgb.g * 0.587f + rgb.b * 0.114f;
}

static float GetU(float3 rgb) {
    return  rgb.b * 0.436f - rgb.r * 0.147f - rgb.g * 0.289f;
}

static float GetV(float3 rgb) {
    return rgb.r * 0.615f - rgb.g * 0.515f - rgb.b * 0.100f;
}

static float GetGrayscale(float3 rgb) {
	return rgb.r * 0.2126f + rgb.g * 0.7152f + rgb.b * 0.0722f;
}

static uint8_t GetGrayscale0255(float3 rgb) {
	return (uint8_t)(GetGrayscale(rgb) * (MAX_GRAYSCALE_COUNT - 1));
}

#endif //#ifndef __RS_YUV_RSH__
