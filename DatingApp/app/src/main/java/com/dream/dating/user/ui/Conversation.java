package com.dream.dating.user.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dream.dating.R;


public class  Conversation extends AppCompatActivity {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);

    }

   /* public void enterReveal() {
        circularReveal = true;
        // get the center for the clipping circle
        int cx = attachments_layout.getMeasuredWidth() / 4;
        int cy = attachments_layout.getMeasuredHeight() / 4;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(attachments_layout.getWidth(), attachments_layout.getHeight()) / 2;

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(attachments_layout, cx, cy, 0, finalRadius);

        // make the view visible and start the animation
        attachments_layout.setVisibility(View.VISIBLE);
        anim.start();
    }

    public void exitReveal() {
        circularReveal = false;
        // get the center for the clipping circle
        int cx = attachments_layout.getMeasuredWidth() / 2;
        int cy = attachments_layout.getMeasuredHeight() / 2;

        // get the initial radius for the clipping circle
        int initialRadius = attachments_layout.getWidth() / 2;

        // create the animation (the final radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(attachments_layout, cx, cy, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                attachments_layout.setVisibility(View.INVISIBLE);
            }
        });

        // start the animation
        anim.start();
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /* exitReveal();*/
    }

    public void hide_attachments(View view) {
        /* exitReveal();*/
    }

}
