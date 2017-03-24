package com.dht.editimage.editimage.view.uitls.easing;

/**
 * Created by dht04 on 2017/1/4.
 */
public interface Easing {
    double easeOut(double time, double start, double end, double duration);

    double easeIn(double time, double start, double end, double duration);

    double easeInOut(double time, double start, double end, double duration);
}
