package id.ndiappink.cakaran;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class DrawingView extends View {
    Paint paint = new Paint();
    Path path = new Path();
    public float GetX;
    public float GetY;

    //tworzenie ścieżki
    private Path rysujSciezke;
    private boolean erase = false;
    //drawing and canvas paint
    private Paint rysowanie, canvasPaint;
    //początkowy kolor
    private int paintColor = 0xFF000000;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;
    private float rozmiarPedzla, ostatniRozmiarPedzla;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setupDrawing();
    }

    public OutputStream startNew() {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
        return null;
    }

    public void setErase(boolean isErase) {
        erase = isErase;
        if (erase) rysowanie.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else rysowanie.setXfermode(null);
    }

    public void setBrushSize(float newSize) {
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        rozmiarPedzla = pixelAmount;
        rysowanie.setStrokeWidth(rozmiarPedzla);
    }

    public float getLastBrushSize() {
        return ostatniRozmiarPedzla;
    }

    public void setLastBrushSize(float lastSize) {
        ostatniRozmiarPedzla = lastSize;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(rysujSciezke, rysowanie);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        GetX = touchX;
        GetY = touchY;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                rysujSciezke.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                rysujSciezke.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(rysujSciezke, rysowanie);
                rysujSciezke.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void setColor(String newColor) {
        invalidate();
        paintColor = Color.parseColor(newColor);
        rysowanie.setColor(paintColor);
    }

    public void setupDrawing() {
        rysujSciezke = new Path();
        rysowanie = new Paint();
        rysowanie.setColor(paintColor);
        rysowanie.setAntiAlias(true);
        rysowanie.setStrokeWidth(5);
        rysowanie.setStyle(Paint.Style.STROKE);
        rysowanie.setStrokeJoin(Paint.Join.ROUND);
        rysowanie.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
        rozmiarPedzla = getResources().getInteger(R.integer.small_size);
        ostatniRozmiarPedzla = rozmiarPedzla;
        rysowanie.setStrokeWidth(rozmiarPedzla);
    }

    public boolean tri(){

        path.reset();
        paint.setColor(paintColor);
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(GetX,GetY);
        path.lineTo(GetX + 100,GetY -100);
        path.lineTo(GetX+200,GetY);
        path.close();

        drawCanvas.drawPath(path, paint);

        return true;
    }
    public boolean rect() {
        path.reset();

        paint.setColor(paintColor);
        //paint.setStrokeWidth(0);
        drawCanvas.drawRect(30 + GetX, 30 + GetY, 2 * rozmiarPedzla + GetX, 2 * rozmiarPedzla + GetY, paint);


        return false;
    }

    public void circ() {
        path.reset();
        paint.setColor(paintColor);
        paint.setStrokeWidth(10);

        drawCanvas.drawCircle(GetX, GetY, rozmiarPedzla * 5, paint);
    }

    public void star() {
        float mid = getWidth() / 2;
        float min = Math.min(getWidth(), getHeight());
        float fat = min / 17;
        float half = min / 2;
        float rad = half - fat;
        mid = mid - half;

        paint.setStrokeWidth(fat);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(paintColor);

        //drawCanvas.drawCircle(GetX, GetY, rad, paint);



        path.reset();

        paint.setStyle(Paint.Style.FILL);

        float sum = mid + half;


        // top left
        path.moveTo(sum * 0.5f, half * 0.84f);
        // top right
        path.lineTo(sum * 1.5f, half * 0.84f);
        // bottom left
        path.lineTo(sum * 0.68f, half * 1.45f);
        // top tip
        path.lineTo(sum * 1.0f, half * 0.5f);
        // bottom right
        path.lineTo(sum * 1.32f, half * 1.45f);
        // top left
        path.lineTo(sum * 0.5f, half * 0.84f);


        path.offset(GetX-500,GetY-500);
        drawCanvas.drawPath(path, paint);

    }

    public void heart() {

        path.reset();

        // Fill the canvas with background color
        //drawCanvas.drawColor(Color.WHITE);
        paint.setShader(null);

        float width = getContext().getResources().getDimension(R.dimen.heart_width);
        float height = getContext().getResources().getDimension(R.dimen.heart_height);

        // Starting point
        path.moveTo(width / 2, height / 5);

        // Upper left path
        path.cubicTo(5 * width / 14, 0,
                0, height / 15,
                width / 28, 2 * height / 5);

        // Lower left path
        path.cubicTo(width / 14, 2 * height / 3,
                3 * width / 7, 5 * height / 6,
                width / 2, height);

        // Lower right path
        path.cubicTo(4 * width / 7, 5 * height / 6,
                13 * width / 14, 2 * height / 3,
                27 * width / 28, 2 * height / 5);

        // Upper right path
        path.cubicTo(width, height / 15,
                9 * width / 14, 0,
                width / 2, height / 5);

        paint.setColor(paintColor);
        paint.setStyle(Style.FILL);

        path.offset(GetX-100,GetY-100);

        drawCanvas.drawPath(path, paint);

    }

    public void Text(String text) {
        paint.setColor(paintColor);
        paint.setTextSize(rozmiarPedzla);
        drawCanvas.drawText(text, GetX, GetY, paint);
    }



}

