#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;
rs_script gScript;

// Magic factors
static const float Radius = 0.5f;
static const float Distortion = 1.5f;
static const float2 Center = {0.5f, 0.5f};

// Static variables
static uint32_t _width;
static uint32_t _height;
static uint32_t realXPos;
static uint32_t realYPos;
static float realRadius;

static void setup() {
	_width = rsAllocationGetDimX(gIn);
	_height = rsAllocationGetDimY(gIn);
	realXPos = _width * Center.x;
	realYPos = _height * Center.y;
	realRadius = min(_width, _height) * Radius;
}

void filter() {
	setup();
    rsForEach(gScript, gIn, gOut, 0, 0);	// for each element of the input allocation,
    										// call root() method on gScript
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	float4 f4 = rsUnpackColor8888(*v_in);	// extract RGBA values, see rs_core.rsh
	
	float3 f3;
	float position = 1.0f - sqrt((float)((x - realXPos) * (x - realXPos) + (y - realYPos) * (y - realYPos))) / realRadius;
    if (position > 0) {
        position = 1.0f - (Distortion * position * position);
        float pos1 = (x - realXPos) * position + realXPos;
        float pos2 = (y - realYPos) * position + realYPos;
        int32_t x1 = pos1;
        float pos3 = pos1 - x1;
        int32_t x2 = (pos3 > 0.0f) ? (x1 + 1) : x1;
        int32_t y1 = pos2;
        float pos4 = pos2 - y1;
        int32_t y2 = (pos4 > 0.0f) ? (y1 + 1) : y1;
        if (x1 < 0) {
            x1 = 0;
        } else if (x1 >= _width) {
            x1 = _width - 1;
        }
        if (x2 < 0) {
            x2 = 0;
        } else if (x2 >= _width) {
            x2 = _width - 1;
        }
        if (y1 < 0) {
            y1 = 0;
        } else if (y1 >= _height) {
            y1 = _height - 1;
        }
        if (y2 < 0) {
            y2 = 0;
        } else if (y2 >= _height) {
            y2 = _height - 1;
        }

        float4 theColor1 = rsUnpackColor8888(*(const uchar4*)rsGetElementAt(gIn, x1, y1));
		float4 theColor2 = rsUnpackColor8888(*(const uchar4*)rsGetElementAt(gIn, x2, y1));
		float4 theColor3 = rsUnpackColor8888(*(const uchar4*)rsGetElementAt(gIn, x2, y2));
		float4 theColor4 = rsUnpackColor8888(*(const uchar4*)rsGetElementAt(gIn, x1, y2));
		
        f3.r = theColor1.r * (1.0f - pos4) * (1.0f - pos3) 
        	+ theColor2.r * (1.0f - pos4) * pos3 
        	+ theColor3.r * pos4 * pos3 
        	+ theColor4.r * pos4 * (1.0f - pos3);
        f3.g = theColor1.g * (1.0f - pos4) * (1.0f - pos3) 
	        + theColor2.g * (1.0f - pos4) * pos3 
	        + theColor3.g * pos4 * pos3 
	        + theColor4.g * pos4 * (1.0f - pos3);
        f3.b = theColor1.b * (1.0f - pos4) * (1.0f - pos3) 
	        + theColor2.b * (1.0f - pos4) * pos3 
	        + theColor3.b * pos4 * pos3 
	        + theColor4.b * pos4 * (1.0f - pos3);
	        
    } else {
        f3 = f4.rgb;
    }
    
    *v_out = rsPackColorTo8888(f3);
}