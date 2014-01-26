#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

#include "Clamp.rsh"

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;
rs_script gScript;
float *gImageWithPaddingBuffer;
float *gTempBuffer;
float gSigma;
int32_t gPadding;

// Magic factors

// Static variables
static uint32_t _width;
static uint32_t _height;
static uint32_t _widthWithPadding;
static uint32_t _heightWithPadding;

static void ConvertImageWithPadding() {    
    int32_t index = 0;
    int32_t numb = 0;
    for (int32_t i = -1 * gPadding; numb < _heightWithPadding; i++) {
        int32_t y = i;
        if (i < 0) {
            y = 0;
        } else if (i >= _height) {
            y = _height - 1;
        }
        int32_t count = 0;
        int32_t negPadding = -1 * gPadding;
        while (count < _widthWithPadding) {
            int32_t x = negPadding;
            if (negPadding < 0) {
                x = 0;
            } else if (negPadding >= _width) {
                x = _width - 1;
            }
            
			float4 theF4 = rsUnpackColor8888(*(const uchar4*)rsGetElementAt(gIn, x, y));
			theF4 *= 255.0f;
			gImageWithPaddingBuffer[index] 		= theF4.r * 0.003921569f;
			gImageWithPaddingBuffer[index + 1] 	= theF4.g * 0.003921569f;
			gImageWithPaddingBuffer[index + 2] 	= theF4.b * 0.003921569f;			

            count++;
            negPadding++;
            index += 3;
        }
        numb++;
    }
}

static void Transpose(float *gImageWithPaddingBuffer, float *dstPixels, uint32_t width, uint32_t height) {
    for (int32_t i = 0; i < height; i++) {
        for (int32_t j = 0; j < width; j++) {
            uint32_t index = j * height * 3 + i * 3;
            uint32_t position = i * width * 3 + j * 3;
            dstPixels[index] 	 = gImageWithPaddingBuffer[position];
            dstPixels[index + 1] = gImageWithPaddingBuffer[position + 1];
            dstPixels[index + 2] = gImageWithPaddingBuffer[position + 2];
        }
    }
}

static void ApplyPass(float *pixels, uint32_t width, uint32_t height, float b0, float b1, float b2, float b3, float b) {
    float numb = 1.0f / b0;
    int32_t tripleWidth = width * 3;
    for (int32_t i = 0; i < height; i++) {
        int32_t stepLength = i * tripleWidth;
        for (int32_t j = stepLength + 9; j < (stepLength + tripleWidth); j += 3) {
            pixels[j] = (b * pixels[j])
                    + ((((b1 * pixels[j - 3]) + (b2 * pixels[j - 6])) + (b3 * pixels[j - 9])) * numb);
            pixels[j + 1] = (b * pixels[j + 1])
                    + ((((b1 * pixels[(j + 1) - 3]) + (b2 * pixels[(j + 1) - 6])) + (b3 * pixels[(j + 1) - 9])) * numb);
            pixels[j + 2] = (b * pixels[j + 2])
                    + ((((b1 * pixels[(j + 2) - 3]) + (b2 * pixels[(j + 2) - 6])) + (b3 * pixels[(j + 2) - 9])) * numb);
        }
        for (int32_t k = ((stepLength + tripleWidth) - 9) - 3; k >= stepLength; k -= 3) {
            pixels[k] = (b * pixels[k])
                    + ((((b1 * pixels[k + 3]) + (b2 * pixels[k + 6])) + (b3 * pixels[k + 9])) * numb);
            pixels[k + 1] = (b * pixels[k + 1])
                    + ((((b1 * pixels[(k + 1) + 3]) + (b2 * pixels[(k + 1) + 6])) + (b3 * pixels[(k + 1) + 9])) * numb);
            pixels[k + 2] = (b * pixels[k + 2])
                    + ((((b1 * pixels[(k + 2) + 3]) + (b2 * pixels[(k + 2) + 6])) + (b3 * pixels[(k + 2) + 9])) * numb);
        }
    }
}
    
static void ApplyBlur() {
    // Calculate the coefficients
    float q = gSigma;
    float q2 = q * q;
    float q3 = q2 * q;

    float b0 = 1.57825f + 2.44413f * q + 1.4281f * q2 + 0.422205f * q3;
    float b1 = 2.44413f * q + 2.85619f * q2 + 1.26661f * q3;
    float b2 = -(1.4281f * q2 + 1.26661f * q3);
    float b3 = 0.422205f * q3;

    float b = 1.0f - ((b1 + b2 + b3) / b0);

    // Apply horizontal pass
    ApplyPass(gImageWithPaddingBuffer, _widthWithPadding, _heightWithPadding, b0, b1, b2, b3, b);

    // Transpose the array
    Transpose(gImageWithPaddingBuffer, gTempBuffer, _widthWithPadding, _heightWithPadding);

    // Apply vertical pass
    ApplyPass(gTempBuffer, _heightWithPadding, _widthWithPadding, b0, b1, b2, b3, b);

    // transpose back
    Transpose(gTempBuffer, gImageWithPaddingBuffer, _heightWithPadding, _widthWithPadding);
}
    
static void setup() {
	_width = rsAllocationGetDimX(gIn);
	_height = rsAllocationGetDimY(gIn);
	_widthWithPadding = _width + gPadding * 2;
    _heightWithPadding = _height + gPadding * 2;
    
    ConvertImageWithPadding();
    ApplyBlur();
}

void filter() {
	setup();
    rsForEach(gScript, gIn, gOut, 0, 0);	// for each element of the input allocation,
    										// call root() method on gScript
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	float4 f4 = rsUnpackColor8888(*v_in);	// extract RGBA values, see rs_core.rsh
	
    int32_t numb = ((y + gPadding) * _widthWithPadding) + gPadding;
    int32_t position = (numb + x) * 3;
    float3 f3 = {gImageWithPaddingBuffer[position], gImageWithPaddingBuffer[position + 1], gImageWithPaddingBuffer[position + 2]};
    f3 = FClamp01Float3(f3);
    
    *v_out = rsPackColorTo8888(f3);
}