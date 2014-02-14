#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

#include "Clamp.rsh"

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;
rs_script gScript;

// Magic factors
static const float Radius = 0.5f;
static const float Distortion = 1.5f;

// Static variables
static uint32_t _width;
static uint32_t _height;
static int32_t realXPos;
static int32_t realYPos;
static float realRadius;

static void setup() {
	_width = rsAllocationGetDimX(gIn);
	_height = rsAllocationGetDimY(gIn);
	realXPos = _width / 2;
	realYPos = _height / 2;
	realRadius = min(_width, _height) * Radius;
}

void filter() {
	setup();
    rsForEach(gScript, gIn, gOut, 0, 0);	// for each element of the input allocation,
    										// call root() method on gScript
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	float4 f4 = rsUnpackColor8888(*v_in);	// extract RGBA values, see rs_core.rsh
	int32_t posX = x;
	int32_t posY = y;
	
	float3 f3;
	float position = 1.0f - (sqrt(pown((float)(posX - realXPos), 2) + pown((float)(posY - realYPos), 2))) / realRadius;
    if (position > 0) {
        position = 1.0f - Distortion * pown(position, 2);
        
        float pos1 = (posX - realXPos) * position + realXPos;
        int32_t x1 = (int32_t)pos1;
        float pos3 = pos1 - x1;
        int32_t x2 = (pos3 > 0.0f) ? (x1 + 1) : x1;
        
        float pos2 = (posY - realYPos) * position + realYPos;
        int32_t y1 = (int32_t)pos2;
        float pos4 = pos2 - y1;
        int32_t y2 = (pos4 > 0.0f) ? (y1 + 1) : y1;
        
        x1 = FClamp(x1, 0, _width - 1);
        x2 = FClamp(x2, 0, _width - 1);
        y1 = FClamp(y1, 0, _height - 1);
        y2 = FClamp(y2, 0, _height - 1);

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