package com.ajay.ridiculousfishing;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;

public class Hook {

    // PROPERTIES
    private Bitmap image;
    private RectF hitbox;

    private float xPosition;
    private float yPosition;

    public Hook(Context context, float x, float y) {
        // 1. set up the initial position of the Hook
        this.xPosition = x;
        this.yPosition = y;

        // 2. Set the default image - all hooks have same image
        this.image = BitmapFactory.decodeResource(context.getResources(), R.drawable.hook);

        // 3. Set the default hitbox - all hooks have same hitbox
        this.hitbox = new RectF(
                this.xPosition - this.getImage().getWidth()/2 - 20,
                this.yPosition - 10,
                this.xPosition + this.image.getWidth(),
                this.yPosition + this.image.getHeight()
        );
    }


    // GETTER AND SETTER METHODS
    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public RectF getHitbox() {
        return hitbox;
    }

    public void setHitbox(RectF hitbox) {
        this.hitbox = hitbox;
    }

    public float getxPosition() {
        return xPosition;
    }

    public void setxPosition(float xPosition) {
        this.xPosition = xPosition;
    }

    public float getyPosition() {
        return yPosition;
    }

    public void setyPosition(float yPosition) {
        this.yPosition = yPosition;
    }

    public void updateHitBox(){
        hitbox.left = xPosition;
        hitbox.top = yPosition;
        hitbox.right = xPosition+image.getWidth();
        hitbox.bottom = yPosition+image.getHeight();
    }
}
