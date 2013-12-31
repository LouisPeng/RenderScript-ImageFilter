#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

static void print(float3 f3) {
	rsDebug("f3 = ", f3);
}

static float3 init() {
	float3 f3 = {0.1f, 0.2f, 0.3f};
    return f3;
}

void test() {
    {
	    float3 f3 = init();
	    f3 *= 2.0f;
	    rsDebug("f3 *= 2.0f", 0);
	    print(f3);
    }
    {
	    float3 f3 = init();
	    f3 /= 2.0f;
	    rsDebug("f3 /= 2.0f", 0);
	    print(f3);
    }
    {
	    float3 f3 = init();
	    f3 -= 0.1f;
	    rsDebug("f3 -= 0.1f;", 0);
	    print(f3);
    }
    {
	    float3 f3 = init();
	    f3 = 1.0f - f3;
	    rsDebug("f3 = 1.0f - f3", 0);
	    print(f3);
    }
    {
	    float3 mult3 = {2.0f, 3.0f, 4.0f};
	    float3 f3 = init();
	    f3 = dot(f3, mult3);
	    rsDebug("f3 = dot(f3, mult3)", 0);
	    print(f3);
    }
    {
	    float3 mult3 = {2.0f, 3.0f, 4.0f};
	    float3 f3 = init();
		f3 = f3 * mult3;
	    rsDebug("f3 = f3 * mult3", 0);
	    print(f3);
    }
}
