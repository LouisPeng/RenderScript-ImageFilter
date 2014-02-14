#ifndef __RS_LUT_RSH__
#define __RS_LUT_RSH__

static float3 LUT_Process(const float4 color, const float* LUT) {
    float3 f3;
    f3.r = LUT[(int32_t)(color.r * 255)] / 255.0f;
    f3.g = LUT[(int32_t)(color.g * 255)] / 255.0f;
    f3.b = LUT[(int32_t)(color.b * 255)] / 255.0f;
    return f3;
}

#endif //#ifndef __RS_LUT_RSH__