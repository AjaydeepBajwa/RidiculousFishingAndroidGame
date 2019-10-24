package com.ajay.ridiculousfishing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.Image;

public class Fish {

    // PROPERTIES:
    // Image
    // Hitbox
    private Bitmap image;
    private Rect hitbox;

    private int xPosition;
    private int yPosition;
    private int imagePath;

    public int getImagePath() {
        return imagePath;
    }

    public void setImagePath(int imagePath) {
        this.imagePath = imagePath;
    }

    public Fish(Context context, int x, int y, int imagePath) {
        // 1. set up the initial position of the Enemy
        this.xPosition = x;
        this.yPosition = y;
        this.imagePath = imagePath;
        this.image = BitmapFactory.decodeResource(context.getResources(), imagePath);

        // Set the default hitbox - all fishes have same hitbox
        this.hitbox = new Rect(
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

    public Rect getHitbox() {
        return hitbox;
    }

    public void setHitbox(Rect hitbox) {
        this.hitbox = hitbox;
    }

    public int getxPosition() {
        return xPosition;
    }

    public void setxPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public void setyPosition(int yPosition) {
        this.yPosition = yPosition;
    }

    public void updateHitBox(){
        hitbox.left = xPosition;
        hitbox.top = yPosition;
        hitbox.right = xPosition+image.getWidth();
        hitbox.bottom = yPosition+image.getHeight();
    }
}
