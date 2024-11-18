package com.example.faceauthentication.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.google.mlkit.vision.face.Face;
import java.util.List;

public class FaceBoxOverlay extends View {

    private final Paint boxPaint = new Paint();
    private List<Face> faces;

    public FaceBoxOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        boxPaint.setColor(Color.GREEN);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(5f);
    }

    public void setFaces(List<Face> faces) {
        this.faces = faces;
        invalidate(); // Redraw the view
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (faces != null) {
            int viewWidth = getWidth();
            int viewHeight = getHeight();

            for (Face face : faces) {
                Log.d("TAG", "onDraw before : " + face.getBoundingBox());
                RectF boundingBox = new RectF(face.getBoundingBox());

                // Transform the bounding box coordinates to match the view dimensions
                float left = boundingBox.left;
                float top = boundingBox.top;
                float right = boundingBox.right;
                float bottom = boundingBox.bottom;

                // Adjust for camera mirroring if using front camera
                left = viewWidth - right; // Mirror the x-coordinates
                right = viewWidth - boundingBox.left; // Mirror the x-coordinates

                // If using a camera with different aspect ratios, adjust here if necessary
                // Example for different aspect ratios:
                // top = (top * viewHeight) / cameraHeight;
                // bottom = (bottom * viewHeight) / cameraHeight;

                boundingBox.set(left, top, right, bottom);
                Log.d("TAG", "onDraw after : " + boundingBox);

                // Draw the rectangle for the bounding box
                canvas.drawRect(boundingBox, boxPaint);
            }
        }
    }
}
