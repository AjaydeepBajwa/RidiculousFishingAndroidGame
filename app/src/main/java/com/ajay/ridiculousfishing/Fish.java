package com.ajay.ridiculousfishing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.media.Image;

public class Fish {

    // PROPERTIES:
    // Image
    // Hitbox
    private Bitmap image;
    private RectF hitbox;

    private float xPosition;
    private float yPosition;
    private int imagePath;
    private String fishStatus = "";

    public String getFishStatus() {
        return fishStatus;
    }

    public void setFishStatus(String fishStatus) {
        this.fishStatus = fishStatus;
    }

    public int getImagePath() {
        return imagePath;
    }

    public void setImagePath(int imagePath) {
        this.imagePath = imagePath;
    }

    public Fish(Context context, float x, float y, int imagePath) {
        // 1. set up the initial position of the Enemy
        this.xPosition = x;
        this.yPosition = y;
        this.imagePath = imagePath;
        this.image = BitmapFactory.decodeResource(context.getResources(), imagePath);

        // Set the default hitbox - all fishes have same hitbox
        this.hitbox = new RectF(
                this.xPosition,
                this.yPosition,
                this.xPosition + this.image.getWidth(),
                this.yPosition + this.image.getHeight()

        );
    }

    // Getter and setters
    // Autogenerate this by doing Right Click --> Generate --> Getter&Setter

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
