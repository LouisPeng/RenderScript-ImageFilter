#ifndef __RS_CLAMP_RSH__
#define __RS_CLAMP_RSH__

static inline int FClamp(const t, const tLow, const tHigh) {
    if (t < tHigh) {
        return ((t > tLow) ? t : tLow);
    }
    return tHigh;
}

static inline double FClampFloat(const float t, const float tLow, const float tHigh) {
    if (t < tHigh) {
        return ((t > tLow) ? t : tLow);
    }
    return tHigh;
}

static inline float FClamp0255Float(const float d) {
    return FClampFloat(d, 0.0f, 255.0f);
}

static inline int FClamp0255(const float d) {
    return (int)(FClamp0255Float(d) + 0.5f);
}

#endif //#ifndef __RS_CLAMP_RSH__
