#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;
int32_t gIsHorizontal = 1;	//0垂直方向， 1水平方向
float gOffset = 0.5f;// 0.8

// Magic factors

// Static variables
static uint32_t _width;
static uint32_t _height;

static void initOutAllcation() {
    for (uint32_t theX = 0; theX < _width; theX++) {
        for (uint32_t theY = 0; theY < _height; theY++) {
        	uchar4 *v_in = (uchar4*)rsGetElementAt(gIn, theX, theY);
            uchar4 *v_out = (uchar4*)rsGetElementAt(gOut, theX, theY);
            *v_out = *v_in;
        }
    }
}

static void horizontalProcess() {
	int32_t start;
    int32_t limit;
	int32_t y_offset = (int32_t) (gOffset * _height);
    if (gOffset > 0.5f) {
        start = y_offset - (_height - y_offset);
        limit = y_offset;
    } else {
        start = y_offset;
        limit = y_offset + y_offset;
    }
    if (start < 0) {
        start = 0;
    }
    
    for (int32_t y = start; (y < limit) && (y < _height); y++) {
        int32_t y_pos = (-y + (2 * y_offset)) - 1;
        y_pos = (y_pos < 0) ? 0 : (y_pos >= _height ? _height - 1 : y_pos);
        for (int32_t x = 0; x < _width; x++) {
            uchar4 *v_in = (uchar4*)rsGetElementAt(gIn, x, y);
            uchar4 *v_out = (uchar4*)rsGetElementAt(gOut, x, y_pos);
            *v_out = *v_in;
        }
    }
} 

static void verticalProcess() {
	int32_t start;
    int32_t limit;
	int32_t x_offset = (int32_t) (gOffset * _width);
    if (gOffset > 0.5f) {
        start = x_offset - (_width - x_offset);
        limit = x_offset;
    } else {
        start = x_offset;
        limit = x_offset + x_offset;
    }
    if (start < 0) {
        start = 0;
    }
    
    for (int32_t x = start; (x < limit) && (x < _width); x++) {
        int32_t x_pos = (-x + (2 * x_offset)) - 1;
        x_pos = x_pos < 0 ? 0 : (x_pos >= _width ? _width - 1 : x_pos);
        for (int32_t y = 0; y < _height; y++) {
        	uchar4 *v_in = (uchar4*)rsGetElementAt(gIn, x, y);
            uchar4 *v_out = (uchar4*)rsGetElementAt(gOut, x_pos, y);
            *v_out = *v_in;
        }
    }
}

static void setup() {
	_width = rsAllocationGetDimX(gIn);
	_height = rsAllocationGetDimY(gIn);
	initOutAllcation();
}

void process() {
	setup();
	if (gIsHorizontal == 1) {
		horizontalProcess();
	} else {
		verticalProcess();
	}
}
