#ifndef __RS_YUV_RSH__
#define __RS_YUV_RSH__

static float GetY(float3 rgb) {
    return rgb.r * 0.299f + rgb.g * 0.587f + rgb.b * 0.114f;
}

static float GetU(float3 rgb) {
    return  rgb.b * 0.436f - rgb.r * 0.147f - rgb.g * 0.289f;
}

static float GetV(float3 rgb) {
    return rgb.r * 0.615f - rgb.g * 0.515f - rgb.b * 0.100f;
}

#endif //#ifndef __RS_YUV_RSH__
