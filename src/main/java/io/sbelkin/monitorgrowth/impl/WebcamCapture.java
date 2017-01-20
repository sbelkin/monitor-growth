package io.sbelkin.monitorgrowth.impl;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamUtils;
import com.github.sarxos.webcam.util.ImageUtils;
import io.sbelkin.monitorgrowth.ActionInterface;

import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by sabelkin on 1/19/2017.
 */
public class WebcamCapture implements ActionInterface {

    private static final Logger LOGGER = Logger.getLogger( WebcamCapture.class.getName() );
    private final String uploadFolder;

    public WebcamCapture(String uploadFolder){
        this.uploadFolder = uploadFolder;
    }

    @Override
    public void action() {
        Webcam webcam = Webcam.getDefault();
        Long time = System.currentTimeMillis();
        String file = uploadFolder+String.valueOf(time);
        WebcamUtils.capture(webcam, file, ImageUtils.FORMAT_JPG);
        LOGGER.info(String.format("Image %1$s.jpg taken: %2$s ", file, new Date()));
        webcam.close();
    }
}
