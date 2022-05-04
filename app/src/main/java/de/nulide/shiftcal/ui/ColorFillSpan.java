package de.nulide.shiftcal.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.text.style.LineBackgroundSpan;

import androidx.annotation.NonNull;

public class ColorFillSpan implements LineBackgroundSpan {

    private int color;
    private boolean top;

    public ColorFillSpan(int color, boolean top) {
        this.color = color;
        this.top = top;
    }

    @Override
    public void drawBackground(@NonNull Canvas canvas, @NonNull Paint paint, int left, int right, int top, int baseline, int bottom,
                               CharSequence charSequence,
                               int start, int end, int lineNum) {
        // This is a bit hacky since we have to use the text background spans for drawing the triangles. Only draw them once, so on the first line.
        if (lineNum != 0) return;
        int w = canvas.getWidth();
        int h = canvas.getHeight();
        // This is the only way I could find to get the top of the canvas in local coordinates (relative to the text box).
        int canvasTop = canvas.getClipBounds().top;

        float osw = paint.getStrokeWidth();
        paint.setStrokeWidth(1);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);

        Point point1_draw;
        Point point2_draw;
        Point point3_draw;
        if (this.top) {
            point1_draw = new Point(0, canvasTop);
            point2_draw = new Point(0, h + canvasTop);
            point3_draw = new Point(w, canvasTop);
        } else {
            point1_draw = new Point(w, canvasTop);
            point3_draw = new Point(0, h + canvasTop);
            point2_draw = new Point(w, h + canvasTop);
        }

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(point1_draw.x,point1_draw.y);
        path.lineTo(point2_draw.x,point2_draw.y);
        path.lineTo(point3_draw.x,point3_draw.y);
        path.lineTo(point1_draw.x,point1_draw.y);
        path.close();

        canvas.drawPath(path, paint);
        paint.setStrokeWidth(osw);
    }
}
